package org.beesden.risk.action;

/**
 * Any commands based on direct player to player input
 */
public class TerritoryActions {

//	/**
//	 * Attack an enemy territory
//	 * @param socket Current player session information
//	 * @param request {"gameId": "Test's Game"}
//	 */
//	public static void attack(GamePlayers player, GameData gameData, JsonObject request) {
//		// Get the territory objects
//		String attackerId = request.getString("attacker");
//		Territory attacker = gameData.getGameMap().getTerritories().getByPlayerId(attackerId);
//		String defenderId = request.getString("defender");
//		Territory defender = gameData.getGameMap().getTerritories().getByPlayerId(defenderId);
//		// Check if attacker is owned by the current player
//		if (attacker == null || !attacker.getGameOwner().getPlayerId().equals(player.getPlayerId())) {
//			System.out.println("Attacker does not own the required territory");
//		}
//		// Check if defender is a viable target
//		if (defender == null || defender.getGameOwner().getPlayerId().equals(player.getPlayerId()) || defender.getNeighbours().getByPlayerId(attackerId) == null) {
//			System.out.println("Defender is not a viable target");
//		}
//		// Get the attack action and display as appropriate
//		String attackAction = request.getString("attack");
//		switch (attackAction) {
//			case "start":
//				Utils.sendGameMessage(gameData.getGameId(), "attack", Json.createObjectBuilder().add("attacker", attacker.toJson()).add("defender", defender.toJson()).build());
//				break;
//			case "attack":
//				GameActions.attackTerritory(gameData, player, attacker, defender);
//				break;
//			case "retreat":
//				GameActions.updateAll(gameData, false, true, false);
//				break;
//		}
//	}
//
//	/**
//	 * Initial territory deployment
//	 * @param socket Current player session information
//	 * @param request {"gameId": "Test's Game"}
//	 */
//	public static void deploy(GamePlayers player, GameData gameData, JsonObject request) {
//		String territoryId = request.getString("territory");
//		if (GameActions.deployTerritory(player, territoryId, gameData)) {
//			if (gameData.getGameMap().getUnclaimedTerritories().size() < 1) {
//				Configuration config = gameData.getConfig();
//				config.setPlayerTurn(null);
//				config.setTurnPhase("reinforce");
//			}
//			GameActions.startTurn(gameData);
//		}
//	}
//
//	/**
//	 * Reinforce a friendly territory
//	 * @param socket Current player session information
//	 * @param request {"gameId": "Test's Game"}
//	 */
//	public static void reinforce(GamePlayers player, GameData gameData, JsonObject request) {
//		String territoryId = request.getString("territory");
//		// GamePlayers must use risk cards before they reinforce
//		if (player.getRiskCards().size() > 4) {
//			Utils.sendMessage(WebSocketServerConfiguration.playerList.getByPlayerId(player.getPlayerId()), "viewCards", request);
//			return;
//		}
//		// Otherwise reinforce territory if possible
//		if (GameActions.reinforceTerritory(player, territoryId, gameData)) {
//			// Take turns reinforcing before game has started
//			if (!gameData.getGameStarted()) {
//				if (player.getReinforcements() < 1) {
//					gameData.setPlayersReady(gameData.getPlayersReady() + 1);
//					if (gameData.getPlayersReady() == gameData.getPlayersActive()) {
//						gameData.setGameStarted(true);
//					}
//				}
//				GameActions.startTurn(gameData);
//				return;
//			}
//			// Move to attack phase when out of reinforcements
//			else if (player.getReinforcements() < 1) {
//				Configuration config = gameData.getConfig();
//				config.setTurnPhase("attack");
//			}
//			GameActions.updateAll(gameData, true, true, true);
//		}
//	}

}
