package org.beesden.risk.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Player {

	private int reinforcements;
	private boolean isNeutral;
	private String playerId;
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
}
