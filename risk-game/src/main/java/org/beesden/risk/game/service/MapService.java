package org.beesden.risk.game.service;

import com.google.gson.Gson;

import org.beesden.risk.game.Exception.MapException;
import org.beesden.risk.game.data.GameMapDTO;
import org.beesden.risk.game.model.GameMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class MapService {

	private static final ClassLoader CLASS_LOADER = MapService.class.getClassLoader();
	private static final Gson GSON_READER = new Gson();
	private static final String MAP_FOLDER = "maps";
	private static final String MAP_SUFFIX = ".map.json";
	private static final Map<String, GameMapDTO> MAP_CACHE = new HashMap<>();

	/**
	 * List all available maps
	 *
	 * @return listIds of map IDs
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
	 * Get a map by ID.
	 * This is currently from a file, but could be extended to use a DB.
	 *
	 * @param mapId map file ID
	 * @return game map
	 */
	public static GameMap getMapById(String mapId) {

		if (MAP_CACHE.get(mapId) != null) {
			return generateMap(mapId, MAP_CACHE.get(mapId));
		}

		try {

			InputStream input = CLASS_LOADER.getResourceAsStream(MAP_FOLDER + "/" + mapId + MAP_SUFFIX);
			if (input == null) {
				throw new MapException("Unable to locate map file", mapId);
			}

			String mapJson = new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n"));
			GameMapDTO mapData = GSON_READER.fromJson(mapJson, GameMapDTO.class);
			MAP_CACHE.put(mapId, mapData);
			return generateMap(mapId, mapData);

		} catch (Exception e) {
			throw new MapException(e.getMessage(), mapId);
		}
	}

	/**
	 * Build a map from a json object
	 *
	 * @param mapData map data
	 * @return game map
	 */
	public static GameMap generateMap(String mapId, GameMapDTO mapData) {

		// Add the continents into the map
		Map<String, GameMap.Continent> continentList = mapData.getContinents().stream().map(continentData -> {
			GameMap.Continent continent = new GameMap.Continent();

			continent.setId(continentData.getId());
			continent.setBonusReinforcements(continentData.getBonus());
			continent.setName(continentData.getName());
			continent.setColor(continentData.getColor());
			continent.setTerritories(new HashSet<>());

			return continent;

		}).collect(Collectors.toMap(GameMap.Continent::getId, c -> c));

		// Add the territories into the map
		Map<String, GameMap.Territory> territoryList = mapData.getTerritories().stream().map(territoryData -> {
			GameMap.Territory territory = new GameMap.Territory();

			territory.setId(territoryData.getId());
			territory.setCardValue(territoryData.getCardValue());
			territory.setName(territoryData.getName());
			territory.setCenter(new GameMap.Axis(territoryData.getCenter()));
			territory.setPath(territoryData.getPath());
			territory.setNeighbours(new HashSet<>());

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

			GameMap.Continent continent = continentList.get(territoryData.getContinentId());
			territory.setContinent(continent);
			continent.getTerritories().add(territory);
		});

		// Construct and return the game map object
		GameMap gameMap = new GameMap();
		gameMap.setName(mapId);
		gameMap.setContinents(new ArrayList<>(continentList.values()));
		gameMap.setTerritories(new ArrayList<>(territoryList.values()));
		gameMap.setSize(new GameMap.Axis(mapData.getSize()));
		return gameMap;
	}

}
