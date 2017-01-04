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
		JsonObjectBuilder continentList = Json.createObjectBuilder();
		continents.entrySet().forEach(c -> continentList.add(c.getKey(), c.getValue().toJson()));

		JsonObjectBuilder territoryList = Json.createObjectBuilder();
		territories.entrySet().forEach(t -> territoryList.add(t.getKey(), t.getValue().toJson()));

		return Json.createObjectBuilder()
				.add("continents", continentList)
				.add("size", JsonUtils.toArray(size))
				.add("territories", territoryList)
				.build();
	}
}
