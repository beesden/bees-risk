package org.beesden.risk.game.action;

public class VictoryActions {

	//	public static Boolean checkVictoryConditions(GameData gameData) {
	//		// Loop over players
	//		for (Integer playerId: gameData.getPlayerList().keySet()) {
	//			GamePlayers player = gameData.getPlayerList().getByPlayerId(playerId);
	//			if (!player.isNeutral()) {
	//				// Test player victory conditions
	//				String victoryCondition = "lastPlayerStanding"; // @TODD - add config option
	//				if (testPlayerVictory(player, gameData, victoryCondition)) {
	//					gameData.setGameFinished(true);
	//					String message = Utils.getBundle("risk.victory." + victoryCondition.toLowerCase(), player.getPlayerId());
	//					JsonObject victoryMessage = Json.createObjectBuilder().add("message", message).build();
	//					Utils.sendGameMessage(gameData.getGameId(), "victory", victoryMessage);
	//					return true;
	//				}
	//			}
	//		}
	//		return false;
	//	}
	//
	//	public static Boolean testPlayerVictory(GamePlayers player, GameData gameData, String victoryCondition) {
	//
	//		switch (victoryCondition) {  //@TODO - add victory conditions
	//			// Last player standing
	//			default:
	//				Integer activePlayers = 0;
	//				for (Integer playerId: gameData.getPlayerList().keySet()) {
	//					GamePlayers gamePlayer = gameData.getPlayerList().getByPlayerId(playerId);
	//					if (!gamePlayer.isNeutral()) {
	//						++activePlayers;
	//					}
	//				}
	//				return activePlayers == 1;
	//		}
	//
	//	}

}
