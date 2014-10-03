package com.icerealm.omnireborn.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.icerealm.omnireborn.util.Point;

/**
 * Service that generate and manage maps.
 * @author punisher & Allov
 *
 */
public class MapService {

	/**
	 * the possible tiles for a map, this is mapped with the client values
	 */
	public static enum TileType { PLAIN, WATER, MOUNTAIN, HILL, GOLD, URANIUM, OIL };
	
	/**
	 * Define the default board width, in number of tile
	 */
	public static final int DEFAULT_WIDTH = 8;
	
	/**
	 * Defines the default board height, in number of tile
	 */
	public static final int DEFAULT_HEIGHT = 8;
	
	/**
	 * defines the maximum index for the tile type
	 */
	private static final int TILE_TYPE_INDEX_MAX = 7;
	
	/**
	 * Generate a random map with default width and height.
	 * @return a view model that represents the map and its tiles
	 */
	public MapViewModel generateNewRandomMap() {
		
		// new random seed
		Random rand = new Random(System.currentTimeMillis());
		
		// defaulting the maps width and height
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_HEIGHT;
		
		// creating the view model taht will be passed to the javascript in JSON format
		MapViewModel model = new MapViewModel();
		model.setWidth(width);
		model.setHeight(height);
		
		// generate a single tile for each possible position, from Allov's map service
		Map<Point, TileViewModel> tiles = new HashMap<Point, TileViewModel>();
		int coef = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				
				// creating a point
				Point position = new Point();
				position.setX(x + coef);
				position.setY(y);
				
				// creating the tile with the position above		
				TileViewModel tile = new TileViewModel();
				tile.setMovementCost(1);
				tile.setType(rand.nextInt(TILE_TYPE_INDEX_MAX));
				tile.setPosition(position);
				
				// add this tile to the list
				tiles.put(position, tile);
			}
		
			if (y % 2 != 0) coef--;
		}
		
		// adding the list of tile to the model
		model.setTiles(tiles);
		
		// return the result to the caller
		return model;
	}
}
