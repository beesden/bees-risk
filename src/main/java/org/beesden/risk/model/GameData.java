package org.beesden.risk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class GameData {

	private int playersReady;
	private int playersActive;

	private Configuration config = new Configuration();
	private String gameId;
	private GameMap gameMap;
	private Boolean gameReady = false;
	private Boolean gameFinished = false;
	private Boolean gameStarted = false;
	private Boolean cardEarned = false;
	private LinkedHashMap<String, Player> playerList = new LinkedHashMap<>();
	private ArrayList<String> riskCards = new ArrayList<>();
	private String[] colours = new String[]{"#33c", "#3c3", "#c33", "#c3c", "#3cc", "#cc3"};
	private HashMap<String, String> playerColours = new LinkedHashMap<>();

	public GameData(String gameId) {
		this.gameId = gameId;
	}

	// Getters and Setters
	
	public Configuration getConfig() {
		return config;
	}

	public String getGameId() {
		return gameId;
	}

	public GameMap getGameMap() {
		return gameMap;
	}

	public Boolean getGameReady() {
		return gameReady;
	}

	public Boolean getGameStarted() {
		return gameStarted;
	}

	public LinkedHashMap<String, Player> getPlayerList() {
		return playerList;
	}

	public JsonObject JsonPlayerList() {
		JsonObjectBuilder value = Json.createObjectBuilder();
		for (String player : playerList.keySet()) {
			value.add(player, playerList.get(player).toJson());
		}
		return value.build();
	}

	public JsonObject playerIds() {
		JsonObjectBuilder playerIds = Json.createObjectBuilder();
		for (String player : playerList.keySet()) {
			playerIds.add(player, playerList.get(player).getColour());
		}
		return playerIds.build();
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}

	public void setGameReady(Boolean gameReady) {
		this.gameReady = gameReady;
	}

	public void setGameStarted(Boolean gameStarted) {
		this.gameStarted = gameStarted;
	}

	public void setPlayerList(LinkedHashMap<String, Player> playerList) {
		this.playerList = playerList;
	}

	
	public int getPlayersReady() {
		return playersReady;
	}

	
	public void setPlayersReady(int playersReady) {
		this.playersReady = playersReady;
	}

	
	public int getPlayersActive() {
		return playersActive;
	}

	
	public void setPlayersActive(int playersActive) {
		this.playersActive = playersActive;
	}

	public Boolean getCardEarned() {
		return cardEarned;
	}

	public void setCardEarned(Boolean cardEarned) {
		this.cardEarned = cardEarned;
	}

	public ArrayList<String> getRiskCards() {
		return riskCards;
	}

	public void setRiskCards(ArrayList<String> riskCards) {
		this.riskCards = riskCards;
	}

	public HashMap<String, String> getPlayerColours() {
		return playerColours;
	}

	public void setPlayerColours(HashMap<String, String> playerColours) {
		this.playerColours = playerColours;
	}

	public String[] getColours() {
		return colours;
	}

	public void setColours(String[] colours) {
		this.colours = colours;
	}

	public Boolean getGameFinished() {
		return gameFinished;
	}

	public void setGameFinished(Boolean gameFinished) {
		this.gameFinished = gameFinished;
	}
}
