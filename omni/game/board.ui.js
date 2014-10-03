(function ($, w, constants, omniscientia, undefined) {
	
	console.log("board.ui.js init");
    // Vars;
    var items = new Image(),
        viewport,
        viewportContext,
        map,
        boardOffsetX = 0,
        boardOffsetY = 0,
        velocityX = 0,
        velocityY = 0,
        panning = false,
        lastUpdate,
        fpsCount = 0,
        elapsedTime = 0,
        selectTool,
        selectToolX = 0,
        selectToolY = 0,
        movingUnits = [],
        selectedUnit,
		selectedTileX,
		selectedTileY,
		tileLoaded = false;

    var requestAnimationFrame =
        requestAnimationFrame ||
        webkitRequestAnimationFrame ||
        mozRequestAnimationFrame ||
        msRequestAnimationFrame ||
        oRequestAnimationFrame;

    // Events;
    function initializeEvents() {
        var mousedownInCanvas = false;

        var lastMouseX, lastMouseY;



        $(document).mouseup(function () {
            mousedownInCanvas = false;
            panning = false;
            lastMouseX = 0;
            lastMouseY = 0;
        });

        $('#viewport').mousedown(function () {
            mousedownInCanvas = true;
        });

        $('#viewport').mousemove(function (e) {
            var position = getBoardCursorPosition(e, boardOffsetX, boardOffsetY);
            if (!position) return;

            var mapPosition = map.geTilePosition(position);

            var tile = map.getTileAt(mapPosition.x, mapPosition.y);
            if (tile) {
                $("#tilex").html(mapPosition.x);
                $("#tiley").html(mapPosition.y);
                $("#tiletype").html(tile.type);
            }

            if (mousedownInCanvas) {
                var dragPosition = getBoardCursorPosition(e);
                if (!lastMouseX) lastMouseX = dragPosition.x;
                if (!lastMouseY) lastMouseY = dragPosition.y;

                var vX = lastMouseX - dragPosition.x;
                var vY = lastMouseY - dragPosition.y;

                if (vX > 10 || vY > 10) panning = true;

                boardOffsetX += vX;
                boardOffsetY += vY;

                velocityX = vX;
                velocityY = vY;

                lastMouseX = dragPosition.x;
                lastMouseY = dragPosition.y;
            }

            $("#mousex").html(position.x);
            $("#mousey").html(position.y);
            $("#offsetx").html(boardOffsetX);
            $("#offsety").html(boardOffsetY);
        });

        $('#viewport').mouseup(function (e) {
            if (!mousedownInCanvas || panning) return;
            var position = getBoardCursorPosition(e, boardOffsetX, boardOffsetY);
            mapPosition = map.geTilePosition(position);

            var unit = map.getUnitAt(mapPosition.x, mapPosition.y);

            // aucune unité dans la case et unité deja sélectionnée
			
            if (selectedUnit) {
				/*
                if (board.requestMove) {
				
					console.log('requestMove');
                    board.requestMove(selectedUnit.id, mapPosition);
                    selectedUnit = null;
                    selectTool.visible = false;
                }*/
				
				if (selectedUnit.position.x == mapPosition.x && selectedUnit.position.y == mapPosition.y) {
					selectedUnit = null;
					selectTool.visible = false;
				}
				else {
					// this is a request to the server to move this unit at this position
					serverInterface.moveUnit(selectedUnit, mapPosition.x, mapPosition.y);
					/*
					tile = map.getTileAt(mapPosition.x, mapPosition.y);
					selectToolX = 100 + tile.x;
					selectToolY = 100 + tile.y;
					selectTool.visible = true;
					
					// set up the current position	
					$("#selectedtilex").html(mapPosition.x);
					$("#selectedtiley").html(mapPosition.y);
					*/
				}
            }
            else if (unit && !selectedUnit) { // il y a une unité a la case et aucune autre n'est deja sélectionnée
                selectedUnit = unit;
                tile = map.getTileAt(mapPosition.x, mapPosition.y);
                selectToolX = 100 + tile.x;
                selectToolY = 100 + tile.y;
                selectTool.visible = true;
            }
            else {
				tile = map.getTileAt(mapPosition.x, mapPosition.y);
                selectToolX = 100 + tile.x;
                selectToolY = 100 + tile.y;
                selectTool.visible = true;
				
				// set up the current position	
				$("#selectedtilex").html(mapPosition.x);
				$("#selectedtiley").html(mapPosition.y);
            }
			
			/*
			tile = map.getTileAt(mapPosition.x, mapPosition.y);
			selectToolX = 100 + tile.x;
			selectToolY = 100 + tile.y;
			selectTool.visible = true;
			*/
			// set up the current position
			
			$("#selectedtilex").html(mapPosition.x);
			$("#selectedtiley").html(mapPosition.y);
			
            $("#mousex").html(position.x);
            $("#mousey").html(position.y);
        });
    }
	function startMapDrawingLoop() {
		map.draw();

		viewport = document.getElementById("viewport");
		viewportContext = viewport.getContext('2d');

		lastUpdate = Date.now();
		requestAnimationFrame(animationLoop);
	}

    function initializeUi() {
        items.src = "game/images/items.png";
		
		if (tileLoaded) {
			startMapDrawingLoop();
		}
		
		items.onload = function () {
			
            startMapDrawingLoop()
			tileLoaded = true;
        }

        selectTool = new omniscientia.SelectTool(0, 0, items);
    }
	
	

    var animationLoop = function () {
        var now = Date.now();
        var elapsed = (now - lastUpdate);
        lastUpdate = now;
        elapsedTime += elapsed;

        update(elapsed);
        render(elapsed);

        fpsCount++;

        if (elapsedTime >= 1000) {
            $("#fps").html(fpsCount);
            elapsedTime = 0;
            fpsCount = 0;
        }
        requestAnimationFrame(animationLoop);
    }

    function updatePanningVelocity(elapsed) {
        if (velocityX > 0) {
            velocityX -= 0.02 * elapsed;
        } else {
            velocityX += 0.02 * elapsed;
        }

        if (velocityY > 0) {
            velocityY -= 0.02 * elapsed;
        } else {
            velocityY += 0.02 * elapsed;
        }

        if (Math.floor(velocityX) == 0) velocityX = 0;
        if (Math.floor(velocityY) == 0) velocityY = 0;

        boardOffsetX += Math.floor(velocityX);
        boardOffsetY += Math.floor(velocityY);
    }

    function ensureOffsetIsValid() {
        var buffer = map.canvas;

        if (boardOffsetX <= 0) boardOffsetX = 0;
        if (boardOffsetY <= 0) boardOffsetY = 0;


        if (boardOffsetX > buffer.width - viewport.width) boardOffsetX = buffer.width - viewport.width;
        if (boardOffsetY > buffer.height - viewport.height) boardOffsetY = buffer.height - viewport.height;
    }

    function updateSelectionTool(elapsed) {
        if (selectTool.visible) {
            selectTool.move(
                selectToolX - boardOffsetX,
                selectToolY - boardOffsetY);
            selectTool.update(elapsed);
        }
    }

    function updateUnits(elapsed) {
    }

    function update(elapsed) {
        updatePanningVelocity(elapsed);
        ensureOffsetIsValid();
        updateSelectionTool(elapsed);
        updateUnits(elapsed);
    }


    function render(elapsed) {
        var buffer = map.canvas;
        var bufferContext = buffer.getContext('2d');
	
        for (var u in movingUnits) {
			
            var move = movingUnits[u];

            bufferContext.strokeStyle = '#f00';
            bufferContext.lineWidth = 2;

            bufferContext.beginPath();
            bufferContext.moveTo(
                100 + move.startPosition.x + (constants.TILE.width / 2),
                100 + move.startPosition.y + (constants.TILE.height / 2));

            for (var i in move.coords) {
                var dest = move.coords[i];

                bufferContext.lineTo(
                    100 + dest.x + (constants.TILE.width / 2),
                    100 + dest.y + (constants.TILE.height / 2));
            }
            bufferContext.stroke();
        }

        viewportContext.clearRect(0, 0, viewport.width, viewport.height);
        viewportContext.drawImage(buffer, boardOffsetX, boardOffsetY, viewport.width, viewport.height, 0, 0, viewport.width, viewport.height);
		

        if (selectTool.visible) {
            selectTool.draw(viewportContext);
        }
    }

    var board = {
        initializeMap: function (mapData) {
            map = new omniscientia.Map(mapData.Width, mapData.Height, items);
            map.create(mapData);

            initializeUi();
        },
        addUnit: function (unit) {
            map.addUnit(unit.Id, unit.OwnerId, unit.Type, unit.Position.X, unit.Position.Y);
            map.draw();
        },
        moveUnit: function (id, tileCoords) {
            var unit = map.getUnitById(id);
            if (!unit) {
				console.log("Invalid unit (" + id + ")");
			}
			else {
				var coords = [];

				var finalPosition = tileCoords;
				map.moveUnit(unit, finalPosition.X, finalPosition.Y);
			}
			

            
/*
            for (var i in tileCoords) {
                coords.push(map.getTileDrawingPosition(tileCoords[i].X, tileCoords[i].Y));
            }

            movingUnits.push({
                unit: unit,
                coords: coords,
                startPosition: { x: unit.x, y: unit.y },
                moving: true
            });
			*/
        },
        removeUnit: function (unit) {
            map.removeUnit(unit);
        },
		moveSelectTool: function(x, y) {
			tile = map.getTileAt(x, y);
			selectToolX = 100 + tile.x;
			selectToolY = 100 + tile.y;
			selectTool.visible = true;
			
			// set up the current position	
			$("#selectedtilex").html(mapPosition.x);
			$("#selectedtiley").html(mapPosition.y);
		},
		removeSelectTool: function() {
			selectTool.visible = false;				
		},
		removeBoard: function() {
			map = new omniscientia.Map(0, 0, null);
			selectedUnit = null;
		}
    }

    $(function () {
        initializeEvents();
    });

    function getBoardCursorPosition(e, offsetX, offsetY) {
        if (!viewport) return;

        var x;
        var y;
        if (e.pageX || e.pageY) {
            x = e.pageX;
            y = e.pageY;
        }
        else {
            x = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
            y = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
        }
        x -= 100 + (viewport.offsetLeft - (offsetX ? offsetX : 0));
        y -= 100 + (viewport.offsetTop - (offsetY ? offsetY : 0));

        return { x: x, y: y };
    }

    window.board = board;

})(jQuery, window, window.constants, window.omniscientia);