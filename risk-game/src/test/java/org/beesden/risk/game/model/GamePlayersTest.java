package org.beesden.risk.game.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class GamePlayersTest {

	private static final Integer PLAYER_1 = 1001;
	private static final Integer PLAYER_2 = 1002;
	private static final Integer PLAYER_3 = 1003;
	private GamePlayers players;

	@Before
	public void setup() {
		players = new GamePlayers(PLAYER_1);
	}

	@Test
	public void testAddPlayers() {

		// Player added on init
		Assert.assertEquals(1, players.countActivePlayers());

		// Can add playerIds
		players.add(PLAYER_2);
		Assert.assertEquals(2, players.countActivePlayers());
		players.add(PLAYER_3);
		Assert.assertEquals(3, players.countActivePlayers());

		// Can't add duplicate playerIds
		players.add(PLAYER_3);
		Assert.assertEquals(3, players.countActivePlayers());

	}

	@Test
	public void testListPlayers() {

		players.add(PLAYER_2);
		players.add(PLAYER_3);

		// Player added on init
		Assert.assertEquals(Arrays.asList(new Integer[]{ PLAYER_1, PLAYER_2, PLAYER_3 }), players.listIds());
	}

	@Test
	public void iterateActivePlayers() {

		players.add(PLAYER_2);

		Assert.assertEquals(players.nextPlayer().getPlayerId(), PLAYER_1);
		Assert.assertEquals(players.nextPlayer().getPlayerId(), PLAYER_2);
		Assert.assertEquals(players.nextPlayer().getPlayerId(), PLAYER_1);
		Assert.assertEquals(players.nextPlayer().getPlayerId(), PLAYER_2);
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
		Assert.assertNull(players.getByPlayerId(PLAYER_1));

		// Check owner updated
		Assert.assertEquals(PLAYER_2, players.getOwner());
		players.add(PLAYER_1);
		Assert.assertEquals(PLAYER_2, players.getOwner());

		// Remove player after game start
		players.remove(PLAYER_1, true);
		Assert.assertEquals(2, players.countActivePlayers());
		Assert.assertEquals(3, players.listIds().size());
		Assert.assertEquals(true, players.getByPlayerId(PLAYER_1).isNeutral());
		Assert.assertEquals(true, players.getByPlayerId(PLAYER_1).isSpectating());

		// Remove all playerIds
		players.remove(PLAYER_1, true);
		players.remove(PLAYER_2, true);
		players.remove(PLAYER_3, true);
		Assert.assertEquals(0, players.countActivePlayers());
		Assert.assertNull(players.getOwner());

	}

}
