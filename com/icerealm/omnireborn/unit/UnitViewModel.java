package com.icerealm.omnireborn.unit;

import com.icerealm.omnireborn.util.Point;

public class UnitViewModel {
	
	private int _id = 0;
	private int _userId = 0;
	private int _type = 0;
	private Point _position = null;
	private int _movement = 1;
	private int _movementLeft = 1;
	
	public int getId() {
		return _id;
	}
	public void setId(int _id) {
		this._id = _id;
	}
	public int getUserId() {
		return _userId;
	}
	public void setUserId(int _userId) {
		this._userId = _userId;
	}
	public int getType() {
		return _type;
	}
	public void setType(int _type) {
		this._type = _type;
	}
	public Point getPosition() {
		return _position;
	}
	public void setPosition(Point _position) {
		this._position = _position;
	}
	public int getMovement() {
		return _movement;
	}
	public void setMovement(int _movement) {
		this._movement = _movement;
	}
	public int getMovementLeft() {
		return _movementLeft;
	}
	public void setMovementLeft(int _movementLeft) {
		this._movementLeft = _movementLeft;
	}
	
	@Override
	public String toString() {
		return "Unit " + _id;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o != null) && 
			   (o instanceof UnitViewModel) &&
			   ((UnitViewModel)o).getId() == _id;
	}
}
