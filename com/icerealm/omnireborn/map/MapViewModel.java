package com.icerealm.omnireborn.map;

import java.util.Map;
import com.icerealm.omnireborn.util.Point;

/**
 * Represents the map that can be used in the client code
 * @author neilson
 *
 */
public class MapViewModel {

	private int width = 0;
	private int height = 0;
	private Map<Point, TileViewModel> tiles = null;
	
	public Map<Point, TileViewModel> getTiles() {
		return tiles;
	}
	public void setTiles(Map<Point, TileViewModel> tiles) {
		this.tiles = tiles;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
