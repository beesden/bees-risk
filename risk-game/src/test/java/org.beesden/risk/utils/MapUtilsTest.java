package org.beesden.risk.utils;

import org.beesden.risk.data.GameMapDTO;
import org.beesden.risk.model.GameMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MapUtilsTest {

	@Test
	public void testMapList() {
		List<String> mapList = MapUtils.getAvailableMaps();
		Assert.assertEquals(2, mapList.size());
	}

	@Test
	public void testGetFromFileInvalid() {
		GameMap gameMap = MapUtils.generateMapFromFile("_INVALID");
		Assert.assertNull(gameMap);
	}

	@Test
	public void testGetFromFile() {
		GameMap gameMap = MapUtils.generateMapFromFile("risk");

		Assert.assertNotNull(gameMap);
		Assert.assertEquals(6, gameMap.getContinents().size());
		Assert.assertEquals(42, gameMap.getTerritories().size());
	}

	@Test
	public void testGenerateMap() {

		// Build sample data
		GameMapDTO mapDto = new GameMapDTO();

		GameMapDTO.Continent continentDto = new GameMapDTO.Continent("europe", "Europe", 5, "red");
		mapDto.setContinents(Collections.singletonList(continentDto));

		GameMapDTO.Territory territory1Dto = new GameMapDTO.Territory("france", 5, new int[]{ 1, 2 }, "europe", "France", new String[]{ "england" }, "path");
		GameMapDTO.Territory territory2Dto = new GameMapDTO.Territory("england", 4, new int[]{ 3, 4 }, "europe", "England", new String[]{ "france" }, "path");
		mapDto.setTerritories(Arrays.asList(territory1Dto, territory2Dto));

		// Build map
		GameMap riskMap = MapUtils.generateMap((mapDto));

		// Run tests
		Assert.assertNotNull(riskMap);

		Assert.assertEquals(1, riskMap.getContinents().size());
		GameMap.Continent continent = riskMap.getContinents().iterator().next();
		Assert.assertEquals("europe", continent.getId());
		Assert.assertEquals("Europe", continent.getName());
		Assert.assertEquals("red", continent.getColor());
		Assert.assertEquals(5, continent.getBonusReinforcements());
		Assert.assertEquals(2, continent.getTerritories().size());

		Assert.assertEquals(2, riskMap.getTerritories().size());
		for (GameMap.Territory territory : riskMap.getTerritories()) {
			Assert.assertNotNull(territory.getId());
			Assert.assertNotNull(territory.getName());
			Assert.assertNotEquals(0, territory.getCenter().getX());
			Assert.assertNotEquals(0, territory.getCenter().getY());
			Assert.assertNotEquals(0, territory.getCardValue());
			Assert.assertEquals(1, territory.getNeighbours().size());
			Assert.assertEquals("europe", territory.getContinent().getId());
		}

	}

}
