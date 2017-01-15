var risk = risk || {};

risk.game = (function (d) {

    var config = risk.gameConfig,
        utils = risk.utils,
        id;

    return {
        /**
         *  Show shared attack information between players
         *  @param {object} data - object containing latest attack information
         */
        attack: function (data) {
            var container = utils.createElement('ul', {'class': 'attack'}),
                player = data.attacker.player,
                sections = ['attacker', 'defender'],
                response = {'action': 'territoryInteract', 'attacker': data.attacker.id, 'defender': data.defender.id},
                title = utils.createElement('li', {'class': 'title'}, container),
                territory,
                side,
                sidePlayer,
                strength,
                visibleStrength,
                dicerollwrap,
                diceroll;
            title.innerHTML = risk.messages.attackTitle(data);
            // Display each side
            for (id in sections) {
                var sideId = sections[id],
                    territory = data[sideId];
                side = utils.createElement('li', {'class': 'combatant ' + sideId}, container);
                // Display territory player
                sidePlayer = utils.createElement('span', {'class': 'player'}, side);
                sidePlayer.innerHTML = territory.player;
                sidePlayer.style.borderColor = risk.playerData[territory.player].colour;
                // Dispaly territory strength
                strength = utils.createElement('span', {'class': 'strength'}, side);
                visibleStrength = data.results && data.conquer ? id == 0 ? data.attacker.battalions + data.defender.battalions : 0 : territory.battalions;
                strength.innerHTML = visibleStrength;
                // Display dice roll information if provided
                dicerollwrap = utils.createElement('span', {'class': 'results'}, side);
                if (data.results) {
                    data.results[sideId].sort().reverse();
                    for (var roll in data.results[sideId]) {
                        diceroll = utils.createElement('em', {'class': data.results[sideId][roll]}, dicerollwrap);
                        diceroll.innerHTML = data.results[sideId][roll];
                    }
                    // Add a little CSS animatino to show losing forces
                    if (data.results.combat && data.results.combat[id] > 0) {
                        strength.setAttribute('data-previous', visibleStrength + parseInt(data.results.combat[id]));
                    }
                }
                // Update the map data
                for (id in territory) {
                    risk.mapData.territories[territory.id][id] = territory[id];
                }
            }
            // If last player territory taken, Show player defeated popup instead of normal popup
            if (data.reason) {
                this[data.riskCards && config.username == risk.config.playerTurn ? 'viewCards' : 'playerOut'](data, function (response) {
                    if (config.username === risk.config.playerTurn) {
                        risk.game.redeploy(response.attacker.id, response.defender.id, true, 0)
                    } else {
                        risk.popups.hide();
                    }
                });
                return;
            }
            var showCancel = player == config.username && !(data.conquer) ? 'Retreat' : false,
                showConfirm = player == config.username && data.conquer ? 'Confirm' : player === config.username && data.attacker.battalions > 1 ? 'Attack' : false;
            // Show popup and update players on command
            risk.popups.show(container, showCancel, showConfirm, function (e) {
                if (data.conquer) {
                    risk.game.redeploy(response.attacker, response.defender, true, 0)
                } else {
                    response.attack = 'attack';
                    risk.setup.sendCommand(response);
                }
            }, function (e) {
                response.attack = 'retreat';
                risk.setup.sendCommand(response);
            });
        },
        /**
         *  Allow chat messages with everyone in the same room
         *  @param {object} data - array containing chat message
         */
        chatMessage: function (data) {
            if (!risk.ui.chat) {
                return
            }
            var chatMessage = utils.createElement('li', {'class': 'message'}),
                chatUser = utils.createElement('span', null, chatMessage);
            chatUser.innerHTML = data.sender + ': ';
            chatMessage.innerHTML += data.message;
            risk.ui.chat.messageList.appendChild(chatMessage);
            if (data.sender !== config.username) {
                risk.ui.chat.heading.classList.add('pending');
                risk.sfx.play('chat');
            }
        },
        /**
         *  Display a list of available games on the server
         *  @param {object} data - array containing list of game IDs
         */
        gameLobby: function (data) {
            var gameList = utils.createElement('dl', {'class': 'riskGames'}),
                gameTitle = utils.createElement('dt', null, gameList),
                gameSelectWrap,
                gameSelect;
            gameTitle.innerHTML = 'Choose a game from the list';
            // Add available games to the list
            data.forEach(function (game) {
                gameSelectWrap = utils.createElement('dd');
                gameSelect = utils.createElement('button', {'value': game.gameName}, gameSelectWrap);
                gameSelect.innerHTML = game.gameName;
                gameSelect.onclick = function (e) {
                    e.preventDefault();
                    risk.setup.sendCommand({'action': 'joinGame', 'gameId': this.value});
                };
                gameList.appendChild(gameSelectWrap);
            });
            // Display the popup
            risk.popups.show(gameList, false, 'New Game', function (e) {
                risk.setup.sendCommand({'action': 'createGame'});
            });
        },
        /**
         *  Join a specific game
         *  @param {object} data - object containing config and player information
         */
        gameSetup: function (data) {
            // Create a list of players based on the player data
            var playerList = utils.createElement('ul', {'class': 'riskPlayers'}),
                player,
                playerData,
                i = 0;
            // Populate the player list
            data.players.forEach(function(player) {
                playerEl = utils.createElement('li');
                playerEl.style.borderColor = '#000';
                playerEl.innerHTML = player;
                playerList.appendChild(playerEl);
                if (id === config.username) {
                    config.playerColour = data.playerList[id];
                    playerEl.onclick = function () {
                        risk.setup.sendCommand({'action': 'playerColour'});
                    }
                }
            });
            if (data.config) {
                this.updateConfig(data.config);
            }
            // Display the popup
            risk.popups.show(playerList, 'Leave game', risk.config.leadPlayer === risk.gameConfig.username && Object.keys(data.playerList).length > 1 ? 'Start game' : false, function (e) {
                if (risk.playerData.length < 2) {
                    return;
                }
                risk.setup.sendCommand({'action': 'startGame', 'mapId': 'risk'});
            }, function () {
                risk.setup.sendCommand({'action': 'leaveGame', 'close': false, 'username': risk.gameConfig.username});
            });
        },
        /**
         *  Show player defeated popup
         *  @param {string} fromTerr - territory id where units are being sent from
         *  @param {string} toTerr - territory id where units are being sent to
         *  @param {boolean} force - Allow cancel button
         *  @param {integer} min - minimum quantity of battalions that must be moved {default:1}
         */
        playerOut: function (data) {
            // Create a list of players based on the player data
            var playerLeft = utils.createElement('div', {'class': 'playerOut'});
            data.playerId = data.player.playerId;
            // Update playerdata / config
            if (data.player) {
                risk.playerData[data.player.playerId] = data.player;
            }
            if (data.config) {
                risk.config = data.config;
            }
            // Display in a popup
            playerLeft.innerHTML = risk.messages.playerOut(data);
            risk.popups.show(playerLeft, false, 'Confirm', function () {
                if (typeof callback === 'function') {
                    callback(data);
                } else {
                    risk.popups.hide()
                }
            }, null);
        },
        /**
         *  Display the redeploy popup
         *  @param {string} fromTerr - territory id where units are being sent from
         *  @param {string} toTerr - territory id where units are being sent to
         *  @param {boolean} force - Allow cancel button
         *  @param {integer} min - minimum quantity of battalions that must be moved {default:1}
         */
        redeploy: function (fromTerr, toTerr, force, min) {
            // Get the full territory objects
            fromTerr = risk.mapData.territories[fromTerr];
            toTerr = risk.mapData.territories[toTerr];
            min = isNaN(min) ? 1 : min;
            // Work out the max / min values
            var maxDeploy = fromTerr.battalions - 1,
                redeployForm = utils.createElement('form', {'class': 'redeploy'}),
                redeployExport = utils.createElement('output', {'class': 'from'}, redeployForm),
                redeployInport = utils.createElement('output', {'class': 'to'}, redeployForm),
                redeployRange = utils.createElement('input', {
                    'type': 'range',
                    'max': maxDeploy,
                    'min': min
                }, redeployForm),
                command = {'action': 'redeploy', 'from': fromTerr.id, 'to': toTerr.id};
            // Show the change visually
            redeployRange.onchange = function (e) {
                redeployInport.innerHTML = '<em>' + toTerr.name + ': </em>' + (parseInt(redeployRange.value) + toTerr.battalions);
                redeployExport.innerHTML = '<em>' + fromTerr.name + ': </em>' + (fromTerr.battalions - parseInt(redeployRange.value));
            }
            redeployRange.onchange();
            // If there are less than the mimumum, move them all
            if (maxDeploy <= min) {
                command.size = min;
                risk.setup.sendCommand(command);
            } else {
                // Otherwise display the popup
                risk.popups.show(redeployForm, !force ? 'Cancel' : false, 'Redeploy', function () {
                    command.size = parseInt(redeployRange.value);
                    risk.setup.sendCommand(command);
                })
            }
        },
        /**
         *  Merge the current config with an updated one
         *  @param {object} data - object containing updated configuration
         */
        updateAll: function (data) {
            risk.popups.hide();
            risk.ui.build();
            if (data.config) {
                this.updateConfig(data.config);
            }
            if (data.playerData) {
                this.updatePlayers(data.playerData);
            }
            if (data.mapData) {
                risk.map.update(data.mapData);
            }
            risk.ui.updateInfo();
            // Show the cards on turn start if force
            if (risk.config.turnPhase === 'reinforce' && risk.config.playerTurn === config.username && risk.playerData[config.username].riskCards.length > 4) {
                this.viewCards();
            }
        },
        /**
         *  Merge the current config with an updated one
         *  @param {object} data - object containing updated configuration
         */
        updateConfig: function (data) {
            // Create config if required
            if (!risk.config) {
                risk.config = {};
            }
            for (id in data) {
                risk.config[id] = data[id];
            }
        },
        /**
         *  Merge the current config with an updated one
         *  @param {object} data - object containing updated configuration
         */
        updatePlayers: function (data) {
            // Create config if required
            if (!risk.playerData) {
                risk.playerData = {};
            }
            for (id in data) {
                risk.playerData[id] = data[id];
            }
            risk.ui.updateInfo();
        },
        useCards: function (data) {
            var cardMessage = utils.createElement('span');
            cardMessage.innerHTML = data.message;
            risk.ui.controls.cards.setAttribute('data-count', risk.playerData[config.username].riskCards.length);
            // Display the popup
            risk.popups.show(cardMessage, false, 'Confirm', function (e) {
                risk.popups.hide();
            });
        },
        victory: function (data) {
            var victoryMessage = utils.createElement('span');
            victoryMessage.innerHTML = data.message;
            risk.config.gameFinished = true;
            // Display the popup
            risk.popups.show(victoryMessage, false, 'Confirm', function (e) {
                risk.popups.hide();
            });
        },
        viewCards: function (data, callback) {
            data = data || {};
            var cardArray = data.riskCards || risk.playerData[config.username].riskCards,
                playable = !data.riskCards && risk.config.turnPhase === 'reinforce' && cardArray.length > 2 && risk.config.playerTurn === config.username,
                cardList = utils.createElement('div', {'class': 'cardList'}),
                cardListMessage = utils.createElement('span', null, cardList),
                cardLabel,
                cardInput,
                card;
            cardListMessage.innerHTML = data.reason ? 'Player defeated - cards earned' : 'View your cards';
            // Validation on card select
            function cardSelect() {
                // Prevent selection unless in the reinforcement phase and < 3 cards selected
                if (!playable || cardList.querySelectorAll('input').length < 3 || cardList.querySelectorAll('input:checked').length > 3 || risk.config.turnPhase !== 'reinforce') {
                    return false;
                }
            }

            // Get list of displayable cards
            for (id in cardArray) {
                // Add a label and an input to each risk card
                card = cardArray[id];
                cardData = risk.mapData.territories[card];
                cardInput = utils.createElement('input', {
                    'name': card,
                    'id': card,
                    'type': 'checkbox',
                    'value': cardData ? cardData.cardValue : 0
                }, cardList);
                cardLabel = utils.createElement('label', {'for': card}, cardList);
                cardLabel.innerHTML = cardData ? cardData.name : '';
                cardInput.onclick = function (e) {
                    cardSelect();
                }
                if (data.addCards) {
                    risk.playerData[config.username].riskCards.push(card);
                }
            }
            // Update the view button
            risk.ui.controls.cards.setAttribute('data-count', risk.playerData[config.username].riskCards.length);
            // Display the popup
            risk.popups.show(cardList, data.riskCards || (cardArray.length > 4 && playable) ? false : 'Close', playable ? 'Play cards' : data.riskCards ? 'Confirm' : false, function (e) {
                if (callback && typeof callback === 'function') {
                    callback(data);
                    return;
                }
                // Card earned at end of attack
                if (risk.config.turnPhase === 'attack') {
                    risk.setup.sendCommand({'action': 'nextPhase'});
                }
                // Cards earned during any other phase
                else if (data.riskCards) {
                    risk.popups.hide();
                }
                // Playing cards during reinforcement phase
                else {
                    var response = {'action': 'playCards'}
                    useSelected = cardList.querySelectorAll('input:checked');
                    if (useSelected.length !== 3) {
                        return;
                    }
                    for (id in useSelected) {
                        response['card' + (parseInt(id) + 1)] = useSelected[id].name;
                    }
                    risk.setup.sendCommand(response);
                }
            });
        }
    }

})(document);