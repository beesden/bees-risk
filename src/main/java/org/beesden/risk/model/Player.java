package org.beesden.risk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.beesden.risk.utils.JsonUtils;

public class Player {

	private Integer battalions;
	private Boolean isNeutral;
	private String playerId;
	private Integer reinforcements;
	private ArrayList<Territory> territories;
	private Map<String, Integer> riskCards;
	private String colour;

	public Player(String username, String playerColour) {
		battalions = 0;
		colour = playerColour;
		isNeutral = false;
		playerId = username;
		reinforcements = 0;
		territories = new ArrayList<>();
		riskCards = new HashMap<String, Integer>();
	}

	public Integer getBattalions() {
		return battalions;
	}

	// Setters and Getters

	public Boolean getIsNeutral() {
		return isNeutral;
	}

	public String getPlayerId() {
		return playerId;
	}

	public Integer getReinforcements() {
		return reinforcements;
	}

	public Map<String, Integer> getRiskCards() {
		return riskCards;
	}

	public void setBattalions(Integer battalions) {
		this.battalions = battalions;
	}

	public void setIsNeutral(Boolean isNeutral) {
		this.isNeutral = isNeutral;
	}

	public void setName(String playerId) {
		this.playerId = playerId;
	}

	public void setReinforcements(Integer reinforcements) {
		this.reinforcements = reinforcements;
	}

	public void setRiskCards(Map<String, Integer> riskCards) {
		this.riskCards = riskCards;
	}

	public JsonObject toJson() {
		JsonObjectBuilder jsonObject = Json.createObjectBuilder();
		jsonObject.add("battalions", battalions);
		jsonObject.add("colour", colour);
		jsonObject.add("isNeutral", isNeutral);
		jsonObject.add("playerId", playerId);
		jsonObject.add("reinforcements", reinforcements);
		jsonObject.add("territories", territories.size());
		jsonObject.add("riskCards", JsonUtils.toArray(riskCards.keySet()));
		return jsonObject.build();
	}

	
	public ArrayList<Territory> getTerritories() {
		return territories;
	}

	
	public void setTerritories(ArrayList<Territory> territories) {
		this.territories = territories;
	}

	
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}
}
