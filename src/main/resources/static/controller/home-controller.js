angular.module('seisoControllers')
	.controller('HomeController', [ '$scope', '$http', function($scope, $http) {
		$scope.pageId = 'home';
		serviceGroupsMap = {};
		$http.get('v1/service-groups').success(function(data) {
			$scope.serviceGroups = data;
			$scope.serviceGroups.push({ "key" : "_ungrouped", "name" : "Ungrouped" });
			for (i = 0; i < data.length; i++) {
				serviceGroup = data[i];
				serviceGroup.services = [];
				serviceGroupsMap[serviceGroup.key] = serviceGroup;
			}
		});
		
		// FIXME If there are more than 300 services, we won't catch them all. We need a JS client for getting the
		// full list from the paging API. (The API will continue to page.) [WLW]
		$http.get('v1/services?page=0&size=300&sort=name').success(function(data) {
			services = data;
			for (i = 0; i < services.length; i++) {
				service = services[i];
				group = service.group;
				if (group === null) {
					serviceGroupsMap['_ungrouped'].services.push(service);
				} else {
					serviceGroupsMap[group.key].services.push(service);
				}
			}
		});
	} ]);
