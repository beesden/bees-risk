package org.beesden.risk.model;

import lombok.Data;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

@Data
public class Configuration {

	private int cardLevel = 0;
	private final int[] cardBonus = { 4, 6, 8, 10, 12, 15 };
	private String leadPlayer;
	private String[] mapList;
	private String playerTurn;
	private final int[] startForces = { 0, 40, 35, 30, 25, 20 };
	private String turnPhase;

	public JsonObject toJson() {
		JsonObjectBuilder object = Json.createObjectBuilder();
		if (leadPlayer != null) {
			object.add("leadPlayer", leadPlayer);
		}
		if (turnPhase != null) {
			object.add("turnPhase", turnPhase);
		}
		if (playerTurn != null) {
			object.add("playerTurn", playerTurn);
		}
		return object.build();
	}
}
