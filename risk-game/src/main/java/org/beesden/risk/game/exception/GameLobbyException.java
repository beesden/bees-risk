package org.beesden.risk.game.exception;

public class GameLobbyException extends RuntimeException {

	public int playerId;
	public String gameId;

	public GameLobbyException(String message, int playerId, String gameId) {
		super(message);
		this.playerId = playerId;
		this.gameId = gameId;
	}

}