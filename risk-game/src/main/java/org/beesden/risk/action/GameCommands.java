package org.beesden.risk.action;

public class GameCommands {

	//		public static void useCards(GameData gameData, GamePlayers player, JsonObject request) {
	//			// Cards can only be played during the reinforcement phase
	//			if (!gameData.getConfig().getTurnPhase().equals("reinforce")) {
	//				System.out.println("Cards may only be played during the reinforcement phase");
	//				return;
	//			}
	//			// Validate the cards
	//			String card1 = request.getString("card1");
	//			String card2 = request.getString("card2");
	//			String card3 = request.getString("card3");
	//			if (card1 == null || card2 == null || card3 == null || card1.equals(card2) || card1.equals(card3) || card2.equals(card3)) {
	//				System.out.println("Error playing cards: same card selected");
	//				return;
	//			}
	//			// Build array of card values
	//			int[] cardArray = new int[3];
	//			Map<String, Territory> territoryList = gameData.getGameMap().getTerritories();
	//			for (int i = 1; i < 4; i++) {
	//				String cardId = request.getString("card" + i);
	//				if (!cardId.startsWith("wild-") && territoryList.getByPlayerId(cardId) == null) {
	//					System.out.println("Unable to find card value for cardId " + cardId);
	//					return;
	//				}
	//				cardArray[i - 1] = cardId.startsWith("wild-") ? 0 : territoryList.getByPlayerId(cardId).getCardValue();
	//			}
	//			Integer cardValue = calculateCardValue(gameData, cardArray);
	//			// Get reinforcement value
	//			if (cardValue > 0) {
	//				Configuration config = gameData.getConfig();
	//				Integer reinforcements = 0;
	//				// Incremental card values
	//				if (config.getCardLevel() > 5) { // TODO - add config option
	//					reinforcements = config.getCardLevel() > 5 ? (config.getCardLevel() - 2) * 5 : config.getCardBonus()[config.getCardLevel()];
	//					config.setCardLevel(config.getCardLevel() + 1);
	//				}
	//				// Static card values
	//				else {
	//					reinforcements = (2 * cardValue) + 2;
	//				}
	//				removeCard(gameData, player.getPlayerId(), card1);
	//				removeCard(gameData, player.getPlayerId(), card2);
	//				removeCard(gameData, player.getPlayerId(), card3);
	//				// Update player
	//				String message = Utils.getBundle("risk.cards.use", player.getPlayerId(), reinforcements);
	//				player.setReinforcements(player.getReinforcements() + reinforcements);
	//				// Send game back to deployment phase
	//				config.setTurnPhase("reinforce");
	//				GameActions.updateAll(gameData, true, true, true);
	//			}
	//		}
//
//	/**
//	 * Adds the current session / player to a currently existing game.
//	 *
//	 * @param socket Current player session information
//	 * @param request {gameId: {string}
//	 */
//	public static void playerColour(GameData gameData, Session socket, JsonObject request) {
//		// Get the game player
//		String username = (String) socket.getUserProperties().getByPlayerId("username");
//		GamePlayers player = gameData.getPlayerList().getByPlayerId(username);
//		// Get the current player colour
//		Integer colourIndex = player.getColour() == null ? 0 : Arrays.asList(gameData.getColours()).indexOf(player.getColour());
//		String colour;
//		do {
//			colourIndex = colourIndex < 0 ? 0 : colourIndex > 5 ? 0 : colourIndex;
//			colour = gameData.getColours()[colourIndex++];
//		} while (gameData.getPlayerColours().getByPlayerId(colour) != null);
//		gameData.getPlayerColours().put(colour, username);
//		gameData.getPlayerColours().remove(player.getColour());
//		player.setColour(colour);
//		// Update the game session
//		Utils.sendGameMessage(gameData.getGameId(), "gameSetup", Json.createObjectBuilder().add("playerList", gameData.playerIds()).build());
//	}
//
//
//	/**
//	 * If a user interacts with a territory, perform a map action as required
//	 *
//	 * @param socket Current player session information
//	 * @param request {"gameId": "Test's Game"}
//	 */
//	public static void territoryInteract(GameData gameData, Session socket, JsonObject request) {
//		String username = (String) socket.getUserProperties().getByPlayerId("username");
//		// Prevent player interaction when it's not their turn
//		if (!gameData.getConfig().getPlayerTurn().equals(username)) {
//			System.out.println("GamePlayers commands sent out of sequence");
//			return;
//		}
//		// Get the actual player object
//		GamePlayers player = gameData.getPlayerList().getByPlayerId(username);
//		if (player == null || player.isNeutral()) {
//			System.out.println("Active player not found");
//			return;
//		}
//		// Get the turn phase and run a game action from that
//		String turnPhase = gameData.getConfig().getTurnPhase();
//		try {
//			Method method = TerritoryActions.class.getMethod(turnPhase, GamePlayers.class, GameData.class, JsonObject.class);
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
//		String username = (String) socket.getUserProperties().getByPlayerId("username");
//		// Prevent player interaction when it's not their turn or it's not the reinforcement phase
//		if (!gameData.getConfig().getPlayerTurn().equals(username) || !gameData.getConfig().getTurnPhase().equals("reinforce")) {
//			System.out.println("GamePlayers commands sent out of sequence");
//			return;
//		}
//		// Get the actual player object
//		GamePlayers player = gameData.getPlayerList().getByPlayerId(username);
//		if (player == null || player.isNeutral()) {
//			System.out.println("Active player not found");
//			return;
//		}
//		CardService.useCards(gameData, player, request);
//	}
//
//	/**
//	 * Redeploy from one territory to another
//	 * @param socket Current player session information
//	 * @param request {"gameId": "Test's Game"}
//	 */
//	public static void redeploy(GameData gameData, Session socket, JsonObject request) {
//		// Prevent player interaction when it's not their turn
//		String username = (String) socket.getUserProperties().getByPlayerId("username");
//		if (!gameData.getConfig().getPlayerTurn().equals(username)) {
//			System.out.println("GamePlayers commands sent out of sequence");
//			return;
//		}
//		Configuration config = gameData.getConfig();
//		// Get the territories to transfer between
//		String fromTerrId = request.getString("from");
//		String toTerrId = request.getString("to");
//		Integer transSize = request.getInt("size");
//		Territory fromTerr = gameData.getGameMap().getTerritories().getByPlayerId(fromTerrId);
//		Territory toTerr = gameData.getGameMap().getTerritories().getByPlayerId(toTerrId);
//		if (fromTerr == null || toTerr == null || !fromTerr.getGameOwner().getPlayerId().equals(toTerr.getGameOwner().getPlayerId())) {
//			System.out.println("Unable to getByPlayerId transfer territories");
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
//		String username = (String) socket.getUserProperties().getByPlayerId("username");
//		if (!gameData.getConfig().getPlayerTurn().equals(username)) {
//			System.out.println("GamePlayers commands sent out of sequence");
//			return;
//		}
//		// Progress turn if changable phase
//		Configuration config = gameData.getConfig();
//		switch (config.getTurnPhase()) {
//			case "attack":
//				if (gameData.getCardEarned()) {
//					gameData.setCardEarned(false);
//					CardService.addCard(gameData, username, null, true);
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

}
