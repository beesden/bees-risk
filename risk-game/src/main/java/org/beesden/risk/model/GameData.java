package org.beesden.risk.model;

import lombok.Data;
import org.beesden.risk.service.MapService;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameData {

	private static final int[] DEFAULT_START_STRENGTH = { 0, 40, 35, 30, 25, 20 };

	public enum GameState {
		SETUP, READY, STARTED, ENDED
	}

	private int cardPlayCount = 0;
	private int[] startForces = DEFAULT_START_STRENGTH;
	private GameConfig config;
	private GameMap map;
	private GameState state = GameState.SETUP;
	private List<GamePlayer> players = new ArrayList<>();
	private List<String> cards;
	private String name;
	private String owner;

	/**
	 * Create a new game
	 *
	 * @param playerId game create ID
	 * @param gameName game name
	 * @param config   game config
	 */
	public GameData(String playerId, String gameName, GameConfig config) {
		this.owner = playerId;
		this.name = gameName;
		this.config = config;

		this.map = MapService.getMapById(config.getGameMap());
	}

}
