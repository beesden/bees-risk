package org.beesden.risk.client.model.lobby;

import lombok.Data;

import org.beesden.risk.client.model.game.PlayerSummary;
import org.beesden.risk.game.model.GameData;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LobbyGame {

	private String id;
	private String name;
	private int owner;
	private GameData.GameState state;
	private List<PlayerSummary> players;
	private long created;

	public LobbyGame(GameData gameData) {
		this.id = gameData.getId();
		this.name = gameData.getConfig().getName();
		this.state = gameData.getState();

		this.players = gameData.getPlayers()
			.getPlayerList()
			.stream()
			.filter(player -> LobbyPlayer.lookup(player.getPlayerId()) != null)
			.map(PlayerSummary::new)
			.collect(Collectors.toList());

		this.created = gameData.getCreated().toInstant(ZoneOffset.UTC).toEpochMilli();

		LobbyPlayer owner = LobbyPlayer.lookup(gameData.getPlayers().getOwner());
		if (owner != null) {
			this.owner = owner.getPlayerId();
		}
	}
}