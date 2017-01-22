package org.beesden.risk.game.exception;

public class MapException extends RuntimeException {

	public String mapId;

	public MapException(String message, String mapId) {
		super(message);
		this.mapId = mapId;
	}

}