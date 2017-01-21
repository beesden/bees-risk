package org.beesden.risk.client.Model;

import lombok.Data;

import org.beesden.risk.game.model.GameData;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LobbyGame {

	private String gameName;
	private GameData.GameState state;
	private List<String> players;
	private long created;

	public LobbyGame(GameData gameData) {
		this.gameName = gameData.getName();
		this.state = gameData.getState();
		this.players = gameData.getPlayers()
				.getPlayerList()
				.stream()
				.map(player -> LobbyPlayer.lookup(player.getPlayerId()).getUsername())
				.collect(Collectors.toList());

		this.created = gameData.getCreated().toInstant(ZoneOffset.UTC).toEpochMilli();
	}
}