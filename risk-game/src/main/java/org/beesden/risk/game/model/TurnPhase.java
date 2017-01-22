package org.beesden.risk.game.model;

public enum TurnPhase {
	/**
	 * Select terriories at start of game
	 */
	INITIAL,

	/**
	 * Deploy battalions at start of game
	 */
	DEPLOY,

	/**
	 * Place additional battalions at start of game
	 */
	REINFORCE,

	/**
	 * Select a controlled territory to attack from
	 */
	ATTACK,

	/**
	 * Redeploy battalions from one controlled territory to another
	 */
	REDEPLOY
}
