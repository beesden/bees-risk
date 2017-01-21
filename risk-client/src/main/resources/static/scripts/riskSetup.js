var risk = risk || {};

risk.setup = (function (d) {

    var config = risk.gameConfig,
        utils = risk.utils,
        id;

    var response = {
        receiveCommand: function (command) {
            command = JSON.parse(command.data);
            console.log('Receive command', command.action, command)
            // console.log(command);
            risk.gameConfig.username = command.username;
            risk.game[command.action](command.message);
        },
        sendCommand: function (command) {
            console.log('To server', command);
            command = JSON.stringify(command);
            risk.socket.send(command);
        },
        start: function () {
            // Reset the data
            risk.config = {};
            risk.gameData = {};
            risk.playerData = {};
            if (config.mute === 0) {
                risk.sfx.music();
            }
            risk.game.login();
        }
    };

    var connectToServer = function() {
        console.info("Establishing connection to server...");

        risk.socket = new WebSocket(risk.gameConfig.server + '/game');
        risk.socket.onmessage = response.receiveCommand;
        risk.socket.onclose = connectToServer;
    };

    connectToServer();


    return response;

})(document);

document.addEventListener('DOMContentLoaded', function () {
    risk.setup.start();
});