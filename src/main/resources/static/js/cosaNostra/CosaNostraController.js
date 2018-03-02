(function() {
    'use strict';

    /**
     * The CosaNostraController allows to explore the Mafia organization
     */
    var module = angular.module('fbi-mafia.cosaNostra');

    module.controller('CosaNostraController', [ '$scope', '$location', '$routeParams', 'I18nService', 'CosaNostraService',
            'CosaNostraConstants', CosaNostraController ]);
    
    /**
     * CosaNostraController Provider
     * 
     * @constructor
     * @ngdoc object
     * @memberof fbi-mafia.cosaNostra
     * @name CosaNostraController
     * @scope
     * @requires $scope {service} controller scope
     * @requires $location {service} $location
     * @requires $routeParams {service} $routeParams
     * @requires $uibModal {service} $uibModal
     * @requires explorerService {service} explorerService
     * @requires CosaNostraConstants {service} CosaNostraConstants
     */
    function CosaNostraController($scope, $location, $routeParams, I18nService, CosaNostraService, CosaNostraConstants) {

        var vm = this;

        vm.scope = $scope;
        $scope.controller = vm;
        $scope.organization = {};
        $scope.organizationLoaded;

        vm.init = init;
        vm.getOrganization = getOrganization;
        vm.getWatchList = getWatchList;
        vm.getMafioso = getMafioso;
        vm.sendToJail = sendToJail;
        vm.releaseFromJail = releaseFromJail;

        /**
         * @ngdoc function
         * @name fbi-mafia.cosaNostra:CosaNostraController#getOrganization
         * @methodOf fbi-mafia.cosaNostra:CosaNostraController
         * @description 
         */
        function getOrganization() {
            CosaNostraService.getOrganization(function(response) {
                console.log("displaying organization tree");
            });
        }

        function getWatchList() {
            CosaNostraService.getWatchList(function(response) {
                console.log("displaying fbi watchlist");
            });
        }

        function getMafioso() {
            CosaNostraService.getMafioso(function(response) {
                console.log("displaying specific mafioso");
            });
        }

        function sendToJail() {
            CosaNostraService.sendToJail(function(response) {
                console.log("mafioso to jail");
            });
        }

        function releaseFromJail() {
            CosaNostraService.releaseFromJail(function(response) {
                console.log("releasing mafioso from jail");
            });
        }
        
        function init() {
            I18nService.setLanguage($routeParams['lang']);
        }
    }

})();
