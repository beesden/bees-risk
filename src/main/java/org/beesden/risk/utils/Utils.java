package org.beesden.risk.utils;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.json.Json;
import javax.json.JsonStructure;
import javax.websocket.Session;

import org.beesden.risk.action.ServerActions;

public class Utils {

	private static final String BUNDLE_NAME = "messages";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * Get a message from resource bundles
	 **/
	public static String getBundle(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * Get a message from resource bundles (w/ params)
	 **/
	public static String getBundle(String key, Object... params) {
		try {
			return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
		}
		catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * Send a message to everyone in the same game
	 **/
	public static void sendGameMessage(String gameId, String action, JsonStructure message) {
		for (String key : ServerActions.playerGames.keySet()) {
			// Check for each connection user in the game and send the new player
			if (ServerActions.playerGames.get(key).equals(gameId)) {
				Session session = ServerActions.playerList.get(key);
				if (session == null || !session.isOpen()) {
					return;
				}
				sendMessage(session, action, message);
			}
		}
	}

	/**
	 * Send a message to a particular player
	 **/
	public static void sendMessage(Session socket, String action, JsonStructure message) {
		String username = (String) socket.getUserProperties().get("username");
		String response = Json.createObjectBuilder().add("action", action).add("username", username).add("message", message).build().toString();
		try {
			socket.getBasicRemote().sendText(response);
		}
		catch (Exception e) {
			System.out.println("Error when sending message - removing player from server");
			// Remove if connection unsuccessful
			ServerActions.playerList.remove(username);
			ServerActions.playerGames.remove(username);		
		}
	}
}
