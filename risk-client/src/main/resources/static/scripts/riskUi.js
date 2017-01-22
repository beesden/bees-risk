{
    var config = risk.gameConfig,
        utils = risk.utils;

    /**
     *  Creates the game button elements
     */
    function createChatWindow() {
        // var chatUi = risk.ui.chat = utils.createElement('div', {'class':'chatBox'}, document.body);
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
     *  Assemble the minimap Ui elements
     *
     *  todo - merge with existing functions
     */
    function createMapUi() {
        var mapUi = risk.ui.map = utils.createElement('ul', {'class': 'mapControls'}, document.body);
        // // Display zoom controls
        // mapUi.zoomIn = utils.createElement('li', {'class':'zoom zoomIn'}, mapUi);
        // mapUi.zoomIn.onclick = function() {
        // 	if (config.zoomLevel > 1) {
        // 		return false;
        // 	}
        // 	risk.map.scale(config.zoomLevel + 1, true);
        // }
        // mapUi.zoomOut = utils.createElement('li', {'class':'zoom zoomOut'}, mapUi);
        // mapUi.zoomOut.onclick = function() {
        // 	if (config.zoomLevel < -1) {
        // 		return false;
        // 	}
        // 	risk.map.scale(config.zoomLevel - 1, true);
        // }
    }

    /**
     *  Assemble the various Ui elements
     */
    function createTurnUi() {
        var turnUi = risk.ui.turn = utils.createElement('ul', {'class': 'turnInfo'}, document.body);
        // End turn phase button
        turnUi.turnPhase = utils.createElement('li', {'class': 'uiButton turnPhase'}, turnUi, 'End Turn');
        turnUi.turnPhase.onclick = function () {
            if (config.username !== config.playerTurn || (config.turnPhase != 'attack' && config.turnPhase != 'redeploy')) {
                return false;
            }
            var confirm = utils.createElement('span');
            confirm.innerHTML = "Are you sure you wish to end the " + config.turnPhase + " phase?"
            risk.popups.show(confirm, 'Hold on...', 'Yes, I\'m sure', function (e) {
                e.preventDefault();
                risk.setup.sendCommand({'action': 'nextPhase'});
            });

        };
        turnUi.turnMessage = utils.createElement('li', {'class': 'message'}, turnUi);
    }

    risk.ui = {
        /**
         *  Redeploy units from one army to another
         *
         *  @param turnInfo turn information
         */
        build: function (turnInfo) {
            this.clear();

            risk.ui.wrapper = document.createElement('nav');
            risk.ui.wrapper.className = 'uiWrapper';
            document.body.appendChild(risk.ui.wrapper);

            this.update(turnInfo);
        },

        /**
         * Clear all game content
         */
        clear: function () {
            // TODO - move the game into it's own element, e.g. for embedding?
            while (document.body.hasChildNodes()) {
                document.body.removeChild(document.body.lastChild);
            }
        },

        /**
         * Add listeners for controls, including sound control, view config, etc
         *
         * @param {HTMLElement} wrapper
         */
        registerControls: function (wrapper) {

            // Config button
            wrapper.querySelectorAll('[data-action="showConfig')
                .forEach(el => el.addEventListener('click', function () {
                        risk.setup.sendCommand({action: 'showConfig'});
                    })
                );

            // Surrender button
            wrapper.querySelectorAll('[data-action="surrender')
                .forEach(el => el.addEventListener('click', function () {
                        risk.templates.popup('surrender')
                            .on('click', '.confirm', function () {
                                risk.setup.sendCommand({'action': 'surrender'});
                            })
                            .on('click', '.cancel', function (event, popup) {
                                popup.close();
                            });
                    })
                );

            // Cards button
            wrapper.querySelectorAll('[data-action="showCards')
                .forEach(el => el.addEventListener('click', function () {
                        risk.setup.sendCommand({action: 'showCards'});
                    })
                );

            // Toggle mute level button
            wrapper.querySelectorAll('[data-action="toggleMute"]')
                .forEach(el => el.addEventListener('click', function () {
                        let muteLevel = risk.sfx.toggleMute();
                        console.log(muteLevel);
                        this.setAttribute('data-tooltip', config.muteLevel[muteLevel]);
                        this.setAttribute('data-mute', muteLevel);
                    })
                );

        },

        /**
         *  Re-generate the UI
         *
         *  @param turnInfo turn information
         */
        update: function (turnInfo) {

            // Calculate strength
            turnInfo.globalStrength = 0;

            // Get the local player
            turnInfo.currentPlayer = turnInfo.players.find(player => player.name === config.username);

            // Calculate player and global strength values
            turnInfo.players.forEach(player => {
                player.strength = 0;
                Object.values(player.territories).forEach(territory => player.strength += territory);
                turnInfo.globalStrength += player.strength;
            });

            // Calculate percentage strength
            turnInfo.players.forEach(function (player) {
                player.strength = player.strength / turnInfo.globalStrength;
            });

            // Generate
            risk.ui.wrapper.innerHTML = risk.templates.render('ui', turnInfo);

            this.registerControls(risk.ui.wrapper);
        }
    }

}