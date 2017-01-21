package org.beesden.risk.client.Model;

import org.beesden.risk.game.Exception.GameLobbyException;
import org.beesden.risk.game.model.Config;
import org.beesden.risk.game.model.GameData;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Lobby {

	private static final Map<String, GameData> SESSION_GAMES = new ConcurrentHashMap<>();

	/**
	 * Clear all games in the lobby
	 */
	public static void clear() {
		SESSION_GAMES.clear();
	}

	/**
	 * List all available games in the lobby
	 *
	 * @return listIds of games
	 */
	public static Collection<LobbyGame> listGames() {
		return SESSION_GAMES.values().stream().map(LobbyGame::new).collect(Collectors.toList());
	}

	/**
	 * Create a game
	 *
	 * @param playerId player id
	 * @param config   game config
	 */
	public static GameData createGame(Integer playerId, String gameId, Config config) {

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
	public static GameData joinGame(Integer playerId, String gameId) {

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
	public static GameData leaveGame(Integer playerId, String gameId) {
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
	public static GameData startGame(Integer playerId, String gameId) {
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
