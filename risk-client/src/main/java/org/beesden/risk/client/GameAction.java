package org.beesden.risk.client;

/**
 * Commands sent from the Server to the UI
 */
public enum GameAction {

	// Global actions
	chatMessage, commandError,

	// Lobby actions
	gameLobby, gameSetup, startGame,

	// Turn actions
	startTurn, deploy, attack, redeploy,

	// Card actions
	viewCards, playCards, getCards,

	// Player actions
	playersUpdate, playerLeave, playerWin,

	// other
	updateAll, playerOut, victory, updateConfig, updatePlayers;

}
