package org.beesden.risk.service;

import org.beesden.risk.Exception.GameLobbyException;
import org.beesden.risk.model.GameConfig;
import org.beesden.risk.model.GameData;
import org.beesden.risk.model.GamePlayer;
import org.beesden.risk.model.LobbyGame;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameLobby {

	private final ConcurrentHashMap<String, GamePlayer> ACTIVE_PLAYERS = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, GameData> SESSION_GAMES = new ConcurrentHashMap<>();

	/**
	 * List all available games in the lobby
	 *
	 * @return list of games
	 */
	public List<LobbyGame> listGames() {
		return SESSION_GAMES.values().stream().map(LobbyGame::new).collect(Collectors.toList());
	}

	/**
	 * Create a game
	 *
	 * @param playerId player id
	 * @param config   game config
	 */
	public GameData createGame(String playerId, String gameId, GameConfig config) {

		if (SESSION_GAMES.get(gameId) != null) {
			throw new GameLobbyException("A game with that name already exists", playerId, gameId);
		} else if (ACTIVE_PLAYERS.get(playerId) != null) {
			throw new GameLobbyException("Cannot create a game if already in one", playerId, gameId);
		}

		GameData gameData = new GameData(playerId, gameId, config);
		SESSION_GAMES.put(gameId, gameData);

		return joinGame(playerId, gameId);
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
		if (gameData.getPlayers().size() >= gameData.getConfig().getMaxPlayers()) {
			throw new GameLobbyException("No more space in the game", playerId, gameId);
		} else if (gameData.getState() != GameData.GameState.SETUP) {
			throw new GameLobbyException("The game has already started", playerId, gameId);
		}

		GamePlayer player = new GamePlayer(playerId);
		player.setCurrentGame(gameId);
		gameData.getPlayers().add(new GamePlayer(playerId));
		ACTIVE_PLAYERS.put(playerId, player);

		return gameData;
	}

	/**
	 * Start a game
	 *
	 * @param playerId player
	 */
	public void leaveGame(String playerId) {
		GamePlayer player = ACTIVE_PLAYERS.get(playerId);
		if (player == null) {
			throw new GameLobbyException("GamePlayer does not exist", playerId, null);
		}

		GameData gameData = SESSION_GAMES.get(player.getCurrentGame());
		if (gameData == null) {
			throw new GameLobbyException("Game cannot be found", playerId, player.getCurrentGame());
		}

		// Remove player if game not yet started
		if (gameData.getState() != GameData.GameState.SETUP) {
			gameData.getPlayers().stream().filter(player::equals).forEach(gamePlayer -> player.setSpectating(true));
		}
		// Set player as inactive if game has started
		else {
			gameData.getPlayers().removeIf(gamePlayer -> playerId.equals(gamePlayer.getPlayerId()));
		}
		ACTIVE_PLAYERS.remove(playerId);

		List<GamePlayer> activePlayers = gameData.getPlayers()
				.stream()
				.filter(p -> !p.isSpectating())
				.collect(Collectors.toList());

		// Close the game if no players remain
		if (activePlayers.size() == 0) {
			SESSION_GAMES.remove(gameData.getName());
		} else {
			gameData.setOwner(activePlayers.get(0).getPlayerId());
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
		} else if (!playerId.equals(gameData.getOwner())) {
			throw new GameLobbyException("Player does not own game", playerId, gameId);
		} else if (gameData.getPlayers().size() < gameData.getConfig().getMinPlayers()) {
			throw new GameLobbyException("Insufficient players to start game", playerId, gameId);
		} else if (gameData.getState() != GameData.GameState.SETUP) {
			throw new GameLobbyException("The game has already started", playerId, gameId);
		}

		gameData.setState(GameData.GameState.READY);

		return gameData;
	}

}
