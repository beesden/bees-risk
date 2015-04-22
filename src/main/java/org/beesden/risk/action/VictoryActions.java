package org.beesden.risk.action;

import javax.json.Json;
import javax.json.JsonObject;
import org.beesden.risk.model.GameData;
import org.beesden.risk.model.Player;
import org.beesden.risk.utils.Utils;

public class VictoryActions {
	
	public static Boolean checkVictoryConditions(GameData gameData) {
		// Loop over players
		for (String playerId: gameData.getPlayerList().keySet()) {
			Player player = gameData.getPlayerList().get(playerId);
			if (!player.getIsNeutral()) {
				// Test player victory conditions
				String victoryCondition = "lastPlayerStanding"; // @TOOD - add config option
				if (testPlayerVictory(player, gameData, victoryCondition)) {
					gameData.setGameFinished(true);
					String message = Utils.getBundle("risk.victory." + victoryCondition.toLowerCase(), player.getPlayerId());
					JsonObject victoryMessage = Json.createObjectBuilder().add("message", message).build(); 
					Utils.sendGameMessage(gameData.getGameId(), "victory", victoryMessage);
					return true;
				};
			}			
		}		
		return false;
	}
	
	public static Boolean testPlayerVictory(Player player, GameData gameData, String victoryCondition) {
		
		switch (victoryCondition) {  //@TODO - add victory conditions
			// Last player standing
			default: 
				Integer activePlayers = 0;
				for (String playerId: gameData.getPlayerList().keySet()) {
					Player gamePlayer = gameData.getPlayerList().get(playerId);
					if (!gamePlayer.getIsNeutral()) {
						++activePlayers;
					}					
				}
				return activePlayers == 1;
		}
		
	}
	

}
