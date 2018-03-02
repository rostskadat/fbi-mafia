(function() {
    'use strict';

    var module = angular.module('fbi-mafia.cosaNostra');

    module.factory('CosaNostraService', [ '$http', CosaNostraService ]);

    /**
     * CosaNostraService Provider 
     * @ngdoc object
     * @memberof fbi-mafia.cosaNostra
     * @name CosaNostraService
     * @scope
     * @requires $http {service} $http
     */
    function CosaNostraService($http) {

        var service = {

            _init : function() {
                // NA
            },

            /**
             * @ngdoc function
             * @name fbi-mafia.cosaNostra:CosaNostraService#getOrganization
             * @methodOf  fbi-mafia.cosaNostra:CosaNostraService
             * @description return the complete organization...
             */
            getOrganization : function(successCallback, errorCallback) {
                $http.get('/api/cosaNostra/getOrganization').then(successCallback, errorCallback);
            },

            /**
             * 
             * @ngdoc function
             * @name fbi-mafia.cosaNostra:CosaNostraService#getListToWatch
             * @methodOf  fbi-mafia.cosaNostra:CosaNostraService
             * @description This function returns the list of Mafioso on the FBI watchList
             */
            getWatchList : function(successCallback, errorCallback) {
                $http.get('/api/cosaNostra/getWatchList').then(successCallback, errorCallback);
            }
        };
        service._init();
        return service;
    }

})();
