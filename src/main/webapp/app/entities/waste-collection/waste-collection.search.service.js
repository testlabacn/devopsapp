(function() {
    'use strict';

    angular
        .module('devopsApp')
        .factory('WasteCollectionSearch', WasteCollectionSearch);

    WasteCollectionSearch.$inject = ['$resource'];

    function WasteCollectionSearch($resource) {
        var resourceUrl =  'api/_search/waste-collections/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
