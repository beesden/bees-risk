package org.beesden.risk.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameDataTest {

	private static final String PLAYER_1 = "PLAYER_1";
	private static final String PLAYER_2 = "PLAYER_2";
	private static final String PLAYER_3 = "PLAYER_3";
	private static final String PLAYER_4 = "PLAYER_4";
	private static final String GAME_NAME = "GAME";
	private GameData gameData;

	@Before
	public void setup() {
		gameData = new GameData(PLAYER_1, GAME_NAME, new GameConfig());
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
		Assert.assertEquals(3, gameData.getPlayers().getPlayers().size());

		gameData = new GameData(PLAYER_1, GAME_NAME, new GameConfig());
		gameData.getPlayers().add(PLAYER_2);
		gameData.getPlayers().add(PLAYER_3);
		Assert.assertEquals(3, gameData.getPlayers().countActivePlayers());
		Assert.assertEquals(3, gameData.getPlayers().getPlayers().size());
	}

	@Test
	public void testAutoAssignTerritories() {
		GameConfig config = new GameConfig();
		config.setAutoAssignTerritories(true);

		gameData = new GameData(PLAYER_1, GAME_NAME, config);
		gameData.getPlayers().add(PLAYER_2);
		gameData.startGame();
		Assert.assertEquals(14, gameData.getPlayers().get(PLAYER_1).getTerritories().size());
		Assert.assertEquals(14, gameData.getPlayers().get(PLAYER_2).getTerritories().size());
		Assert.assertEquals(14, gameData.getPlayers().get("Neutral Player").getTerritories().size());
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());

		gameData = new GameData(PLAYER_1, GAME_NAME, config);
		gameData.getPlayers().add(PLAYER_2);
		gameData.getPlayers().add(PLAYER_3);
		gameData.getPlayers().add(PLAYER_4);
		gameData.startGame();
		Assert.assertEquals(11, gameData.getPlayers().get(PLAYER_1).getTerritories().size());
		Assert.assertEquals(11, gameData.getPlayers().get(PLAYER_2).getTerritories().size());
		Assert.assertEquals(10, gameData.getPlayers().get(PLAYER_3).getTerritories().size());
		Assert.assertEquals(10, gameData.getPlayers().get(PLAYER_4).getTerritories().size());
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());
	}

	@Test
	public void testAutoAssignBattalions() {
		GameConfig config = new GameConfig();
		config.setAutoAssignTerritories(false);
		config.setAutoPlaceBattalions(true);

		// Don't assign battalions if no assigned territories
		gameData = new GameData(PLAYER_1, GAME_NAME, config);
		gameData.getPlayers().add(PLAYER_2);
		gameData.startGame();
		Assert.assertEquals(0, gameData.getPlayers().get(PLAYER_1).getTerritories().size());
		Assert.assertEquals(GameData.GameState.READY, gameData.getState());

		config.setAutoAssignTerritories(true);
		config.setAutoPlaceBattalions(true);

		// Auto assign battalions
		gameData = new GameData(PLAYER_1, GAME_NAME, config);
		gameData.getPlayers().add(PLAYER_2);
		gameData.startGame();
		Assert.assertEquals(14, gameData.getPlayers().get(PLAYER_1).getTerritories().size());
		Assert.assertEquals(40, gameData.getPlayers().get(PLAYER_1).getStrength());
		Assert.assertEquals(GameData.GameState.STARTED, gameData.getState());
	}

}
