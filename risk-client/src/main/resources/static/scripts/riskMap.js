var risk = risk || {};

risk.map = (function (d) {

    var config = risk.gameConfig,
        mapData,
        id;

    /**
     * Territory object
     *
     * @typedef {Object} Territory
     * @property {TerritoryData} data
     * @property {Kinetic.Group} group kinetic group
     * @property {Kinetic.Path} path kinetic path object
     * @property {Kinetic.Group} player holder for player info
     * @property {Kinetic.Circle} colour battalion background colour
     * @property {Kinetic.Text} count battalion count
     */

    /**
     *
     * @type {Object.<String, Territory>}
     */
    var interactions = [];
    const territories = {};

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
                var neighbour = mapData.territories[territory.neighbours[id]],
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
            neighbour = mapData.territories[id];
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

    return {

        /**
         *  Build Kinetic object and add to the map.
         *
         *  @param mapLayer maplayer to add territories to
         *  @param {TerritoryData} data territory data object
         *  @return {Territory}
         */
        buildTerritory: function (mapLayer, data) {

            let territory = {
                data: data,
                group: new Kinetic.Group(),
                path: new Kinetic.Path({
                    data: data.path,
                    fill: config.mapColours.territory
                }),
                player: new Kinetic.Group(),
                colour: new Kinetic.Circle({
                    x: data.center[0],
                    y: data.center[1],
                    radius: 12,
                    shadowColor: 'black',
                    shadowBlur: 4,
                    shadowOffset: {x: -2, y: 2},
                    shadowOpacity: 0.3
                }),
                count: new Kinetic.Text({
                    fontSize: 12,
                    fontStyle: 'Bold',
                    fill: '#fff'
                })
            };

            // Construct groups
            territory.group.add(territory.path);
            territory.player.add(territory.colour);
            territory.player.add(territory.count);
            territory.group.add(territory.player);

            // Add events
            territory.group
                .on('mousemove', function () {
                    if (interactions.find(t => t === data.id)) {
                        document.body.classList.add("select");
                        territory.path.setFill(config.mapColours.hover);
                        territory.group.moveTo(risk.topLayer);
                        risk.topLayer.draw();
                    }
                })
                .on('mouseout', function () {
                    if (interactions.find(t => t === data.id)) {
                        document.body.classList.remove("select");
                        territory.path.setFill(config.mapColours.territory);
                        territory.group.moveTo(risk.mapLayer);
                        risk.topLayer.draw();
                    }
                })
                .on('click', function () {
                    if (interactions.find(t => t === data.id)) {
                        risk.setup.sendCommand({action: 'territorySelect', territory: data.id});
                    }
                });

            return territory;
        },

        /**
         * Highlight a territory
         *
         * @param {String} territoryId
         * @param {Boolean} selected
         */
        highlightTerritory: function (territoryId, selected) {
            let territory = territories[territoryId];

            if (selected) {
                territory.path.setFill(config.mapColours.neighbour);
                territory.group.moveTo(risk.topLayer);
            } else {
                territory.pathObject.setFill(config.mapColours.territory);
                territory.group.moveTo(risk.mapLayer);
            }
        },

        /**
         *  Reset map to default for phase change
         *  @TODO
         */
        reset: function () {
            // Empty the top layer
            for (id in mapData.territories) {
                var territory = mapData.territories[id];
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
         *  Create Kinetic map layers and append the territories into it.
         *
         *  @param {MapSummary} data
         */
        render: function (data) {
            mapData = data;

            // Initiate a Kinetic stage
            let mapWrapper = document.createElement('main') || document.getElementById('riskMap');
            mapWrapper.id = 'riskMap';
            if (!mapWrapper.parentNode) {
                document.body.appendChild(mapWrapper);
            }

            risk.mapWrapper = new Kinetic.Stage({
                container: 'riskMap',
                width: risk.gameConfig.mapSize[0],
                height: risk.gameConfig.mapSize[1]
            });

            // Background layer shows the map
            risk.bgLayer = new Kinetic.Layer();
            let imgObj = new Image();
            imgObj.src = './assets/map.png';
            let img = new Kinetic.Image({
                image: imgObj
            });
            imgObj.onload = function () {
                risk.bgLayer.draw();
            };
            risk.bgLayer.add(img);
            // Map layer displays everything
            risk.mapLayer = new Kinetic.Layer();
            // Top layer used for interaction to improve performance
            risk.topLayer = new Kinetic.Layer();

            // Draw the territories and render the map
            mapData.territories
                .forEach(function (territoryData) {
                    territories[territoryData.id] = risk.map.buildTerritory(risk.mapLayer, territoryData);
                    risk.mapLayer.add(territories[territoryData.id].group);
                });

            risk.mapWrapper.add(risk.bgLayer);
            risk.mapWrapper.add(risk.mapLayer);
            risk.mapWrapper.add(risk.topLayer);

            this.scale();
        },

        /**
         * Set available territory interactions
         *
         * @param {String[]} territories
         */
        setInteractions: function (territories) {
            interactions = territories;
        },

        /**
         * Update a territory
         *
         * @param {TerritoryData} territoryData
         */
        updateTerritory: function (territoryData) {
            let territory = territories[territoryId];

            territory.colour.setFill('#000');
            territory.count.setText(territoryData.battalions);
        },

        /**
         *  Update the territory display
         *  @param {object} territory - territory data object
         *  @TODO
         */
        update: function (mapData) {
            // Check if the map needs constructing
            if (!mapData) {
                this.render(mapData);
            }
            for (id in mapData.territories) {
                var territory = mapData.territories[id],
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
         *
         *  @param {Integer} level - level of zoom
         *  @param {Boolean} [animate] - Whether changes are animated
         *  @todo
         */
        scale: function (level = 0, animate = false) {
            let defaultScale = risk.gameConfig.mapSize[0] / (parseInt(mapData.size[0])),
                currentScale = defaultScale + (risk.gameConfig.zoomLevel / 10),
                targetScale = defaultScale + (level / 10),
                targetWidth = parseInt(mapData.size[0]) * targetScale,
                targetHeight = parseInt(mapData.size[1]) * targetScale,
                scale = 0;

            // Animate the zoom on change
            if (risk.mapWrapper.isRunning && animate) {
                return
            }

            risk.gameConfig.zoomLevel = level;
            if (animate) {
                // Set the speed (ms) and the change
                let time = 0,
                    mapChange = targetScale - currentScale;
                // Create an animation container - better than timeout?
                let anim = new Kinetic.Animation(function (frame) {
                    // If we've reached the target, stop
                    if (currentScale === targetScale) {
                        risk.map.scale(risk.gameConfig.zoomLevel);
                        anim.stop();
                        return
                    }
                    // Decrement the time
                    time = (frame.timeDiff / 2000);
                    // Calculate the scale
                    currentScale += (mapChange * time);
                    currentScale = mapChange > 0 && currentScale >= targetScale ? targetScale : currentScale;
                    currentScale = mapChange < 0 && currentScale <= targetScale ? targetScale : currentScale;
                    // Update the map layers
                    scale = {x: currentScale, y: currentScale};
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
                scale = {x: targetScale, y: targetScale};
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