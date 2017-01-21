package org.beesden.risk.game.model;

import org.beesden.risk.game.Exception.GameLobbyException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {

	private final Map<String, GameData> SESSION_GAMES = new ConcurrentHashMap<>();

	/**
	 * List all available games in the lobby
	 *
	 * @return listIds of games
	 */
	public Collection<GameData> listGames() {
		return SESSION_GAMES.values();
	}

	/**
	 * Create a game
	 *
	 * @param playerId player id
	 * @param config   game config
	 */
	public GameData createGame(Integer playerId, String gameId, Config config) {

		if (SESSION_GAMES.get(gameId) != null) {
			throw new GameLobbyException("A game with that name already exists", playerId, gameId);
		}

		GameData gameData = new GameData(playerId, gameId, config);
		SESSION_GAMES.put(gameId, gameData);

		return gameData;
	}

	/**
	 * Join a game
	 *
	 * @param playerId player id
	 * @param gameId   game id
	 */
	public GameData joinGame(Integer playerId, String gameId) {

		GameData gameData = SESSION_GAMES.get(gameId);

		if (gameData == null) {
			throw new GameLobbyException("Game cannot be found", playerId, gameId);
		}

		// Check if player has already joined the game
		if (gameData.getPlayers().countActivePlayers() >= gameData.getConfig().getMaxPlayers()) {
			throw new GameLobbyException("No more space in the game", playerId, gameId);
		} else if (gameData.getState() != GameData.GameState.SETUP) {
			throw new GameLobbyException("The game has already started", playerId, gameId);
		}

		gameData.getPlayers().add(playerId);

		return gameData;
	}

	/**
	 * Start a game
	 *
	 * @param playerId player
	 */
	public GameData leaveGame(Integer playerId, String gameId) {
		GameData gameData = SESSION_GAMES.get(gameId);

		// Remove player if game not yet started
		if (gameData != null) {
			gameData.getPlayers().remove(playerId, gameData.getState() != GameData.GameState.SETUP);

			// Close the game if no playerIds remain
			if (gameData.getPlayers().getOwner() == null) {
				SESSION_GAMES.remove(gameData.getName());
			}
		}

		return gameData;
	}

	/**
	 * Start a game
	 *
	 * @param playerId player
	 * @param gameId   game id
	 */
	public GameData startGame(Integer playerId, String gameId) {
		GameData gameData = SESSION_GAMES.get(gameId);

		if (gameData == null) {
			throw new GameLobbyException("Game cannot be found", playerId, gameId);
		} else if (!playerId.equals(gameData.getPlayers().getOwner())) {
			throw new GameLobbyException("Player does not own game", playerId, gameId);
		} else if (gameData.getPlayers().countActivePlayers() < gameData.getConfig().getMinPlayers()) {
			throw new GameLobbyException("Insufficient playerIds to start game", playerId, gameId);
		} else if (gameData.getState() != GameData.GameState.SETUP) {
			throw new GameLobbyException("The game has already started", playerId, gameId);
		}

		gameData.startGame();
		return gameData;
	}
}
