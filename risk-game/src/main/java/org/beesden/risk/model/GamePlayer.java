package org.beesden.risk.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GamePlayer {

	private int reinforcements;
	private boolean isSpectating;
	private String playerId;
	private Map<String, Integer> riskCards = new HashMap<>();
	private String colour;
	private String currentGame;

	/**
	 * Default constructor.
	 *
	 * @param playerId player username
	 */
	public GamePlayer(String playerId) {
		this.playerId = playerId;
	}
}
