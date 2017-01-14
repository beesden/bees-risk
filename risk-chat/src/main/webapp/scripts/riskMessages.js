var risk = risk || {};

risk.messages = (function (d) {

    var config = risk.gameConfig,
    	utils = risk.utils,
        id;

	return {
		attackTitle: function(data) {
			return data.attacker.name + ' attacking ' + data.defender.name;
		},
		playerOut: function(data) {
			var message = data.playerId;
			switch (data.reason) {
				case 'defeat':
					message += ' has been defeated';
					break;
				case 'close':
					message += ' has left the game';
					break;
				case 'surrender':
					message += ' has surrendered';
					break;
			}
			return message;

		},
		turn: function() {
			var currentPlayer = risk.playerData[risk.config.playerTurn],
				message;
			switch (risk.config.turnPhase) {
				case 'deploy':
					message = 'Select an unclaimed territory to deploy to';
					break;
				case 'reinforce':
					message = currentPlayer.reinforcements + " battalions left to place";
					break;
				case 'attack':
					message = 'Select a territory to attack';
					break;
				case 'redeploy':
					message = 'Select a territory to redeploy';
					break;
			}
			return '<em>' + currentPlayer.playerId + ': </em>' + message;
		}
	}

})(document);