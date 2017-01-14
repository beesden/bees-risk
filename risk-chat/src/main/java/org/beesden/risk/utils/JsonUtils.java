package org.beesden.risk.utils;

import java.io.InputStream;
import java.util.Collection;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class JsonUtils {

	public static Integer[] fromArray(JsonArray input) {
		Integer[] array = new Integer[input.size()];
		for (int i = 0; i < input.size(); ++i) {
			array[i] = input.getInt(i);
		}
		return array;
	}

	public static JsonArray toArray(Collection<?> array) {
		JsonArrayBuilder value = Json.createArrayBuilder();
		for (Object i : array) {
			value.add(i.toString());
		}
		return value.build();
	}

	public static JsonArray toArray(Object[] array) {
		JsonArrayBuilder value = Json.createArrayBuilder();
		for (Object i : array) {
			value.add(i.toString());
		}
		return value.build();
	}

	public static JsonObject toObject(InputStream input) {
		JsonReader jsonReader = Json.createReader(input);
		return jsonReader.readObject();
	}
}
