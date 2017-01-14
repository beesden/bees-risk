package org.beesden.risk.Exception;

public class GameLobbyException extends RuntimeException {

	public String playerId;
	public String gameId;

	public GameLobbyException(String message, String playerId, String gameId) {
		super(message);
		this.playerId = playerId;
		this.gameId = gameId;
	}

}