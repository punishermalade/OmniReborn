(function ($, w, undefined) {

    var ui = {
        appendToConsole: function (message) {
            $("#game_console_area").append(message);
            $("#game_console_area").scrollTop($("#game_console_area")[0].scrollHeight);
        }
    }

    function initializeEvents() {
        $("#toggle-console-view").click(function (event) {
            $("#game-console").toggle()
        });

        $(".game-console-header").click(function () {
            $("#game_console_area").toggle();
            $("#game_console_input_area").toggle();
        });
    }

    $(function () {
        initializeEvents();
    });

    if (!window.game) window.game = {};
    window.game.ui = ui;

})(jQuery, window);