package org.beesden.risk.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Player {
	private String playerId;
	private String colour;
	private boolean isNeutral;
	private boolean isSpectating;
	private int reinforcements;
	private Map<String, GameMap.Territory> territories = new HashMap<>();

	public Player(String playerId) {
		this.playerId = playerId;
	}

	/**
	 * List how many battalions the player has on the board
	 *
	 * @return number of battalions
	 */
	public int getStrength() {
		return territories.values().stream().map(GameMap.Territory::getBattalions).mapToInt(Integer::intValue).sum();
	}

	/**
	 * Reinforce a territory
	 *
	 * @param territoryId territory id
	 */
	public void reinforce(String territoryId) {
		if (reinforcements > 0) {
			territories.get(territoryId).setBattalions(territories.get(territoryId).getBattalions() + 1);
			reinforcements--;
		}
	}

	/**
	 * Take a territory
	 *
	 * @param territory territory
	 */
	public void takeTerritory(GameMap.Territory territory) {
		territory.setOwnerId(playerId);
		territories.put(territory.getId(), territory);
	}
}
