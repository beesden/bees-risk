package org.beesden.risk;

import org.beesden.risk.model.Lobby;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

	private Lobby lobby = new Lobby();

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		lobby.leaveGame(session.getId());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable throwable) {
		lobby.leaveGame(session.getId());
		System.out.println("Closing a connection due to error: " + throwable.getMessage());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage jsonTextMessage) {
		// TODO
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		lobby.listGames();
		try {
			session.sendMessage(new TextMessage("Hello"));
		} catch (Exception e) {

		}
	}
}
