package org.beesden.risk.client.model.lobby;

import org.beesden.risk.game.exception.GameLobbyException;
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
	 * @return list of player games
	 */
	public static Collection<LobbyGame> listGames() {
		return SESSION_GAMES.values().stream().map(LobbyGame::new).collect(Collectors.toList());
	}

	/**
	 * Create a game
	 *
	 * @param playerId player id
	 * @param gameName game name
	 * @param config   game config
	 */
	public static GameData createGame(int playerId, String gameName, Config config) {

		GameData gameData = new GameData(playerId, gameName, config);
		SESSION_GAMES.put(gameData.getId(), gameData);

		return gameData;
	}

	/**
	 * Join a game
	 *
	 * @param playerId player id
	 * @param gameId   game id
	 */
	public static GameData joinGame(int playerId, String gameId) {

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
	 * Leave a game
	 *
	 * @param playerId player
	 */
	public static GameData leaveGame(int playerId, String gameId) {
		GameData gameData = SESSION_GAMES.get(gameId);

		// Remove player if game not yet started
		if (gameData != null) {
			gameData.getPlayers().remove(playerId, gameData.getState() != GameData.GameState.SETUP);

			// Close the game if no playerIds remain
			if (gameData.getPlayers().getOwner() == -1) {
				SESSION_GAMES.remove(gameData.getId());
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
	public static GameData startGame(int playerId, String gameId) {
		GameData gameData = SESSION_GAMES.get(gameId);

		if (gameData == null) {
			throw new GameLobbyException("Game cannot be found", playerId, gameId);
		} else if (playerId != gameData.getPlayers().getOwner()) {
			throw new GameLobbyException("PlayerSummary does not own game", playerId, gameId);
		} else if (gameData.getPlayers().countActivePlayers() < gameData.getConfig().getMinPlayers()) {
			throw new GameLobbyException("Insufficient playerIds to start game", playerId, gameId);
		} else if (gameData.getState() != GameData.GameState.SETUP) {
			throw new GameLobbyException("The game has already started", playerId, gameId);
		}

		gameData.startGame();
		return gameData;
	}
}
