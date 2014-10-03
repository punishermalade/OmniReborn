package com.icerealm.omnireborn.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.icerealm.omnireborn.unit.UnitViewModel;
import com.icerealm.omnireborn.util.Point;

/**
 * encapsulate all the logic to find the best path for a unit. it uses recursivity in order
 * to optimize the pather finding method.
 * @author neilson
 *
 */
public class PathFinder {
	
	private static final Logger LOGGER = Logger.getLogger("Icerealm");

	/**
	 * Determine if the destination is valid. This method is the public access to the functionnality. clients
	 * call this function to get a path.
	 * @param unit the unit that is moving
	 * @param destination the destination tile
	 * @param map the current map on which to search
	 * @param path the found path, the client needs to provide an instance of this
	 * @return true if the current tile is valid, the search continue; otherwise false; the search for this particular
	 * path is cancelled.
	 */
    public boolean IsDestinationValid(UnitViewModel unit, Point destination, MapViewModel map, List<Point> path) {
        return IsDestinationValidRecursive(unit.getPosition(), destination, map, unit.getMovementLeft(), path);
    }
    
    /**
     * gets the list of the tile around a given central point
     * @param pt the central point
     * @return a Map with each direction and the corresponding point
     */
    public Map<Direction, Point> GetNeighbors(Point pt) {
    	Map<Direction, Point> dict = 	new HashMap<Direction, Point>();
        dict.put(Direction.NORTHEAST, 	new Point(pt.getX() + 1	, pt.getY() - 1));
        dict.put(Direction.NORTHWEST, 	new Point(pt.getX()	 	, pt.getY() - 1));
        dict.put(Direction.WEST, 		new Point(pt.getX() - 1	, pt.getY()));
        dict.put(Direction.SOUTHWEST, 	new Point(pt.getX() - 1 , pt.getY() + 1));
        dict.put(Direction.SOUTHEAST,	new Point(pt.getX() 	, pt.getY() + 1));
        dict.put(Direction.EAST, 		new Point(pt.getX() + 1 , pt.getY()));
        return dict;
    }

    private boolean IsDestinationValidRecursive(Point original, Point destination, MapViewModel map, int moveLeft, List<Point> path) {
    	
    	LOGGER.finer("recursive: o: " + original + " d: " + destination + " moveleft: " + moveLeft);
    	
        // on vérifie si la coordonnée est bonne, sinon la fonction arrete
        // et ne cherche pas les autres possibilités
        TileViewModel tile = map.getTiles().get(original);
        
        if (tile == null) {
        	return false;
        }

        // on ne peux pas traverser certain type de terrain
        if (tile.getType() == 1) { // WATER TYPE
            return false;
        }

        // on ne peut plus aller plus loin et on n'a pas atteint la destination
        if (moveLeft < 0) {
            return false;
        }

        // on vient de trouver la destination avec le nombre de mouvement restant,
        // sinon, on vérifie le nombre de cout restant, si 0, terminé!
        if (original.equals(destination) && moveLeft >= 0) {
        	LOGGER.finer("Found the destination!");
        	
            return true;
        }
        else if (moveLeft == 0) {
            return false;
        }

        // on cherche la case de destination autour du point original
        boolean result = false;
        List<Point> around = GetOrderedNeighborsDirection(original, destination);
        Point currentPoint = null;
        TileViewModel t = null;
        int i = 0;
        while (i < around.size() && !result) {
        	currentPoint = around.get(i);
            t = map.getTiles().get(currentPoint);
            if (t != null && currentPoint.equals((destination))) {
            	LOGGER.finer("find in neightbor: " + currentPoint);
             	result = true;
            }      
            i++;         
        }
        
        // the destination was not in the neighbor tile, need to continue searching
        if (!result) {
        	int j = 0;
        	while (j < around.size() && !result) {
        		Point searchPoint = around.get(j);
        		t = map.getTiles().get(searchPoint);
        		
        		if (t != null) {
        			int moveCost = moveLeft - t.getMovementCost();
                    result = IsDestinationValidRecursive(searchPoint, destination, map, moveCost, path);
        		}
            	
                j++;
            }
        }

        if (result) {
            path.add(currentPoint);
        }

        return result;

    }

    private List<Point> GetOrderedNeighborsDirection(Point pt, Point destination)
    {
       	List<Point> points = new ArrayList<Point>();

        if (pt.getX() > destination.getX()) // direction WEST
        {
            // en premier
        	points.add(new Point(pt.getX() - 1, pt.getY())); // LOOKING WEST

            if (pt.getY() > destination.getY()) // LOOKING NORTH
            {
            	points.add(new Point(pt.getX(), pt.getY() - 1));
            	points.add(new Point(pt.getX() + 1, pt.getY() - 1));
            	points.add(new Point(pt.getX() - 1, pt.getY() + 1));
            	points.add(new Point(pt.getX(), pt.getY() + 1));
            }
            else if (pt.getY() < destination.getY()) // LOOKING SOUTH
            {
            	points.add(new Point(pt.getX() - 1, pt.getY() + 1));
            	points.add(new Point(pt.getX(), pt.getY() + 1));
            	points.add(new Point(pt.getX(), pt.getY() - 1));
            	points.add(new Point(pt.getX() + 1, pt.getY() - 1));
            }
            else { // SAME HORIZAONTAL
            	
            }

            // en dernier
            points.add(new Point(pt.getX() + 1, pt.getY()));
        }
        else // direction EAST
        {
            // en premier
        	points.add(new Point(pt.getX() + 1, pt.getY()));

            if (pt.getY() > destination.getY()) // direction NORTH
            {
            	points.add(new Point(pt.getX() + 1, pt.getY() - 1));
            	points.add(new Point(pt.getX(), pt.getY() - 1));
            	points.add(new Point(pt.getX() - 1, pt.getY() + 1));
            	points.add(new Point(pt.getX(), pt.getY() + 1));
            }
            else // direction SOUTH
            {
            	points.add(new Point(pt.getX() - 1, pt.getY() + 1));
            	points.add(new Point(pt.getX(), pt.getY() + 1));
            	points.add(new Point(pt.getX() + 1, pt.getY() - 1));
            	points.add(new Point(pt.getX(), pt.getY() - 1));
            }


            // en dernier
            points.add(new Point(pt.getX() - 1, pt.getY()));
        }

        return points;

    }
    
    /*
    public List<Point> GetOrderedNeighborsDirection(Point pt, Point destination)
    {
       	List<Point> points = new ArrayList<Point>();

        if (pt.getX() > destination.getX()) // direction WEST
        {
            // en premier
        	points.add(new Point(pt.getX() - 1, pt.getY()));

            if (pt.getY() > destination.getY()) // direction NORTH
            {
            	points.add( new Point(pt.getX(), pt.getY() - 1));
            	points.add(new Point(pt.getX() + 1, pt.getY() - 1));
            	points.add(new Point(pt.getX() - 1, pt.getY() + 1));
            	points.add(new Point(pt.getX(), pt.getY() + 1));
            }
            else // direction SOUTH
            {
            	points.add(new Point(pt.getX() - 1, pt.getY() + 1));
            	points.add(new Point(pt.getX(), pt.getY() + 1));
            	points.add(new Point(pt.getX(), pt.getY() - 1));
            	points.add(new Point(pt.getX() + 1, pt.getY() - 1));
            }

            // en dernier
            points.add(new Point(pt.getX() + 1, pt.getY()));
        }
        else // direction EAST
        {
            // en premier
        	points.add(new Point(pt.getX() + 1, pt.getY()));

            if (pt.getY() > destination.getY()) // direction NORTH
            {
            	points.add(new Point(pt.getX() + 1, pt.getY() - 1));
            	points.add(new Point(pt.getX(), pt.getY() - 1));
            	points.add(new Point(pt.getX() - 1, pt.getY() + 1));
            	points.add(new Point(pt.getX(), pt.getY() + 1));
            }
            else // direction SOUTH
            {
            	points.add(new Point(pt.getX() - 1, pt.getY() + 1));
            	points.add(new Point(pt.getX(), pt.getY() + 1));
            	points.add(new Point(pt.getX() + 1, pt.getY() - 1));
            	points.add(new Point(pt.getX(), pt.getY() - 1));
            }


            // en dernier
            points.add(new Point(pt.getX() - 1, pt.getY()));
        }

        return points;

    }	
    */
    
    private Map<Direction, Point> GetNeighborsDirection(Point pt, Point destination)
    {
        Map<Direction, Point> dict = new HashMap<Direction, Point>();

        if (pt.getX() > destination.getX()) // direction WEST
        {
            // en premier
            dict.put(Direction.WEST, new Point(pt.getX() - 1, pt.getY()));

            if (pt.getY() > destination.getY()) // direction NORTH
            {
                dict.put(Direction.NORTHWEST, new Point(pt.getX(), pt.getY() - 1));
                dict.put(Direction.NORTHEAST, new Point(pt.getX() + 1, pt.getY() - 1));
                dict.put(Direction.SOUTHWEST, new Point(pt.getX() - 1, pt.getY() + 1));
                dict.put(Direction.SOUTHEAST, new Point(pt.getX(), pt.getY() + 1));
            }
            else // direction SOUTH
            {
                dict.put(Direction.SOUTHWEST, new Point(pt.getX() - 1, pt.getY() + 1));
                dict.put(Direction.SOUTHEAST, new Point(pt.getX(), pt.getY() + 1));
                dict.put(Direction.NORTHWEST, new Point(pt.getX(), pt.getY() - 1));
                dict.put(Direction.NORTHEAST, new Point(pt.getX() + 1, pt.getY() - 1));
            }

            // en dernier
            dict.put(Direction.EAST, new Point(pt.getX() + 1, pt.getY()));
        }
        else // direction EAST
        {
            // en premier
            dict.put(Direction.EAST, new Point(pt.getX() + 1, pt.getY()));

            if (pt.getY() > destination.getY()) // direction NORTH
            {
                dict.put(Direction.NORTHEAST, new Point(pt.getX() + 1, pt.getY() - 1));
                dict.put(Direction.NORTHWEST, new Point(pt.getX(), pt.getY() - 1));
                dict.put(Direction.SOUTHWEST, new Point(pt.getX() - 1, pt.getY() + 1));
                dict.put(Direction.SOUTHEAST, new Point(pt.getX(), pt.getY() + 1));
            }
            else // direction SOUTH
            {
                dict.put(Direction.SOUTHWEST, new Point(pt.getX() - 1, pt.getY() + 1));
                dict.put(Direction.SOUTHEAST, new Point(pt.getX(), pt.getY() + 1));
                dict.put(Direction.NORTHEAST, new Point(pt.getX() + 1, pt.getY() - 1));
                dict.put(Direction.NORTHWEST, new Point(pt.getX(), pt.getY() - 1));
            }


            // en dernier
            dict.put(Direction.WEST, new Point(pt.getX() - 1, pt.getY()));
        }

        return dict;

    }	
}