package com.icerealm.omnireborn.controller;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonObject;

public class ChainController {
	
	private List<ChainNode> _list = null;

	public ChainController() {
		_list = new LinkedList<ChainNode>();
	}
	
	public void addNode(ChainNode node) {
		_list.add(node);
	}
	
	public JsonObject handleRequest(JsonObject object) {
		return handleRequest(object, 0, new JsonObject());
	}
	
	private JsonObject handleRequest(JsonObject request, int index, JsonObject response) {
		if (index < _list.size()) {
			ChainNode node = _list.get(index);
			return handleRequest(request, index++, node.handleRequest(request));
		}
		else {
			return response;
		}
	}
	
}
