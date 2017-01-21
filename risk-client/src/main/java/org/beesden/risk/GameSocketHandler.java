package org.beesden.risk;

import com.google.gson.Gson;
import lombok.Data;
import org.beesden.risk.model.Config;
import org.beesden.risk.model.GameData;
import org.beesden.risk.model.Lobby;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameSocketHandler extends TextWebSocketHandler {

	private Lobby lobby = new Lobby();

	private Map<Integer, String> usernames = new ConcurrentHashMap<>();

	@Data
	private class Message {
		private String username;
		private GameCommand action;
		private String description;
		private String gameId;
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		// lobby.leaveGame(session.getId());
		// TODO - leave game(s)
		System.out.println("Closing a connection due to reason: " + status);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable throwable) {
		// .leaveGame(session.getId());
		// TODO - leave game(s)
		System.out.println("Closing a connection due to error: " + throwable.getMessage());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage jsonTextMessage) {
		try {
			Message message = GSON_READER.fromJson(jsonTextMessage.getPayload(), Message.class);

			int userId = Integer.valueOf(session.getId());
			String gameId = message.getGameId();

			switch (message.getAction()) {
				case login:
					session.getAttributes().put("username", message.getUsername());
					sendMessage(session, GameAction.gameLobby, lobby.listGames());
					break;
				case createGame:
					GameData gameData = lobby.createGame(userId, getGameName(session), new Config());
					sendMessage(session, GameAction.gameSetup, new Lobby.Summary(gameData));
					break;
				case joinGame:
					gameData = lobby.joinGame(userId, gameId);
					sendMessage(session, GameAction.gameSetup, new Lobby.Summary(gameData));
					break;
				case leaveGame:
					lobby.leaveGame(userId, gameId);
					sendMessage(session, GameAction.gameLobby, lobby.listGames());
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private String getGameName(WebSocketSession session) {
		return getUserName(session) + "'s game";
	}

	private Integer getUserId(WebSocketSession session) {
		return Integer.valueOf(session.getId());
	}

	private String getUserName(WebSocketSession session) {
		String username = (String) session.getAttributes().get("username");
		if (username == null) {
			username = "guest-" + getUserId(session);
		}
		return username;
	}

	private static final Gson GSON_READER = new Gson();

	private void sendMessage(WebSocketSession session, GameAction action, Object data) {
		Map<String, Object> message = new HashMap<>();
		message.put("action", action);
		message.put("message", data);
		message.put("username", getUserName(session));
		try {
			session.sendMessage(new TextMessage(GSON_READER.toJson(message)));
		} catch (Exception e) {

		}
	}
}
