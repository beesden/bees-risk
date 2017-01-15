package org.beesden.risk.model;

import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class GamePlayers {

	@Getter
	private String owner;
	private List<Player> players;

	@Data
	public class Player {
		private String playerId;
		private String colour;
		private boolean isNeutral;
		private boolean isSpectating;
		private int reinforcements;

		public Player(String playerId) {
			this.playerId = playerId;
		}
	}

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
		Player player = new Player(playerId);
		players.add(player);
		return player;
	}

	/**
	 * Count active players
	 */
	public long count() {
		return players.stream().filter(p -> !p.isSpectating() && !p.isNeutral()).count();
	}

	/**
	 * List all players
	 */
	public List<String> list() {
		return players.stream().map(Player::getPlayerId).collect(Collectors.toList());
	}

	/**
	 * Remove player
	 */
	public void remove(String playerId, boolean gameStarted) {

		// Remove or set inactive if game already started
		if (gameStarted) {
			players.stream()
					.filter(player -> playerId.equals(player.getPlayerId()))
					.forEach(player -> player.setNeutral(true));
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
