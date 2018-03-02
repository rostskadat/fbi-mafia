(function() {
	'use strict';
	angular.module('navigation', [ 'ngRoute' ]).controller('navigation', ['$route', NavigationController]);

	function NavigationController($route) {

		var self = this;

		self.tab = function(route) {
			return $route.current && route === $route.current.controller;
		};

	}
})();
