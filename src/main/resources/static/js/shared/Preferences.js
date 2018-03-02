(function() {
    'use strict';

    /**
     * This service provides an easy way to store and retrieve preferences from one session to another...
     */
    var module = angular.module('fbi-mafia.common');
    module.factory('PreferencesService', [ '$window', 'BaseService', PreferencesService ]);

    function PreferencesService($window, BaseService) {
        var service = Object.create(BaseService);

        function _init() {
        }

        service.set = function(key, value) {
            $window.localStorage[key] = value;
        };

        service.get = function(key, defaultValue) {
            return $window.localStorage[key] || defaultValue;
        };

        service.setObject = function(key, value) {
            $window.localStorage[key] = JSON.stringify(value);
        };

        service.getObject = function(key) {
            return JSON.parse($window.localStorage[key] || '{}');
        };

        _init();
        return service;
    }

})();
