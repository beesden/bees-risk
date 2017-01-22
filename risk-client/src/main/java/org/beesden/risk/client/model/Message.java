package org.beesden.risk.client.model;

import lombok.Data;

import org.beesden.risk.client.GameCommand;

@Data
public class Message {
	private String username;
	private GameCommand action;
	private String description;
	private String gameId;
}
