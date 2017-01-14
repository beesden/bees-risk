package org.beesden.risk.model;

import lombok.Data;

@Data
public class Configuration {

	private int cardLevel = 0;
	private final int[] cardBonus = { 4, 6, 8, 10, 12, 15 };
	private String leadPlayer;
	private String[] mapList;
	private String playerTurn;
	private final int[] startForces = { 0, 40, 35, 30, 25, 20 };
	private String turnPhase;

}
