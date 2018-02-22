(function() {
    'use strict';

    var module = angular.module('fbi-mafia.directive');

    module.directive('ngResize', ngResize);

    function ngResize($window) {
        return {
            link : function(scope) {
                function onResize(event) {
                    // Namespacing events with name of directive + event to avoid collisions
                    scope.$broadcast('resize::resize');
                }

                function cleanUp() {
                    angular.element($window).off('resize', onResize);
                }

                angular.element($window).on('resize', onResize);
                scope.$on('$destroy', cleanUp);
            }
        }
    }

})();
