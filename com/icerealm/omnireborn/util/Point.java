package com.icerealm.omnireborn.util;

/**
 * Represent a 2D point
 * @author neilson
 *
 */
public class Point {

	private int x = 0;
	private int y = 0;
	
	public Point() {	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "[" + x + "][" + y + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		return (o != null) &&
			   (o instanceof Point) &&
			   ((Point)o).getX() == x &&
			   ((Point)o).getY() == y;
	}
}
