package org.beesden.risk.service;

import org.beesden.risk.Exception.GameLobbyException;
import org.beesden.risk.model.GameConfig;
import org.beesden.risk.model.GameData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameLobbyTest {

	private static final String PLAYER_1 = "PLAYER_1";
	private static final String PLAYER_2 = "PLAYER_2";
	private static final String GAME_NAME = "GAME";
	private GameLobby lobby;

	@Before
	public void setup() {
		lobby = new GameLobby();
	}

	@Test
	public void testCreateGame() {

		// Player 1 can create a game
		GameData gameData = lobby.createGame(PLAYER_1, GAME_NAME, new GameConfig());
		Assert.assertEquals(PLAYER_1, gameData.getPlayers().getOwner());
		Assert.assertEquals(1L, gameData.getPlayers().countActivePlayers());
		Assert.assertEquals(GameData.GameState.SETUP, gameData.getState());
		Assert.assertNotNull(gameData.getMap());

		// Can't create games with the same name
		try {
			lobby.createGame(PLAYER_2, GAME_NAME, new GameConfig());
			Assert.fail("Should not be possible to create games with the same name");
		} catch (GameLobbyException e) {
			// Yay
		}

		// Can't create a game if already playing
		try {
			lobby.createGame(PLAYER_1, "game2", new GameConfig());
			Assert.fail("Should not be possible to create games if already in another");
		} catch (GameLobbyException e) {
			// Yay
		}
	}

	@Test
	public void testToString() {
		lobby.toString();
	}

	@Test
	public void testJoiningGame() {

		String invalidGame = "UNKNOWN_GAME";

		// Player 1 can't join a non-existent game
		try {
			lobby.joinGame(PLAYER_1, invalidGame);
			Assert.fail("Should not be possible to join a non-existent game");
		} catch (GameLobbyException e) {
			// Yay
		}

		GameData gameData = lobby.createGame(PLAYER_1, GAME_NAME, new GameConfig());

		// Player 1 can't join another game
		try {
			lobby.createGame("player0", invalidGame, new GameConfig());
			lobby.joinGame(PLAYER_1, "game2");
			Assert.fail("Should not be possible to join a new game while in another");
		} catch (GameLobbyException e) {
			// Yay
		}

		// Player 2 can join
		lobby.joinGame(PLAYER_2, gameData.getName());
		Assert.assertEquals(PLAYER_1, gameData.getPlayers().getOwner());
		Assert.assertEquals(2, gameData.getPlayers().countActivePlayers());

		// Player 1 can leave
		lobby.leaveGame(PLAYER_1);
		Assert.assertEquals(PLAYER_2, gameData.getPlayers().getOwner());
		Assert.assertEquals(1, gameData.getPlayers().countActivePlayers());

		// Player 1 can rejoin
		lobby.joinGame(PLAYER_1, gameData.getName());
		Assert.assertEquals(PLAYER_2, gameData.getPlayers().getOwner());
		Assert.assertEquals(2, gameData.getPlayers().countActivePlayers());

		// 4 More players can join
		for (int i = 3; i <= gameData.getConfig().getMaxPlayers(); i++) {
			lobby.joinGame("player" + i, gameData.getName());
		}
		Assert.assertEquals(6, gameData.getPlayers().countActivePlayers());

		// 7 Players is too many
		try {
			lobby.joinGame("player" + (gameData.getConfig().getMaxPlayers() + 1), GAME_NAME);
			Assert.fail("Player countActivePlayers should not be able to go above the limit");
		} catch (GameLobbyException e) {
			// Yay
		}
	}

	@Test
	public void testStartGame() {
		String game = "game";

		GameData gameData = lobby.createGame(PLAYER_1, game, new GameConfig());

		// Can't start game with insufficient players
		try {
			lobby.startGame(PLAYER_1, game);
			Assert.fail("Should not be able to start game with insufficient players");
		} catch (GameLobbyException e) {
			// Yay
		}

		lobby.joinGame(PLAYER_2, game);

		// Player 2 can't start the game
		try {
			lobby.startGame(PLAYER_2, game);
			Assert.fail("Player countActivePlayers should not be able to start the game if not the gameOwner");
		} catch (GameLobbyException e) {
			// Yay
		}

		// Now we can start!
		lobby.startGame(PLAYER_1, game);
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());

		// Can't start again
		try {
			lobby.startGame(PLAYER_1, game);
			Assert.fail("Game should not be restartable");
		} catch (GameLobbyException e) {
			// Yay
		}

	}

	@Test
	public void testGameList() {

		// Game lobby empty
		Assert.assertEquals(0, lobby.listGames().size());

		// Can create a game in the lobby
		lobby.createGame(PLAYER_1, GAME_NAME, new GameConfig());
		Assert.assertEquals(1, lobby.listGames().size());

		// Can create a second game
		lobby.createGame(PLAYER_2, "game2", new GameConfig());
		Assert.assertEquals(2, lobby.listGames().size());

	}

}
