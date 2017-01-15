package org.beesden.risk.model;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class GamePlayers {

	@Getter
	private String owner;
	@Getter
	private List<Player> playerList;

	/**
	 * Default constructor.
	 *
	 * @param playerId owner player id
	 */
	public GamePlayers(String playerId) {
		playerList = new ArrayList<>();
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
			playerList.add(player);
		}
		return player;
	}

	/**
	 * Add neutral player
	 */
	public Player addNeutralPlayer() {
		Player player = new Player("Neutral Player");
		player.setNeutral(true);
		playerList.add(player);
		return player;
	}

	/**
	 * Count active playerList.
	 */
	public long countActivePlayers() {
		return playerList.stream().filter(p -> !p.isSpectating() && !p.isNeutral()).count();
	}

	/**
	 * Count active playerList.
	 */
	public List<Player> getActivePlayers() {
		return playerList.stream().filter(p -> !p.isSpectating() && !p.isNeutral()).collect(Collectors.toList());
	}

	/**
	 * Get a player
	 */
	public Player get(String playerId) {
		return playerList.stream().filter(p -> playerId.equals(p.getPlayerId())).findFirst().orElse(null);
	}

	/**
	 * List all playerList
	 */
	public List<String> listIds() {
		return playerList.stream().map(Player::getPlayerId).collect(Collectors.toList());
	}

	/**
	 * Remove player
	 */
	public void remove(String playerId, boolean gameStarted) {

		// Remove or set inactive if game already started
		if (gameStarted) {
			playerList.stream().filter(player -> playerId.equals(player.getPlayerId())).forEach(player -> {
				player.setNeutral(true);
				player.setSpectating(true);
			});
		} else {
			playerList.removeIf(player -> playerId.equals(player.getPlayerId()));
		}

		// Update game owner
		if (playerId.equals(owner)) {
			owner = null;
			playerList.stream()
					.filter(p -> !p.isNeutral())
					.findFirst()
					.ifPresent(player1 -> owner = player1.getPlayerId());

		}
	}
}
