package com.icerealm.omnireborn.controller;

import com.google.gson.JsonObject;

public interface ChainNode {
	
	public JsonObject handleRequest(JsonObject req);
	
}
