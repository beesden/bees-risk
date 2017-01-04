package org.beesden.risk.action;

import javax.json.Json;
import javax.json.JsonObject;

import org.beesden.risk.Server;
import org.beesden.risk.model.Configuration;
import org.beesden.risk.model.GameData;
import org.beesden.risk.model.Player;
import org.beesden.risk.model.Territory;
import org.beesden.risk.utils.Utils;

/**
 * Any commands based on direct player to player input
 */
public class TerritoryActions {

	/**
	 * Attack an enemy territory
	 * @param socket Current player session information
	 * @param request {"gameId": "Test's Game"}
	 */
	public static void attack(Player player, GameData gameData, JsonObject request) {
		// Get the territory objects
		String attackerId = request.getString("attacker");
		Territory attacker = gameData.getGameMap().getTerritories().get(attackerId);
		String defenderId = request.getString("defender");
		Territory defender = gameData.getGameMap().getTerritories().get(defenderId);
		// Check if attacker is owned by the current player
		if (attacker == null || !attacker.getOwner().getUsername().equals(player.getUsername())) {
			System.out.println("Attacker does not own the required territory");
		}
		// Check if defender is a viable target
		if (defender == null || defender.getOwner().getUsername().equals(player.getUsername()) || defender.getNeighbours().get(attackerId) == null) {
			System.out.println("Defender is not a viable target");
		}
		// Get the attack action and display as appropriate
		String attackAction = request.getString("attack");
		switch (attackAction) {
			case "start":
				Utils.sendGameMessage(gameData.getGameId(), "attack", Json.createObjectBuilder().add("attacker", attacker.toJson()).add("defender", defender.toJson()).build());
				break;
			case "attack":
				GameActions.attackTerritory(gameData, player, attacker, defender);
				break;
			case "retreat":
				GameActions.updateAll(gameData, false, true, false);
				break;
		}
	}

	/**
	 * Initial territory deployment
	 * @param socket Current player session information
	 * @param request {"gameId": "Test's Game"}
	 */
	public static void deploy(Player player, GameData gameData, JsonObject request) {
		String territoryId = request.getString("territory");
		if (GameActions.deployTerritory(player, territoryId, gameData)) {	
			if (gameData.getGameMap().getUnclaimedTerritories().size() < 1) {
				Configuration config = gameData.getConfig();
				config.setPlayerTurn(null);
				config.setTurnPhase("reinforce");
			}
			GameActions.startTurn(gameData);
		}
	}

	/**
	 * Reinforce a friendly territory
	 * @param socket Current player session information
	 * @param request {"gameId": "Test's Game"}
	 */
	public static void reinforce(Player player, GameData gameData, JsonObject request) {
		String territoryId = request.getString("territory");
		// Player must use risk cards before they reinforce
		if (player.getRiskCards().size() > 4) {
			Utils.sendMessage(Server.playerList.get(player.getUsername()), "viewCards", request);
			return;
		}
		// Otherwise reinforce territory if possible
		if (GameActions.reinforceTerritory(player, territoryId, gameData)) {
			// Take turns reinforcing before game has started
			if (!gameData.getGameStarted()) {
				if (player.getReinforcements() < 1) {
					gameData.setPlayersReady(gameData.getPlayersReady() + 1);
					if (gameData.getPlayersReady() == gameData.getPlayersActive()) {
						gameData.setGameStarted(true);
					}
				}
				GameActions.startTurn(gameData);
				return;
			}
			// Move to attack phase when out of reinforcements
			else if (player.getReinforcements() < 1) {
				Configuration config = gameData.getConfig();
				config.setTurnPhase("attack");		
			}	
			GameActions.updateAll(gameData, true, true, true);
		}
	}

}
