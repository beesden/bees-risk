package org.beesden.risk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
public class GameMap {

	private Location size;
	private Collection<Continent> continents;
	private Collection<Territory> territories;

	@Data
	public static final class Location {
		private int x;
		private int y;

		public Location(int[] location) {
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
		private Integer bonusReinforcements;
		private String color;
		private Collection<Territory> territories;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode(exclude={"neighbours"})
	public static final class Territory {
		private Integer battalions;
		private Integer cardValue;
		private Location center;
		private Continent continent;
		private String id;
		private String name;
		private Collection<Territory> neighbours;
		private Player owner;
		private String path;
	}
}
