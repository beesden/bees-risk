package org.beesden.risk.model;

import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Continent {

	private Integer bonus;
	private String color;
	private String id;
	private String name;
	private HashMap<String, Territory> territories = new HashMap<>();

	public Integer getBonus() {
		return bonus;
	}

	public String getColor() {
		return color;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public HashMap<String, Territory> getTerritories() {
		return territories;
	}

	public void setBonus(Integer bonus) {
		this.bonus = bonus;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTerritories(HashMap<String, Territory> territories) {
		this.territories = territories;
	}

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
