(function() {
    'use strict';

    angular.module(
            'fbi-mafia',
            [ 'ngRoute', 'ngResource', 'ngSanitize', 'pascalprecht.translate', 'fbi-mafia.common', 'fbi-mafia.I18n', 'fbi-mafia.filter', 'fbi-mafia.directive', 'fbi-mafia.cosaNostra' ]).config(
            [ '$routeProvider', '$httpProvider', '$locationProvider', '$sceDelegateProvider', ConfigFBI ]).filter(
            'escape', function() {
                
                return function(input) {
                    if (input) {
                        return window.encodeURIComponent(input);
                    }
                    return "";
                }
            })

    function ConfigFBI($routeProvider, $httpProvider, $locationProvider, $sceDelegateProvider) {

        $locationProvider.html5Mode(true);
        $routeProvider.when('/cosaNostra', {
            templateUrl : 'js/cosaNostra/cosaNostra.html'
        }).otherwise('/cosaNostra');
        $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
        $sceDelegateProvider.resourceUrlWhitelist([ 'self', '**' ]);
    }
})();
