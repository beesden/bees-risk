package org.beesden.risk.client.model.game;

import lombok.Data;

import org.beesden.risk.game.model.GameMap;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class MapSummary {

	private String id;
	private int[] size;
	private List<ContinentSummary> continents;
	private List<TerritorySummary> territories;

	public MapSummary(GameMap gameMap) {
		id = gameMap.getName();
		size = new int[]{ gameMap.getSize().getX(), gameMap.getSize().getY() };
		continents = gameMap.getContinents().stream().map(ContinentSummary::new).collect(Collectors.toList());
		territories = gameMap.getTerritories().stream().map(TerritorySummary::new).collect(Collectors.toList());
	}
}