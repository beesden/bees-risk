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

		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(" Name: " + name + NEW_LINE);
		result.append(" Number of continents: " + continents.size() + NEW_LINE);
		result.append(" Number of territories: " + territories.size() + NEW_LINE);
		result.append(" Size: " + size.x + " x " + size.y + NEW_LINE);
		result.append("}");

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
		private GamePlayer owner;
		private String path;
	}
}
