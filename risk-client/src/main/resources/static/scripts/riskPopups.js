var risk = risk || {config:{}};

risk.popups = (function (d) {

    var config = risk.gameConfig,
    	utils = risk.utils,
        id;

	// Popup variable and wrapper
	var popup = utils.createElement('div', {'class': 'popup'}),
		popupOverlay = utils.createElement('div', {'class':'overlay'});
	// Popup content container
	popup.content = utils.createElement('div', {'class':'content'});
	popup.appendChild(popup.content);
	// Popup buttons
	popup.cancel = utils.createElement('button', {'class':'button cancel', 'value':'cancel'}, popup);
	popup.confirm = utils.createElement('button', {'class':'button confirm', 'value':'confirm'}, popup);
	// Add a mute button
	popup.mute = utils.createElement('span', {'class':'uiButton ctrl mute-' + config.mute, 'data-tooltip': config.muteLevel[config.mute]}, popup);
	popup.mute.onclick = function() {
		risk.sfx.toggleMute();
		popup.mute.setAttribute('data-tooltip', config.muteLevel[config.mute]);
		popup.mute.className = 'uiButton ctrl mute-' + config.mute;
	};

	return {
		/**
		 *  Hide the popup window
		 */
		hide: function() {
			if (popup.parentNode == d.body) {
				d.body.removeChild(popupOverlay);
				d.body.removeChild(popup);
			}
		},
		/**
		 *  Hide the various modals that appear
		 *  @param {object} content - DOM object
		 *  @param {string} cancel - message to dispaly for the cancel button - hides if false
		 *  @param {string} confirm - message to dispaly for the confirm button - hides if false
		 *  @param {function} success - action to be run on confirm
		 *  @param {function} failure - action to be run on cancel - overrides the default
		 */
		show: function(content, cancel, confirm, success, failure) {
			// Only show if there is content and at least one actionable button
			if (!content) {
				console.warn('Invalid popup requested');
				return
			}
			// Update the cancel / confirm buttons as appropriate
			if (confirm && typeof success === 'function') {
				popup.confirm.classList.remove('hide');
				popup.confirm.innerHTML = confirm;
				popup.confirm.onclick = success;
			} else {
				popup.confirm.classList.add('hide');
			}
			popup.cancel.onclick = function (e) {
				e.preventDefault();
				if (typeof failure === 'function') {
					failure();
				} else {
					risk.popups.hide();
				}
			}
			popup.cancel.classList[cancel ? 'remove' : 'add']('hide');
			popup.cancel.innerHTML = cancel;
			// Display the popup content
			popup.replaceChild(content, popup.content)
			popup.content = content;
			d.body.appendChild(popupOverlay);
			d.body.appendChild(popup);
		}
	}

})(document);