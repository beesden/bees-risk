package org.beesden.risk.service;

import org.beesden.risk.Exception.GameLobbyException;
import org.beesden.risk.model.GameConfig;
import org.beesden.risk.model.GameData;
import org.junit.Assert;
import org.junit.Test;

public class GameLobbyTest {

	@Test
	public void testCreateGame() {

		GameLobby lobby = new GameLobby();
		String player1 = "player1";

		// Player 1 can create a game
		GameData gameData = lobby.createGame(player1, "game1", new GameConfig());
		Assert.assertEquals(player1, gameData.getOwner());
		Assert.assertEquals(1, gameData.getPlayers().size());
		Assert.assertEquals(GameData.GameState.SETUP, gameData.getState());
		Assert.assertNotNull(gameData.getMap());

		// Can't create games with the same name
		try {
			lobby.createGame("player2", "game1", new GameConfig());
			Assert.fail("Should not be possible to create games with the same name");
		} catch (GameLobbyException e) {
			// Yay
		}

		// Can't create a game if already playing
		try {
			lobby.createGame(player1, "game2", new GameConfig());
			Assert.fail("Should not be possible to create games if already in another");
		} catch (GameLobbyException e) {
			// Yay
		}

	}

	@Test
	public void testToString() {
		GameLobby gameLobby = new GameLobby();
		gameLobby.toString();
	}

	@Test
	public void testJoiningGame() {

		GameLobby lobby = new GameLobby();
		String player1 = "player1";
		String player2 = "player2";

		// Player 1 can't join a non-existent game
		try {
			lobby.joinGame(player1, "game2");
			Assert.fail("Should not be possible to join a non-existent game");
		} catch (GameLobbyException e) {
			// Yay
		}

		GameData gameData = lobby.createGame(player1, "game1", new GameConfig());

		// Player 1 can't join another game
		try {
			lobby.createGame("player0", "game2", new GameConfig());
			lobby.joinGame(player1, "game2");
			Assert.fail("Should not be possible to join a new game while in another");
		} catch (GameLobbyException e) {
			// Yay
		}

		// Player 2 can join
		lobby.joinGame(player2, gameData.getName());
		Assert.assertEquals(player1, gameData.getOwner());
		Assert.assertEquals(2, gameData.getPlayers().size());

		// Player 1 can leave
		lobby.leaveGame(player1);
		Assert.assertEquals(player2, gameData.getOwner());
		Assert.assertEquals(1, gameData.getPlayers().size());

		// Player 1 can rejoin
		lobby.joinGame(player1, gameData.getName());
		Assert.assertEquals(player2, gameData.getOwner());
		Assert.assertEquals(2, gameData.getPlayers().size());

		// 4 More players can join
		for (int i = 3; i <= gameData.getConfig().getMaxPlayers(); i++) {
			lobby.joinGame("player" + i, gameData.getName());
		}
		Assert.assertEquals(6, gameData.getPlayers().size());

		// 7 Players is too many
		try {
			lobby.joinGame("player" + (gameData.getConfig().getMaxPlayers() + 1), "game1");
			Assert.fail("Player count should not be able to go above the limit");
		} catch (GameLobbyException e) {
			// Yay
		}
	}

	@Test
	public void testStartGame() {

		GameLobby lobby = new GameLobby();
		String player1 = "player1";
		String player2 = "player2";
		String game = "game";

		GameData gameData = lobby.createGame(player1, game, new GameConfig());

		// Player 2 can't start the game
		try {
			lobby.startGame(player1, game);
			Assert.fail("Should not be able to start game with insufficient players");
		} catch (GameLobbyException e) {
			// Yay
		}

		lobby.joinGame(player2, game);

		// Player 2 can't start the game
		try {
			lobby.startGame(player2, game);
			Assert.fail("Player count should not be able to start the game if not the owner");
		} catch (GameLobbyException e) {
			// Yay
		}

		// Now we can start!
		lobby.startGame(player1, game);
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());

		// Can't start again
		try {
			lobby.startGame(player1, game);
			Assert.fail("Game should not be restartable");
		} catch (GameLobbyException e) {
			// Yay
		}

	}

	@Test
	public void testGameList() {

		GameLobby lobby = new GameLobby();

		// Game lobby empty
		Assert.assertEquals(0, lobby.listGames().size());

		// Can create a game in the lobby
		lobby.createGame("player1", "game1", new GameConfig());
		Assert.assertEquals(1, lobby.listGames().size());

		// Can create a second game
		lobby.createGame("player2", "game2", new GameConfig());
		Assert.assertEquals(2, lobby.listGames().size());

	}

}
