package org.beesden.risk.client.model.game;

import lombok.Data;

import org.beesden.risk.client.model.lobby.LobbyPlayer;
import org.beesden.risk.game.model.GameMap;
import org.beesden.risk.game.model.Player;

import java.util.Map;
import java.util.stream.Collectors;

@Data
public class PlayerSummary {

	private int id;
	private String name = "Neutral Player";
	private String colour;
	private Map<String, Integer> territories;
	private int cardCount;
	private boolean active;
	private int battalions;

	PlayerSummary(Player player) {
		id = player.getPlayerId();
		if (player.getPlayerId() != -1) {
			name = LobbyPlayer.lookup(player.getPlayerId()).getUsername();
		}

		active = player.isNeutral() || player.isSpectating();
		battalions = player.getStrength();

		// cardCount
		territories = player.getTerritories()
			.values()
			.stream()
			.collect(Collectors.toMap(GameMap.Territory::getId, GameMap.Territory::getBattalions));
	}

}