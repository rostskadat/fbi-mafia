(function() {
    'use strict';

    var CosaNostraConstants = {
        COLUMN_DEFS : [ {
            field : 'name',
            displayName : 'Name',
            cellTemplate : 'fileItemName.html'
        }, {
            field : 'fileItemType',
            displayName : 'Type',
            cellFilter : 'fileType',
        }, {
            field : 'lastModifiedTime',
            displayName : 'Last Modified',
            enableFiltering: false,
            cellFilter : 'friendlyDate',
            type: 'date'
        }, {
            field : 'size',
            displayName : 'Size',
            enableFiltering: false,
            cellFilter : 'size',
            type: 'number'
        }, {
            field : 'action',
            displayName : 'Actions',
            cellTemplate : 'fileItemActions.html',
            width: '10%',
            enableColumnResizing: false,
            enableFiltering: false
        } ],
    };

    angular.module('fbi-mafia.cosaNostra').constant('CosaNostraConstants', CosaNostraConstants);

})();