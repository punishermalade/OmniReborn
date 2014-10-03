(function ($, w, icerealm, constants, undefined) {
    if (!window.omniscientia) window.omniscientia = {};
    var omniscientia = window.omniscientia;

    omniscientia.SelectTool = function (x, y, items) {
        this.canvas = document.createElement("canvas");
        this.canvas.width = constants.TILE.width;
        this.canvas.height = constants.TILE.height;
        this.context = this.canvas.getContext("2d");

        this.animeCanvas = document.createElement("canvas");
        this.animeCanvas.width = constants.TILE.width;
        this.animeCanvas.height = constants.TILE.height;
        this.animeContext = this.animeCanvas.getContext("2d");

        this.items = items;
        this.x = x;
        this.y = y;

        this.rotation = 0;
        this.rotationSpeed = 0.005;
        this.visible = false;

        this.move = function (x, y) {
            this.x = x;
            this.y = y;
        }

        this.drawSelectTool = function (context) {
            this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
            this.context.drawImage(
                this.items,
                SelectToolInfo.x,
                SelectToolInfo.y,
                SelectToolInfo.w,
                SelectToolInfo.h,
                0,
                0,
                constants.TILE.width,
                constants.TILE.height);
            icerealm.canvas.colorize(this.context, 0, 0, constants.TILE.width, constants.TILE.height, 0.8, 0.1, 0.1);

            context.drawImage(this.canvas, this.x, this.y);
        }
    }

    omniscientia.SelectTool.prototype.update = function (elasped) {
        this.rotation = -(this.rotationSpeed * elasped);
    }

    omniscientia.SelectTool.prototype.draw = function (context) {
        this.drawSelectTool(context);

        this.animeContext.clearRect(0, 0, this.animeCanvas.width, this.animeCanvas.height);

        this.animeContext.translate(this.animeCanvas.width / 2, this.animeCanvas.height / 2);
        this.animeContext.rotate(this.rotation);
        this.animeContext.translate(-(this.animeCanvas.width / 2), -(this.animeCanvas.height / 2));
        this.animeContext.drawImage(
            this.items,
            SelectAnimeToolInfo.x,
            SelectAnimeToolInfo.y,
            SelectAnimeToolInfo.w,
            SelectAnimeToolInfo.h,
            0,
            0,
            constants.TILE.width,
            constants.TILE.height);

        context.drawImage(this.animeCanvas, this.x, this.y);
    }

    var SelectToolInfo = { x: constants.TILE.width, y: constants.TILE.height * 3, w: constants.TILE.width, h: constants.TILE.height };
    var SelectAnimeToolInfo = { x: 0, y: constants.TILE.height * 3, w: constants.TILE.width, h: constants.TILE.height };
    var Fog = { x: 0, y: constants.TILE.height * 3, w: constants.TILE.width, h: constants.TILE.height };
})(jQuery, window, window.icerealm, window.constants);