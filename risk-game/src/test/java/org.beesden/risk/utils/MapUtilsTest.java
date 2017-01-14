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
		GameMapDTO mapData = new GameMapDTO();

		GameMapDTO.Continent continent = new GameMapDTO.Continent("europe", "Europe", 5, "red");
		mapData.setContinents(Collections.singletonList(continent));

		GameMapDTO.Territory territory1 = new GameMapDTO.Territory("england", 5, new int[]{ 1, 2 }, "europe", "France", new String[]{ "england" }, "path");
		GameMapDTO.Territory territory2 = new GameMapDTO.Territory("france", 4, new int[]{ 3, 4 }, "europe", "England", new String[]{ "france" }, "path");
		mapData.setTerritories(Arrays.asList(territory1, territory2));

		GameMap riskMap = MapUtils.generateMap((mapData));

		Assert.assertNotNull(riskMap);
		Assert.assertEquals(1, riskMap.getContinents().size());
		Assert.assertEquals(2, riskMap.getTerritories().size());
	}

}
