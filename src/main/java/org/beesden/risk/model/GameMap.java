package org.beesden.risk.model;

import java.util.ArrayList;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.beesden.risk.utils.JsonUtils;

public class GameMap {

	private HashMap<String, Continent> continents;
	private Integer[] size;
	private HashMap<String, Territory> territories;
	private ArrayList<String> unclaimedTerritories;

	public HashMap<String, Continent> getContinents() {
		return continents;
	}

	public Integer[] getSize() {
		return size;
	}

	public HashMap<String, Territory> getTerritories() {
		return territories;
	}

	public void setContinents(HashMap<String, Continent> continents) {
		this.continents = continents;
	}

	public void setSize(Integer[] size) {
		this.size = size;
	}

	public void setTerritories(HashMap<String, Territory> territories) {
		this.territories = territories;
	}

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

	
	public ArrayList<String> getUnclaimedTerritories() {
		return unclaimedTerritories;
	}

	
	public void setUnclaimedTerritories(ArrayList<String> unclaimedTerritories) {
		this.unclaimedTerritories = unclaimedTerritories;
	}
}
