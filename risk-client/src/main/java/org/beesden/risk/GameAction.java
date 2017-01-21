package org.beesden.risk;

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
