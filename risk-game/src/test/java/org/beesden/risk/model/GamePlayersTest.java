package org.beesden.risk.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class GamePlayersTest {

	private static final String PLAYER_1 = "PLAYER_1";
	private static final String PLAYER_2 = "PLAYER_2";
	private static final String PLAYER_3 = "PLAYER_3";
	private GamePlayers players;

	@Before
	public void setup() {
		players = new GamePlayers(PLAYER_1);
	}

	@Test
	public void testAddPlayers() {

		// Player added on init
		Assert.assertEquals(1, players.countActivePlayers());

		// Can add players
		players.add(PLAYER_2);
		Assert.assertEquals(2, players.countActivePlayers());
		players.add(PLAYER_3);
		Assert.assertEquals(3, players.countActivePlayers());

		// Can't add duplicate players
		players.add(PLAYER_3);
		Assert.assertEquals(3, players.countActivePlayers());

	}

	@Test
	public void testListPlayers() {

		players.add(PLAYER_2);
		players.add(PLAYER_3);

		// Player added on init
		Assert.assertEquals(Arrays.asList(new String[]{ PLAYER_1, PLAYER_2, PLAYER_3 }), players.listIds());
	}

	@Test
	public void testRemovePlayers() {

		players.add(PLAYER_2);
		players.add(PLAYER_3);

		Assert.assertEquals(3, players.countActivePlayers());
		Assert.assertEquals(PLAYER_1, players.getOwner());

		// Remove before start
		players.remove(PLAYER_1, false);
		Assert.assertEquals(2, players.countActivePlayers());
		Assert.assertEquals(2, players.listIds().size());
		Assert.assertNull(players.get(PLAYER_1));

		// Check owner updated
		Assert.assertEquals(PLAYER_2, players.getOwner());
		players.add(PLAYER_1);
		Assert.assertEquals(PLAYER_2, players.getOwner());

		// Remove player after game start
		players.remove(PLAYER_1, true);
		Assert.assertEquals(2, players.countActivePlayers());
		Assert.assertEquals(3, players.listIds().size());
		Assert.assertEquals(true, players.get(PLAYER_1).isNeutral());
		Assert.assertEquals(true, players.get(PLAYER_1).isSpectating());

		// Remove all players
		players.remove(PLAYER_1, true);
		players.remove(PLAYER_2, true);
		players.remove(PLAYER_3, true);
		Assert.assertEquals(0, players.countActivePlayers());
		Assert.assertNull(players.getOwner());

	}

}
