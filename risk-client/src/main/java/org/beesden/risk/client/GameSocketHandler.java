package org.beesden.risk.client;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import org.beesden.risk.client.model.Message;
import org.beesden.risk.client.model.game.TurnSummary;
import org.beesden.risk.client.model.lobby.Lobby;
import org.beesden.risk.client.model.lobby.LobbyGame;
import org.beesden.risk.client.model.lobby.LobbyPlayer;
import org.beesden.risk.client.service.MessageService;
import org.beesden.risk.game.exception.GameLobbyException;
import org.beesden.risk.game.model.Config;
import org.beesden.risk.game.model.GameData;

@Component
@Log
public class GameSocketHandler extends TextWebSocketHandler {

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		LobbyPlayer player = LobbyPlayer.lookup(session);
		player.getGames().forEach(game -> Lobby.leaveGame(player.getPlayerId(), game.getName()));
		log.info("Connection closed: " + player.getUsername());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable throwable) {
		LobbyPlayer player = LobbyPlayer.lookup(session);
		player.getGames().forEach(game -> Lobby.leaveGame(player.getPlayerId(), game.getName()));
		log.warning("Closing a connection due to error for user " + player.getUsername() + ": " + throwable.getMessage());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage jsonTextMessage) {

		LobbyPlayer player = LobbyPlayer.lookup(session);

		try {
			Message message = MessageService.read(jsonTextMessage.getPayload());
			GameData gameData;
			String gameId = message.getGameId();

			switch (message.getAction()) {

				case login:
					player.setUsername(message.getUsername());
					MessageService.sendMessage(player, GameAction.gameLobby, Lobby.listGames());
					break;

				case createGame:
					gameData = Lobby.createGame(player.getPlayerId(), player.getUsername() + "'s game", new Config());
					LobbyPlayer.joinGame(player.getPlayerId(), gameData);
					MessageService.sendMessage(player, GameAction.gameSetup, new LobbyGame(gameData));
					break;

				case joinGame:
					gameData = Lobby.joinGame(player.getPlayerId(), gameId);
					LobbyPlayer.joinGame(player.getPlayerId(), gameData);
					MessageService.sendMessage(gameData, GameAction.gameSetup, new LobbyGame(gameData));
					break;

				case leaveGame:
					gameData = Lobby.leaveGame(player.getPlayerId(), gameId);
					LobbyPlayer.leaveGame(player.getPlayerId(), gameId);
					MessageService.sendMessage(gameData, GameAction.gameSetup, new LobbyGame(gameData));
					MessageService.sendMessage(player, GameAction.gameLobby, Lobby.listGames());
					break;

				case startGame:
					gameData = Lobby.startGame(player.getPlayerId(), gameId);
					MessageService.sendMessage(gameData, GameAction.startTurn, new TurnSummary(gameData));
					break;
			}
		} catch (GameLobbyException e) {
			MessageService.sendMessage(player, GameAction.commandError, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		LobbyPlayer.connectPlayer(session);
	}
}
