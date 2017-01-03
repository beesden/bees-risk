package org.beesden.risk.model;

import java.util.ArrayList;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import lombok.Data;
import org.beesden.risk.utils.JsonUtils;

@Data
public class GameMap {

	private HashMap<String, Continent> continents;
	private Integer[] size;
	private HashMap<String, Territory> territories;
	private ArrayList<String> unclaimedTerritories;

	public JsonObject toJson() {
		JsonObjectBuilder object = Json.createObjectBuilder();
		JsonObjectBuilder continentList = Json.createObjectBuilder();
		for (String c : continents.keySet()) {
			continentList.add(c, continents.get(c).toJson());
		}
		JsonObjectBuilder territoryList = Json.createObjectBuilder();
		for (String t : territories.keySet()) {
			territoryList.add(t, territories.get(t).toJson());
		}
		object.add("continents", continentList);
		object.add("size", JsonUtils.toArray(size));
		object.add("territories", territoryList);
		return object.build();
	}
}
