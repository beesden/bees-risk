package org.beesden.risk.client.model.game;

import lombok.Data;

import org.beesden.risk.game.model.GameMap;

@Data
public class ContinentSummary {

	private String id;
	private String name;
	private String colour;

	public ContinentSummary(GameMap.Continent continent) {
		id = continent.getId();
		name = continent.getName();
		colour = continent.getColor();
	}
}