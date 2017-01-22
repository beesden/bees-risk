package org.beesden.risk.game.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GameMap {

	private String name;
	private Axis size;
	private List<Continent> continents;
	private List<Territory> territories;

	/**
	 * Calculate reinforcements for a player
	 *
	 * @param playerId player id
	 * @return number of additional reinforcements
	 */
	public Integer calculateReinforcement(int playerId) {
		Integer globalTerritories = 0;
		Integer bonusReinforcement = 0;

		// Manually recount territories to allow for continent bonuses
		for (Continent continent : continents) {

			int control = 0;

			// Calculate how many territories are controlled by the current player
			for (Territory terr : continent.getTerritories()) {
				if (playerId == terr.getOwnerId()) {
					control++;
				}
			}

			// If the player controls the whole continent, add bonus
			if (control == continent.getTerritories().size()) {
				bonusReinforcement += continent.getBonusReinforcements();
			}

			globalTerritories += control;

		}
		// Ensure a mimimum of three battalions (excluding continent bonus)
		return (int) Math.max(3, Math.floor(globalTerritories / 3)) + bonusReinforcement;
	}

	/**
	 * Get unclaimed territories
	 */
	public List<Territory> getTerritoriesByOwner(Integer ownerId) {
		return territories.stream()
			.filter(territory -> territory.getOwnerId().equals(ownerId))
			.collect(Collectors.toList());
	}

	/**
	 * Get valid territories
	 */
	public List<Territory> getValidTerritories(int playerId, TurnPhase phase, Territory current) {

		switch (phase) {
			case INITIAL:
				return getTerritoriesByOwner(null);
			case DEPLOY:
			case REINFORCE:
				return getTerritoriesByOwner(playerId);
			case ATTACK:
				if (current == null) {
					return getTerritoriesByOwner(playerId);
				} else {
					return current.neighbours.stream()
						.filter(territory -> territory.getOwnerId() != playerId)
						.collect(Collectors.toList());
				}
			case REDEPLOY:
				if (current == null) {
					return getTerritoriesByOwner(playerId);
				} else {
					List<Territory> neighbours = getNeighboursRecursive(new ArrayList<>(), current);
					neighbours.remove(current);
					return neighbours;
				}
		}

		return new ArrayList<>();
	}

	private List<Territory> getNeighboursRecursive(List<Territory> neighbours, Territory current) {
		current.neighbours.stream().filter(n -> n.getOwnerId().equals(current.getOwnerId())).forEach(neighbour -> {
			if (!neighbours.contains(neighbour)) {
				neighbours.add(neighbour);
				getNeighboursRecursive(neighbours, neighbour);
			}
		});
		return neighbours;
	}

	@Data
	public static final class Axis {
		private int x;
		private int y;

		public Axis(int... location) {
			if (location != null && location.length == 2) {
				x = location[ 0 ];
				y = location[ 1 ];
			}
		}
	}

	@Data
	@EqualsAndHashCode(exclude = { "territories" })
	@ToString(exclude = "territories")
	public static final class Continent {
		private String id;
		private String name;
		private int bonusReinforcements;
		private String color;
		private Collection<Territory> territories;
	}

	@Data
	@EqualsAndHashCode(exclude = { "neighbours" })
	@ToString(exclude = "neighbours")
	public static final class Territory {
		private int battalions;
		private int cardValue;
		private Axis center;
		private Continent continent;
		private String id;
		private String name;
		private Collection<Territory> neighbours;
		private Integer ownerId;
		private String path;
	}
}
