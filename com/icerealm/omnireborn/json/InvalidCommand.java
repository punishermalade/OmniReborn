package com.icerealm.omnireborn.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icerealm.omnireborn.util.JsonElementTranslator;

public class InvalidCommand implements JsonElementTranslator{

	private String _action = null;
	private String _error = null;
	
	public InvalidCommand(String action, String error) {
		_action = action;
		_error = error;
	}
	
	@Override
	public JsonElement getJsonElement() {
		JsonObject error = new JsonObject();
		error.addProperty("action", _action);
		error.addProperty("distance", _error);
		return error;
	}

}
