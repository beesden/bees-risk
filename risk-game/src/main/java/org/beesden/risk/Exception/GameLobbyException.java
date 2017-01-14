package org.beesden.risk.Exception;

public class GameLobbyException extends RuntimeException {

	public String playerId;
	public String gameId;
	private String message;

	public GameLobbyException(String message, String playerId, String gameId) {
		this.playerId = playerId;
		this.gameId = gameId;
		this.message = message;
	}

}