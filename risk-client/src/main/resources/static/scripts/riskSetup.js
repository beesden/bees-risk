{
    let config = risk.gameConfig;

     risk.setup = {
        receiveCommand: function (command) {
            command = JSON.parse(command.data);

            console.log('-------');
            console.log('Receive game command', command.action, command.message);

            risk.gameConfig.username = command.username;
            risk.game[command.action](command.message);
        },
        sendCommand: function (command) {
            console.log('To server', command);
            console.log('-------');

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

            console.info("Establishing connection to server...");

            risk.socket = new WebSocket(risk.gameConfig.server + '/game');
            risk.socket.onmessage = this.receiveCommand;
            risk.socket.onopen = risk.game.login;
            risk.socket.onclose = this.start;
        }
    };

    document.addEventListener('DOMContentLoaded', function () {
        risk.setup.start();
    });

};