package org.beesden.risk.client.model.game;

import lombok.Data;

import org.beesden.risk.client.model.lobby.LobbyPlayer;
import org.beesden.risk.game.model.GameMap;
import org.beesden.risk.game.model.Player;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class PlayerSummary {

	private String name = "Neutral Player";
	private String colour;
	private List<String> territories;
	private int cardCount;
	private boolean active;
	private boolean current;
	private int reinforcements;

	PlayerSummary(Player player) {
		if (player.getPlayerId() != -1) {
			name = LobbyPlayer.lookup(player.getPlayerId()).getUsername();
		}

		active = player.isNeutral() || player.isSpectating();

		reinforcements = player.getReinforcements();

		// cardCount
		territories = player.getTerritories()
			.values()
			.stream()
			.map(GameMap.Territory::getName)
			.collect(Collectors.toList());
	}

}