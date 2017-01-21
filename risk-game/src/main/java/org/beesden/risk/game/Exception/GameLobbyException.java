package org.beesden.risk.game.Exception;

public class GameLobbyException extends RuntimeException {

	public Integer playerId;
	public String gameId;

	public GameLobbyException(String message, Integer playerId, String gameId) {
		super(message);
		this.playerId = playerId;
		this.gameId = gameId;
	}

}