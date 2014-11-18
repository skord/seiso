angular.module('seisoControllers')
	.controller('NodeDetailsController', [ '$scope', '$http', '$routeParams',
			function($scope, $http, $routeParams) {
				$http.get('v1/nodes/' + $routeParams.name).success(function(data) {
					// FIXME This probably generates null pointers where optional fields are concerned. [WLW]
					$scope.node = data;
					$scope.serviceInstance = $scope.node.serviceInstance;
					$scope.service = $scope.serviceInstance.service;
					$scope.owner = $scope.service.owner;
					$scope.owner.fullName = $scope.owner.firstName + " " + $scope.owner.lastName;
					$scope.environment = $scope.serviceInstance.environment;
					$scope.dataCenter = $scope.serviceInstance.dataCenter;
					$scope.region = $scope.dataCenter.region;
					$scope.infrastructureProvider = $scope.region.infrastructureProvider;
					$scope.machine = $scope.node.machine;
				});
			} ]);
