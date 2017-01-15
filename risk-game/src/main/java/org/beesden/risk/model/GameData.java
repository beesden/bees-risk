package org.beesden.risk.model;

import lombok.Data;
import org.beesden.risk.service.MapService;

@Data
public class GameData {

	private static final int[] DEFAULT_START_STRENGTH = { 0, 40, 35, 30, 25, 20 };

	public enum GameState {
		SETUP, READY, STARTED, ENDED
	}
	public enum TurnPhase {
		REINFORCE, ATTACK, REDEPLOY
	}

	private int[] startForces = DEFAULT_START_STRENGTH;

	private String name;
	private GameState state = GameState.SETUP;

	// Game data objects
	private GameConfig config;
	private GameMap map;
	private CardDeck cards;
	private GamePlayers players;

	// Game turn data
	private int cardPlayCount = 0;

	/**
	 * Create a new game
	 *
	 * @param playerId game create ID
	 * @param gameName game name
	 * @param config   game config
	 */
	public GameData(String playerId, String gameName, GameConfig config) {
		this.name = gameName;
		this.config = config;

		this.map = MapService.getMapById(config.getGameMap());
		this.players = new GamePlayers(playerId);
		this.cards = new CardDeck(this.map);
	}
	//

	/**
	 * Updates the config with the next player's turn
	 */
	public void nexTurn() {
//		if (gameData.getPlayersActive() < 2) {
//			System.out.println("Game won!"); // TODO - check for victory
//			return;
//		}
//		// Update the config with the new player turn
//		Iterator<GamePlayers> iter = players.iterator();
//		while (iter.hasNext()) {
//			GamePlayers player = iter.next();
//			// If no player, use the first value - this is used to reset to player 1
//			if (config.getPlayerTurn() == null) {
//				config.setPlayerTurn(player.getPlayerId());
//				break;
//			}
//			// If we match the player to the iterator, get the next player
//			else if (player.getPlayerId().equals(config.getPlayerTurn())) {
//				String playerId = iter.hasNext() ? iter.next() : players.get(players.keySet().toArray()[ 0 ])
//						.getPlayerId();
//				config.setPlayerTurn(playerId);
//				break;
//			}
//		}
//		// If it's a neutral player, re-iterate the function
//		GamePlayers newTurn = players.get(config.getPlayerTurn());
//		if (newTurn.isNeutral() && !config.getTurnPhase().equals("deploy")) {
//			startTurn(gameData);
//			return;
//		}
//		// Generic start turn - e.g. calculate reinforcements
//		else if (gameData.getState() == gameData.getPlayersActive()) {
//			newTurn.setReinforcements(calculateReinforcemnt(newTurn, gameData));
//		}
//		// Automatically place starting territories
//		else if (config.getTurnPhase().equals("deploy")) { // TODO - add config option
//			List<String> unclaimed = gameData.getGameMap().getUnclaimedTerritories();
//			String territoryId = unclaimed.get(new Random().nextInt(unclaimed.size()));
//			TerritoryActions.deploy(newTurn, gameData, Json.createObjectBuilder()
//					.add("territory", territoryId)
//					.build());
//			return;
//		}
//		// Automatically place starting battalions
//		else if (config.getTurnPhase().equals("reinforce")) { // TODO - add config option
//			List<Territory> territoryList = newTurn.getTerritories();
//			String territoryId = territoryList.get(new Random().nextInt(territoryList.size())).getId();
//			TerritoryActions.reinforce(newTurn, gameData, Json.createObjectBuilder()
//					.add("territory", territoryId)
//					.build());
//			return;
//		}
	}

}
