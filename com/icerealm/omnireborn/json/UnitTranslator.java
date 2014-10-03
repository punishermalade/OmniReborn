package com.icerealm.omnireborn.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icerealm.omnireborn.unit.UnitViewModel;
import com.icerealm.omnireborn.util.JsonElementTranslator;

public class UnitTranslator implements JsonElementTranslator {

	private UnitViewModel _model = null;
	
	public UnitTranslator(UnitViewModel model) {
		_model = model;
	}
	
	@Override
	public JsonElement getJsonElement() {
		JsonObject unit = new JsonObject();
		unit.addProperty("Id", _model.getId());
		unit.addProperty("Type", _model.getType());
		unit.addProperty("OwnerId", _model.getUserId());
		
		JsonObject position = new JsonObject();
		position.addProperty("X", _model.getPosition().getX());
		position.addProperty("Y", _model.getPosition().getY());
		unit.add("Position", position);
		
		return unit;
	}
}
