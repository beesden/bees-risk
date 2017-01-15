package org.beesden.risk.model;

import lombok.Getter;
import org.beesden.risk.service.MapService;

import java.util.*;

@Getter
public class GameData {

	private static final int[] DEFAULT_START_STRENGTH = { 40, 35, 30, 25, 20 };

	public enum GameState {
		SETUP, READY, STARTED, ENDED
	}

	public enum TurnPhase {
		REINFORCE, ATTACK, REDEPLOY
	}

	private int[] startForces = DEFAULT_START_STRENGTH;

	private String name;
	private GameState state = GameState.SETUP;
	private TurnPhase phase;

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

	/**
	 * Starts the game
	 */
	public void startGame() {

		int activePlayerCount = (int) players.countActivePlayers();
		if (activePlayerCount < 2) {
			return;
		} else if (activePlayerCount == 2) {
			players.addNeutralPlayer();
		}

		// Assign starting battalions
		List<Player> playerList = players.getPlayers();
		int startingForces = startForces[ activePlayerCount - 2 ];
		for (Player player : playerList) {
			player.setReinforcements(startingForces);
		}

		// Automatically place starting territories
		if (config.isAutoAssignTerritories()) {

			Iterator<Player> playerIterator = playerList.iterator();
			List<GameMap.Territory> territories = map.getTerritories();
			Collections.shuffle(territories);

			for (GameMap.Territory territory : territories) {
				if (!playerIterator.hasNext()) {
					playerIterator = playerList.iterator();
				}
				Player player = playerIterator.next();
				player.takeTerritory(territory);
				player.reinforce(territory.getId());
			}

			// Automatically place starting battalions
			if (config.isAutoPlaceBattalions()) {

				for (Player player : playerList) {
					List<String> playerTerritories = new ArrayList<>(player.getTerritories().keySet());

					while (player.getReinforcements() > 0) {
						player.reinforce(playerTerritories.get(new Random().nextInt(playerTerritories.size())));
					}
				}

				state = GameState.STARTED;
				startTurn();
				return;
			}
		}

		state = GameState.READY;
	}

	/**
	 * Updates the config with the next player's turn
	 */
	public void startTurn() {
		if (players.countActivePlayers() < 2) {
			System.out.println("Game won!"); // TODO - replace with victory lookup
			return;
		}
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
		//		// Generic start turn - e.g. calculate reinforcements
		//		if (state == GameState.STARTED) {
		//			newTurn.setReinforcements(map.calculateReinforcements());
		//		}

	}

}
