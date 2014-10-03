(function ($, w, icerealm, undefined) {
    if (!window.omniscientia) window.omniscientia = {};
    var omniscientia = window.omniscientia;

    omniscientia.Map = function (width, height, items) {
        this.canvas = document.createElement('canvas');
		this.canvas.width = (width * constants.TILE.width) + (constants.TILE.width * 0.5) + 200;
        this.canvas.height = height * constants.TILE.offsetY + 200;
        this.context = this.canvas.getContext('2d');
        this.items = items;
        this.tiles = [];
        this.units = [];
        this.width = width;
        this.height = height;

        this.players = [];
        this.currentColor = 0;

        //    this.selectTool = new SelectTool(0, 0, SelectTool, items);

        //    this.selectTile = function (position) {
        //        if (this.selectedTile) {
        //            var neighbours = this.getNeighbours(this.geTilePosition({ x: this.selectedTile.x, y: this.selectedTile.y }));
        //            for (neighbour in neighbours) {
        //                if (neighbours[neighbour]) {
        //                    var neighbourPosition = this.geTilePosition({ x: neighbours[neighbour].x, y: neighbours[neighbour].y });
        //                    this.tiles[neighbourPosition.y][neighbourPosition.x].draw();
        //                }
        //            }

        //            this.selectedTile.selected = false;
        //            this.selectedTile.draw();
        //        }

        //        this.selectedTile = this.tiles[position.y][position.x];

        //        if (this.selectedTile) {
        //            this.selectedTile.selected = true;
        //            this.selectedTile.draw();

        //            this.selectTool.x = this.selectedTile.x;
        //            this.selectTool.y = this.selectedTile.y;
        //            this.selectTool.draw();

        //            var neighbours = this.getNeighbours(position);
        //            for (neighbour in neighbours) {
        //                if (neighbours[neighbour]) {
        //                    var n = new Tile(neighbours[neighbour].x, neighbours[neighbour].y, Fog, context, this.items);
        //                    n.draw();
        //                }
        //            }
        //        }

        //        this.drawUnits();
        //    }

        this.getTile = function (type, x, y, context) {
            if (type == constants.TILE_TYPES.Plain) {
                return new omniscientia.Tile(x, y, omniscientia.definitions.Plain, this.items);
            }

            if (type == constants.TILE_TYPES.Water) {
                return new omniscientia.Tile(x, y, omniscientia.definitions.Water, this.items);
            }

            if (type == constants.TILE_TYPES.Mountain) {
                return new omniscientia.Tile(x, y, omniscientia.definitions.Mountain, this.items);
            }

            if (type == constants.TILE_TYPES.Hill) {
                return new omniscientia.Tile(x, y, omniscientia.definitions.Hill, this.items);
            }

            if (type == constants.TILE_TYPES.Gold) {
                return new omniscientia.Tile(x, y, omniscientia.definitions.Gold, this.items);
            }

            if (type == constants.TILE_TYPES.Uranium) {
                return new omniscientia.Tile(x, y, omniscientia.definitions.Uranium, this.items);
            }

            if (type == constants.TILE_TYPES.Oil) {
                return new omniscientia.Tile(x, y, omniscientia.definitions.Oil, this.items);
            }

            return new omniscientia.Tile(x, y, omniscientia.definitions.Plain, this.items);
        }

        this.getUnit = function (type, x, y, color, context) {
		
            if (type == constants.UNIT_TYPES.Civilian) {
                return new omniscientia.Unit(x, y, omniscientia.definitions.Civilian, color, this.items);
            }

            if (type == constants.UNIT_TYPES.Scout) {
                return new omniscientia.Unit(x, y, omniscientia.definitions.Scout, color, this.items);
            }
			
			if (type == constants.UNIT_TYPES.Tank) {
                return new omniscientia.Unit(x, y, omniscientia.definitions.Tank, color, this.items);
            }
			
			if (type == constants.UNIT_TYPES.HQ) {
                return new omniscientia.Unit(x, y, omniscientia.definitions.HQ, color, this.items);
            }

            return new omniscientia.Unit(x, y, omniscientia.definitions.Civilian, color, this.items);
        }

        this.geTilePosition = function (screenPosition) {
            var y = Math.floor(screenPosition.y / constants.TILE.offsetY);
            var x = Math.floor((screenPosition.x / (constants.TILE.width - 1)) - (y * 0.5));

            return { x: x, y: y };
        }

        this.getUnitAt = function (x, y) {
            if (this.units[y]) {
                return this.units[y][x];
            }
        }

        this.getUnitById = function (id) {
            for (var y in this.units) {
                for (var x in this.units[y]) {
                    var unit = this.units[y][x];
                    if (unit.id == id) {
                        return unit;
                    }
                }
            }
        }

        this.getTileAt = function (x, y) {
            if (this.tiles[y]) {
                return this.tiles[y][x];
            }
        }

        this.addUnit = function (id, owner, type, x, y) {
            var drawingPosition = this.getTileDrawingPosition(x, y);
			
			if (!this.players[owner]) {
                colorIndex = this.currentColor;
                this.players[owner] = { color: colorIndex };
                this.currentColor++;
            } else {
                colorIndex = this.players[owner].color;
            }

            var unit = this.getUnit(type, drawingPosition.x, drawingPosition.y, constants.PLAYER_COLORS[colorIndex], this.context);
            unit.id = id;
            unit.owner = owner;
            unit.position = { x: x, y: y };
            this.addUnitAt(unit, x, y);
        }

        this.addUnitAt = function (unit, x, y) {
            this.removeUnitAt(unit, unit.position.x, unit.position.y);
            if (!this.units[y]) this.units[y] = [];
            this.units[y][x] = unit;
        }

        this.addUnits = function (units) {
            for (var i in units) {
                var unit = units[i];
                this.addUnit(unit.Id, unit.OwnerId, unit.Type, unit.Position.X, unit.Position.Y);
            }
        }

        this.removeUnit = function (unit) {
            this.removeUnitAt(unit.defenderwon, unit.posX, unit.posY);
        }

        this.removeUnitAt = function (unit, x, y) {
		
            if (this.units && y in this.units && x in this.units[y]) {
                delete this.units[y][x];

                if (this.units[y].length == 0) {
                    delete this.units[y];
                }
            }
            this.draw();
        }

        this.getNeighbours = function (position) {
            return {
                NorthWest: this.tiles[position.y - 1] ? this.tiles[position.y - 1][position.x] : undefined,
                NorthEast: this.tiles[position.y - 1] ? this.tiles[position.y - 1][position.x + 1] : undefined,
                West: this.tiles[position.y] ? this.tiles[position.y][position.x - 1] : undefined,
                East: this.tiles[position.y] ? this.tiles[position.y][position.x + 1] : undefined,
                SouthWest: this.tiles[position.y + 1] ? this.tiles[position.y + 1][position.x - 1] : undefined,
                SouthEast: this.tiles[position.y + 1] ? this.tiles[position.y + 1][position.x] : undefined
            };
        }

        this.getTileDrawingPosition = function (x, y) {
            var px = (constants.TILE.width - 1) * (y * 0.5 + x);
            var py = (constants.TILE.offsetY * y);

            return { x: px, y: py };
        }

        this.moveUnit = function (unit, x, y) {
            this.addUnitAt(unit, x, y);

            unit.position.x = x;
            unit.position.y = y;

            var dp = this.getTileDrawingPosition(x, y);
            unit.x = dp.x;
            unit.y = dp.y;

            this.draw();
        }
    }

    omniscientia.Map.prototype.create = function (data) {
        var tiles = data.Tiles;
        for (var i in tiles) {
			
            var serverTile = tiles[i];
            var px = (constants.TILE.width - 1) * (serverTile.Position.Y * 0.5 + serverTile.Position.X);
            var py = (constants.TILE.offsetY * serverTile.Position.Y);

            var tile = this.getTile(serverTile.Type, px, py, this.context);
            tile.text = "(" + serverTile.Position.X + "," + serverTile.Position.Y + ")";

            if (!this.tiles[serverTile.Position.Y]) this.tiles[serverTile.Position.Y] = [];
            this.tiles[serverTile.Position.Y][serverTile.Position.X] = tile;
        }

        this.addUnits(data.MyUnits);
        this.addUnits(data.VisibleUnits);
    }

    omniscientia.Map.prototype.draw = function () {
        this.context.fillColor = "#000000";
        this.context.fillRect(0, 0, this.canvas.width, this.canvas.height);

        this.drawTiles();
        this.drawUnits();
    }

    omniscientia.Map.prototype.drawTiles = function () {
        for (var y in this.tiles) {
            for (var x in this.tiles[y]) {
                this.tiles[y][x].draw(this.context);
            }
        }
    }

    omniscientia.Map.prototype.drawUnits = function () {
        for (var y in this.units) {
            for (var x in this.units[y]) {
                this.units[y][x].draw(this.context);
            }
        }
    }
})(jQuery, window, window.icerealm);