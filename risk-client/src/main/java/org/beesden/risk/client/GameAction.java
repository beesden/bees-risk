package org.beesden.risk.client;

public enum GameAction {

	// Global actions
	chatMessage, commandError,

	// Lobby actions
	gameLobby, gameSetup,

	// Territory actions
	attack, redeploy, updateAll,

	// Unsorted
	playerOut, updateConfig, updatePlayers, useCards, victory, viewCards;
}
