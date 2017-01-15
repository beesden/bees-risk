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
	private Config config;
	private GameMap map;
	private CardDeck cards;
	private GamePlayers players;

	// Game turn data
	private String currentTurn = "__INVALID";

	/**
	 * Create a new game
	 *
	 * @param playerId game create ID
	 * @param gameName game name
	 * @param config   game config
	 */
	public GameData(String playerId, String gameName, Config config) {
		this.name = gameName;
		this.config = config;

		this.map = MapService.getMapById(config.getGameMap());
		this.players = new GamePlayers(playerId);
		this.cards = new CardDeck(this.map);
	}

	/**
	 * Starts the game
	 */
	public Player startGame() {

		int activePlayerCount = (int) players.countActivePlayers();
		if (activePlayerCount < 2) {
			return null;
		} else if (activePlayerCount == 2) {
			players.addNeutralPlayer();
		}

		// Assign starting battalions
		List<Player> playerList = players.getPlayerList();
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
				return startTurn();
			}
		}

		state = GameState.READY;
		return startTurn();
	}

	/**
	 * Updates the config with the next player's turn
	 */
	public Player startTurn() {
		if (players.countActivePlayers() < 2) {
			System.out.println("Game won!"); // TODO - replace with victory lookup
			return null; // TODO - return winner!
		}

		Player currentPlayer = players.get(currentTurn);
		List<Player> activePlayers = players.getActivePlayers();
		int playerIndex = activePlayers.indexOf(currentPlayer);
		if (++playerIndex == activePlayers.size()) {
			playerIndex = 0;
		}
		currentPlayer = activePlayers.get(playerIndex);
		currentTurn = currentPlayer.getPlayerId();

		// Generic start turn - e.g. calculate reinforcements
		if (state == GameState.STARTED) {
			phase = TurnPhase.REINFORCE;
			currentPlayer.setReinforcements(map.calculateReinforcement(currentTurn));
		}

		return currentPlayer;

	}

}