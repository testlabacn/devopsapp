(function() {
    'use strict';

    angular
        .module('devopsApp')
        .factory('TruckSearch', TruckSearch);

    TruckSearch.$inject = ['$resource'];

    function TruckSearch($resource) {
        var resourceUrl =  'api/_search/trucks/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
