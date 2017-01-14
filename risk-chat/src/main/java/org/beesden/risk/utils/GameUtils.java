package org.beesden.risk.utils;

import javax.websocket.Session;

import org.beesden.risk.model.GameData;
import org.beesden.risk.model.Player;

public class GameUtils {

	/**
	 * Fetch and confirm the current player's turn from the session
	 * 
	 * @param {Session} socket - Current player session information
	 * @param {GameData} gameData - Game data
	 * @param {Boolean} afterStart - Command is only run after game start
	 * @param {Boolean} beforeComplete - Command is only run before game completion
	 * 
	 * @throws Exception 
	 */
	public static Player getCurrentPlayer(Session socket, GameData gameData, Boolean afterStart, Boolean beforeComplete) throws Exception {
		// Do not interact if game has already completed
		if (beforeComplete && gameData.getGameFinished()) {
			throw new Exception("Game has already completed");
		}
		// Get the actual player object
		String username = (String) socket.getUserProperties().get("username");
		Player player = gameData.getPlayerList().get(username);
		if (player == null || player.isNeutral()) {
			throw new Exception("Active player not found");
		}
		// Prevent player interaction when it's not their turn or it's not the reinforcement phase
		if (!gameData.getConfig().getPlayerTurn().equals(username) || !gameData.getConfig().getTurnPhase().equals("reinforce")) {
			throw new Exception("Player commands sent out of sequence");
		}
		return player;
	}
}
