package org.beesden.risk.client.model.game;

import lombok.Data;

import org.beesden.risk.game.model.GameData;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TurnSummary {

	private GameData.TurnPhase phase;
	private List<PlayerSummary> players;

	public TurnSummary(GameData gameData) {
		phase = gameData.getPhase();

		players = gameData.getPlayers().getPlayerList().stream().map(player -> {
			PlayerSummary summary = new PlayerSummary(player);
			if (player.getPlayerId() != -1 && player.getPlayerId() == gameData.getPlayers().getCurrentTurn()) {
				summary.setCurrent(true);
			}
			return summary;
		}).collect(Collectors.toList());
	}

}