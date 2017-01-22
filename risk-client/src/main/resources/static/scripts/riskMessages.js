{
    const messages = {

        'mutelevel.0': 'Play all sounds',
        'mutelevel.1': 'Play sound effects only',
        'mutelevel.2': 'Mute all sounds',

        'turnphase.initial': 'Select a territory to claim',
        'turnphase.deploy': '$0 battallions left to deploy',
        'turnphase.reinforce': '$0 battallions left to deploy',
        'turnphase.attack': 'Select a territory to attack',
        'turnphase.redeploy': 'Select a territory to redeploy to'

    };

    risk.messages = function(key, ...params) {
        console.log('Get message', key, params);

        let message = messages[key.toLowerCase()] || '';

        params.forEach(function(param, index) {
            message.replace(new RegExp('$' + index, 'g'), param);
        });

        return message;
    }
}