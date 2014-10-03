package com.icerealm.omnireborn.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icerealm.omnireborn.util.JsonElementTranslator;

public class PlayerListTranslator implements JsonElementTranslator{

	private String[] _list = null;
	
	public PlayerListTranslator(String[] list) {
		_list = list;
	}
	
	@Override
	public JsonElement getJsonElement() {
		// building the array to be sent to the client
		JsonArray array = new JsonArray();
		for (String s : _list) {
			JsonObject object = new JsonObject();
			object.addProperty("name", s);
			array.add(object);
		}
		
		// build the output result and return it.
		JsonObject outputResult = new JsonObject();
		outputResult.add("distance", array);
		return outputResult;
	}

}
