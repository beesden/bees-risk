package org.beesden.risk.game.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GamePlayers {

	private static final List<String> COLOURS = Arrays.asList("#33c", "#3c3", "#c33", "#c3c", "#3cc", "#cc3");

	@Getter
	private int owner;
	@Getter
	private List<Player> playerList;
	@Getter
	private int currentTurn = -1;

	/**
	 * Default constructor.
	 *
	 * @param playerId owner player id
	 */
	public GamePlayers(int playerId) {
		playerList = new ArrayList<>();
		add(playerId);
		owner = playerId;
	}

	/**
	 * Add player
	 */
	public Player add(int playerId) {
		Player player = getByPlayerId(playerId);
		if (player == null) {
			player = new Player(playerId);
			playerList.add(player);
		}
		changeColour(playerId);
		return player;
	}

	/**
	 * Update colour
	 */
	public void changeColour(int playerId) {
		List<String> usedColours = playerList.stream().map(Player::getColour).collect(Collectors.toList());
		getByPlayerId(playerId).setColour(COLOURS.stream()
			.filter(colour -> !usedColours.contains(colour))
			.findFirst()
			.orElse(null));
	}

	/**
	 * Add neutral player
	 */
	public Player addNeutralPlayer() {
		Player player = new Player();
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
	public Player getByPlayerId(int playerId) {
		return playerList.stream().filter(p -> playerId == p.getPlayerId()).findFirst().orElse(null);
	}

	/**
	 * List all playerList
	 */
	public List<Integer> listActivePlayerIds() {
		return playerList.stream().filter(p -> !p.isNeutral()).map(Player::getPlayerId).collect(Collectors.toList());
	}

	/**
	 * Increment current player
	 */
	public Player nextPlayer() {

		Player currentPlayer = getByPlayerId(currentTurn);
		List<Player> activePlayers = getActivePlayers();

		int playerIndex = activePlayers.indexOf(currentPlayer);
		if (playerIndex == -1 || ++playerIndex == activePlayers.size()) {
			playerIndex = 0;
		}

		currentPlayer = activePlayers.get(playerIndex);
		currentTurn = currentPlayer.getPlayerId();

		return currentPlayer;
	}

	/**
	 * Remove player
	 */
	public void remove(int playerId, boolean gameStarted) {

		// Remove or set inactive if game already started
		if (gameStarted) {
			playerList.stream().filter(player -> playerId == player.getPlayerId()).forEach(player -> {
				player.setNeutral(true);
				player.setSpectating(true);
			});
		} else {
			playerList.removeIf(player -> playerId == player.getPlayerId());
		}

		// Update game owner
		if (playerId == owner) {
			owner = -1;
			playerList.stream()
				.filter(p -> !p.isNeutral())
				.findFirst()
				.ifPresent(player1 -> owner = player1.getPlayerId());
		}
	}
}
