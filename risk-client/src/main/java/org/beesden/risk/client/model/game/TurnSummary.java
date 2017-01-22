package org.beesden.risk.client.model.game;

import lombok.Data;

import org.beesden.risk.game.model.GameData;
import org.beesden.risk.game.model.GameMap;
import org.beesden.risk.game.model.TurnPhase;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TurnSummary {

	private TurnPhase phase;
	private List<PlayerSummary> players;
	private int current;
	private int reinforcements;
	private List<String> interactive;

	public TurnSummary(GameData gameData) {
		phase = gameData.getPhase();
		current = gameData.getPlayers().getCurrentTurn();

		players = gameData.getPlayers().getPlayerList().stream().map(player -> {
			PlayerSummary summary = new PlayerSummary(player);
			if (player.getPlayerId() == gameData.getPlayers().getCurrentTurn()) {
				reinforcements = player.getReinforcements();
			}
			return summary;
		}).collect(Collectors.toList());

		interactive = gameData.getMap()
			.getValidTerritories(current, gameData.getPhase(), null)
			.stream()
			.map(GameMap.Territory::getId)
			.collect(Collectors.toList());
	}

}