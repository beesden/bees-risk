package org.beesden.risk.model;

import lombok.Data;

import java.util.List;

@Data
public class LobbyGame {

	private String gameName;
	private GameData.GameState state;
	private List<String> players;

	public LobbyGame(GameData gameData) {
		this.gameName = gameData.getName();
		this.state = gameData.getState();
		this.players = gameData.getPlayers().listIds();
	}

}