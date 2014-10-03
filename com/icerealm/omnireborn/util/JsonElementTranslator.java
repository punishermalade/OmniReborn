package com.icerealm.omnireborn.util;

import com.google.gson.JsonElement;

/**
 * encapsulate an object Json translation. Use an instance with the JsonMasterTranslator
 * to get JsonElement from a Java object.
 * @author neilson
 *
 */
public interface JsonElementTranslator {

	/**
	 * Get a json element. the implementation can decide what to send.
	 * @return
	 */
	public JsonElement getJsonElement();
	
}
