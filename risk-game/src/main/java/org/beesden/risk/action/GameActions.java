package org.beesden.risk.action;

public class GameActions {

//	public static void attackTerritory(GameData gameData, GamePlayers player, Territory attacker, Territory defender) {
//		JsonObjectBuilder response = Json.createObjectBuilder();
//		// Attacker must have > 1 battalions to attack
//		if (attacker.getBattalions() < 2) {
//			System.out.println("insufficient force to attack");
//			GameActions.updateAll(gameData, false, true, false);
//			return;
//		}
//		// Generate attacker's rolls
//		List<Integer> attackDice = new ArrayList<>();
//		for (int i = 1; i < attacker.getBattalions() && i < 4; i++) {
//			Integer roll = new Random().nextInt(6) + 1;
//			attackDice.add(roll);
//		}
//		// Generate defender's rolls
//		List<Integer> defendDice = new ArrayList<>();
//		for (int i = 0; i < defender.getBattalions() && i < 2; i++) {
//			Integer roll = new Random().nextInt(6) + 1;
//			defendDice.add(roll);
//		}
//		// Calculate results and update territories
//		JsonObjectBuilder results = Json.createObjectBuilder().add("attacker", JsonUtils.toArray(attackDice)).add("defender", JsonUtils.toArray(defendDice));
//		Integer[] combat = {0, 0};
//		Collections.sort(attackDice);
//		Collections.reverse(attackDice);
//		Collections.sort(defendDice);
//		Collections.reverse(defendDice);
//		for (int i = 0; i < attackDice.size() && i < defendDice.size(); i++) {
//			Integer index = attackDice.get(i) > defendDice.get(i) ? 1 : 0;
//			if (index == 0) {
//				attacker.setBattalions(attacker.getBattalions() - 1);
//			} else {
//				defender.setBattalions(defender.getBattalions() - 1);
//			}
//			combat[index]++;
//		}
//		results.add("combat", JsonUtils.toArray(combat));
//		// Change ownership if defender runs out of battalions
//		if (defender.getBattalions() == 0) {
//			GamePlayers defensivePlayer = defender.getGameOwner();
//			Integer invadeStrength = attacker.getBattalions() - 1;
//			invadeStrength = invadeStrength > 3 ? 3 : invadeStrength;
//			attacker.setBattalions(attacker.getBattalions() - invadeStrength);
//			defender.setBattalions(invadeStrength);
//			attacker.getGameOwner().getTerritories().add(defender);
//			defensivePlayer.getTerritories().remove(defender);
//			defender.setGameOwner(attacker.getGameOwner());
//			response.add("conquer", true);
//			// Has the player been defeated
//			if (defensivePlayer.getTerritories().size() < 1 && !defensivePlayer.isNeutral()) {
//				defensivePlayer.setNeutral(true);
//				response.add("reason", "defeat");
//				response.add("player", defensivePlayer.toJson());
//				// Transfer cards
//				List<String> cardTransfer = new ArrayList<>(defensivePlayer.getRiskCards().keySet());
//				for (String cardId : cardTransfer) {
//					CardService.removeCard(gameData, defensivePlayer.getPlayerId(), cardId);
//				}
//				if (cardTransfer.size() > 0) {
//					CardService.addCard(gameData, player.getPlayerId(), cardTransfer, false);
//				}
//				response.add("riskCards", JsonUtils.toArray(cardTransfer));
//				// TODO - check for victory;
//			}
//			gameData.setCardEarned(true);
//		}
//		// Send results to all players
//		response.add("attacker", attacker.toJson()).add("defender", defender.toJson()).add("results", results);
//		Utils.sendGameMessage(gameData.getGameId(), "attack", response.build());
//	}
//
//	public static boolean deployTerritory(GamePlayers player, String territoryId, GameData gameData) {
//		GameMap gameMap = gameData.getGameMap();
//		if (gameMap == null) {
//			System.out.println("Error retrieving the map object");
//			return false;
//		}
//		Territory target = gameMap.getTerritories().get(territoryId);
//		if (target == null || target.getGameOwner() != null) {
//			System.out.println("Unable to deploy to requested territory");
//			return false;
//		}
//		target.setBattalions(player.isNeutral() ? 2 : 1);
//		target.setGameOwner(player);
//		player.setReinforcements(player.getReinforcements() - 1);
//		player.getTerritories().add(target);
//		gameData.getGameMap().getUnclaimedTerritories().remove(territoryId);
//		return true;
//	}
//
//	public static boolean reinforceTerritory(GamePlayers player, String territoryId, GameData gameData) {
//		GameMap gameMap = gameData.getGameMap();
//		if (gameMap == null) {
//			System.out.println("Error retrieving the map object");
//			return false;
//		}
//		Territory target = gameMap.getTerritories().get(territoryId);
//		if (target == null || !target.getGameOwner().getPlayerId().equals(gameData.getConfig().getPlayerTurn())) {
//			System.out.println("Unable to reinforce selected territory");
//			return false;
//		}
//		target.setBattalions(target.getBattalions() + 1);
//		player.setReinforcements(player.getReinforcements() - 1);
//		return true;
//	}
//
//	public static void startGame(GameData gameData, JsonObject request) {
//		Configuration config = gameData.getConfig();
//		// Start populating the gamedata object
//		gameData.setGameReady(true);
//		Integer startForces = config.getStartForces()[gameData.getPlayersActive()];
//		for (String playerId : gameData.getPlayerList().keySet()) {
//			GamePlayers player = gameData.getPlayerList().get(playerId);
//			player.setReinforcements(startForces);
//		}
//		config.setTurnPhase("deploy");
//		// Assemble the map
//		String mapId = request.getString("mapId");
//		GameMap gameMap = MapService.generateMap(mapId);
//		gameData.setGameMap(gameMap);
//		// Generate the risk cards listIds
//		for (String territoryId : gameMap.getTerritories().keySet()) {
//			Territory territory = gameMap.getTerritories().get(territoryId);
//			gameData.getRiskCards().add(territory.getId());
//		}
//		for (int i = 0; i < 2; i++) { // TODO - add config option
//			gameData.getRiskCards().add("wild-" + i);
//		}
//		startTurn(gameData);
//	}
//
//	public static void updateAll(GameData gameData, Boolean updateConfig, Boolean updateMap, Boolean updatePlayers) {
//		JsonObjectBuilder jsonObject = Json.createObjectBuilder();
//		if (updateConfig) {
//			jsonObject.add("config", gameData.getConfig().toJson());
//		}
//		if (updateMap) {
//			jsonObject.add("mapData", gameData.getGameMap().toJson());
//		}
//		if (updatePlayers) {
//			jsonObject.add("playerData", gameData.JsonPlayerList());
//		}
//		Utils.sendGameMessage(gameData.getGameId(), "updateAll", jsonObject.build());
//	}

}
