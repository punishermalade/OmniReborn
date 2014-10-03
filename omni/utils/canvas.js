(function ($, w, undefined) {

    var canvas = {
        colorize: function (context, x, y, width, height, r, g, b) {
            this.imgd = context.getImageData(x, y, width, height);
            var pix = this.imgd.data;

            for (var i = 0, n = pix.length; i < n; i += 4) {
                var red = pix[i];
                var green = pix[i + 1];
                var blue = pix[i + 2];
                var alpha = pix[i + 3];

                var avg = (red + green + blue) / 3;

                pix[i] = avg * r;
                pix[i + 1] = avg * g;
                pix[i + 2] = avg * b;
                pix[i + 3] = alpha;
            }

            context.putImageData(this.imgd, x, y);
        }
    };

    if (!window.icerealm) window.icerealm = {};
    window.icerealm.canvas = canvas;
})(jQuery, window);