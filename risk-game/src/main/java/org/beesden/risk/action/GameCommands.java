package org.beesden.risk.action;

public class GameCommands {

//	/**
//	 * If a user interacts with a territory, perform a map action as required
//	 *
//	 * @param socket Current player session information
//	 * @param request {"gameId": "Test's Game"}
//	 */
//	public static void chat(GameData gameData, Session socket, JsonObject request) {
//		// Get the game id based on the username
//		String gameId = gameData.getGameId();
//		String chatMessage = request.getString("message");
//		if (chatMessage == null) {
//			System.out.println("Empty or no chat message sent");
//			return;
//		}
//		String chatSender = request.getString("sender");
//		if (chatSender == null) {
//			System.out.println("Unable to retrieve sender username");
//			return;
//		}
//		// Get a timestamp for the message
//		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
//		Utils.sendGameMessage(gameId, "chatMessage", Json.createObjectBuilder().add("message", chatMessage).add("sender", chatSender).add("timeStamp", sdfDate.format(new Date())).build());
//	}
//
//	/**
//	 * Creates a new game session using the username from the session.
//	 *
//	 * @param socket Current player session information
//	 * @param request null
//	 */
//	public static void createGame(GameData gameData, Session socket, JsonObject request) {
//		// Player is already in a game
//		if (gameData != null) {
//			leaveGame(gameData, socket, request);
//		}
//		// Create a game and generate a fresh data object
//		String username = (String) socket.getUserProperties().get("username");
//		String gameName = Utils.getBundle("risk.lobby.gamename", username);
//		// Prevent two games of the same name being created
//		String gameId = gameName;
//		int i = 1;
//		while (WebSocketServerConfiguration.gameList.get(gameId) != null) {
//			gameId = gameName + " " + ++i;
//		}
//		gameData = new GameData(gameId);
//		gameData.getConfig().setLeadPlayer(username);
//		WebSocketServerConfiguration.gameList.put(gameId, gameData);
//		// If all checks out, add the player to the game
//		joinGame(gameData, socket, Json.createObjectBuilder().add("gameId", gameId).build());
//		viewLobby(gameData, socket, request);
//	}
//
//	/**
//	 * Adds the current session / player to a currently existing game.
//	 *
//	 * @param socket Current player session information
//	 * @param request {gameId: {string}
//	 */
//	public static void playerColour(GameData gameData, Session socket, JsonObject request) {
//		// Get the game player
//		String username = (String) socket.getUserProperties().get("username");
//		Player player = gameData.getPlayerList().get(username);
//		// Get the current player colour
//		Integer colourIndex = player.getColour() == null ? 0 : Arrays.asList(gameData.getColours()).indexOf(player.getColour());
//		String colour;
//		do {
//			colourIndex = colourIndex < 0 ? 0 : colourIndex > 5 ? 0 : colourIndex;
//			colour = gameData.getColours()[colourIndex++];
//		} while (gameData.getPlayerColours().get(colour) != null);
//		gameData.getPlayerColours().put(colour, username);
//		gameData.getPlayerColours().remove(player.getColour());
//		player.setColour(colour);
//		// Update the game session
//		Utils.sendGameMessage(gameData.getGameId(), "gameSetup", Json.createObjectBuilder().add("playerList", gameData.playerIds()).build());
//	}
//
//	/**
//	 * Adds the current session / player to a currently existing game.
//	 *
//	 * @param socket Current player session information
//	 * @param request {gameId: {string}
//	 */
//	public static void joinGame(GameData gameData, Session socket, JsonObject request) {
//		String gameId;
//		String username = (String) socket.getUserProperties().get("username");
//		// Joining via socket instead of through classes
//		if (gameData == null) {
//			gameId = request.getString("gameId");
//			gameData = WebSocketServerConfiguration.gameList.get(gameId);
//		}
//		// Get the gamedata object using the game id
//		if (gameData == null) {
//			System.out.println("Unable to find open game session");
//			return;
//		}
//		gameId = gameData.getGameId();
//		// Provide the game config
//		WebSocketServerConfiguration.playerGames.put(username, gameId);
//		Utils.sendMessage(socket, "updateConfig", gameData.getConfig().toJson());
//		// If all checks out, add the player to the game
//		WebSocketServerConfiguration.playerGames.put(username, gameId);
//		Player newPlayer = new Player(username, null);
//		gameData.getPlayerList().put(newPlayer.getPlayerId(), newPlayer);
//		// Generate a player colour and update the session
//		playerColour(gameData, socket, request);
//	}
//
//	/**
//	 * Remove the player from a game
//	 *
//	 * @param socket Current player session information
//	 * @param request {gameId: {string}
//	 */
//	public static void leaveGame(GameData gameData, Session socket, JsonObject request) {
//		// If the game has started, disable the player: otherwise remove
//		String gameId = gameData.getGameId();
//		String username = (String) socket.getUserProperties().get("username");
//		// Whether player is spectating or completely leaving the game
//		Boolean closeConnection = request.getBoolean("close");
//		if (request.getBoolean("close")) {
//			WebSocketServerConfiguration.playerGames.put(username, WebSocketServerConfiguration.lobbyName);
//			viewLobby(gameData, socket, request);
//		}
//		// Count the number of players left in the session
//		int activePlayers = 0;
//		for (String playerId: WebSocketServerConfiguration.playerGames.keySet()) {
//			if (WebSocketServerConfiguration.playerGames.get(playerId).equals(gameId)) {
//				activePlayers++;
//			}
//		}
//		// If everyone has left the session, kill it (WITH FIRE)
//		if (activePlayers == 0) {
//			WebSocketServerConfiguration.gameList.remove(gameId);
//		}
//		// If game is started, set player to neutral
//		else if (gameData.getGameReady()) {
//			Player player = gameData.getPlayerList().get(username);
//			player.setNeutral(true);
//			gameData.setPlayersActive(gameData.getPlayersActive() - 1);
//			// Start the next player's turn if current player's turn
//			if (gameData.getConfig().getPlayerTurn().equals(username)) {
//				GameActions.startTurn(gameData);
//			}
//			// Return cards to the pack
//			ArrayList<String> cardTransfer = new ArrayList<>(player.getRiskCards().keySet());
//			for (String cardId: cardTransfer) {
//				CardActions.removeCard(gameData, player.getPlayerId(), cardId);
//			}
//			// Trigger victory condition check
//			if (!gameData.getGameFinished() && !VictoryActions.checkVictoryConditions(gameData)) {
//				Utils.sendGameMessage(gameId, "playerOut", Json.createObjectBuilder().add("reason", closeConnection ? "close" : "surrender").add("config", gameData.getConfig().toJson()).add("player", player.toJson()).build());
//			}
//		}
//		// If game not started, remove user
//		else if (gameData.getGameStarted()) {
//			gameData.getPlayerList().remove(username);
//			// If no players left, delete the game
//			if (gameData.getPlayerList().size() == 0) {
//				WebSocketServerConfiguration.gameList.remove(gameId);
//			}
//			// Update lead player if necessary
//			else if (gameData.getConfig().getLeadPlayer().equals(username)) {
//				for (String playerId : gameData.getPlayerList().keySet()) {
//					Player player = gameData.getPlayerList().get(playerId);
//					if (!player.isNeutral() && !player.getPlayerId().equals(username)) {
//						gameData.getConfig().setLeadPlayer(player.getPlayerId());
//						break;
//					}
//				}
//			}
//			WebSocketServerConfiguration.playerGames.put(username, WebSocketServerConfiguration.lobbyName);
//			Utils.sendGameMessage(gameId, "gameSetup", Json.createObjectBuilder().add("config", gameData.getConfig().toJson()).add("playerList", gameData.playerIds()).build());
//		}
//		viewLobby(gameData, socket, request);
//	}
//
//	/**
//	 * Starts a game and sends the relevant information to the player
//	 *
//	 * @param socket Current player session information
//	 * @param request {"mapId": "Risk"}
//	 */
//	public static void startGame(GameData gameData, Session socket, JsonObject request) {
//		if (gameData.getGameReady()) {
//			return;
//		}
//		// Check the player count
//		gameData.setPlayersActive(gameData.getPlayerList().size());
//		switch (gameData.getPlayerList().size()) {
//			case 0:
//			case 1:
//				System.out.println("Insufficient players to start a game.");
//				break;
//			case 2:
//				Player neutral = new Player("Neutral Player", "#555");
//				neutral.setNeutral(true);
//				gameData.getPlayerList().put(neutral.getPlayerId(), neutral);
//				break;
//		}
//		GameActions.startGame(gameData, request);
//	}
//
//	/**
//	 * If a user interacts with a territory, perform a map action as required
//	 *
//	 * @param socket Current player session information
//	 * @param request {"gameId": "Test's Game"}
//	 */
//	public static void territoryInteract(GameData gameData, Session socket, JsonObject request) {
//		String username = (String) socket.getUserProperties().get("username");
//		// Prevent player interaction when it's not their turn
//		if (!gameData.getConfig().getPlayerTurn().equals(username)) {
//			System.out.println("Player commands sent out of sequence");
//			return;
//		}
//		// Get the actual player object
//		Player player = gameData.getPlayerList().get(username);
//		if (player == null || player.isNeutral()) {
//			System.out.println("Active player not found");
//			return;
//		}
//		// Get the turn phase and run a game action from that
//		String turnPhase = gameData.getConfig().getTurnPhase();
//		try {
//			Method method = TerritoryActions.class.getMethod(turnPhase, Player.class, GameData.class, JsonObject.class);
//			method.invoke(TerritoryActions.class, player, gameData, request);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Error processing game action: " + turnPhase);
//		}
//	}
//
//	/**
//	 * If a user interacts with a territory, perform a map action as required
//	 *
//	 * @param socket Current player session information
//	 * @param request {"gameId": "Test's Game"}
//	 */
//	public static void playCards(GameData gameData, Session socket, JsonObject request) {
//		String username = (String) socket.getUserProperties().get("username");
//		// Prevent player interaction when it's not their turn or it's not the reinforcement phase
//		if (!gameData.getConfig().getPlayerTurn().equals(username) || !gameData.getConfig().getTurnPhase().equals("reinforce")) {
//			System.out.println("Player commands sent out of sequence");
//			return;
//		}
//		// Get the actual player object
//		Player player = gameData.getPlayerList().get(username);
//		if (player == null || player.isNeutral()) {
//			System.out.println("Active player not found");
//			return;
//		}
//		CardActions.useCards(gameData, player, request);
//	}
//
//	/**
//	 * Redeploy from one territory to another
//	 * @param socket Current player session information
//	 * @param request {"gameId": "Test's Game"}
//	 */
//	public static void redeploy(GameData gameData, Session socket, JsonObject request) {
//		// Prevent player interaction when it's not their turn
//		String username = (String) socket.getUserProperties().get("username");
//		if (!gameData.getConfig().getPlayerTurn().equals(username)) {
//			System.out.println("Player commands sent out of sequence");
//			return;
//		}
//		Configuration config = gameData.getConfig();
//		// Get the territories to transfer between
//		String fromTerrId = request.getString("from");
//		String toTerrId = request.getString("to");
//		Integer transSize = request.getInt("size");
//		Territory fromTerr = gameData.getGameMap().getTerritories().get(fromTerrId);
//		Territory toTerr = gameData.getGameMap().getTerritories().get(toTerrId);
//		if (fromTerr == null || toTerr == null || !fromTerr.getOwner().getPlayerId().equals(toTerr.getOwner().getPlayerId())) {
//			System.out.println("Unable to get transfer territories");
//			return;
//		}
//		// Update the territories
//		fromTerr.setBattalions(fromTerr.getBattalions() - transSize);
//		toTerr.setBattalions(toTerr.getBattalions() + transSize);
//		// Update the game
//		if (config.getTurnPhase().equals("redeploy")) {
//			config.setTurnPhase("reinforce");
//			GameActions.startTurn(gameData);
//		} else {
//			GameActions.updateAll(gameData, false, true, true);
//		}
//	}
//
//	/**
//	 * Proceed to the next phase
//	 *
//	 * @param socket Current player session information
//	 * @param request {"gameId": "Test's Game"}
//	 */
//	public static void nextPhase(GameData gameData, Session socket, JsonObject request) {
//		// Prevent player interaction when it's not their turn
//		String username = (String) socket.getUserProperties().get("username");
//		if (!gameData.getConfig().getPlayerTurn().equals(username)) {
//			System.out.println("Player commands sent out of sequence");
//			return;
//		}
//		// Progress turn if changable phase
//		Configuration config = gameData.getConfig();
//		switch (config.getTurnPhase()) {
//			case "attack":
//				if (gameData.getCardEarned()) {
//					gameData.setCardEarned(false);
//					CardActions.addCard(gameData, username, null, true);
//				} else {
//					config.setTurnPhase("redeploy");
//					GameActions.updateAll(gameData, true, false, false);
//				}
//				break;
//			case "redeploy":
//				config.setTurnPhase("reinforce");
//				GameActions.startTurn(gameData);
//				break;
//			default:
//				System.out.println("Unable to change turn phase.");
//		}
//	}
//
//	public static void viewLobby(GameData gameData, Session socket, JsonObject request) {
//		ArrayList<String> gameList = new ArrayList<>();
//		// Only add games that are not yet started
//		for (String key: WebSocketServerConfiguration.gameList.keySet()) {
//			GameData showGame = WebSocketServerConfiguration.gameList.get(key);
//			if (showGame != null && !showGame.getGameReady()) {
//				gameList.add(key);
//			}
//		}
//		// Count how many players are in the lobby
//		Integer lobbyPlayers = 0;
//		for (String player: WebSocketServerConfiguration.playerGames.keySet()) {
//			if (WebSocketServerConfiguration.playerGames.get(player).equals(WebSocketServerConfiguration.lobbyName)) {
//				lobbyPlayers++;
//			}
//		}
//		JsonObjectBuilder response = Json.createObjectBuilder();
//		response.add("gameList", JsonUtils.toArray(gameList)).add("activePlayers", lobbyPlayers);
//		Utils.sendGameMessage(WebSocketServerConfiguration.lobbyName, "gameLobby", response.build());
//	}

}
