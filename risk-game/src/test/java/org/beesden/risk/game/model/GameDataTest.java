package org.beesden.risk.game.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameDataTest {

	private static final Integer PLAYER_1 = 1001;
	private static final Integer PLAYER_2 = 1002;
	private static final Integer PLAYER_3 = 1003;
	private static final Integer PLAYER_4 = 1004;
	private static final String GAME_NAME = "GAME";
	private GameData gameData;

	@Before
	public void setup() {
		gameData = new GameData(PLAYER_1, GAME_NAME, new Config());
	}

	@Test
	public void testCreateData() {
		Assert.assertEquals(GAME_NAME, gameData.getName());
		Assert.assertEquals("risk", gameData.getMap().getName());

		Assert.assertNotNull(gameData.getConfig());
		Assert.assertNotNull(gameData.getCards());

		Assert.assertNotNull(gameData.getPlayers());
		Assert.assertEquals(PLAYER_1, gameData.getPlayers().getOwner());

		Assert.assertEquals(GameData.GameState.SETUP, gameData.getState());
		Assert.assertNull(gameData.getPhase());
	}

	@Test
	public void testStartGame() {
		gameData.startGame();
		Assert.assertEquals(GameData.GameState.SETUP, gameData.getState());

		gameData.getPlayers().add(PLAYER_2);
		gameData.startGame();
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());
	}

	@Test
	public void testNeutralPlayer() {

		gameData.getPlayers().add(PLAYER_2);
		gameData.startGame();
		Assert.assertEquals(2, gameData.getPlayers().countActivePlayers());
		Assert.assertEquals(3, gameData.getPlayers().getPlayerList().size());

		gameData = new GameData(PLAYER_1, GAME_NAME, new Config());
		gameData.getPlayers().add(PLAYER_2);
		gameData.getPlayers().add(PLAYER_3);
		Assert.assertEquals(3, gameData.getPlayers().countActivePlayers());
		Assert.assertEquals(3, gameData.getPlayers().getPlayerList().size());
	}

	@Test
	public void testAutoAssignTerritories() {
		Config config = new Config();
		config.setAutoAssignTerritories(true);

		gameData = new GameData(PLAYER_1, GAME_NAME, config);
		gameData.getPlayers().add(PLAYER_2);
		gameData.startGame();
		Assert.assertEquals(14, gameData.getPlayers().getByPlayerId(PLAYER_1).getTerritories().size());
		Assert.assertEquals(14, gameData.getPlayers().getByPlayerId(PLAYER_2).getTerritories().size());
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());

		gameData = new GameData(PLAYER_1, GAME_NAME, config);
		gameData.getPlayers().add(PLAYER_2);
		gameData.getPlayers().add(PLAYER_3);
		gameData.getPlayers().add(PLAYER_4);
		gameData.startGame();
		Assert.assertEquals(11, gameData.getPlayers().getByPlayerId(PLAYER_1).getTerritories().size());
		Assert.assertEquals(11, gameData.getPlayers().getByPlayerId(PLAYER_2).getTerritories().size());
		Assert.assertEquals(10, gameData.getPlayers().getByPlayerId(PLAYER_3).getTerritories().size());
		Assert.assertEquals(10, gameData.getPlayers().getByPlayerId(PLAYER_4).getTerritories().size());
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());
	}

	@Test
	public void testAutoAssignBattalions() {
		Config config = new Config();
		config.setAutoAssignTerritories(false);
		config.setAutoPlaceBattalions(true);

		// Don't assign battalions if no assigned territories
		gameData = new GameData(PLAYER_1, GAME_NAME, config);
		gameData.getPlayers().add(PLAYER_2);
		gameData.startGame();
		Assert.assertEquals(0, gameData.getPlayers().getByPlayerId(PLAYER_1).getTerritories().size());
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());
		Assert.assertNull(gameData.getPhase());

		config.setAutoAssignTerritories(true);
		config.setAutoPlaceBattalions(true);

		// Auto assign battalions
		gameData = new GameData(PLAYER_1, GAME_NAME, config);
		gameData.getPlayers().add(PLAYER_2);
		gameData.startGame();
		Assert.assertEquals(14, gameData.getPlayers().getByPlayerId(PLAYER_1).getTerritories().size());
		Assert.assertEquals(40, gameData.getPlayers().getByPlayerId(PLAYER_1).getStrength());
		Assert.assertEquals(GameData.GameState.STARTED, gameData.getState());
		Assert.assertEquals(GameData.TurnPhase.REINFORCE, gameData.getPhase());
	}

	@Test
	public void testNextTurn() {
		Config config = new Config();
		config.setAutoAssignTerritories(true);
		config.setAutoPlaceBattalions(true);

		// Don't assign battalions if no assigned territories
		gameData = new GameData(PLAYER_1, GAME_NAME, config);
		gameData.getPlayers().add(PLAYER_2);
		gameData.startGame();

		Assert.assertEquals(PLAYER_1, gameData.getPlayers().getCurrentTurn());
		Assert.assertNotEquals(0, gameData.getPlayers().getByPlayerId(PLAYER_1).getReinforcements());

		gameData.startTurn();
		Assert.assertEquals(gameData.getPlayers().getCurrentTurn(), PLAYER_2);
		gameData.startTurn();
		Assert.assertEquals(gameData.getPlayers().getCurrentTurn(), PLAYER_1);
		gameData.startTurn();
		Assert.assertEquals(gameData.getPlayers().getCurrentTurn(), PLAYER_2);
	}

}
