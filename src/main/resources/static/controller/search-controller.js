
var app = angular.module('seisoControllers');


app.service("SearchService", function($http) {
    var baseUrl = 'v1/search?query=';
    var query   = {};
    var results = {};

    // TODO replace concat with template?
    this.buildSearchUrl = function() {
        return baseUrl + query.value;
    }

    this.setQuery = function(newQuery) {
        query = { value : newQuery };
    };

    this.getQuery = function() {
        return query;
    };

    this.setResults = function(newResults) {
        results = { value : newResults };
    };

    this.getResults = function() {
        return results;
    };

    this.search = function(callback) {
        this.results = {};
        searchUrl = this.buildSearchUrl();
        $http.get(searchUrl).success( function(data) { 
                                          results = { value : data }; 
                                          callback();
                                       });
    };

});

app.controller('SearchController', [ '$rootScope', '$scope', 'SearchService', '$location', 

	function($rootScope, $scope, SearchService, $location) {

        $scope.searchService = SearchService;
        $scope.searchQuery   = SearchService.getQuery();
        $scope.searchResults = SearchService.getResults();
        $scope.search        = function() {
                                   SearchService.search(function() { 
                                                            $scope.searchResults = SearchService.getResults();
                                                            $rootScope.searchResults = SearchService.getResults();
                                                            $location.path('/search');
                                                        } );
                               };
	} ]);




