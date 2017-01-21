package org.beesden.risk.client;

import com.google.gson.Gson;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import org.beesden.risk.client.Model.LobbyPlayer;
import org.beesden.risk.client.Model.Message;
import org.beesden.risk.game.model.GameData;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Component
@Log
public class MessageService {

	private static final Gson GSON_READER = new Gson();

	public static void sendMessage(GameData gameData, GameAction action, Object data) {
		gameData.getPlayers().listIds().forEach(playerId -> sendMessage(LobbyPlayer.lookup(playerId), action, data));
	}

	public static void sendMessage(LobbyPlayer player, GameAction action, Object data) {

		Map<String, Object> message = new HashMap<>();
		message.put("action", action);
		message.put("message", data);
		message.put("username", player.getUsername());

		try {
			player.getSession().sendMessage(new TextMessage(GSON_READER.toJson(message)));
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage());
		}
	}

	public static Message read(String message) {
		return GSON_READER.fromJson(message, Message.class);
	}
}
