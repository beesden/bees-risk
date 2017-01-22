package org.beesden.risk.client.model.game;

import lombok.Data;

import org.beesden.risk.game.model.GameData;
import org.beesden.risk.game.model.GameMap;

import java.util.List;

@Data
public class MapSummary {

	private String id;
	private String owner;
	private GameData.GameState state;
	private List<String> players;
	private long created;

	public MapSummary(GameMap gameMap) {
	//	gameMap.get
	}
}