package org.beesden.risk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import lombok.Data;
import org.beesden.risk.utils.JsonUtils;

@Data
public class GameMap {

	private Map<String, Continent> continents;
	private Integer[] size;
	private Map<String, Territory> territories = new HashMap<>();
	private List<String> unclaimedTerritories;

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
