(function() {
	'use strict';

	/**
	 * The CosaNostraController allows to explore the Mafia organization
	 */
	var module = angular.module('fbi-mafia.cosaNostra');

	module.controller('CosaNostraController', [ '$scope', '$location',
			'$routeParams', '$q', 'I18nService', 'CosaNostraService',
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
	function CosaNostraController($scope, $location, $routeParams, $q,
			I18nService, CosaNostraService, CosaNostraConstants) {

		var vm = this;

		vm.scope = $scope;
		$scope.controller = vm;

		$scope.organizationLoaded = false;
		$scope.watchlist = [];
		$scope.organization = {
			options : {
				layout : {
					hierarchical : {
						sortMethod : "directed",
						levelSeparation : 200,
						direction : "UD"
					}
				},
				edges : {
					arrows : {
						to : true
					}
				}
			}
		};
		$scope.jail = {
			data : {
				nodes : []
			}
		};
		$scope.sortableOptions = {
			placeholder : "mafioso",
			connectWith : ".mafiosos-container",
			stop : function(e, ui) {
				// BEWARE of the index.
				var elementClass = ui.item.sortable.droptarget[0].classList[2];
				if (elementClass == "dropzone-jail") {
					sendToJail(ui.item.sortable.model);
				} else if (elementClass == "dropzone-free") {
					releaseFromJail(ui.item.sortable.model);
				}

			}
		};

		/**
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		vm.init = init;
		vm.getMafioso = getMafioso;
		vm.releaseFromJail = releaseFromJail;

		/**
		 * @ngdoc function
		 * @name fbi-mafia.cosaNostra:CosaNostraController#getMafioso
		 * @methodOf fbi-mafia.cosaNostra:CosaNostraController
		 * @description
		 */
		function getMafioso() {
			CosaNostraService.getMafioso(function(response) {
				console.log("displaying specific mafioso");
			});
		}

		function sendToJail(model) {
			CosaNostraService.sendToJail(model.id, function(response) {
				_updateOrganizationGraph();
			});
		}

		function releaseFromJail(model) {
			CosaNostraService.releaseFromJail(model.id, function(response) {
				_updateOrganizationGraph();
			});
		}

		/**
		 * @ngdoc function
		 * @name fbi-mafia.cosaNostra:CosaNostraController#init
		 * @methodOf fbi-mafia.cosaNostra:CosaNostraController
		 * @description
		 */
		function init() {
			I18nService.setLanguage($routeParams['lang']);
			_updateOrganizationGraph();
		}

		function _updateOrganizationGraph() {
			var promise = $q(function(proceed, failed) {
				setTimeout(function() {
					CosaNostraService.getWatchList(proceed, failed);
				}, 1000);
			});
			promise.then(function(response) {
				angular.forEach(response.data, function(mafioso) {
					$scope.watchlist.push(mafioso.id);
				});
				CosaNostraService.getOrganization(_getOrganizationOk,
						_getOrganizationKo);
			}, function(reason) {
				console.log("Failed to load watchList: " + reason)
			});
		}

		function _getOrganizationOk(response) {
			var nodes = [];
			var edges = [];
			_buildNodesAndEdges(nodes, edges, response.data);
			$scope.organization.data = {
				nodes : nodes,
				edges : edges
			};
			$scope.organizationLoaded = true;
		}

		function _getOrganizationKo(response) {
			console.log("Failed to load organization...");
		}

		function _buildNodesAndEdges(nodes, edges, mafiaCell) {
			var mafioso = mafiaCell.mafioso;
			var node = {
				id : mafioso.id,
				label : mafioso.firstName + ' ' + mafioso.lastName + '\n'
						+ mafioso.age
			};
			if ($scope.watchlist.indexOf(node.id) !== -1) {
				node.shape = 'star';
				node.color = '#EE1B09';
			}
			nodes.push(node);
			angular.forEach(mafiaCell.subordinates, function(subordinateCell) {
				edges.push({
					from : mafioso.id,
					to : subordinateCell.mafioso.id
				})
				_buildNodesAndEdges(nodes, edges, subordinateCell);
			});

		}
	}

})();
