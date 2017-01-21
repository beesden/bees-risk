package org.beesden.risk.client.Model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import org.beesden.risk.game.model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LobbyPlayer {

	private static Map<Integer, LobbyPlayer> players = new ConcurrentHashMap<>();

	private int playerId;
	@Setter(AccessLevel.PUBLIC)
	private String username;
	private List<GameData> games = new ArrayList<>();
	private WebSocketSession session;

	/**
	 * Add a player into the lobby.
	 *
	 * @param session session
	 */
	public static void connectPlayer(WebSocketSession session) {
		LobbyPlayer player = new LobbyPlayer();
		player.setPlayerId(Integer.valueOf(session.getId()));
		player.setUsername("Guest " + session.getId());
		player.setSession(session);
		players.put(Integer.valueOf(session.getId()), player);
	}

	/**
	 * Add a game to a player
	 *
	 * @param playerId player id
	 * @param game     game data
	 */
	public static void joinGame(Integer playerId, GameData game) {
		players.get(playerId).getGames().add(game);
	}

	/**
	 * Leave a game
	 *
	 * @param playerId player id
	 * @param gameId   game id
	 */
	public static void leaveGame(Integer playerId, String gameId) {
		players.get(playerId).getGames().removeIf(game -> game.getName().equals(gameId));
	}

	/**
	 * Get a player with a session
	 *
	 * @param session session
	 * @return lobby player
	 */
	public static LobbyPlayer lookup(WebSocketSession session) {
		return players.get(Integer.valueOf(session.getId()));
	}

	/**
	 * Get a player with a player id
	 *
	 * @param playerId player id
	 * @return lobby player
	 */
	public static LobbyPlayer lookup(Integer playerId) {
		return players.get(playerId);
	}

}