package org.beesden.risk.model;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class GamePlayers {

	@Getter
	private String owner;
	@Getter
	private List<Player> players;

	/**
	 * Default constructor.
	 *
	 * @param playerId owner player id
	 */
	public GamePlayers(String playerId) {
		players = new ArrayList<>();
		add(playerId);
		owner = playerId;
	}

	/**
	 * Add player
	 */
	public Player add(String playerId) {
		Player player = get(playerId);
		if (player == null) {
			player = new Player(playerId);
			players.add(player);
		}
		return player;
	}

	/**
	 * Add neutral player
	 */
	public Player addNeutralPlayer() {
		Player player = new Player("Neutral Player");
		player.setNeutral(true);
		players.add(player);
		return player;
	}

	/**
	 * Count active players.
	 */
	public long countActivePlayers() {
		return players.stream().filter(p -> !p.isSpectating() && !p.isNeutral()).count();
	}

	/**
	 * Get a player
	 */
	public Player get(String playerId) {
		return players.stream().filter(p -> playerId.equals(p.getPlayerId())).findFirst().orElse(null);
	}

	/**
	 * List all players
	 */
	public List<String> listIds() {
		return players.stream().map(Player::getPlayerId).collect(Collectors.toList());
	}

	/**
	 * Remove player
	 */
	public void remove(String playerId, boolean gameStarted) {

		// Remove or set inactive if game already started
		if (gameStarted) {
			players.stream().filter(player -> playerId.equals(player.getPlayerId())).forEach(player -> {
				player.setNeutral(true);
				player.setSpectating(true);
			});
		} else {
			players.removeIf(player -> playerId.equals(player.getPlayerId()));
		}

		// Update game owner
		if (playerId.equals(owner)) {
			owner = null;
			players.stream()
					.filter(p -> !p.isNeutral())
					.findFirst()
					.ifPresent(player1 -> owner = player1.getPlayerId());

		}
	}
}
