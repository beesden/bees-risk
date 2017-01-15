package org.beesden.risk.model;

import lombok.Data;
import org.beesden.risk.Exception.GameLobbyException;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Lobby {

	private final ConcurrentHashMap<String, String> ACTIVE_PLAYERS = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, GameData> SESSION_GAMES = new ConcurrentHashMap<>();

	/**
	 * Get an immutable version of a game
	 */
	public GameData getGame(String playerId) {
		String gameId = ACTIVE_PLAYERS.get(playerId);
		if (gameId == null) {
			// Player not currently in a game
			return null;
		}

		GameData gameData = SESSION_GAMES.get(gameId);
		if (gameData == null) {
			ACTIVE_PLAYERS.remove(playerId);
			throw new GameLobbyException("Game cannot be found", playerId, gameId);
		}

		return gameData;
	}

	/**
	 * List all available games in the lobby
	 *
	 * @return listIds of games
	 */
	public List<Summary> listGames() {
		return SESSION_GAMES.values().stream().map(Summary::new).collect(Collectors.toList());
	}

	/**
	 * Create a game
	 *
	 * @param playerId player id
	 * @param config   game config
	 */
	public GameData createGame(String playerId, String gameId, Config config) {

		if (SESSION_GAMES.get(gameId) != null) {
			throw new GameLobbyException("A game with that name already exists", playerId, gameId);
		} else if (ACTIVE_PLAYERS.get(playerId) != null) {
			throw new GameLobbyException("Cannot create a game if already in one", playerId, gameId);
		}

		GameData gameData = new GameData(playerId, gameId, config);
		SESSION_GAMES.put(gameId, gameData);
		ACTIVE_PLAYERS.put(playerId, gameId);

		return gameData;
	}

	/**
	 * Join a game
	 *
	 * @param playerId player id
	 * @param gameId   game id
	 */
	public GameData joinGame(String playerId, String gameId) {

		if (ACTIVE_PLAYERS.get(playerId) != null) {
			throw new GameLobbyException("Cannot join multiple games", playerId, gameId);
		}

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
		ACTIVE_PLAYERS.put(playerId, gameId);

		return getGame(playerId);
	}

	/**
	 * Start a game
	 *
	 * @param playerId player
	 */
	public void leaveGame(String playerId) {
		GameData gameData = getGame(playerId);

		// Remove player if game not yet started
		gameData.getPlayers().remove(playerId, gameData.getState() != GameData.GameState.SETUP);
		ACTIVE_PLAYERS.remove(playerId);

		// Close the game if no players remain
		if (gameData.getPlayers().getOwner() == null) {
			SESSION_GAMES.remove(gameData.getName());
		}
	}

	/**
	 * Start a game
	 *
	 * @param playerId player
	 * @param gameId   game id
	 */
	public GameData startGame(String playerId, String gameId) {
		GameData gameData = SESSION_GAMES.get(gameId);

		if (gameData == null) {
			throw new GameLobbyException("Game cannot be found", playerId, gameId);
		} else if (!playerId.equals(gameData.getPlayers().getOwner())) {
			throw new GameLobbyException("Player does not own game", playerId, gameId);
		} else if (gameData.getPlayers().countActivePlayers() < gameData.getConfig().getMinPlayers()) {
			throw new GameLobbyException("Insufficient players to start game", playerId, gameId);
		} else if (gameData.getState() != GameData.GameState.SETUP) {
			throw new GameLobbyException("The game has already started", playerId, gameId);
		}

		gameData.startGame();
		return getGame(playerId);
	}

	@Data
	public static class Summary {

		private String gameName;
		private GameData.GameState state;
		private List<String> players;

		public Summary(GameData gameData) {
			this.gameName = gameData.getName();
			this.state = gameData.getState();
			this.players = gameData.getPlayers().listIds();
		}

	}
}
