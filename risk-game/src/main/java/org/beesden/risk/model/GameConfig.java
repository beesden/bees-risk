package org.beesden.risk.model;

import lombok.Data;

@Data
public class GameConfig {

	private static final String DEFAULT_MAP = "risk";

	private boolean autoAssignTerritories;
	private boolean autoPlaceBatallions;
	private String gameMap = DEFAULT_MAP;
	private int maxPlayers = 6;
	private int minPlayers= 2;
	private boolean ascendingCardValues;

}