angular.module('seisoControllers')
	.controller('MachineDetailsController', [ '$scope', '$http', '$routeParams',
			function($scope, $http, $routeParams) {
				$http.get('v1/machines/' + $routeParams.name).success(function(data) {
					$scope.machine = data;
					$scope.nodes = $scope.machine.nodes;
				});
			} ]);
