'use strict';

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