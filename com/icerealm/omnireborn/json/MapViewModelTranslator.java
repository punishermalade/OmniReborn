package com.icerealm.omnireborn.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icerealm.omnireborn.map.MapViewModel;
import com.icerealm.omnireborn.map.TileViewModel;
import com.icerealm.omnireborn.util.JsonElementTranslator;
import com.icerealm.omnireborn.util.Point;

public class MapViewModelTranslator implements JsonElementTranslator {

	private MapViewModel _model = null;
	
	public MapViewModelTranslator(MapViewModel model) {
		_model = model;
	}
	
	@Override
	public JsonElement getJsonElement() {
		JsonObject object = new JsonObject();
		object.addProperty("Width", _model.getWidth());
		object.addProperty("Height", _model.getHeight());
		
		// creating the list of tiles in JSON
		JsonArray tiles = new JsonArray();
		for (Point key : _model.getTiles().keySet()) {
			
			TileViewModel modelTile = _model.getTiles().get(key);
			
			// creating the main tile json object
			JsonObject tile = new JsonObject();
			tile.addProperty("MovementCost", modelTile.getMovementCost());
			tile.addProperty("Type", modelTile.getType());
			
			// creating the position
			JsonObject point = new JsonObject();
			point.addProperty("X", modelTile.getPosition().getX());
			point.addProperty("Y", modelTile.getPosition().getY());
			tile.add("Position", point);
			
			// adding this new tile 
			tiles.add(tile);
		}
		
		// setting the list of tile in the output result
		object.add("Tiles", tiles);
		return object;
	}

}
