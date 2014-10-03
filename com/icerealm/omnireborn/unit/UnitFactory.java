package com.icerealm.omnireborn.unit;

import com.icerealm.omnireborn.unit.UnitService.UnitType;

/**
 * a simple unit factory to abstract the unit creation
 * @author neilson
 *
 */
public class UnitFactory {
	
	/**
	 * create a new instace of UnitViewModel and return it
	 * @param unitType the type of unit
	 * @return the new unit instance. If the unitType param is not valid, a default unit will be created
	 */
	public UnitViewModel getNewUnit(UnitType unitType) {
		
		UnitViewModel unit = new UnitViewModel();
		
		if (unitType == UnitType.CIVILIAN) {
			unit.setMovement(1);
			unit.setMovementLeft(1);
			unit.setType(0);
		}
		else if (unitType == UnitType.SCOUT) {
			unit.setMovement(1);
			unit.setMovementLeft(1);
			unit.setType(1);
		}
		else if (unitType == UnitType.TANK) {
			unit.setMovement(4);
			unit.setMovementLeft(4);
			unit.setType(5);
		}
		else if (unitType == UnitType.HQ) {
			unit.setMovement(0);
			unit.setMovementLeft(0);
			unit.setType(6);
		}
		else {
			unit.setMovement(1);
			unit.setMovementLeft(1);
			unit.setType(0);
		}
		
		return unit;
	}
	
}
