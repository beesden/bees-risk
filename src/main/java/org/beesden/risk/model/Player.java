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
public class Player implements ResponseObject {

	private int reinforcements;
	private boolean isNeutral;
	private String playerId;
	private List<Territory> territories = new ArrayList<>();
	private Map<String, Integer> riskCards = new HashMap<>();
	private String colour;

	/**
	 * Default constructor.
	 *
	 * @param username player username
	 * @param colour   player colour
	 */
	public Player(String username, String colour) {
		this.colour = colour;
		this.username = username;
	}

	@Override
	public JsonObject toJson() {
		return Json.createObjectBuilder()
				.add("colour", colour)
				.add("isNeutral", isNeutral)
				.add("playerId", playerId)
				.add("reinforcements", reinforcements)
				.add("territories", territories.size())
				.add("riskCards", JsonUtils.toArray(riskCards.keySet()))
				.build();
	}
}
