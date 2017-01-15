var risk = risk || {config:{}};

risk.utils = (function(d) {

	return {
		/**
		 *  Utility function to create DOM element
		 *  @param {string} el - element nody type: e.g. div, ul, span etc
		 *  @param {object} attrs - custom element attributes: e.g. {'type':'range', 'name':'strength'}
		 *  @param {object} parent - parent element container for new element
		 */
		createElement: function(el, attrs, parent) {
			var el = d.createElement(el);
			this.setAttributes(el, attrs);
			if (parent) {
				parent.appendChild(el);
			}
			return el;
		},
		/**
		 *  Update multiple attributes at once on a particular element
		 *  @param {object} el - element object to be updated
		 *  @param {object} attrs - key / value map for new attributes
		 */
		setAttributes: function(el, attrs) {
			for(var key in attrs) {
				el.setAttribute(key, attrs[key]);
			}
			return el;
		},
		/**
		 *  Convert absolute SVG paths to relative
		 *  @param {string} id - id of the container to create the map in
		 */
		updateSVGPaths: function(pathObject) {
			for (path in pathObject) {
				var path2 = pathObject[path];
				if (path2.match(/z, m/)) {
					var first = pathObject[path].match(/^m ([-\d\.\,]+)/)[1];
					first = first.split(',');
					pathObject[path] = pathObject[path].replace(/z, m ([-\d\.\,]+)/g, function(a) {
						b = a.replace('z, m ', '').split(',');
						c = 'z, m ' + (b[0] - first[0]) + ',' + (b[1] - first[1]);
						first = b;
						return c;
					});


				}
			}
		}

	}

})(document);