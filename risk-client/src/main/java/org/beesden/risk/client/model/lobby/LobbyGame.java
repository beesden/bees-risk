package org.beesden.risk.client.model.lobby;

import lombok.Data;

import org.beesden.risk.game.model.GameData;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LobbyGame {

	private String id;
	private String owner;
	private GameData.GameState state;
	private List<String> players;
	private long created;

	public LobbyGame(GameData gameData) {
		this.id = gameData.getName();
		this.state = gameData.getState();

		this.players = gameData.getPlayers()
			.getPlayerList()
			.stream()
			.map(player -> {
				LobbyPlayer lobbyPlayer = LobbyPlayer.lookup(player.getPlayerId());
				if (lobbyPlayer != null) {
					return lobbyPlayer.getUsername();
				} else {
					return "Unknown Player";
				}
			})
			.collect(Collectors.toList());

		this.created = gameData.getCreated().toInstant(ZoneOffset.UTC).toEpochMilli();

		LobbyPlayer owner = LobbyPlayer.lookup(gameData.getPlayers().getOwner());
		if (owner != null) {
			this.owner = owner.getUsername();
		}
	}
}