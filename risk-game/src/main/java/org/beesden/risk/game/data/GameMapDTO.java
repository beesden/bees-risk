package org.beesden.risk.game.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
public class GameMapDTO {

	private int[] size;
	private Collection<Continent> continents;
	private Collection<Territory> territories;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static final class Continent {
		private String id;
		private String name;
		private Integer bonus;
		private String color;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static final class Territory {
		private String id;
		private int cardValue;
		private int[] center;
		private String continentId;
		private String name;
		private String[] neighbours;
		private String path;
	}
}
