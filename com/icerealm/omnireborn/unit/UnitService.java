package com.icerealm.omnireborn.unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.icerealm.omnireborn.user.OmniUser;
import com.icerealm.omnireborn.util.Point;

/**
 * Provide functionality to create and manage the units that the player are
 * using during a game.
 * @author neilson
 *
 */
public class UnitService {
	
	private static final Logger LOGGER = Logger.getLogger("Icerealm");
	
	/**
	 * Different type of unit that can be used in the game
	 */
	public static enum UnitType { CIVILIAN, SCOUT, TANK, HQ };
	
	/**
	 * Default X position
	 */
	public static final int DEFAULT_X_POS = 16;
	
	/**
	 * Default Y position
	 */
	public static final int DEFAULT_Y_POS = 16;
	
	/**
	 * represents the uniqueness of units by having this id counter
	 */
	private static int UNIT_ID = 0;
	
	/**
	 * represent the list of units by unitId
	 */
	private Map<Integer, UnitViewModel> _units = null;
	
	/**
	 * a simple factory to get different type of unit
	 */
	private UnitFactory _factory = null;
		
	/**
	 * default constructor
	 */
	public UnitService() {
		_units = new HashMap<Integer, UnitViewModel>();
		_factory = new UnitFactory();
	}
	
	/**
	 * Check if a unit has the same position values
	 * @param position the position that needs to be checked
	 * @return true if a unit is already there; otherwise false;
	 */
	public boolean isOtherUnitThere(Point position, Set<UnitViewModel> units) {
		
		for (UnitViewModel model : units) {
			if (model.getPosition().equals(position)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return the unit int the given position
	 * @param pos the pôsition that needs to be checked
	 * @param units the list of unit to look at. This is done to prevent checking units in other games
	 * @return the unit if found, otherwise null
	 */
	public UnitViewModel getUnitAtPosition(Point pos, Set<UnitViewModel> units) {

		for (UnitViewModel u : units) {
			if (u.getPosition().equals(pos)) {
				LOGGER.fine("found unit " + u + " at " + pos);
				return u;
			}
		}
		
		LOGGER.fine("no unit at " + pos);
		return null;
	}
	
	/**
	 * Create a default unit at the default position for a specific user
	 * @param owner unit's owner
	 * @return a new unit
	 */
	public UnitViewModel createNewUnitAtDefaultPosition(OmniUser owner) {
		
		Point position = new Point();
		position.setX(DEFAULT_X_POS);
		position.setY(DEFAULT_Y_POS);
		
		return createNewUnitAtPosition(owner, position);
	}
	
	/**
	 * Create a default unit at the position for a specific user
	 * @param owner unit's owner
	 * @param pos the position of the unit
	 * @return a new unit
	 */
	public UnitViewModel createNewUnitAtPosition(OmniUser owner, Point pos) {
		return createNewUnitAtPosition(owner, pos, "Civilian");
	}
	
	/**
	 * Create a new unit based on the type received
	 * @param owner the owner id
	 * @param pos the starting position
	 * @param type the type of unit
	 * @return
	 */
	public UnitViewModel createNewUnitAtPosition(OmniUser owner, Point pos, String type) {
		
		UnitType enumType = UnitType.valueOf(type);
		LOGGER.fine("creating a new unit with enum type: " + enumType.toString());
		
		UnitViewModel unit = _factory.getNewUnit(enumType);
		unit.setUserId(owner.getId());
		unit.setId(UNIT_ID++);
		unit.setPosition(pos);
		_units.put(unit.getId(), unit);
		return unit;
	}
	
	/**
	 * return the unit by supplying the global id
	 * @param id global id for the units
	 * @return an existing unit; if not in the list, return null;
	 */
	public UnitViewModel getUnit(int id) {
		return _units.get(id);
	}
	
	/**
	 * remove a unit from the list
	 * @param id the global id for this unit
	 */
	public void removeUnit(int id) {
		_units.remove(id);
	}
	
	/**
	 * Check if the user is the owner of the unit
	 * @param user the that needs to be validated as the owner
	 * @param unitId the unitId that is checked
	 * @return true if the user is the unit owner; otherwise false
	 */
	public boolean isOwner(OmniUser user, int unitId) {
		UnitViewModel unit = _units.get(unitId);
		return unit != null && unit.getUserId() == user.getId();
	}
	
	public int getCountUnitForPlayer(int id, Set<UnitViewModel> units) {
		int count = 0;
		for (UnitViewModel m : units) {
			if  (m.getUserId() == id) {
				count++;
			}
		}
		return count;
	}
}
