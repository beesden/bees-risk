package org.beesden.risk.client;

/**
 * Commands sent from the UI to the Server
 */
public enum GameCommand {

	// Lobby commands
	login, createGame, joinGame, leaveGame, startGame,

	// Game commands
	territorySelect,

	// Card commands
	showCards,

	// Extra commands
	showConfig, surrender

}
