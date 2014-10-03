(function ($, w, connection, board, ui, constants, undefined) {

    var game = connection.gameHub;

    function initializeGame() {
        game.refreshMap()
            .done(function (serverMap) {
                board.initializeMap(serverMap);

            });
    }

    function playerQuitGame() {
        game.quitGame()
            .done(function (success) {
                // dirty redirect to the lobby
                window.location = "/Home/Lobby";
            });
    }

    function displayConnectedUsers() {
        game.getConnectedUsers()
            .done(function (listOfPlayers) {
                ui.appendConsole("<div>Connected users on the system:</div>");
                for (i = 0; i < listOfPlayers; i++) {
                    ui.appendToConsole("<div>" + listOfPlayers[i].User.Nickname + "</div>");
                }
            });
    }

    function displayPlayersInGame() {
        game.getPlayers()
            .done(function (players) {
                ui.appendToConsole("<div>Current players:</div>");
                for (i = 0; i < players.length; i++) {
                    ui.appendToConsole("<div>" + players[i].User.Nickname + "</div>");
                }
            });
    }

    function displayPlayerInfo() {
        game.getCurrentPlayer()
            .done(function (player) {
                ui.appendToConsole("<div>Gold: " + player.Gold + "</div>");
                ui.appendToConsole("<div>Units: " + player.Units.length + "</div>");
                for (i = 0; i < player.Units.length; i++) {
                    // affiche le id des units, le nom et la position.
                    ui.appendToConsole("<div>" + player.Units[i].Id + " " + player.Units[i].Name +
                                       " (" + player.Units[i].Position.X + ", " + player.Units[i].Position.Y +
                                       ") " + player.Units[i].MovementPointLeft + "</div>");
                }
            });
    }

    function moveUnit(id, newX, newY) {
        game.moveUnit(id, newX, newY)
            .done(function (moveaction) {
                ui.appendToConsole("<div>Unit " + moveaction.Unit.Id + " moved to (" + moveaction.Unit.Position.X +
                                    ", " + moveaction.Unit.Position.Y + ")</div>");
            })
            .fail(function (error) {
                ui.appendToConsole("<div>" + error + "</div>");
            });
    }

    function resetMovementPoint() {
        game.resetMovementPoint()
            .done(function (units) {
                ui.appendToConsole("<div>Your units has recovered their movement points</div>");
                for (i = 0; i < units.length; i++) {
                    ui.appendToConsole("<div>" + units[i].Name + " (" + units[i].Position.X + ", " + units[i].Position.Y +
                                        ") Mov. Pt: " + units[i].MovementPointLeft + "</div>");
                }
            });

    }

    function displayTileAroundUnit(id) {
        game.getTilesAroundUnit(id)
            .done(function (tiles) {
                for (i = 0; i < tiles.length; i++) {
                    ui.appendToConsole("<div>" + tiles[i].X + ", " + tiles[i].Y + "</div>");
                }

            });
    }


    $(function () {
        connection.hub.start(function () {
            game.join()
                .done(function (success) {
                    if (success > -1) {
                        ui.appendToConsole("<div>Welcome to the game</div>");
                        initializeGame();
                    } else if (success == -1) {
                        ui.appendToConsole("<div>User was not found.</div>");
                    } else if (success == -2) {
                        ui.appendToConsole("<div>The game was not retreived for this user</div>");
                    }
                });

            $("#game_console_input_textbox").keydown(function (event) {
                if (event.keyCode == '13') {
                    if ($("#game_console_input_textbox").val() == ":refresh") {
                        displayConnectedUsers();
                    }
                    else if ($("#game_console_input_textbox").val() == ":list") {
                        displayPlayersInGame();
                    }
                    else if ($("#game_console_input_textbox").val().search(":move") != -1) {
                        array = $("#game_console_input_textbox").val().split(" ");
                        moveUnit(array[1], array[2], array[3]);

                        // inclure le code du board pour afficher le unit!

                    }
                    else if ($("#game_console_input_textbox").val() == ":info") {
                        displayPlayerInfo();
                    }
                    else if ($("#game_console_input_textbox").val() == ":quit") {
                        playerQuitGame();
                    }
                    else if ($("#game_console_input_textbox").val() == ":reset") {
                        resetMovementPoint();
                    }
                    else if ($("#game_console_input_textbox").val().search(":around") != -1) {
                        array = $("#game_console_input_textbox").val().split(" ");
                        displayTileAroundUnit(array[1]);
                    }
                    else {
                        game.sendToConsole($("#game_console_input_textbox").val());
                    }

                    $("#game_console_input_textbox").val("");
                }
            });

            $("#buy-scout").click(function () {
                game.buyUnit(constants.UNIT_TYPES.Scout)
                    .done(function (unit) {
                        board.addUnit(unit);
                        ui.appendToConsole("<div>Unit spawned at (" + unit.Position.X + "," + unit.Position.Y + ")</div>");
                    })
                    .fail(function (error) {
                        ui.appendToConsole("<div>" + error + "</div>");
                    });
            });
        });
    });

    board.requestMove = function (id, position) {
        game.moveUnit(id, position.x, position.y)
            .done(function (move) {
                board.moveUnit(move.Unit.Id, move.Path);
            })
            .fail(function (error) {
                ui.appendToConsole("<div>" + error + "</div>");
            });
    }

    // Hub functions;
    game.playerJoined = function (nickname) {
        ui.appendToConsole("<div class='player-join'>" + nickname + " joined the game</div>");
    }

    game.writeOnConsole = function (msg) {
        ui.appendToConsole("<div>" + msg + "</div>");
    }

    game.writeOnConsoleFromUser = function (msg, user) {
        if (user != null) {
            ui.appendToConsole("<div class='user-message'>" + user.Nickname + ": " + msg + "</div>");
        }
    }

    game.newUnitAdded = function (unit) {
        board.addUnit(unit);
    }

    game.removeUnit = function (unitid) {
        //ui.appendToConsole("<div class='user-message'>removing unit</div>");
        board.removeUnit(unit);
        ui.appendToConsole("<div class='user-message'>removing unit</div>");
    }

    game.unitHasMoved = function (moveaction) {
        board.moveUnit(moveaction.Unit.Id, moveaction.Path);
    }

})(jQuery, window, $.connection, window.board, window.game.ui, window.constants);