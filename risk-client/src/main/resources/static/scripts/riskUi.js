var risk = risk || {config: {}};

risk.ui = (function (d) {

    var config = risk.gameConfig,
        utils = risk.utils;

    /**
     *  Creates the game button elements
     */
    function createChatWindow() {
        // var chatUi = risk.ui.chat = utils.createElement('div', {'class':'chatBox'}, d.body);
        // // Rules button - displays custom rules
        // chatUi.heading = utils.createElement('span', {'class':'title'}, chatUi);
        // chatUi.heading.innerHTML = 'Game Chat';
        // chatUi.messageList =  utils.createElement('ul', {'class':'messageList'}, chatUi)
        // chatUi.messageForm =  utils.createElement('form', null, chatUi)
        // chatUi.messageInput =  utils.createElement('textarea', {'placeholder':'Write message...','name':'message'}, chatUi.messageForm)
        // chatUi.messageForm.onsubmit = function(e) {
        // e.preventDefault();
        // if (chatUi.messageInput.value) {
        // 	risk.setup.sendCommand({'action':'chat','message':chatUi.messageInput.value});
        // 	chatUi.messageInput.value = "";
        // }
        // }
        // chatUi.messageInput.onkeyup = function(e){
        // e.preventDefault();
        // if (e.keyCode === 13) {
        // 	chatUi.messageForm.onsubmit(e);
        // }
        // }
        // chatUi.heading.onmousemove = function() {
        // 	this.classList.remove('pending');
        // 	}
    }

    /**
     *  Creates the game button elements
     */
    function createButtons() {

        // Config button
        risk.ui.wrapper.querySelector('[data-action="showConfig').addEventListener('click', function () {
            risk.setup.sendCommand('showConfig');
        });

        // Cards button
        risk.ui.wrapper.querySelector('[data-action="showConfig').addEventListener('click', function () {
            risk.setup.sendCommand('showCards');
        });

        risk.ui.wrapper.querySelector('[data-action="surrender').addEventListener('click', function () {
            risk.templates.popup('surrender')
                .on('click', '.confirm', function () {
                    risk.setup.sendCommand({'action': 'surrender'});
                })
                .on('click', '.cancel', function (event, popup) {
                    popup.close();
                });
        });

        risk.ui.wrapper.querySelector('[data-action="toggleMute"]').addEventListener('click', function () {
            var muteLevel = risk.sfx.toggleMute();
            this.setAttribute('data-tooltip', config.muteLevel[muteLevel]);
            this.setAttribute('data-mute-level', muteLevel);
        });
    }

    /**
     *  Assemble the minimap Ui elements
     */
    function createMapUi() {
        var mapUi = risk.ui.map = utils.createElement('ul', {'class': 'mapControls'}, d.body);
        // // Display zoom controls
        // mapUi.zoomIn = utils.createElement('li', {'class':'zoom zoomIn'}, mapUi);
        // mapUi.zoomIn.onclick = function() {
        // 	if (config.zoomLevel > 1) {
        // 		return false;
        // 	}
        // 	risk.map.zoomMap(config.zoomLevel + 1, true);
        // }
        // mapUi.zoomOut = utils.createElement('li', {'class':'zoom zoomOut'}, mapUi);
        // mapUi.zoomOut.onclick = function() {
        // 	if (config.zoomLevel < -1) {
        // 		return false;
        // 	}
        // 	risk.map.zoomMap(config.zoomLevel - 1, true);
        // }
    }

    /**
     *  Assemble the various Ui elements
     */
    function createTurnUi() {
        var turnUi = risk.ui.turn = utils.createElement('ul', {'class': 'turnInfo'}, d.body);
        // End turn phase button
        turnUi.turnPhase = utils.createElement('li', {'class': 'uiButton turnPhase'}, turnUi, 'End Turn');
        turnUi.turnPhase.onclick = function () {
            if (config.username !== risk.config.playerTurn || (risk.config.turnPhase != 'attack' && risk.config.turnPhase != 'redeploy')) {
                return false;
            }
            var confirm = utils.createElement('span');
            confirm.innerHTML = "Are you sure you wish to end the " + risk.config.turnPhase + " phase?"
            risk.popups.show(confirm, 'Hold on...', 'Yes, I\'m sure', function (e) {
                e.preventDefault();
                risk.setup.sendCommand({'action': 'nextPhase'});
            });

        }
        turnUi.turnMessage = utils.createElement('li', {'class': 'message'}, turnUi);
    }

    return {
        /**
         *  Redeploy units from one army to another
         *
         *  @param turnInfo turn information
         */
        build: function (turnInfo) {
            this.clear();

            risk.ui.wrapper = d.createElement('nav');
            risk.ui.wrapper.className = 'uiWrapper';
            d.body.appendChild(risk.ui.player);

            this.update(turnInfo);
        },

        /**
         * Clear all game content
         */
        clear: function () {
            // TODO - move the game into it's own element, e.g. for embedding?
            while (d.body.hasChildNodes()) {
                d.body.removeChild(d.body.lastChild);
            }
        },

        /**
         *  Re-generate the UI
         *
         *  @param turnInfo turn information
         */
        update: function (turnInfo) {

            // Calculate strength
            turnInfo.globalStrength = 0;
            var playerStrength = {};
            turnInfo.territories
                .filter(function (territory) {
                    return territory.player;
                }).forEach(function (territory) {
                if (!playerStrength[territory.player]) {
                    playerStrength[territory.player] = 0;
                }
                playerStrength[territory.player] += territory.battalions;
                strength.global += territory.battalions;
            });

            // Get the local player
            turnInfo.currentPlayer = turnInfo.players.find(function (player) {
                return config.username === player.name;
            });

            // Add additional player info
            turnInfo.players.forEach(function (player) {
                player.strength = playerStrength[player.id] / turnInfo.globalStrength;
            });

            // Generate
            risk.ui.wrapper.innerHTML = risk.templates.render('ui', turnInfo);
        }
    }

})(document);