var risk = risk || {};

risk.setup = (function (d) {

    var config = risk.gameConfig,
    	utils = risk.utils,
        id;

    return {
    	receiveCommand: function(command) {
	    	command = JSON.parse(command.data);
	    	// console.log(command);
    		risk.gameConfig.username = command.username;
	    	risk.game[command.action](command.message);
	    },
    	sendCommand: function(command) {
	    	command = JSON.stringify(command);
	    	risk.socket.send(command);
	    },
        start: function () {
        	// Close existing connections
        	if (risk.socket) {
        		risk.socket.close();
        	}
        	// Reset the data
        	risk.config = {};
        	risk.gameData = {};
        	risk.playerData = {};
        	if (config.mute === 0) {
	        	risk.sfx.music();
        	}
        	// Create a form to get username
        	var userForm = utils.createElement('div', {'class':'riskUser'}),
				userLabel =  utils.createElement('label', {'for':'riskUser'}, userForm),
				userField =  utils.createElement('input', {'id':'riskUser','name':'username','placeholder':'Enter name...','type':'text'}, userForm),
				random = Math.floor(Math.random() * config.defaultNames.length);
			userField.value = config.defaultNames[random];
			// Mute button - turns off sound effects
			userLabel.innerHTML = 'Select a name to connect with';
			// Establish websocket connection on submit
			risk.popups.show(userForm, false, 'Connect', function(e) {
				e.preventDefault();
				var username = userField.value,
					wsocket;
				if (!userField.disabled && username) {
					console.info("Establishing connection to server...");
					// Prevent multiple connections from the same client
					userField.disabled = true;
					// Connect to the server
					risk.socket = new WebSocket(risk.gameConfig.server + '/game/' + username);
					risk.socket.onmessage = risk.setup.receiveCommand;
					risk.socket.onclose = risk.setup.start;
				}
			});
        }
    }

})(document);

document.addEventListener('DOMContentLoaded', function () {
    risk.setup.start();
});