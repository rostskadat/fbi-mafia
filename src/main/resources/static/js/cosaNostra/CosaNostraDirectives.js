(function() {
    'use strict';

    var module = angular.module('fbi-mafia.cosaNostra');

    module.directive('mafiosoLookup', MafiosoLookupDirective);

    function MafiosoLookupDirective() {
        return {
            template : '<label>{{colFilter.term}}</label><button ng-click="showAgeModal()">...</button>',
            controller : 'MafiosoController'
        };
    }
})();
