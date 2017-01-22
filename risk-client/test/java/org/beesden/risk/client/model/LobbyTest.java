package org.beesden.risk.client.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.beesden.risk.client.model.lobby.Lobby;
import org.beesden.risk.game.exception.GameLobbyException;
import org.beesden.risk.game.model.Config;
import org.beesden.risk.game.model.GameData;

public class LobbyTest {

	private static final int PLAYER_1 = 1001;
	private static final int PLAYER_2 = 1002;
	private static final String GAME_NAME = "GAME";

	@Before
	public void setup() {
		Lobby.clear();
	}

	@Test
	public void testCreateGame() {

		// PlayerSummary 1 can create a game
		GameData gameData = Lobby.createGame(PLAYER_1, GAME_NAME, new Config());
		Assert.assertEquals(PLAYER_1, gameData.getPlayers().getOwner());
		Assert.assertEquals(1L, gameData.getPlayers().countActivePlayers());
		Assert.assertEquals(GameData.GameState.SETUP, gameData.getState());
		Assert.assertNotNull(gameData.getMap());

		// Can create games with the same name (these are separated by ID)
		Lobby.createGame(PLAYER_2, GAME_NAME, new Config());
	}

	@Test
	public void testJoiningGame() {

		// PlayerSummary 1 can't join a non-existent game
		try {
			Lobby.joinGame(PLAYER_1, "Unknown Game");
			Assert.fail("Should not be possible to join a non-existent game");
		} catch (GameLobbyException e) {
			// Yay
		}

		GameData gameData = Lobby.createGame(PLAYER_1, GAME_NAME, new Config());

		// PlayerSummary 1 can join another game
		String gameId = Lobby.createGame(0, "Another Game", new Config()).getId();
		Lobby.joinGame(PLAYER_1, gameId);

		// PlayerSummary 2 can join
		Lobby.joinGame(PLAYER_2, gameData.getId());
		Assert.assertEquals(PLAYER_1, gameData.getPlayers().getOwner());
		Assert.assertEquals(2, gameData.getPlayers().countActivePlayers());

		// PlayerSummary 1 can leave
		Lobby.leaveGame(PLAYER_1, gameData.getId());
		Assert.assertEquals(PLAYER_2, gameData.getPlayers().getOwner());
		Assert.assertEquals(1, gameData.getPlayers().countActivePlayers());

		// PlayerSummary 1 can rejoin
		Lobby.joinGame(PLAYER_1, gameData.getId());
		Assert.assertEquals(PLAYER_2, gameData.getPlayers().getOwner());
		Assert.assertEquals(2, gameData.getPlayers().countActivePlayers());

		// 4 More playerIds can join
		for (int i = 3; i <= gameData.getConfig().getMaxPlayers(); i++) {
			Lobby.joinGame(1000 + i, gameData.getId());
		}
		Assert.assertEquals(6, gameData.getPlayers().countActivePlayers());

		// 7 Players is too many
		try {
			Lobby.joinGame(gameData.getConfig().getMaxPlayers() + 1, gameData.getId());
			Assert.fail("PlayerSummary countActivePlayers should not be able to go above the limit");
		} catch (GameLobbyException e) {
			// Yay
		}
	}

	@Test
	public void testStartGame() {

		GameData gameData = Lobby.createGame(PLAYER_1, "Start Game", new Config());
		String gameId = gameData.getId();

		// Can't start game with insufficient playerIds
		try {
			Lobby.startGame(PLAYER_1, gameId);
			Assert.fail("Should not be able to start game with insufficient playerIds");
		} catch (GameLobbyException e) {
			// Yay
		}

		Lobby.joinGame(PLAYER_2, gameId);

		// PlayerSummary 2 can't start the game
		try {
			Lobby.startGame(PLAYER_2, gameId);
			Assert.fail("PlayerSummary 2 should not be able to start the game if not the gameOwner");
		} catch (GameLobbyException e) {
			// Yay
		}

		// Now we can start!
		Lobby.startGame(PLAYER_1, gameId);
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());

		// Can't start again
		try {
			Lobby.startGame(PLAYER_1, gameId);
			Assert.fail("Game should not be restartable");
		} catch (GameLobbyException e) {
			// Yay
		}

		// Leaving the game sets player to neutral / spectating
		Lobby.leaveGame(PLAYER_2, gameId);
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
