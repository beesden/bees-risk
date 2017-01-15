package org.beesden.risk.action;

public class VictoryActions {
	
//	public static Boolean checkVictoryConditions(GameData gameData) {
//		// Loop over players
//		for (String playerId: gameData.getPlayerList().keySet()) {
//			GamePlayers player = gameData.getPlayerList().get(playerId);
//			if (!player.isNeutral()) {
//				// Test player victory conditions
//				String victoryCondition = "lastPlayerStanding"; // @TOOD - add config option
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
//				for (String playerId: gameData.getPlayerList().keySet()) {
//					GamePlayers gamePlayer = gameData.getPlayerList().get(playerId);
//					if (!gamePlayer.isNeutral()) {
//						++activePlayers;
//					}
//				}
//				return activePlayers == 1;
//		}
//
//	}
	

}
