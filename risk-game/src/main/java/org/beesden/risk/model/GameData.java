package org.beesden.risk.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Data
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

}
