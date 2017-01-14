package org.beesden.risk.model;

import lombok.Data;
import org.beesden.risk.utils.JsonUtils;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Player {

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
		playerId = username;
	}

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
