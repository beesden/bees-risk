package org.beesden.risk.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.List;

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
	public Integer calculateReinforcement(String playerId) {
		Integer globalTerritories = 0;
		Integer bonusReinforcement = 0;

		// Manually recount territories to allow for continent bonuses
		for (Continent continent : continents) {

			int control = 0;

			// Calculate how many territories are controlled by the current player
			for (Territory terr : continent.getTerritories()) {
				if (playerId.equals(terr.getOwnerId())) {
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

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append(this.getClass().getName())
				.append(" Object {")
				.append(NEW_LINE)
				.append(" Name: ")
				.append(name)
				.append(NEW_LINE)
				.append(" Number of continents: ")
				.append(continents.size())
				.append(NEW_LINE)
				.append(" Number of territories: ")
				.append(territories.size())
				.append(NEW_LINE)
				.append(" Size: ")
				.append(size.x)
				.append(" x ")
				.append(size.y)
				.append(NEW_LINE)
				.append("}");

		return result.toString();
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
	public static final class Continent {
		private String id;
		private String name;
		private int bonusReinforcements;
		private String color;
		private Collection<Territory> territories;
	}

	@Data
	@EqualsAndHashCode(exclude = { "neighbours" })
	public static final class Territory {
		private int battalions;
		private int cardValue;
		private Axis center;
		private Continent continent;
		private String id;
		private String name;
		private Collection<Territory> neighbours;
		private String ownerId;
		private String path;
	}
}
