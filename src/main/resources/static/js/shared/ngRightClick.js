(function() {
    'use strict';

    var module = angular.module('fbi-mafia.directive');

    module.directive('ngRightClick', ngRightClick);
    
    function ngRightClick($parse) {
        return function(scope, element, attrs) {
            var fn = $parse(attrs.ngRightClick);
            element.bind('contextmenu', function(event) {
                scope.$apply(function() {
                    event.preventDefault();
                    fn(scope, {
                        $event : event
                    });
                });
            });
        };
    }

})();
