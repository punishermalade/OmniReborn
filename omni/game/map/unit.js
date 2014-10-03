(function ($, w, icerealm, constants, undefined) {
    if (!window.omniscientia) window.omniscientia = {};
    var omniscientia = window.omniscientia;

    omniscientia.Unit = function (x, y, unitInfo, color, items) {
        this.canvas = document.createElement("canvas");
        this.canvas.width = constants.UNIT.width;
        this.canvas.height = constants.UNIT.height;
        this.context = this.canvas.getContext('2d');

        this.items = items;
        this.x = x;
        this.y = y;
        this.unitInfo = unitInfo;
        this.position = { x: 0, y: 0 };

        this.moveSpeed = 0.005;
        this.moving = false;
        this.movingToPosition = { x: 0, y: 0 };
        this.color = color;
    }

    omniscientia.Unit.prototype.update = function (elapsed) {
        if (this.moving) {

        }
    }

    omniscientia.Unit.prototype.draw = function (context) {
        this.context.clearRect(0, 0, constants.UNIT.width, constants.UNIT.height);

        var x = 98 + (this.x + constants.TILE.width / 2 - constants.UNIT.width / 2);
        var y = 98 + (this.y + constants.TILE.height / 2 - constants.UNIT.height / 2);

        this.context.shadowOffsetX = 4;
        this.context.shadowOffsetY = 4;
        this.context.shadowBlur = 5;
        this.context.shadowColor = "black";

        this.context.drawImage(
                this.items,
                this.unitInfo.x,
                this.unitInfo.y,
                this.unitInfo.w,
                this.unitInfo.h,
                0,
                0,
                constants.UNIT.width,
                constants.UNIT.height);
				
        icerealm.canvas.colorize(this.context, 0, 0, constants.UNIT.width, constants.UNIT.height, this.color.r, this.color.g, this.color.b);	
        context.drawImage(this.canvas, x, y);
    }

    if (!omniscientia.definitions) omniscientia.definitions = {};

    omniscientia.definitions.Civilian = { x: 0, y: 280, w: constants.UNIT.width, h: constants.UNIT.height };
    omniscientia.definitions.Scout = { x: constants.UNIT.width, y: 280, w: constants.UNIT.width, h: constants.UNIT.height };
    omniscientia.definitions.Tank = { x: constants.UNIT.width * 2, y: 280, w: constants.UNIT.width, h: constants.UNIT.height };
	omniscientia.definitions.HQ = { x: constants.UNIT.width * 3, y: 280, w: constants.UNIT.width, h: constants.UNIT.height };
})(jQuery, window, window.icerealm, window.constants);