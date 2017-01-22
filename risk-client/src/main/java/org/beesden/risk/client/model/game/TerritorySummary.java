package org.beesden.risk.client.model.game;

import lombok.Data;

import org.beesden.risk.game.model.GameMap;

@Data
public class TerritorySummary {

	private String name;
	private int battalions;
	private int owner;

	public TerritorySummary(GameMap.Territory territory) {
		name = territory.getName();
		battalions = territory.getBattalions();
		owner = territory.getOwnerId();
	}
}