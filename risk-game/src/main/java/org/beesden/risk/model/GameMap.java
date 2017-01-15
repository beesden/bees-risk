package org.beesden.risk.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@Data
public class GameMap {

	private String name;
	private Axis size;
	private Collection<Continent> continents;
	private Collection<Territory> territories;

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

		public Axis(int[] location) {
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
		private GamePlayers owner;
		private String path;
	}
}
