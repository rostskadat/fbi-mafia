(function() {
    'use strict';

    angular.module(
            'fbi-mafia',
            [ 'ngRoute', 'ngResource', 'auth', 'navigation', 'fbi-mafia.filter',
                    'fbi-mafia.directive', 'fbi-mafia.explorer' ]).config(
            [ '$routeProvider', '$httpProvider', '$locationProvider', '$sceDelegateProvider', ConfigWFS ]).filter(
            'escape', function() {
                return function(input) {
                    if (input) {
                        return window.encodeURIComponent(input);
                    }
                    return "";
                }
            }).run([ 'auth', RunWFS ])

    function ConfigWFS($routeProvider, $httpProvider, $locationProvider, $sceDelegateProvider) {

        $locationProvider.html5Mode(true);
        $routeProvider.when('/explore', {
            templateUrl : 'js/explore/explore.html'
        }).when('/login', {
            templateUrl : 'js/navigation/login.html',
            controller : 'navigation',
            controllerAs : 'controller'
        }).otherwise('/login');

        $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
        $sceDelegateProvider.resourceUrlWhitelist([ 'self', '**' ]);

        // $translateProvider.translations('en', {
        // HEAD : {
        // TYPE : 'Type',
        // NAME : 'Name',
        // LAST_MODIFIED_TIME : 'Last Modified',
        // SIZE : 'Size'
        //
        // }
        // });
    }

    function RunWFS(auth) {
        // Initialize auth module with the home page and login/logout
        // path respectively
        auth.init('/', '/login', '/logout');
    }
})();
