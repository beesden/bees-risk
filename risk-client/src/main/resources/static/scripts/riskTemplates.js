(function () {
    'use strict';

    risk.templates = {

        /**
         * Render a template and display it in a popup
         *
         * @param {String} templateId template ID
         * @param {Object} [data] template data
         * @returns {Popup}
         */
        popup: function (templateId, data) {
            var template = this.render(templateId, data);
            risk.popups.removeAll();
            return risk.popups.show(template);
        },

        /**
         * Get a template and render it with data
         *
         * @param {String} templateId template ID
         * @param {Object} [data] template data
         * @returns {String} templated HTML string
         */
        render: function (templateId, data) {
            console.log('Render template', templateId, data);
            return Handlebars.templates[templateId.replace(/_/g, '/')](data);
        }
    };

    Handlebars.registerHelper("xif", function (expression, options) {
        return Handlebars.helpers["x"].apply(this, [expression, options]) ? options.fn(this) : options.inverse(this);
    });

})();