{
    risk.templates = {

        /**
         * Render a template and display it in a popup
         *
         * @param {String} templateId template ID
         * @param {Object} [data] template data
         * @returns {Popup}
         */
        popup: function (templateId, data) {
            let template = this.render(templateId, data);
            risk.popups.removeAll();
            return risk.popups.show(template);
        },

        /**
         * Get a template and render it with data
         *
         * @param {String} templateId template ID
         * @param {Object} [dataModel] template data
         * @returns {String} templated HTML string
         */
        render: function (templateId, dataModel) {
            console.log('Render template', templateId, dataModel);

            let template = Handlebars.templates[templateId.replace(/_/g, '/')];
            if (template.constructor === Function) {
                return template(dataModel);
            }
        }
    };

    Handlebars.registerHelper("message", risk.messages);
}