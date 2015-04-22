package org.beesden.risk.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.beesden.risk.model.Continent;
import org.beesden.risk.model.GameMap;
import org.beesden.risk.model.Territory;

public class MapUtils {

	public static GameMap generateMap(String mapId) {
		InputStream input = MapUtils.class.getResourceAsStream("/maps/" + mapId + ".json");
		if (input == null) {
			System.out.println("Unable to locate requested map object");
			return null;
		}
		JsonObject mapJson = JsonUtils.toObject(input);
		// Add the continents into the map
		JsonObject continents = mapJson.getJsonObject("continents");
		HashMap<String, Continent> continentList = new HashMap<>();
		for (String c : continents.keySet()) {
			Continent continent = new Continent();
			JsonObject jsonContinent = continents.getJsonObject(c);
			continent.setId(c);
			continent.setBonus(jsonContinent.getInt("bonus"));
			continent.setName(jsonContinent.getString("name"));
			continent.setColor(jsonContinent.getString("color"));
			continentList.put(c, continent);
		}
		// Add the territories into the map
		JsonObject territories = mapJson.getJsonObject("territories");
		HashMap<String, Territory> territoryList = new HashMap<>();
		ArrayList<String> unclaimed = new ArrayList<>();
		for (String t : territories.keySet()) {
			Territory territory = new Territory();
			JsonObject jsonTerritory = territories.getJsonObject(t);
			territory.setId(t);
			territory.setCardValue(jsonTerritory.getInt("cardValue"));
			territory.setName(jsonTerritory.getString("name"));
			territory.setCenter(JsonUtils.fromArray(jsonTerritory.getJsonArray("center")));
			territory.setPath(jsonTerritory.getString("path"));
			territoryList.put(t, territory);
			unclaimed.add(t);
		}
		// Setup the continents and neighbours once all territories have been build
		for (String t : territories.keySet()) {
			Territory territory = territoryList.get(t);
			JsonObject jsonTerritory = territories.getJsonObject(t);
			// Territory continent links
			Continent continent = continentList.get(jsonTerritory.getString("continentId"));
			continent.getTerritories().put(t, territory);
			territory.setContinent(continent);
			// Territory neighbours
			for (JsonValue n : jsonTerritory.getJsonArray("neighbours")) {
				Territory neighbour = territoryList.get(n.toString());
				territory.getNeighbours().put(n.toString(), neighbour);
			}
		}
		// Contruct and return the game map object
		GameMap gameMap = new GameMap();
		gameMap.setContinents(continentList);
		gameMap.setTerritories(territoryList);
		gameMap.setSize(JsonUtils.fromArray(mapJson.getJsonArray("size")));
		gameMap.setUnclaimedTerritories(unclaimed);
		return gameMap;
	}

}
