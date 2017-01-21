package org.beesden.risk.client.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.beesden.risk.client.Model.Lobby;
import org.beesden.risk.game.Exception.GameLobbyException;
import org.beesden.risk.game.model.Config;
import org.beesden.risk.game.model.GameData;

public class LobbyTest {

	private static final Integer PLAYER_1 = 1001;
	private static final Integer PLAYER_2 = 1002;
	private static final String GAME_NAME = "GAME";

	@Before
	public void setup() {
		Lobby.clear();
	}

	@Test
	public void testCreateGame() {

		// Player 1 can create a game
		GameData gameData = Lobby.createGame(PLAYER_1, GAME_NAME, new Config());
		Assert.assertEquals(PLAYER_1, gameData.getPlayers().getOwner());
		Assert.assertEquals(1L, gameData.getPlayers().countActivePlayers());
		Assert.assertEquals(GameData.GameState.SETUP, gameData.getState());
		Assert.assertNotNull(gameData.getMap());

		// Can't create games with the same name
		try {
			Lobby.createGame(PLAYER_2, GAME_NAME, new Config());
			Assert.fail("Should not be possible to create games with the same name");
		} catch (GameLobbyException e) {
			// Yay
		}
	}

	@Test
	public void testJoiningGame() {

		// Player 1 can't join a non-existent game
		try {
			Lobby.joinGame(PLAYER_1, "Unknown Game");
			Assert.fail("Should not be possible to join a non-existent game");
		} catch (GameLobbyException e) {
			// Yay
		}

		GameData gameData = Lobby.createGame(PLAYER_1, GAME_NAME, new Config());

		// Player 1 can join another game
		Lobby.createGame(0, "Another Game", new Config());
		Lobby.joinGame(PLAYER_1, "Another Game");

		// Player 2 can join
		Lobby.joinGame(PLAYER_2, gameData.getName());
		Assert.assertEquals(PLAYER_1, gameData.getPlayers().getOwner());
		Assert.assertEquals(2, gameData.getPlayers().countActivePlayers());

		// Player 1 can leave
		Lobby.leaveGame(PLAYER_1, GAME_NAME);
		Assert.assertEquals(PLAYER_2, gameData.getPlayers().getOwner());
		Assert.assertEquals(1, gameData.getPlayers().countActivePlayers());

		// Player 1 can rejoin
		Lobby.joinGame(PLAYER_1, gameData.getName());
		Assert.assertEquals(PLAYER_2, gameData.getPlayers().getOwner());
		Assert.assertEquals(2, gameData.getPlayers().countActivePlayers());

		// 4 More playerIds can join
		for (int i = 3; i <= gameData.getConfig().getMaxPlayers(); i++) {
			Lobby.joinGame(1000 + i, gameData.getName());
		}
		Assert.assertEquals(6, gameData.getPlayers().countActivePlayers());

		// 7 Players is too many
		try {
			Lobby.joinGame(gameData.getConfig().getMaxPlayers() + 1, GAME_NAME);
			Assert.fail("Player countActivePlayers should not be able to go above the limit");
		} catch (GameLobbyException e) {
			// Yay
		}
	}

	@Test
	public void testStartGame() {
		String game = "game";

		GameData gameData = Lobby.createGame(PLAYER_1, game, new Config());

		// Can't start game with insufficient playerIds
		try {
			Lobby.startGame(PLAYER_1, game);
			Assert.fail("Should not be able to start game with insufficient playerIds");
		} catch (GameLobbyException e) {
			// Yay
		}

		Lobby.joinGame(PLAYER_2, game);

		// Player 2 can't start the game
		try {
			Lobby.startGame(PLAYER_2, game);
			Assert.fail("Player 2 should not be able to start the game if not the gameOwner");
		} catch (GameLobbyException e) {
			// Yay
		}

		// Now we can start!
		Lobby.startGame(PLAYER_1, game);
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());

		// Can't start again
		try {
			Lobby.startGame(PLAYER_1, game);
			Assert.fail("Game should not be restartable");
		} catch (GameLobbyException e) {
			// Yay
		}

		// Leaving the game sets player to neutral / spectating
		Lobby.leaveGame(PLAYER_2, game);
		Assert.assertTrue(gameData.getPlayers().getByPlayerId(PLAYER_2).isNeutral());
		Assert.assertTrue(gameData.getPlayers().getByPlayerId(PLAYER_2).isSpectating());

	}

	@Test
	public void testGameList() {

		// Game Lobby empty
		Assert.assertEquals(0, Lobby.listGames().size());

		// Can create a game in the Lobby
		Lobby.createGame(PLAYER_1, GAME_NAME, new Config());
		Assert.assertEquals(1, Lobby.listGames().size());

		// Can create a second game
		Lobby.createGame(PLAYER_2, "game2", new Config());
		Assert.assertEquals(2, Lobby.listGames().size());

	}

}
