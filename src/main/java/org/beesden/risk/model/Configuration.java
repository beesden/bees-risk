package org.beesden.risk.model;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Configuration {

	private int cardLevel = 0;
	private final int[] cardBonus = { 4, 6, 8, 10, 12, 15 };
	private String leadPlayer;
	private String[] mapList;
	private String playerTurn;
	private final int[] startForces = { 0, 40, 35, 30, 25, 20 };
	private String turnPhase;

	public int[] getCardBonus() {
		return cardBonus;
	}

	public String getLeadPlayer() {
		return leadPlayer;
	}

	public String[] getMapList() {
		return mapList;
	}

	public String getPlayerTurn() {
		return playerTurn;
	}

	public int[] getStartForces() {
		return startForces;
	}

	public String getTurnPhase() {
		return turnPhase;
	}

	public void setLeadPlayer(String leadPlayer) {
		this.leadPlayer = leadPlayer;
	}

	public void setMapList(String[] mapList) {
		this.mapList = mapList;
	}

	public void setPlayerTurn(String playerTurn) {
		this.playerTurn = playerTurn;
	}

	public void setTurnPhase(String turnPhase) {
		this.turnPhase = turnPhase;
	}

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

	public int getCardLevel() {
		return cardLevel;
	}

	public void setCardLevel(int cardLevel) {
		this.cardLevel = cardLevel;
	}
}
