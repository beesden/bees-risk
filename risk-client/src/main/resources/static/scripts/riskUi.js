var risk = risk || {config:{}};

risk.ui = (function (d) {

    var config = risk.gameConfig,
    	utils = risk.utils,
        id;

	/**
	 *  Creates the game button elements
	 */
	function createChatWindow() {
		var chatUi = risk.ui.chat = utils.createElement('div', {'class':'chatBox'}, d.body);
		// Rules button - displays custom rules
		chatUi.heading = utils.createElement('span', {'class':'title'}, chatUi);
		chatUi.heading.innerHTML = 'Game Chat';
		chatUi.messageList =  utils.createElement('ul', {'class':'messageList'}, chatUi)
		chatUi.messageForm =  utils.createElement('form', null, chatUi)
		chatUi.messageInput =  utils.createElement('textarea', {'placeholder':'Write message...','name':'message'}, chatUi.messageForm)
		chatUi.messageForm.onsubmit = function(e) {
			e.preventDefault();
			if (chatUi.messageInput.value) {
				risk.setup.sendCommand({'action':'chat','message':chatUi.messageInput.value});
				chatUi.messageInput.value = "";
			}
		}
		chatUi.messageInput.onkeyup = function(e){
			e.preventDefault();
			if (e.keyCode === 13) {
				chatUi.messageForm.onsubmit(e);
			}
		 }
    	chatUi.heading.onmousemove = function() {
    		this.classList.remove('pending');
    		}
	}

	/**
	 *  Creates the game button elements
	 */
	function createButtons() {
		var controlUi = risk.ui.controls = utils.createElement('ul', {'class':'controlList'}, d.body);
		// Rules button - displays custom rules
		controlUi.options = utils.createElement('li', {'class':'uiButton ctrl options', 'data-tooltip': 'View game options'}, controlUi);
		controlUi.options.onclick = function() {
			var customRules = utils.createElement('span');
			customRules.innerHTML = 'View the game rules';
			risk.popups.show(customRules, 'Close');
		}
		// Surrender button - turn player neutral
		controlUi.surrender = utils.createElement('li', {'class':'uiButton ctrl surrender', 'data-tooltip': 'Surrender'}, controlUi);
		controlUi.surrender.onclick = function() {
			var surrender = utils.createElement('span');
			if (risk.playerData[config.username].isNeutral || risk.config.gameFinished) {
				surrender.innerHTML = 'Are you sure you wish to leave the game session?';
				risk.popups.show(surrender, 'No wait...', 'Yes, I\'m sure', function() {
					risk.setup.sendCommand({'action':'leaveGame','close':true,'username':risk.gameConfig.username});
				})
			} else {
				surrender.innerHTML = 'Are you sure you wish to surrender?';
				risk.popups.show(surrender, 'No wait...', 'Yes, I\'m sure', function() {
					controlUi.surrender.classList.remove('surrender');
					controlUi.surrender.classList.add('leave');
					controlUi.surrender.setAttribute('data-tooltip', 'Leave game');
					risk.setup.sendCommand({'action':'leaveGame','close':false,'username':risk.gameConfig.username});
				});
			}
		}
		// Mute button - turns off sound effects
		controlUi.cards = utils.createElement('li', {'class':'uiButton ctrl cards', 'data-tooltip': 'View risk cards', 'data-count': 0}, controlUi);
		controlUi.cards.onclick = function() {
			risk.game.viewCards();
		}
		// Mute button - turns off sound effects
		controlUi.sound = utils.createElement('li', {'class':'uiButton ctrl mute-' + config.mute, 'data-tooltip': config.muteLevel[config.mute]}, controlUi);
		controlUi.sound.onclick = function() {
			risk.sfx.toggleMute();
			controlUi.sound.setAttribute('data-tooltip', config.muteLevel[config.mute]);
			controlUi.sound.className = 'uiButton ctrl mute-' + config.mute;
		}
	}

	/**
	 *  Assemble the minimap Ui elements
	 */
	function createMapUi() {
		var mapUi = risk.ui.map = utils.createElement('ul', {'class':'mapControls'}, d.body);
		// Display zoom controls
		mapUi.zoomIn = utils.createElement('li', {'class':'zoom zoomIn'}, mapUi);
		mapUi.zoomIn.onclick = function() {
			if (config.zoomLevel > 1) {
				return false;
			}
			risk.map.zoomMap(config.zoomLevel + 1, true);
		}
		mapUi.zoomOut = utils.createElement('li', {'class':'zoom zoomOut'}, mapUi);
		mapUi.zoomOut.onclick = function() {
			if (config.zoomLevel < -1) {
				return false;
			}
			risk.map.zoomMap(config.zoomLevel - 1, true);
		}
	}

	/**
	 *  Assemble the player info Ui elements
	 */
	function createPlayerUi() {
		var playerUi = risk.ui.info = utils.createElement('div', {'class':'playerInfo'}, d.body);
		// Show current player
		playerUi.playerName = utils.createElement('span', {'class':'currentPlayer'}, playerUi);
		playerUi.playerName.innerHTML = config.username;
		playerUi.playerName.style.borderColor = config.playerColour;
		// Display other player info
		playerUi.playerList = utils.createElement('ul', {'class':'playerList'}, playerUi);
	}

	/**
	 *  Assemble the various Ui elements
	 */
	function createTurnUi() {
		var turnUi = risk.ui.turn = utils.createElement('ul', {'class':'turnInfo'}, d.body);
		// End turn phase button
		turnUi.turnPhase = utils.createElement('li', {'class':'uiButton turnPhase'}, turnUi, 'End Turn');
		turnUi.turnPhase.onclick = function() {
			if (config.username !== risk.config.playerTurn || (risk.config.turnPhase != 'attack' && risk.config.turnPhase != 'redeploy')) {
				return false;
			}
			var confirm = utils.createElement('span');
			confirm.innerHTML = "Are you sure you wish to end the " + risk.config.turnPhase + " phase?"
			risk.popups.show(confirm, 'Hold on...', 'Yes, I\'m sure', function(e) {
				e.preventDefault();
				risk.setup.sendCommand({'action':'nextPhase'});
			});

		}
		turnUi.turnMessage = utils.createElement('li', {'class':'message'}, turnUi);
	}

	function calculateStrength() {
		var territory;
		// Reset counters
		config.globalStrength = 0;
		config.playerStrength = {};
		// Loop through territories and update player / config
		for (id in risk.mapData.territories) {
			territory = risk.mapData.territories[id];
			// Create player count if missing
			if (!config.playerStrength[territory.player]) {
				config.playerStrength[territory.player] = 0;
			}
			// Update counts
			config.playerStrength[territory.player] += territory.battalions;
			config.globalStrength += territory.battalions;
		}
	}

	function refreshUI() {
		risk.ui.controls.surrender.classList.remove('leave');
		risk.ui.controls.surrender.setAttribute('data-tooltip', 'Surrender');
	}

	return {
		/**
		 *  Redeploy units from one army to another
		 */
		build: function () {
			// Assemble Ui wrappers
			if (!risk.ui.isBuilt) {
				createChatWindow();
				createButtons();
				createMapUi();
				createPlayerUi();
				createTurnUi();
				risk.ui.isBuilt = true;
			} else {
				refreshUI();
			}
		},
		/**
		 *  Update player information panel
		 */
		updateInfo: function () {
			// Update player information
			var playerInfo = risk.ui.info,
				showButton = risk.config.playerTurn === config.username && (risk.config.turnPhase === 'attack' || risk.config.turnPhase === 'redeploy');
			if (!playerInfo) {
				console.error('Either the player object or ui wrapper cannot be found');
				return
			}
			// Calculate how many battalions each player controls
			if (risk.mapData) {
				calculateStrength();
			}
			// Build the player list object
			playerInfo.playerList.innerHTML = '';
			playerInfo.playerName.style.borderColor = config.playerColour;
			for (id in risk.playerData) {
				var player = risk.playerData[id],
					playerData = utils.createElement('li', null, playerInfo.playerList),
					playerName = utils.createElement('span', {'class':'name'}, playerData),
					playerStrength = utils.createElement('span', {'class':'strength'}, playerData),
					playerCards = utils.createElement('span', {'class':'cards'}, playerData),
					playerRank = utils.createElement('span', {'class':'rank'}, playerData);
				// Populate the elements
				playerName.innerHTML = player.playerId;
				playerName.style.borderColor = player.colour;
				playerStrength.innerHTML = (player.territories || 0) + ' territories';
				playerCards.innerHTML = Object.keys(player.riskCards).length + ' cards';
				if (config.playerStrength) {
					playerRank.style.width = (config.playerStrength[player.playerId] / config.globalStrength) * 100 + '%';
				}
			}
			// Update turn information
			var turn = risk.ui.turn,
				turnInfo = risk.config.playerTurn + ': ' + risk.messages.turn(risk.config.turnPhase);
			if (!turn) {
				console.error('Either the player object or ui wrapper cannot be found');
				return
			}
			// Update turn info
			turn.turnPhase.classList[showButton ? 'remove' : 'add']('disabled');
			turn.turnPhase.innerHTML = 'Finish ' + risk.config.turnPhase + ' phase';
			turn.turnMessage.innerHTML = risk.messages.turn();
			if (!risk.history) {
				risk.history = [];
			}
			risk.history.push(turnInfo); // @TODO - log messages properly
		}
	}

})(document);