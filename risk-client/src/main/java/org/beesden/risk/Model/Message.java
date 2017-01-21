package org.beesden.risk.Model;

import lombok.Data;
import org.beesden.risk.GameCommand;

@Data
public class Message {
	private String username;
	private GameCommand action;
	private String description;
	private String gameId;
}
