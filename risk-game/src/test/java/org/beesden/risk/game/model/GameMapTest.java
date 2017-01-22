package org.beesden.risk.game.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GameMapTest {

	private static final int PLAYER_1 = 1001;
	private static final int PLAYER_2 = 1002;
	private static final int PLAYER_3 = 1003;
	private static final String GAME_NAME = "GAME";
	private GameMap gameMap;

	@Before
	public void setup() {
		buildGameMap(false, false);
	}

	private void buildGameMap(boolean placeTerritories, boolean placeBatallions) {
		Config config = new Config();
		config.setAutoAssignTerritories(placeTerritories);
		config.setAutoPlaceBattalions(placeBatallions);

		GameData gameData = new GameData(PLAYER_1, GAME_NAME, config);
		gameData.getPlayers().add(PLAYER_2);
		gameData.getPlayers().add(PLAYER_3);
		gameData.startGame();

		gameMap = gameData.getMap();
	}

	@Test
	public void testValidTerritories() {
		// Test initial phase (none claimed)
		Assert.assertEquals(42, gameMap.getValidTerritories(PLAYER_1, TurnPhase.INITIAL, null).size());
		Assert.assertEquals(42, gameMap.getValidTerritories(PLAYER_2, TurnPhase.INITIAL, null).size());

		// Test initial phase (with claimed)
		gameMap.getTerritories().get(0).setOwnerId(PLAYER_1);
		Assert.assertEquals(41, gameMap.getValidTerritories(PLAYER_1, TurnPhase.INITIAL, null).size());
		Assert.assertEquals(41, gameMap.getValidTerritories(PLAYER_2, TurnPhase.INITIAL, null).size());
		gameMap.getTerritories().get(1).setOwnerId(PLAYER_1);
		gameMap.getTerritories().get(2).setOwnerId(PLAYER_2);
		Assert.assertEquals(39, gameMap.getValidTerritories(PLAYER_1, TurnPhase.INITIAL, null).size());
		Assert.assertEquals(39, gameMap.getValidTerritories(PLAYER_2, TurnPhase.INITIAL, null).size());

		buildGameMap(true, false);
		Assert.assertEquals(0, gameMap.getValidTerritories(PLAYER_1, TurnPhase.INITIAL, null).size());

		// Test deploy phase
		Assert.assertEquals(14, gameMap.getValidTerritories(PLAYER_1, TurnPhase.DEPLOY, null).size());
		Assert.assertEquals(14, gameMap.getValidTerritories(PLAYER_2, TurnPhase.DEPLOY, null).size());
		Assert.assertEquals(14, gameMap.getValidTerritories(PLAYER_3, TurnPhase.DEPLOY, null).size());

		// All other phases
		buildGameMap(true, true);

		Assert.assertEquals(14, gameMap.getValidTerritories(PLAYER_1, TurnPhase.REINFORCE, null).size());
		Assert.assertEquals(14, gameMap.getValidTerritories(PLAYER_2, TurnPhase.REINFORCE, null).size());
		Assert.assertEquals(14, gameMap.getValidTerritories(PLAYER_3, TurnPhase.REINFORCE, null).size());

		// Get territory + neighbour
		GameMap.Territory player1Territory = gameMap.getValidTerritories(PLAYER_1, TurnPhase.DEPLOY, null).get(0);
		GameMap.Territory neighbour = player1Territory.getNeighbours()
			.stream()
			.filter(t -> t.getOwnerId() != PLAYER_1)
			.findFirst()
			.orElse(null);

		// Test attack phase
		Assert.assertEquals(14, gameMap.getValidTerritories(PLAYER_1, TurnPhase.ATTACK, null).size());
		Assert.assertTrue(gameMap.getValidTerritories(PLAYER_1, TurnPhase.ATTACK, null).contains(player1Territory));

		// Test attack phase (selected)
		Assert.assertFalse(gameMap.getValidTerritories(PLAYER_1, TurnPhase.ATTACK, player1Territory)
			.contains(player1Territory));
		if (neighbour != null) {
			Assert.assertTrue(gameMap.getValidTerritories(PLAYER_1, TurnPhase.ATTACK, player1Territory)
				.contains(neighbour));
		} else {
			Assert.assertEquals(0, gameMap.getValidTerritories(PLAYER_1, TurnPhase.ATTACK, player1Territory).size());
		}

		// Test redeploy phase (unselected)
		Assert.assertEquals(14, gameMap.getValidTerritories(PLAYER_1, TurnPhase.REDEPLOY, null).size());
		Assert.assertTrue(gameMap.getValidTerritories(PLAYER_1, TurnPhase.REDEPLOY, null).contains(player1Territory));

		// Test redeploy hpase (selected)
		Assert.assertFalse(gameMap.getValidTerritories(PLAYER_1, TurnPhase.REDEPLOY, player1Territory)
			.contains(player1Territory));
		if (neighbour == null) {
			Assert.assertEquals(0, gameMap.getValidTerritories(PLAYER_1, TurnPhase.REDEPLOY, player1Territory).size());
		} else {
			Assert.assertFalse(gameMap.getValidTerritories(PLAYER_1, TurnPhase.REDEPLOY, player1Territory)
				.contains(neighbour));
		}

	}

}
