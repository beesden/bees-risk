var risk = risk || {config: {}};

risk.popups = (function (d) {

    var overlay = document.createElement('div');
    overlay.className = 'overlay';

    var toggleOverlay = function () {
        if (overlay.parentNode) {
            overlay.parentNode.removeChild(overlay);
        } else {
            document.body.appendChild(overlay);
        }
    };

    /**
     * Popup model
     *
     * @param template
     * @constructor
     */
    var Popup = function () {
        var _self = this;

        _self.content = document.createElement('div');
        _self.content.className = 'popup';
        _self.listeners = [];
    };

    Popup.prototype = {

        /**
         * Close the popup
         */
        close: function () {
            var _self = this;

            if (_self.content.parentNode) {
                _self.content.parentNode.removeChild(_self.content);
            }
            if (overlay.parentNode) {
                toggleOverlay()
            }
            return _self;
        },

        /**
         * Register an event within the modal
         *
         * @type {String} type event type
         * @param {String} lookup
         * @param {Function} callback
         */
        on: function (type, lookup, callback) {
            var _self = this;

            _self.listeners.push({type: type, lookup: lookup, callback: callback});

            _self.content.querySelectorAll(lookup).forEach(function (el) {
                el.addEventListener(type, function (event) {
                    callback(event, _self.content);
                });
            });

            return _self;
        },

        /**
         * Open the popup
         *
         * @returns {Popup}
         */
        open: function (content) {
            var _self = this;

            _self.refresh(content);

            d.body.appendChild(_self.content);
            if (!overlay.parentNode) {
                toggleOverlay();
            }

            return _self;
        },

        /**
         * Changes the content within the popup
         *
         * @param {String} content
         * @returns {Popup}
         */
        refresh: function (content) {
            var _self = this;

            _self.content.innerHTML = content;

            _self.listeners.forEach(function (listener) {
                _self.content.querySelectorAll(listener.lookup).forEach(function (el) {
                    el.addEventListener(listener.type, function (event) {
                        listener.callback(event, _self.content);
                    });
                });
            });

            return _self;
        }
    };

    var popup;

    return {
        /**
         *  Hide the popup window
         */
        hide: function () {
            popup.close();
        },

        removeAll: function() {
            // TODO - allow multiple popups
            if (popup) {
                popup.close();
            }
        },

        /**
         *  Hide the various modals that appear
         *  @param {object} content - DOM object
         *  @param {string} [cancel] - message to dispaly for the cancel button - hides if false
         *  @param {string} [confirm] - message to dispaly for the confirm button - hides if false
         *  @param {function} [success] - command to be run on confirm
         *  @param {function} [failure] - command to be run on cancel - overrides the default
         */
        show: function (content, cancel, confirm, success, failure) {

            popup = new Popup();
            popup.open(content);

            return popup;

            // // Update the cancel / confirm buttons as appropriate
            // if (confirm && typeof success === 'function') {
            //     popup.confirm.classList.remove('hide');
            //     popup.confirm.innerHTML = confirm;
            //     popup.confirm.onclick = success;
            // } else {
            //     popup.confirm.classList.add('hide');
            // }
            // popup.cancel.onclick = function (e) {
            //     e.preventDefault();
            //     if (typeof failure === 'function') {
            //         failure();
            //     } else {
            //         risk.popups.hide();
            //     }
            // }
            // popup.cancel.classList[cancel ? 'remove' : 'add']('hide');
            // popup.cancel.innerHTML = cancel;
            // // Display the popup content
            // popup.replaceChild(content, popup.content)
            // popup.content = content;
            // d.body.appendChild(popupOverlay);
            // d.body.appendChild(popup);
        }
    }

})(document);