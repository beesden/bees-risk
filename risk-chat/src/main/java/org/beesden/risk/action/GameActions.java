package org.beesden.risk.action;

import java.util.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.beesden.risk.model.Configuration;
import org.beesden.risk.model.Continent;
import org.beesden.risk.model.GameData;
import org.beesden.risk.model.GameMap;
import org.beesden.risk.model.Player;
import org.beesden.risk.model.Territory;
import org.beesden.risk.utils.JsonUtils;
import org.beesden.risk.utils.MapUtils;
import org.beesden.risk.utils.Utils;

public class GameActions {

	public static void attackTerritory(GameData gameData, Player player, Territory attacker, Territory defender) {
		JsonObjectBuilder response = Json.createObjectBuilder();
		// Attacker must have > 1 battalions to attack
		if (attacker.getBattalions() < 2) {
			System.out.println("Insufficent force to attack");
			GameActions.updateAll(gameData, false, true, false);
			return;
		}
		// Generate attacker's rolls
		List<Integer> attackDice = new ArrayList<>();
		for (int i = 1; i < attacker.getBattalions() && i < 4; i++) {
			Integer roll = new Random().nextInt(6) + 1;
			attackDice.add(roll);
		}
		// Generate defender's rolls
		List<Integer> defendDice = new ArrayList<>();
		for (int i = 0; i < defender.getBattalions() && i < 2; i++) {
			Integer roll = new Random().nextInt(6) + 1;
			defendDice.add(roll);
		}
		// Calculate results and update territories
		JsonObjectBuilder results = Json.createObjectBuilder().add("attacker", JsonUtils.toArray(attackDice)).add("defender", JsonUtils.toArray(defendDice));
		Integer[] combat = {0, 0};
		Collections.sort(attackDice);
		Collections.reverse(attackDice);
		Collections.sort(defendDice);
		Collections.reverse(defendDice);
		for (int i = 0; i < attackDice.size() && i < defendDice.size(); i++) {
			Integer index = attackDice.get(i) > defendDice.get(i) ? 1 : 0;
			if (index == 0) {
				attacker.setBattalions(attacker.getBattalions() - 1);
			} else {
				defender.setBattalions(defender.getBattalions() - 1);
			}
			combat[index]++;
		}
		results.add("combat", JsonUtils.toArray(combat));
		// Change ownership if defender runs out of battalions
		if (defender.getBattalions() == 0) {
			Player defensivePlayer = defender.getOwner();
			Integer invadeStrength = attacker.getBattalions() - 1;
			invadeStrength = invadeStrength > 3 ? 3 : invadeStrength;
			attacker.setBattalions(attacker.getBattalions() - invadeStrength);
			defender.setBattalions(invadeStrength);
			attacker.getOwner().getTerritories().add(defender);
			defensivePlayer.getTerritories().remove(defender);
			defender.setOwner(attacker.getOwner());
			response.add("conquer", true);
			// Has the player been defeated
			if (defensivePlayer.getTerritories().size() < 1 && !defensivePlayer.isNeutral()) {
				defensivePlayer.setNeutral(true);
				response.add("reason", "defeat");
				response.add("player", defensivePlayer.toJson());
				// Transfer cards
				List<String> cardTransfer = new ArrayList<>(defensivePlayer.getRiskCards().keySet());
				for (String cardId : cardTransfer) {
					CardActions.removeCard(gameData, defensivePlayer.getPlayerId(), cardId);
				}
				if (cardTransfer.size() > 0) {
					CardActions.addCard(gameData, player.getPlayerId(), cardTransfer, false);
				}
				response.add("riskCards", JsonUtils.toArray(cardTransfer));
				// TODO - check for victory;
			}
			gameData.setCardEarned(true);
		}
		// Send results to all players
		response.add("attacker", attacker.toJson()).add("defender", defender.toJson()).add("results", results);
		Utils.sendGameMessage(gameData.getGameId(), "attack", response.build());
	}

	public static boolean deployTerritory(Player player, String territoryId, GameData gameData) {
		GameMap gameMap = gameData.getGameMap();
		if (gameMap == null) {
			System.out.println("Error retrieving the map object");
			return false;
		}
		Territory target = gameMap.getTerritories().get(territoryId);
		if (target == null || target.getOwner() != null) {
			System.out.println("Unable to deploy to requested territory");
			return false;
		}
		target.setBattalions(player.isNeutral() ? 2 : 1);
		target.setOwner(player);
		player.setReinforcements(player.getReinforcements() - 1);
		player.getTerritories().add(target);
		gameData.getGameMap().getUnclaimedTerritories().remove(territoryId);
		return true;
	}

	public static boolean reinforceTerritory(Player player, String territoryId, GameData gameData) {
		GameMap gameMap = gameData.getGameMap();
		if (gameMap == null) {
			System.out.println("Error retrieving the map object");
			return false;
		}
		Territory target = gameMap.getTerritories().get(territoryId);
		if (target == null || !target.getOwner().getPlayerId().equals(gameData.getConfig().getPlayerTurn())) {
			System.out.println("Unable to reinforce selected territory");
			return false;
		}
		target.setBattalions(target.getBattalions() + 1);
		player.setReinforcements(player.getReinforcements() - 1);
		return true;
	}

	public static Integer calculateReinforcemnt(Player player, GameData gameData) {
		GameMap gameMap = gameData.getGameMap();
		Integer globalTerritories = 0;
		Integer bonusReinforcement = 0;
		// Manually recount territories to allow for continent bonuses
		for (String c : gameMap.getContinents().keySet()) {
			Continent cont = gameMap.getContinents().get(c);
			Integer control = 0;
			// Calculate how many territories are controlled by the current player
			for (String t : cont.getTerritories().keySet()) {
				Territory terr = cont.getTerritories().get(t);
				if (terr.getOwner() == player) {
					control++;
					globalTerritories++;
				}
			}
			// If the player controls the whole continent, add bonus
			if (control == cont.getTerritories().size()) {
				bonusReinforcement += cont.getBonus();
			}
		}
		// Ensure a mimumum of three battalions (excluding continent bonus)
		globalTerritories = globalTerritories < 9 ? 9 : globalTerritories;
		return (int) Math.floor(globalTerritories / 3) + bonusReinforcement;
	}

	public static void startGame(GameData gameData, JsonObject request) {
		Configuration config = gameData.getConfig();
		// Start populating the gamedata object
		gameData.setGameReady(true);
		Integer startForces = config.getStartForces()[gameData.getPlayersActive()];
		for (String playerId : gameData.getPlayerList().keySet()) {
			Player player = gameData.getPlayerList().get(playerId);
			player.setReinforcements(startForces);
		}
		config.setTurnPhase("deploy");
		// Assemble the map
		String mapId = request.getString("mapId");
		GameMap gameMap = MapUtils.generateMap(mapId);
		gameData.setGameMap(gameMap);
		// Generate the risk cards list
		for (String territoryId : gameMap.getTerritories().keySet()) {
			Territory territory = gameMap.getTerritories().get(territoryId);
			gameData.getRiskCards().add(territory.getId());
		}
		for (int i = 0; i < 2; i++) { // TODO - add config option
			gameData.getRiskCards().add("wild-" + i);
		}
		startTurn(gameData);
	}

	/**
	 * Updates the config with the next player's turn
	 *
	 * @param socket  Current player session information
	 * @param request {"gameId": "Test's Game"}
	 */
	public static void startTurn(GameData gameData) {
		if (gameData.getPlayersActive() < 2) {
			System.out.println("Game won!"); // TODO - check for victory
			return;
		}
		Configuration config = gameData.getConfig();
		LinkedHashMap<String, Player> playerList = gameData.getPlayerList();
		// Update the config with the new player turn
		Iterator<String> iter = playerList.keySet().iterator();
		while (iter.hasNext()) {
			Player player = playerList.get(iter.next());
			// If no player, use the first value - this is used to reset to player 1
			if (config.getPlayerTurn() == null) {
				config.setPlayerTurn(player.getPlayerId());
				break;
			}
			// If we match the player to the iterator, get the next player		
			else if (player.getPlayerId().equals(config.getPlayerTurn())) {
				String playerId = iter.hasNext() ? iter.next() : playerList.get(playerList.keySet().toArray()[0]).getPlayerId();
				config.setPlayerTurn(playerId);
				break;
			}
		}
		// If it's a neutral player, re-iterate the function
		Player newTurn = playerList.get(config.getPlayerTurn());
		if (newTurn.isNeutral() && !config.getTurnPhase().equals("deploy")) {
			startTurn(gameData);
			return;
		}
		// Generic start turn - e.g. calculate reinforcements
		else if (gameData.getPlayersReady() == gameData.getPlayersActive()) {
			newTurn.setReinforcements(calculateReinforcemnt(newTurn, gameData));
		}
		// Automatically place starting territories
		else if (config.getTurnPhase().equals("deploy")) { // TODO - add config option
			List<String> unclaimed = gameData.getGameMap().getUnclaimedTerritories();
			String territoryId = unclaimed.get(new Random().nextInt(unclaimed.size()));
			TerritoryActions.deploy(newTurn, gameData, Json.createObjectBuilder().add("territory", territoryId).build());
			return;
		}
		// Automatically place starting battalions
		else if (config.getTurnPhase().equals("reinforce")) { // TODO - add config option		
			List<Territory> territoryList = newTurn.getTerritories();
			String territoryId = territoryList.get(new Random().nextInt(territoryList.size())).getId();
			TerritoryActions.reinforce(newTurn, gameData, Json.createObjectBuilder().add("territory", territoryId).build());
			return;
		}
		updateAll(gameData, true, true, true);
	}

	public static void updateAll(GameData gameData, Boolean updateConfig, Boolean updateMap, Boolean updatePlayers) {
		JsonObjectBuilder jsonObject = Json.createObjectBuilder();
		if (updateConfig) {
			jsonObject.add("config", gameData.getConfig().toJson());
		}
		if (updateMap) {
			jsonObject.add("mapData", gameData.getGameMap().toJson());
		}
		if (updatePlayers) {
			jsonObject.add("playerData", gameData.JsonPlayerList());
		}
		Utils.sendGameMessage(gameData.getGameId(), "updateAll", jsonObject.build());
	}

}
