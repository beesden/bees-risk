package org.beesden.risk.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.beesden.risk.model.GameData;
import org.beesden.risk.utils.JsonUtils;
import org.beesden.risk.utils.MessageDecoder;
import org.beesden.risk.utils.MessageEncoder;

@ServerEndpoint(value = "/game/{id}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class ServerActions {

	public static Map<String, GameData> gameList = new ConcurrentHashMap<>();
	public static Map<String, String> playerGames = new ConcurrentHashMap<>();
	public static Map<String, Session> playerList = new ConcurrentHashMap<>();
	public static String lobbyName = "lobby";

	/**
	 * Disconnect a player from the server
	 * 
	 * @param socket Current player session information
	 * @param request {"gameId": "Test's Game"}
	 */
	@OnClose
	public void onClose(Session socket, CloseReason reason) {
		 // Check the session username
		 for (String playerId : playerList.keySet()) {
			 if (playerList.get(playerId).equals(socket)) {
				// Get the game id based on the playerId
				String gameId = ServerActions.playerGames.get(playerId);
				if (gameId == null) {
					System.out.println("Unable to find a valid game id");
					return;
				}
				// Get the gamedata object using the game id
				GameData gameData = ServerActions.gameList.get(gameId);
				if (gameData == null) {
					System.out.println("Unable to find open game session");
					return;
				}
				 GameCommands.leaveGame(gameData, socket, Json.createObjectBuilder().add("close", true).build());
				 break;
			 }
		 }
		System.out.println("Connection closed by user");
	}

	/**
	 * Was the disconnect expected?
	 * 
	 * @param socket Current player session information
	 * @param request {"gameId": "Test's Game"}
	 */
	@OnError
	public void onError(Session socket, Throwable throwable) {
		throwable.printStackTrace();
		System.out.println("Closing a connection due to error");
	}

	/**
	 * Run a function on receiving a command from a client
	 * 
	 * @param socket Current player session information
	 * @param request {"gameId": "Test's Game"}
	 */
	@OnMessage
	public void onMessage(Session socket, String message) {
		// Check the session username
		String username = (String) socket.getUserProperties().get("username");
		if (username == null) {
			System.out.println("Unable to find a valid username in the session");
			return;
		}
		// Convert the message into JSON and extract the method call
		InputStream input = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
		JsonObject request = JsonUtils.toObject(input);
		String action = request.getString("action");
		// Get the game id based on the username
		String gameId = ServerActions.playerGames.get(username);
		if (gameId == null) {
			playerGames.put(username, lobbyName);
			System.out.println("Unable to find a valid game id - returning to lobby");
		}
		// Get the gamedata object using the game id
		GameData gameData = ServerActions.gameList.get(gameId);
		if (!action.equals("createGame") && !action.equals("joinGame") && gameData == null) {
			System.out.println("Unable to find open game session");
			return;
		}
		// If the method call exists try and run it
		try {
			Method method = GameCommands.class.getMethod(action, GameData.class, Session.class, JsonObject.class);
			method.invoke(GameCommands.class, gameData, socket, request);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error running game command: " + action);
		}
	}

	/**
	 * Add a new player to the server
	 * 
	 * @param socket Current player session information
	 * @param String id - ?
	 * @throws Exception 
	 */
	@OnOpen
	public void onOpen(Session socket, @PathParam(value = "id") String id) throws Exception {
		// Prevent duplicate users
		String username = id;
		int i = 0;
		while (playerGames.get(username) != null) {
			username = id + "-" + i++;
		}
		// Add the user to the static variables
		socket.getUserProperties().put("username", username);
		playerGames.put(username, lobbyName);
		playerList.put(username, socket);
		// Create a game if none exists
		if (gameList.isEmpty()) {
			GameCommands.createGame(null, socket, null);
		}
		// Show the visitor the game list
		else {
			GameCommands.viewLobby(null, socket, null);
		}
	}
}
