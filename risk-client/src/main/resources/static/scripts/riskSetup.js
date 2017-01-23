{
    let config = risk.gameConfig;

    risk.setup = {
        receiveCommand: function (command) {
            command = JSON.parse(command.data);

            console.log('-------');
            console.log('Receive game command', command.action, command.message);

            config.userId = command.userId;
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

            risk.socket = new WebSocket((window.location.protocol === 'https:' ? 'wss://' : 'ws://') + window.location.host + '/game');
            risk.socket.onmessage = risk.setup.receiveCommand;
            risk.socket.onopen = risk.game.login;
            risk.socket.onclose = risk.setup.start;
        }
    };

    document.addEventListener('DOMContentLoaded', function () {
        risk.setup.start();
    });

}