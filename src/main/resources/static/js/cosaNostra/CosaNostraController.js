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
	function CosaNostraController($scope, $location, $routeParams, $q, I18nService,
			CosaNostraService, CosaNostraConstants) {

		var vm = this;

		vm.scope = $scope;
		$scope.controller = vm;
		$scope.layoutMethod = "directed";
		$scope.idsWatchList = [];
		$scope.organization = {
			options : {
				layout : {
					hierarchical : {
						sortMethod : $scope.layoutMethod,
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
		$scope.organizationLoaded;

		var groupOptions = {
			groups : {
				mafioso : {
					shape : 'icon',
					icon : {
						face : 'FontAwesome',
						code : '\uf007',
						size : 100,
						color : '#EE1B09'
					}
				},
				mafiosoToWatch : {
					shape : 'icon',
					icon : {
						face : 'FontAwesome',
						code : '\uf007',
						size : 50,
						color : '#57169a'
					}
				}
			}
		};

		vm.init = init;
		vm.updateOrganization = updateOrganization;
		vm.updateWatchList = updateWatchList;
		vm.getMafioso = getMafioso;
		vm.sendToJail = sendToJail;
		vm.releaseFromJail = releaseFromJail;

		/**
		 * @ngdoc function
		 * @name fbi-mafia.cosaNostra:CosaNostraController#updateOrganization
		 * @methodOf fbi-mafia.cosaNostra:CosaNostraController
		 * @description
		 */
		function updateOrganization() {
			_getOrganization(_getOrganizationOk, _getOrganizationKo);
		}

		function _getOrganization(resolve, reject) {
			CosaNostraService.getOrganization(resolve, reject);
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

		/**
		 * @ngdoc function
		 * @name fbi-mafia.cosaNostra:CosaNostraController#updateWatchList
		 * @methodOf fbi-mafia.cosaNostra:CosaNostraController
		 * @description
		 */
		function updateWatchList() {
			_getWatchList(_getWatchListOk, _getWatchListKo);
		}

		function _getWatchList(resolve, reject) {
			CosaNostraService.getWatchList(resolve, reject);
		}

		function _getWatchListOk(response) {
			angular.forEach(response.data, function(mafioso) {
				this.push(mafioso.id);
			}, $scope.idsWatchList);
		}

		function _getWatchListKo(response) {
			console.log("Failed to load watchList...")
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
			var promise = _asyncUpdate();
			promise.then(function(response) {
				_getWatchListOk(response);
				_getOrganization(_getOrganizationOk, _getOrganizationKo);
			}, function(reason) {
				_getWatchListKo(reason);
			});
		}

		function _asyncUpdate(name) {
			// perform some asynchronous operation, resolve or reject the promise when appropriate.
			return $q(function(proceed, failed) {
				setTimeout(function() {
					_getWatchList(proceed, failed);
				}, 1000);
			});
		}

		function _buildNodesAndEdges(nodes, edges, mafiaCell) {
			var mafioso = mafiaCell.mafioso;
			var node ={
				id : mafioso.id,
				label : mafioso.firstName + ' ' + mafioso.lastName + '\n'
						+ mafioso.age
			};
			if ($scope.idsWatchList.indexOf(node.id) !== -1) {
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
