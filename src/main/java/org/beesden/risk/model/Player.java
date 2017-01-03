package org.beesden.risk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import lombok.Data;
import lombok.Getter;
import org.beesden.risk.utils.JsonUtils;

@Data
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
		riskCards = new HashMap<>();
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
}
