(function() {
    'use strict';

    /**
     * This is a generic service that provide common facilities between different Service.
     * All service should extend this service using the JavaScript inheritance pattern described in 
     * http://blog.mgechev.com/2013/12/18/inheritance-services-controllers-in-angularjs/
     */
    var module = angular.module('fbi-mafia.common', []);
    module.factory('BaseService', [ '$rootScope', '$filter', '$http', BaseService ]);

    function BaseService() {
        
        var service = {

            saveState : function(key, value) {
                sessionStorage[key] = angular.toJson(value);
            },

            restoreState : function(key) {
                try {
                    return angular.fromJson(sessionStorage[key]);
                } catch (error) {
                    return undefined;
                }
            },
            
            hasSessionKey : function(key) {
                return sessionStorage.hasOwnProperty(key);
            },
            
        };
        return service;
    }

})();
