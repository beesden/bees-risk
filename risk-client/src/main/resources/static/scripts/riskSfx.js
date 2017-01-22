var risk = risk || {};

risk.sfx = (function (d) {

    var config = risk.gameConfig,
    	utils = risk.utils,
        id;

    return {
    	music: function(muteLevel) {
            var musicObj = risk.sfx.musicObj;
    		if (!musicObj) {
    			musicObj =document.createElement('audio');
                musicObj.track = 0;
                musicObj.src = './sounds/music.mp3';
    			musicObj.volume = 0.3;
                risk.sfx.musicObj = musicObj;
    		}
    		musicObj[muteLevel ? 'pause' : 'play']();
            // Play it again, Sam!
            musicObj.onended = function() {
                risk.sfx.nextTrack();
            }
    	},
        nextTrack: function() {
            var musicObj = risk.sfx.musicObj,
                newTrack;
            do {
                newTrack = Math.floor(Math.random() * 6);
            } while (newTrack === musicObj.track);
            musicObj.track = newTrack
            musicObj.src = './sounds/music.mp3';
            musicObj[risk.gameConfig.mute ? 'pause' : 'play']();
        },
    	play: function(file) {
    		if (config.mute === 2) {
    			return;
    		}
    		var audio =document.createElement('audio');
    		audio.src = './sounds/' + file + '.mp3';
    		audio.play();
    	},
    	toggleMute: function(muteLevel) {
			config.mute = muteLevel || (config.mute + 1) % 3;
			risk.sfx.music(config.mute);
			return config.mute;
    	}
    }

})(document);