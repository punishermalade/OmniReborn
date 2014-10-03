package com.icerealm.omnireborn.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icerealm.omnireborn.game.GameViewModel;
import com.icerealm.omnireborn.unit.UnitViewModel;
import com.icerealm.omnireborn.user.OmniUser;
import com.icerealm.omnireborn.util.JsonElementTranslator;

public class GameViewModelTranslator implements JsonElementTranslator {

	private GameViewModel _model = null;
	
	public GameViewModelTranslator(GameViewModel m) {
		_model = m;
	}
	
	@Override
	public JsonElement getJsonElement() {
		JsonObject result = new JsonObject();
		
		// building the map
		MapViewModelTranslator map = new MapViewModelTranslator(_model.getCurrentMap());
		JsonObject mapJson = map.getJsonElement().getAsJsonObject();
		result.add("map", mapJson);
		
		// adding the list of players
		JsonObject arrayPlayer = new JsonObject();
		for (OmniUser user : _model.getUsers()) {
			arrayPlayer.addProperty("userId", user.getId());
			arrayPlayer.addProperty("userName", user.getName());
		}
		result.add("players", arrayPlayer);
		
		// adding the list of unit
		JsonArray units = new JsonArray();
		for (UnitViewModel unit : _model.getUnits()) {
			// reusing the unit translator
			UnitTranslator ut = new UnitTranslator(unit);
			units.add(ut.getJsonElement());
		}
		result.add("Units", units);
		
		// send the data to the users
		return result;
	}

}
