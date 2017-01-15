package org.beesden.risk.model;

import org.beesden.risk.service.MapService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CardDeckTest {

	private static final GameMap GAME_MAP = MapService.getMapById("risk");
	private static final String PLAYER_1 = "PLAYER_1";
	private static final String PLAYER_2 = "PLAYER_2";
	private CardDeck deck;

	@Before
	public void setup() {
		deck = new CardDeck(GAME_MAP);
	}

	@Test
	public void testAddCards() {

		// Can add a card
		CardDeck.Card card = deck.addCard(PLAYER_1);
		Assert.assertEquals(1, deck.getPlayerCards(PLAYER_1).size());
		Assert.assertEquals(card, deck.getPlayerCards(PLAYER_1).get(0));

		// Can add multiple cards
		deck.addCard(PLAYER_1);
		deck.addCard(PLAYER_1);
		Assert.assertEquals(3, deck.getPlayerCards(PLAYER_1).size());
		Assert.assertEquals(true, deck.getPlayerCards(PLAYER_1).contains(card));
	}

	@Test
	public void testRemoveCards() {

		CardDeck.Card card = deck.addCard(PLAYER_1);
		deck.addCard(PLAYER_1);
		deck.addCard(PLAYER_1);
		deck.addCard(PLAYER_1);
		Assert.assertEquals(4, deck.getPlayerCards(PLAYER_1).size());

		// Can return owned card
		deck.returnCardToPack(PLAYER_1, card.getCardId());
		Assert.assertEquals(3, deck.getPlayerCards(PLAYER_1).size());

		// Can't return unowned card
		deck.returnCardToPack(PLAYER_1, "some_card");
		Assert.assertEquals(3, deck.getPlayerCards(PLAYER_1).size());

		// Don't error if no cards
		deck.returnCardToPack("player3", "some_card");
	}

	@Test
	public void testCardValues() {

		// Wrong number of params
		Assert.assertEquals(0, deck.calculateCardValue());
		Assert.assertEquals(0, deck.calculateCardValue(1));
		Assert.assertEquals(0, deck.calculateCardValue(1, 1));
		Assert.assertEquals(0, deck.calculateCardValue(1, 1, 1, 1));

		// 3 of a kind
		Assert.assertEquals(1, deck.calculateCardValue(1, 1, 1));
		Assert.assertEquals(2, deck.calculateCardValue(2, 2, 2));
		Assert.assertEquals(3, deck.calculateCardValue(3, 3, 3));

		// 1 of each
		Assert.assertEquals(4, deck.calculateCardValue(1, 2, 3));

		// Wildcards
		Assert.assertEquals(2, deck.calculateCardValue(0, 2, 2));
		Assert.assertEquals(4, deck.calculateCardValue(0, 2, 3));
		Assert.assertEquals(4, deck.calculateCardValue(0, 0, 1));
		Assert.assertEquals(4, deck.calculateCardValue(0, 0, 0));

		// Not a match
		Assert.assertEquals(0, deck.calculateCardValue(1, 1, 3));

	}

	@Test
	public void testTransferCards() {

		deck.addCard(PLAYER_1);
		deck.addCard(PLAYER_1);
		deck.addCard(PLAYER_1);
		deck.addCard(PLAYER_2);
		deck.addCard(PLAYER_2);

		Assert.assertEquals(3, deck.getPlayerCards(PLAYER_1).size());
		Assert.assertEquals(2, deck.getPlayerCards(PLAYER_2).size());

		// Can transfer all cards to another player
		List<CardDeck.Card> transferred = deck.transferCards(PLAYER_1, PLAYER_2);
		Assert.assertEquals(3, transferred.size());
		Assert.assertEquals(0, deck.getPlayerCards(PLAYER_1).size());
		Assert.assertEquals(5, deck.getPlayerCards(PLAYER_2).size());
	}

}
