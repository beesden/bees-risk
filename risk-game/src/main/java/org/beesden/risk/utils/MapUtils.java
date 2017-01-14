package org.beesden.risk.utils;

import com.google.gson.Gson;
import org.beesden.risk.data.GameMapDTO;
import org.beesden.risk.model.GameMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class MapUtils {

	private static final ClassLoader CLASS_LOADER = MapUtils.class.getClassLoader();
	private static final Gson GSON_READER = new Gson();
	private static final String MAP_FOLDER = "maps";
	private static final String MAP_SUFFIX = ".map.json";

	/**
	 * List all available maps
	 *
	 * @return list of map IDs
	 */
	public static List<String> getAvailableMaps() {
		List<String> mapFiles = new ArrayList<>();

		// Get file from resources folder
		File directory = new File(CLASS_LOADER.getResource(MAP_FOLDER).getFile());

		Arrays.stream(directory.listFiles())
				.filter(m -> m.getName().endsWith(MAP_SUFFIX))
				.forEach(m -> mapFiles.add(m.getName().substring(0, m.getName().length() - MAP_SUFFIX.length())));

		return mapFiles;

	}

	/**
	 * Build a map from a file
	 *
	 * @param mapId map file ID
	 * @return game map
	 */
	public static GameMap generateMapFromFile(String mapId) {

		try {

			InputStream input = CLASS_LOADER.getResourceAsStream(MAP_FOLDER + "/" + mapId + MAP_SUFFIX);
			if (input == null) {
				throw new NullPointerException("Unable to locate map file: " + mapId);
			}

			String mapJson = new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n"));
			GameMapDTO mapData = GSON_READER.fromJson(mapJson, GameMapDTO.class);
			return generateMap(mapData);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	/**
	 * Build a map from a json object
	 *
	 * @param mapData map data
	 * @return game map
	 */
	public static GameMap generateMap(GameMapDTO mapData) {

		// Add the continents into the map
		Map<String, GameMap.Continent> continentList = mapData.getContinents().stream().map(continentData -> {
			GameMap.Continent continent = new GameMap.Continent();

			continent.setId(continentData.getId());
			continent.setBonusReinforcements(continentData.getBonus());
			continent.setName(continentData.getName());
			continent.setColor(continentData.getColor());
			continent.setTerritories(new HashSet<>());

			return continent;

		}).collect(Collectors.toMap(c -> c.getId(), c -> c));

		// Add the territories into the map
		Map<String, GameMap.Territory> territoryList = mapData.getTerritories().stream().map(territoryData -> {
			GameMap.Territory territory = new GameMap.Territory();

			territory.setId(territoryData.getId());
			territory.setCardValue(territoryData.getCardValue());
			territory.setName(territoryData.getName());
			territory.setCenter(new GameMap.Location(territoryData.getCenter()));
			territory.setPath(territoryData.getPath());
			territory.setNeighbours(new HashSet<>());

			GameMap.Continent continent = continentList.get(territoryData.getContinentId());
			territory.setContinent(continent);
			continent.getTerritories().add(territory);

			return territory;
		}).collect(Collectors.toMap(GameMap.Territory::getId, t -> t));

		// Setup the continents and neighbours once all territories have been built
		mapData.getTerritories().forEach(territoryData -> {

			GameMap.Territory territory = territoryList.get(territoryData.getId());

			// Territory neighbours
			for (String neighbourId : territoryData.getNeighbours()) {
				GameMap.Territory neighbour = territoryList.get(neighbourId);
				territory.getNeighbours().add(neighbour);
			}
		});

		// Contruct and return the game map object
		GameMap gameMap = new GameMap();
		gameMap.setContinents(continentList.values());
		gameMap.setTerritories(territoryList.values());
		gameMap.setSize(new GameMap.Location(mapData.getSize()));
		return gameMap;
	}

}
