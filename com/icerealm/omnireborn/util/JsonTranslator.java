package com.icerealm.omnireborn.util;

import com.google.gson.JsonElement;

/**
 * Use an interface to provide an easy way to get a JSON representation of any object
 * @author neilson
 *
 */
public class JsonTranslator {

	/**
	 * Use the translator to generate a JsonElement and return it
	 * @param translator a concrete implementation of the JsonElementTranslator
	 * @return a JsonElement
	 */
	public JsonElement translate(JsonElementTranslator translator) {
		return translator.getJsonElement();
	}
	
	
}
