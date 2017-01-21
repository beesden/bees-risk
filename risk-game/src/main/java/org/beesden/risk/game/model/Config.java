package org.beesden.risk.game.model;

import lombok.Data;

@Data
public class Config {

	private static final String DEFAULT_MAP = "risk";

	private boolean autoAssignTerritories;
	private boolean autoPlaceBattalions;
	private String gameMap = DEFAULT_MAP;
	private int maxPlayers = 6;
	private int minPlayers = 2;
	private boolean ascendingCardValues;

}