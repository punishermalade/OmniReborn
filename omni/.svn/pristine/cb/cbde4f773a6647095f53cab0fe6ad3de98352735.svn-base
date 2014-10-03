(function ($, w, icerealm, constants, undefined) {
    if (!window.omniscientia) window.omniscientia = {};
    var omniscientia = window.omniscientia;

    omniscientia.Tile = function (x, y, tileInfo, items) {
        this.canvas = document.createElement("canvas");
        this.canvas.width = constants.TILE.width;
        this.canvas.height = constants.TILE.height;
        this.context = this.canvas.getContext("2d");

        this.items = items;
        this.x = x;
        this.y = y;
        this.tileInfo = tileInfo;
        this.type = tileInfo.type;
    }

    omniscientia.Tile.prototype.draw = function (context) {
        this.context.clearRect(0, 0, constants.TILE.width, constants.TILE.height);

        var x = constants.BOARD.margin.left + this.x;
        var y = constants.BOARD.margin.right + this.y;

        this.context.drawImage(
            this.items,
            this.tileInfo.x,
            this.tileInfo.y,
            this.tileInfo.w,
            this.tileInfo.h,
            0,
            0,
            constants.TILE.width,
            constants.TILE.height);

        context.drawImage(this.canvas, x, y);

        //    if (this.text != undefined) {
        //        var x = 100 + this.x + TILE.width / 2;
        //        var y = 100 + this.y + TILE.height / 2

        //        this.context.textAlign = "center";
        //        this.context.textBaseline = "middle";
        //        this.context.font = "bold 10pt Verdana";
        //        this.context.fillStyle = "black";
        //        this.context.fillText(this.text, x + 2, y + 2);

        //        this.context.fillStyle = "white";
        //        this.context.fillText(this.text, x, y);

        //    }
    }

    if (!omniscientia.definitions) omniscientia.definitions = {};

    omniscientia.definitions.Plain = { x: 0, y: 0, w: constants.TILE.width, h: constants.TILE.height, type: "Plain" };
    omniscientia.definitions.Water = { x: constants.TILE.width, y: 0, w: constants.TILE.width, h: constants.TILE.height, type: "Water" };
    omniscientia.definitions.Mountain = { x: constants.TILE.width * 2, y: 0, w: constants.TILE.width, h: constants.TILE.height, type: "Mountain" };
    omniscientia.definitions.Hill = { x: constants.TILE.width * 3, y: 0, w: constants.TILE.width, h: constants.TILE.height, type: "Hill" };
    omniscientia.definitions.Gold = { x: 0, y: 0, w: constants.TILE.width, h: constants.TILE.height, type: "Gold" };
    omniscientia.definitions.Uranium = { x: 0, y: 0, w: constants.TILE.width, h: constants.TILE.height, type: "Uranium" };
    omniscientia.definitions.Oil = { x: 0, y: 0, w: constants.TILE.width, h: constants.TILE.height, type: "Oil" };

})(jQuery, window, window.icerealm, window.constants);