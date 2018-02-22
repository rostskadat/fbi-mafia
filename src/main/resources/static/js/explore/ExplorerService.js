(function() {
    'use strict';

    var module = angular.module('fbi-mafia.explorer');

    module.factory('ExplorerService', [ '$http', ExplorerService ]);

    /**
     * ExplorerService Provider 
     * @ngdoc object
     * @memberof fbi-mafia.explorer
     * @name ExplorerService
     * @scope
     * @requires $http {service} $http
     */
    function ExplorerService($http) {

        var service = {

            _init : function() {
                // NA
            },

            /**
             * @ngdoc function
             * @name fbi-mafia.explorer:ExplorerService#get
             * @methodOf  fbi-mafia.explorer:ExplorerService
             * @description retrieve a list of subordinates Mafioso of Mafioso with {mafiosoId}
             * @param {string} path the path of the file to retrieve
             */
            tree : function(mafiosoId, successCallback, errorCallback) {
                $http.get('/api/mafia/tree', {
                    params : {
                        mafiosoId : mafiosoId
                    }
                }).then(successCallback, errorCallback);
            },

            /**
             * 
             * @ngdoc function
             * @name fbi-mafia.explorer:ExplorerService#download
             * @methodOf  fbi-mafia.explorer:ExplorerService
             * @description This function returns the list of Mafioso that need watching...
             */
            getListToWatch : function(path, successCallback, errorCallback) {
                $http.get('/api/mafia/getListToWatch').then(successCallback, errorCallback);
            }
        };
        service._init();
        return service;
    }

})();
