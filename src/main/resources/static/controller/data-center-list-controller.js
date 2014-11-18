angular.module('seisoControllers')
	.controller('DataCenterListController', [ '$scope', '$http', 'generalRegions', function($scope, $http, generalRegions) {
		
		// Format providers for rendering. We want a matrix with general regions (NA, EU, APAC, SA) for columns and
		// providers for rows. Any given cell contains the provider's special regions (falling under the relevant
		// general region) and corresponding data centers.
		$http.get('v1/infrastructure-providers').success(function(data) {
			var srcProviders = data;
			var destProviders = {};
			for (i = 0; i < srcProviders.length; i++) {
			
				// Initialize provider data structure.
				var srcProvider = srcProviders[i];
				var providerKey = srcProvider.key;
				destProviders[providerKey] = {
					'name' : srcProvider.name,
					'specialRegions' : {}
				};
				var destProvider = destProviders[providerKey];
				for (j = 0; j < generalRegions.length; j++) {
					var generalRegion = generalRegions[j];
					destProvider.specialRegions[generalRegion.key] = [];
				};
				
				// Distribute the provider's special regions into into general regional buckets.
				destProvider = destProviders[providerKey];
				var srcSpecialRegions = srcProvider.regions;
				for (j = 0; j < srcSpecialRegions.length; j++) {
					var srcSpecialRegion = srcSpecialRegions[j];
					var generalRegionKey = srcSpecialRegion.regionKey;
					destProvider.specialRegions[generalRegionKey].push(srcSpecialRegion);
				}
			}
			$scope.infrastructureProviders = destProviders;
		});
	} ]);
