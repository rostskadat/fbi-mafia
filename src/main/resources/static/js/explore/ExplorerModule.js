(function() {
    'use strict';

    /**
     * @memberof fbi-mafia.explorer
     * @ngdoc module
     * @description The main module for the explore functionaity
     */
    var module = angular.module('fbi-mafia.explorer', [ 'ngRoute', 'ngFileSaver', 'ui.bootstrap', 'ui.grid', 'ui.grid.saveState',
            'ui.grid.autoResize', 'ui.grid.resizeColumns', 'ui.grid.exporter', 'ui.grid.expandable',
            'ui.grid.selection', 'ui.grid.treeView', 'smart-table' ]);

    module.run(function() {
        // Init code here...
    });

})();
