var risk = risk || {};

risk.map = (function (d) {

    var config = risk.gameConfig,
    	utils = risk.utils,
        id;

	/**
	 *  Place player colour and battalions on the map
	 *  @param {object} territory - updated territory object
	 */
	function deployTerritory(update) {
		var territory = risk.mapData.territories[update.id],
			group = territory.group,
			sprite = new Kinetic.Group();
		// Circle object is used as the player colour
		var circle = new Kinetic.Circle({
			x: territory.center[0],
			y: territory.center[1],
			radius: 12,
			fill: risk.playerData[update.player].colour,
			shadowColor: 'black',
			shadowBlur: 4,
			shadowOffset: {x:-2,y:2},
			shadowOpacity: 0.3
		});
		// Text object is used as the player battalion count
		var text = new Kinetic.Text({
			fontSize: 12,
			fontStyle: 'Bold',
			fill: '#fff'
		});
		// Add to map layer
		sprite.add(circle);
		sprite.add(text);
		group.add(sprite);
		// Update territory object
		territory.playerColour = circle;
		territory.playerStrength = text;
	}

	/**
	 *  Convert territory paths to kinetic path object and add them to the map
	 *  @param {string} id - territory id reference
	 */
	function territoryDisplay() {
		for (id in risk.mapData.territories) {
			var territory = risk.mapData.territories[id],
				continent = risk.mapData.continents[territory.continent],
				group = new Kinetic.Group();
			// Create kinetic territory object
			var pathObject = new Kinetic.Path({
				data: territory.path,
				fill: config.mapColours.territory
			});
			// Add path to group and group to map layer
			group.add(pathObject);
			risk.mapLayer.add(group);
			// Create references on territory object
			territory.id = id;
			territory.group = group;
			territory.pathObject = pathObject;
			territory.contintent = continent;
			continent.territories[id] = territory
			// Configure map interactions / battalion display
			territoryInteractions(territory);
		}
	}

    /**
     *  Highlight neighbours to a specific territory
     *  @param {object} territory - Territory object to find neighbours of
     *  @param {boolean} select - Whether to highlight or deslect neighbours
     *  @param {boolean} friendly - Highlight friendly (true) or enemy (false) territories
     *  @param {boolean} recursive - Whether to find all adjacent countries or immediate neighbours only
     *  @param {object} neighbourList - Pass through list of neighbours to ignore
     */
    function highlightNeighbours(territory, select, friendly, recursive, neighbourList) {
        // 	Gather list of neighbours if required
        config.neighbours = config.neighbours || {};
        neighbourList = neighbourList || {};
        if (select) {
            for (id in territory.neighbours) {
                var neighbour = risk.mapData.territories[territory.neighbours[id]],
                	isFriendly = neighbour.player == risk.config.playerTurn;
                neighbourList[territory.id] = territory.id;
                if (neighbour.id != config.activeTerritory && friendly ? isFriendly : !isFriendly) {
                    config.neighbours[neighbour.id] = neighbour;
                }
                if (recursive && (friendly ? isFriendly : !isFriendly) && !neighbourList[neighbour.id]) {
                    highlightNeighbours(neighbour, select, friendly, recursive, neighbourList);
                }
            }
        }
        // Highlight / deselect neighbours
        neighbourList = config.neighbours;
        for (id in neighbourList) {
            neighbour = risk.mapData.territories[id];
            if (select) {
                neighbour.pathObject.setFill(config.mapColours.neighbour);
                neighbour.group.moveTo(risk.topLayer);
                config.neighbours = neighbourList;
            } else {
                neighbour.pathObject.setFill(config.mapColours.territory);
                neighbour.group.moveTo(risk.mapLayer);
                config.neighbours = {};
            }
        }
    }

	function territoryInteractions(territory) {
		// Territory variables
		var group = territory.group,
			territoryList = risk.mapLayer.children,
			currentNeighbours;
		// Hoverable if: during territory selection at start of game; territory owned by current player; territory is linked to active territory
		var territoryHover = function (territory) {
			var yourTurn = config.username === risk.config.playerTurn,
				yourTerritory = !territory.player || territory.player == risk.config.playerTurn,
				isActive = !config.activeTerritory || config.activeTerritory == territory.id,
				isNeighbour = config.neighbours && config.neighbours[territory.id]
			// Can only hover on your turn;
			return yourTurn && ((isActive && yourTerritory) || isNeighbour);
		};
		// Set opacity to 1 on hover
		group.on('mousemove', function () {
			if (territoryHover(territory) && config.activeTerritory != territory.id) {
				d.body.classList.add("select");
				currentNeighbours = config.neighbours || {};
				territory.pathObject.setFill(config.mapColours.hover);
				group.moveTo(risk.topLayer);
				risk.topLayer.draw();
			}
		});
		// Set opacity to default on hoverend
		group.on('mouseout', function () {
			if (territoryHover(territory) && config.activeTerritory != territory.id) {
				currentNeighbours = config.neighbours || {};
				territory.pathObject.setFill(config.mapColours[currentNeighbours[territory.id] ? 'neighbour' : 'territory']);
				// Move out of top layer unless a neighbour
				if (!currentNeighbours[territory.id]) {
					group.moveTo(risk.mapLayer);
				}
				risk.topLayer.draw();
				if (config.updateMap) {
					config.updateMap = false;
					risk.mapLayer.draw();
				}
			}
			d.body.classList.remove("select");
		});
		// Run various game functions on click
		group.on('click', function (e) {
			if (territoryHover(territory)) {
				var friendly = risk.config.turnPhase == 'redeploy',
					recursive = friendly && !risk.config.noRecursion;
				switch(risk.config.turnPhase) {
					case 'deploy':
					case 'reinforce':
						risk.setup.sendCommand({'action':'territoryInteract','territory':territory.id});
						break;
					case 'attack':
					case 'redeploy':
						// Insufficient battalions
						if (!config.activeTerritory && territory.battalions === 1) {
							console.log("insufficient force to interact"); // @TODO - move to info
						}
						// Select if no territory
						else if (!config.activeTerritory) {
							highlightNeighbours(territory, true, friendly, recursive);
			                if (!Object.keys(config.neighbours).length) {
			                    console.log("no neighbours"); // @TODO - move to info
								return
			                }
			                config.activeTerritory = territory.id;
			                territory.pathObject.setFill(config.mapColours.select);
			                territory.group.moveTo(risk.topLayer);
			                risk.topLayer.draw();
			                console.log("Select neighbour"); // @TODO - move to info
			            }
			            // If already selected, deslect territory
			            else if (config.activeTerritory == territory.id) {
			                config.activeTerritory = null;
			                territory.pathObject.setFill(config.mapColours.hover);
			                highlightNeighbours(territory, false, friendly, recursive);
			                risk.topLayer.draw();
			                console.log("Territory deselected"); // @TODO - move to info
			            }
			            // Else interact with neighbour
			            else if (config.neighbours && config.neighbours[territory.id]) {
			            	// Show the new redeploy popup
			            	if (friendly) {
			            		risk.game.redeploy(config.activeTerritory, territory.id);
			            	} else {
								risk.setup.sendCommand({'action':'territoryInteract','attack':'start','attacker':config.activeTerritory,'defender':territory.id});
			            	}
			            }
						break;
				}
			}
		});
	}

	return {
		/**
		 *  Reset map to default for phase change
		 */
		reset: function () {
			// Empty the top layer
			for (id in risk.mapData.territories) {
				var territory = risk.mapData.territories[id];
				territory.group.moveTo(risk.mapLayer);
				territory.pathObject.setFill(config.mapColours.territory)
			}
			// Reset all maps and config
			config.activeTerritory = config.neighbours = null;
			config.updateMap = false;
			risk.mapLayer.draw();
			risk.topLayer.draw();
		},
		/**
		 *  Create the inital map object
		 *  @param {string} id - id of the container to create the map in
		 */
		render: function (data) {
			risk.mapData = data;
			//Initiate a Kinetic stage
			d.getElementById('riskMap') || utils.createElement('div', {'id':'riskMap'}, d.body);
			risk.mapWrapper = new Kinetic.Stage({
				container: 'riskMap',
				width: risk.gameConfig.mapSize[0],
				height: risk.gameConfig.mapSize[1]
			});
			// Background layer shows the map
			risk.bgLayer = new Kinetic.Layer();
		    var imgObj = new Image();
		    imgObj.src = './assets/map.png';
		    var img = new Kinetic.Image({
		        image: imgObj,
		    });
		    imgObj.onload = function() {
		    	risk.bgLayer.draw();
		    }
		    risk.bgLayer.add(img);
			// Map layer displays everything
			risk.mapLayer = new Kinetic.Layer();
			 // Top layer used for interaction to improve performance
			risk.topLayer = new Kinetic.Layer();
			// Draw the territories and render the map
			territoryDisplay();
			risk.mapWrapper.add(risk.bgLayer);
			risk.mapWrapper.add(risk.mapLayer);
			risk.mapWrapper.add(risk.topLayer);
			this.zoomMap();
		},
		/**
		 *  Update the territory display
		 *  @param {object} territory - territory data object
		 */
		update: function (mapData) {
			// Check if the map needs constructing
			if (!risk.mapData) {
				this.render(mapData);
			}
			for (id in mapData.territories) {
				var territory = risk.mapData.territories[id],
					update = mapData.territories[id];
				// Don't update unclaimed territories
				if (!update.player) {
					continue;
				}
				// Deploy if previously unclaimed
				if (!territory.playerColour) {
					deployTerritory(update);
				}
				// Update territory information
				territory.playerColour.setFill(risk.playerData[update.player].colour);
				territory.playerStrength.setText(update.battalions || 1);
				territory.battalions = update.battalions;
				territory.player = update.player;
				// Adjust territory icon
				territory.playerStrength.setX(territory.playerColour.getX() - territory.playerStrength.getWidth() / 2);
				territory.playerStrength.setY(territory.playerColour.getY() - territory.playerStrength.getHeight() / 2);
			}
			this.reset();
		},
		/**
		 *  Update map scale
		 *  @param {integer} level - level of zoom
		 *  @param {boolean} animate - Whether changes are animated
		 */
		zoomMap: function(level, animate) {
			level = level || 0;
			var defaultScale = risk.gameConfig.mapSize[0] / (parseInt(risk.mapData.size[0])),
				currentScale = defaultScale - (-risk.gameConfig.zoomLevel / 10),
				targetScale = defaultScale - (-level / 10),
				targetWidth = parseInt(risk.mapData.size[0]) * targetScale,
				targetHeight = parseInt(risk.mapData.size[1]) * targetScale,
				scale = 0;
			// Animate the zoom on change
			if (risk.mapWrapper.isRunning && animate) {
				return
			}
			risk.gameConfig.zoomLevel = level;
			if (animate) {
				// Set the speed and the change
				var speed = 0.4,
					time = 0,
					mapChange = targetScale - currentScale,
					wrapChange = (config.mapSize[0] * config.zoomLevel);
				// Create an animation container - better than timeout?
				var anim = new Kinetic.Animation(function(frame) {
					// If we've reached the target, stop
					if (currentScale === targetScale) {
				 		risk.map.zoomMap(risk.gameConfig.zoomLevel);
				 		anim.stop();
				 		return
				 	}
				 	// Decrement the time
				 	time = (frame.timeDiff / (1000 * speed));
				 	// Calculate the scale
					currentScale += (mapChange * time);
					currentScale = mapChange > 0 && currentScale >= targetScale ? targetScale : currentScale;
					currentScale = mapChange < 0 && currentScale <= targetScale ? targetScale : currentScale;
					// Update the map layers
					scale = {x:currentScale, y:currentScale};
					risk.bgLayer.scale(scale);
					risk.mapLayer.scale(scale);
					risk.topLayer.scale(scale);
					// Update the map wrapper dimensions as well
					risk.mapWrapper.height(targetHeight * (currentScale / targetScale));
					risk.mapWrapper.width(targetWidth * (currentScale / targetScale));
				}, risk.mapWrapper);
				risk.mapWrapper.isRunning = true;
				anim.start();
			}
			// Statically update map zoom
			else {
				risk.mapWrapper.isRunning = false;
				scale = {x:targetScale, y:targetScale};
				if (level <= 0) {
					risk.mapWrapper.setHeight(risk.gameConfig.mapSize[1] - (risk.gameConfig.mapSize[1] * (-level / 10)))
				}
				risk.bgLayer.scale(scale);
				risk.mapLayer.scale(scale);
				risk.topLayer.scale(scale);
				risk.bgLayer.draw();
				risk.mapLayer.draw();
				risk.topLayer.draw();
			}
		}
	}

})(document);