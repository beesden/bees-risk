package org.beesden.risk.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@Data
public class GameMap {

	private Axis size;
	private Collection<Continent> continents;
	private Collection<Territory> territories;

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
	@EqualsAndHashCode(exclude={"territories"})
	public static final class Continent {
		private String id;
		private String name;
		private int bonusReinforcements;
		private String color;
		private Collection<Territory> territories;
	}

	@Data
	@EqualsAndHashCode(exclude={"neighbours"})
	public static final class Territory {
		private int battalions;
		private int cardValue;
		private Axis center;
		private Continent continent;
		private String id;
		private String name;
		private Collection<Territory> neighbours;
		private Player owner;
		private String path;
	}
}
