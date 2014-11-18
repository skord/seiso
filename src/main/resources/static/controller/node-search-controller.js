angular.module('seisoControllers')
	.controller('NodeSearchController', [ '$scope', '$http',
			function($scope, $http) {
				$scope.setShowAdvanced = function(showAdvanced) {
					$scope.showAdvanced = !$scope.showAdvanced;
					$scope.searchLabel = ($scope.showAdvanced ? "Advanced Search" : "Basic Search");
					if ($scope.showAdvanced) {
						$http.get('v1/data-centers').success(function(data) {
							$scope.dataCenters = data;
						});
						$http.get('v1/environments').success(function(data) {
							$scope.environments = data;
						});
						$http.get('v1/infrastructure-providers').success(function(data) {
							$scope.infrastructureProviders = data;
						});
						$http.get('v1/regions').success(function(data) {
							$scope.regions = data;
						});
					}
				}
				$scope.toggleShowAdvanced = function() {
					$scope.setShowAdvanced(!$scope.showAdvanced);
				}
				$scope.setShowAdvanced(false);
			} ]);
