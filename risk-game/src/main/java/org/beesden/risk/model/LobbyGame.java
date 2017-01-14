package org.beesden.risk.model;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class LobbyGame {

	private String gameName;
	private GameData.GameState state;
	private List<String> players;

	public LobbyGame(GameData gameData) {
		this.gameName = gameData.getName();
		this.state = gameData.getState();
		this.players = gameData.getPlayers().stream().map(GamePlayer::getPlayerId).collect(Collectors.toList());
	}

}