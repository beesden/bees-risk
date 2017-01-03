package org.beesden.risk.model;

import lombok.Data;

import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

@Data
public class Continent {

	private Integer bonus;
	private String color;
	private String id;
	private String name;
	private HashMap<String, Territory> territories = new HashMap<>();

	public JsonObject toJson() {
		JsonObjectBuilder object = Json.createObjectBuilder();
		object.add("color", color);
		object.add("name", name);
		JsonArrayBuilder territoryList = Json.createArrayBuilder();
		for (String t : territories.keySet()) {
			territoryList.add(t);
		}
		object.add("territories", territoryList);
		return object.build();
	}

}
