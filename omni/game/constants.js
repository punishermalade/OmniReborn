(function () {
    var constants = {
        UNIT_TYPES: {
            Civilian: 0,
            Scout: 1,
            Infantry: 2,
            Marines: 3,
            Jeep: 4,
            Tank: 5,
            HQ: 6
        },
        UNIT: {
            width: 35,
            height: 35
        },
        TILE_TYPES: {
            Plain: 0,
            Water: 1,
            Mountain: 2,
            Hill: 3,
            Gold: 4,
            Uranium: 5,
            Oil: 6
        },
        TILE: {
            width: 70,
            height: 70,
            offsetY: 53
        },
        BOARD: {
            margin: { left: 100, right: 100, top: 100, bottom: 100 }
        },
        PLAYER_COLORS: [
            { r: 1, g: 0, b: 0 },
            { r: 1, g: 1, b: 0 },
            { r: 0, g: 1, b: 0 },
            { r: 0, g: 0, b: 1 }
        ]
    };

    window.constants = constants;
})();