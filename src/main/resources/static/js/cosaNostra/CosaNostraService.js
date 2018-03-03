(function() {
    'use strict';

    var module = angular.module('fbi-mafia.cosaNostra');

    module.factory('CosaNostraService', [ '$http', CosaNostraService ]);

    /**
	 * CosaNostraService Provider
	 * 
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
			 * @methodOf fbi-mafia.cosaNostra:CosaNostraService
			 * @description return the complete organization...
			 */
            getOrganization : function(successCallback, errorCallback) {
                $http.get('/api/cosaNostra/getOrganization').then(successCallback, errorCallback);
            },

            /**
			 * 
			 * @ngdoc function
			 * @name fbi-mafia.cosaNostra:CosaNostraService#getListToWatch
			 * @methodOf fbi-mafia.cosaNostra:CosaNostraService
			 * @description This function returns the list of Mafioso on the FBI
			 *              watchList
			 */
            getWatchList : function(successCallback, errorCallback) {
                $http.get('/api/cosaNostra/getWatchList').then(successCallback, errorCallback);
            },

            /**
			 * 
			 * @ngdoc function
			 * @name fbi-mafia.cosaNostra:CosaNostraService#sendToJail
			 * @methodOf fbi-mafia.cosaNostra:CosaNostraService
			 * @description This function send a Mafioso to jail
			 */
            sendToJail : function(id, successCallback, errorCallback) {
                $http.post('/api/cosaNostra/sendToJail/'+id).then(successCallback, errorCallback);
            },
            
            /**
			 * 
			 * @ngdoc function
			 * @name fbi-mafia.cosaNostra:CosaNostraService#releaseFromJail
			 * @methodOf fbi-mafia.cosaNostra:CosaNostraService
			 * @description This function release a Mafioso from jail
			 */
            releaseFromJail : function(id, successCallback, errorCallback) {
                $http.post('/api/cosaNostra/releaseFromJail/'+id).then(successCallback, errorCallback);
            }
        };
        service._init();
        return service;
    }

})();
