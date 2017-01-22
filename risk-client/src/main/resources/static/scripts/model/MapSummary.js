'use strict';


/**
 * Server MapSummary response object
 *
 * @typedef {Object} ServerData~MapData
 * @property {String} id map id
 * @property {TerritoryData[]} territories map territories
 * @property {ContinentData[]} contninents map continents
 */

/**
 * Server ContinentSummary response object
 *
 * @typedef {Object} ContinentData
 * @property {String} id id of territory
 * @property {String} name display name of continent
 * @property {String} colour territory colour
 */

/**
 * Server TerritorySummary response object
 *
 * @typedef {Object} TerritoryData
 * @property {String} id id of territory
 * @property {String} name name of territory
 * @property {Integer} battalions strength of battalions at the territory
 * @property {String} continentId id of continent this territory is part of
 * @property {Integer[]} center co-ordinates of territory center
 * @property {String} path path to draw territory from
 */

/**
 * Server PlayerSummary response object
 *
 * @typedef {Object} PlayerData
 * @property {Integer} id player id
 * @property {String} name player name
 * @property {String} colour player colour
 * @property {Object.<String, Integer>} territories number of battalions by territory
 * @property {Integer} cardCount number of cards owned
 * @property {Boolean} active if a player is currently undefeated
 */

/**
 * Server TurnSummary response object
 *
 * @typedef {Object} TurnData
 * @property {String} phase turn phase
 * @property {PlayerData[]} players current player info
 * @property {Integer} current player id whose turn it is
 * @property {Integer} reinforcements available reinforcements
 * @property {String[]} list of interactive territory IDs
 */

class MapSummary {
    constructor(custData) {
        this.id = custData.id;
        this.name = custData.name;
        this.groupId = custData.groupId;
        this.status = custData.status;
        this.state = this._getState(custData.status);
    }

    _getState(status) {
        let state = (status == 'active' ? 'good' : 'bad');
        return state;
    }
}