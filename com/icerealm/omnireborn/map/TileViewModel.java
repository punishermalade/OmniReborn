package com.icerealm.omnireborn.map;

import com.icerealm.omnireborn.util.Point;

/**
 * Represents an individual tile from the map
 * @author neilson
 *
 */
public class TileViewModel {
	
	private int type = 0;
	private Point position = null;
	private int movementCost = 0;
	
	public int getMovementCost() {
		return movementCost;
	}

	public void setMovementCost(int movementCost) {
		this.movementCost = movementCost;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

	
}
