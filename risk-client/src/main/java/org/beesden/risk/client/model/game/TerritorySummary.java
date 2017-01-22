package org.beesden.risk.client.model.game;

import lombok.Data;

import org.beesden.risk.game.model.GameMap;

@Data
public class TerritorySummary {

	private String id;
	private String continentId;
	private int battalions;
	private int owner;
	private String path;
	private int[] center;

	public TerritorySummary(GameMap.Territory territory) {
		id = territory.getName();
		continentId = territory.getContinent().getId();

		// Player info
		battalions = territory.getBattalions();
		owner = territory.getOwnerId();

		// Map info
		path = territory.getPath();
		center = new int[]{ territory.getCenter().getX(), territory.getCenter().getY() };
	}
}