package org.beesden.risk.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class CardDeck {

	private List<Card> cards;
	private int cardPlayCount;

	@Data
	@AllArgsConstructor
	public class Card {
		private final int value;
		private final String cardId;
		private Integer playerId;
	}

	/**
	 * Default constructor
	 *
	 * @param map use territories from the game map to generate cards
	 */
	public CardDeck(GameMap map) {
		cards = map.getTerritories()
				.stream()
				.map(territory -> new Card(territory.getCardValue(), territory.getId(), null))
				.collect(Collectors.toList());

	}

	/**
	 * Sort the deck into a random order
	 */
	public void shuffleDeck() {
		Collections.shuffle(cards, new Random(System.nanoTime()));
	}

	/**
	 * Add a random card to the player
	 *
	 * @param playerId player id
	 * @return new card
	 */
	public Card addCard(Integer playerId) {
		shuffleDeck();

		Optional<Card> card = cards.stream().filter(c -> c.getPlayerId() == null).findFirst();
		card.ifPresent(c -> c.setPlayerId(playerId));
		return card.orElse(null);

	}

	/**
	 * List all cards for a player
	 *
	 * @param playerId player id
	 * @return listIds of cards
	 */
	public List<Card> getPlayerCards(Integer playerId) {
		return cards.stream()
				.filter(card -> card.getPlayerId() != null && playerId.equals(card.getPlayerId()))
				.collect(Collectors.toList());
	}

	/**
	 * Transfer all cards from one player to another.
	 *
	 * @param currentPlayerId current player
	 * @param targetPlayerId  target player
	 * @return list of cards
	 */
	public List<Card> transferCards(Integer currentPlayerId, Integer targetPlayerId) {
		List<Card> cards = getPlayerCards(currentPlayerId);
		cards.forEach(card -> card.setPlayerId(targetPlayerId));
		return cards;
	}

	/**
	 * Return a card to the pack.
	 *
	 * @param playerId player id
	 * @param cardId   card id
	 */
	public void returnCardToPack(Integer playerId, String cardId) {
		cards.stream()
				.filter(card -> playerId.equals(card.getPlayerId()) && cardId.equals(card.getCardId()))
				.forEach(card -> card.setPlayerId(null));
	}

	/**
	 * Calculate card values
	 */
	public int calculateCardValue(int... cardArray) {

		// Wrong number of cards
		if (cardArray.length != 3) {
			return 0;
		}

		Arrays.sort(cardArray);

		// 3 of a kind (with wildcard)
		if (cardArray[ 0 ] == 0 && cardArray[ 1 ] != 0 && cardArray[ 1 ] == cardArray[ 2 ]) {
			for (int id : cardArray) {
				if (cardArray[ id ] != 0) {
					return cardArray[ id ];
				}
			}
		}
		// 1 of each type (with wildcard)
		else if (cardArray[ 0 ] == 0) {
			return 4;
		}
		// Three of a kind
		else if (cardArray[ 0 ] == cardArray[ 1 ] && cardArray[ 1 ] == cardArray[ 2 ]) {
			return cardArray[ 0 ];
		}
		// 1 of each type
		else if (cardArray[ 0 ] < cardArray[ 1 ] && cardArray[ 1 ] < cardArray[ 2 ]) {
			return 4;
		}

		// Cards do not make a set
		return 0;
	}
}
