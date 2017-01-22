package org.beesden.risk.client.model.game;

import lombok.Data;

import org.beesden.risk.game.model.GameData;

@Data
public class GameSummary {

	private MapSummary map;
	private TurnSummary turn;

	public GameSummary(GameData gameData) {
		map = new MapSummary(gameData.getMap());
		turn = new TurnSummary(gameData);
	}

}