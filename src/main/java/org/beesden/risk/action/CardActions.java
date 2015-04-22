package org.beesden.risk.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.Session;

import org.beesden.risk.model.Configuration;
import org.beesden.risk.model.GameData;
import org.beesden.risk.model.Player;
import org.beesden.risk.model.Territory;
import org.beesden.risk.utils.JsonUtils;
import org.beesden.risk.utils.Utils;

public class CardActions {
	
	public static void addCard(GameData gameData, String playerId, List<String> cardIds, Boolean updatePlayer) {
		// Get the target player object
		Player player = gameData.getPlayerList().get(playerId);
		if (player == null || player.getIsNeutral()) {
			System.out.println("Active player not found");
			return;
		}
		// Add a random card
		if (cardIds == null || cardIds.size() < 1) {
			cardIds = new ArrayList<>();
			Integer cardIndex = new Random().nextInt(gameData.getRiskCards().size());
			cardIds.add(gameData.getRiskCards().get(cardIndex));			
		}
		// Remove cards from pack and add to player
		for (String cardId: cardIds) {
			int cardIndex = gameData.getRiskCards().indexOf(cardId);
			if (cardIndex < 0) {
				continue;
			}
			gameData.getRiskCards().remove(cardIndex);
			Territory cardTerritory = gameData.getGameMap().getTerritories().get(cardId);
			player.getRiskCards().put(cardId, cardTerritory == null ? 0 : cardTerritory.getCardValue());
		}
		// Show cards to the player
		if (updatePlayer) {
			Session playerSession = ServerActions.playerList.get(playerId);
			Utils.sendMessage(playerSession, "viewCards", Json.createObjectBuilder().add("addCards", true).add("riskCards", JsonUtils.toArray(cardIds)).build());
		}
	}
	
	public static void removeCard(GameData gameData, String playerId, String cardId) {
		// Get the target player object
		Player player = gameData.getPlayerList().get(playerId);
		if (player == null) {
			System.out.println("Active player not found");
			return;
		}
		// If id provided, get index of card from list
		if (cardId == null) {
			System.out.println("No valid Card ID supplied");
			return;
		}
		// Remove from player and add to pack
		player.getRiskCards().remove(cardId);
		gameData.getRiskCards().add(cardId);
		// Update all players
		GameActions.updateAll(gameData, false, false, true);
	}
	
	public static int calculateCardValue(GameData gameData, int[] cardArray) {
		Arrays.sort(cardArray);
        // 3 of a kind (with wildcard)
        if (cardArray[0] == 0 && (cardArray[1] == cardArray[2]) || cardArray[1] == 0) {
            for (int id: cardArray) {
                if (cardArray[id] != 0) {
                	return cardArray[id];
                }
            }
        }
        // 1 of each type (with wildcard)
        else if (cardArray[0] == 0) {
        	return 4;
        }
        // Three of a kind
        else if (cardArray[0] == cardArray[1] && cardArray[1] == cardArray[2]) {
        	return cardArray[0];
        }
        // 1 of each type
        else if (cardArray[0] < cardArray[1] && cardArray[1] < cardArray[2]) {
        	return 4;
        }
        // Cards do not make a set
		return 0;		
	}
	
	public static void useCards(GameData gameData, Player player, JsonObject request) {
		// Cards can only be played during the reinforcement phase
		if (!gameData.getConfig().getTurnPhase().equals("reinforce")) {
			System.out.println("Cards may only be played during the reinforcement phase");
			return;
		}
		// Validate the cards
		String card1 = request.getString("card1");
		String card2 = request.getString("card2");
		String card3 = request.getString("card3");
		if (card1 == null || card2 == null || card3 == null || card1.equals(card2) || card1.equals(card3) || card2.equals(card3)) {
			System.out.println("Error playing cards: same card selected");
			return;
		}
		// Build array of card values
		int[] cardArray = new int[3];
		Map<String, Territory> territoryList = gameData.getGameMap().getTerritories();
		for (int i = 1; i < 4; i++) {
			String cardId = request.getString("card" + i);
			if (!cardId.startsWith("wild-") && territoryList.get(cardId) == null) {
				System.out.println("Unable to find card value for cardId " + cardId);
				return;
			}
			cardArray[i - 1] = cardId.startsWith("wild-") ? 0 : territoryList.get(cardId).getCardValue();
		}
		Integer cardValue = calculateCardValue(gameData, cardArray);
		// Get reinforcement value
		if (cardValue > 0) {
			Configuration config = gameData.getConfig();
			Integer reinforcements = 0;
			// Incremental card values
			if (config.getCardLevel() > 5) { // TODO - add config option
				reinforcements = config.getCardLevel() > 5 ? (config.getCardLevel() - 2) * 5 : config.getCardBonus()[config.getCardLevel()];
				config.setCardLevel(config.getCardLevel() + 1);
			} 
			// Static card values
			else {
				reinforcements = (2 * cardValue) + 2;
			}
			removeCard(gameData, player.getPlayerId(), card1);
			removeCard(gameData, player.getPlayerId(), card2);
			removeCard(gameData, player.getPlayerId(), card3);
			// Update player
			String message = Utils.getBundle("risk.cards.use", player.getPlayerId(), reinforcements);
			player.setReinforcements(player.getReinforcements() + reinforcements);
			// Send game back to deployment phase
			config.setTurnPhase("reinforce");
			GameActions.updateAll(gameData, true, true, true);
		}
	}

}
