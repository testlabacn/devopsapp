(function() {
    'use strict';

    angular
        .module('devopsApp')
        .factory('TrashBinSearch', TrashBinSearch);

    TrashBinSearch.$inject = ['$resource'];

    function TrashBinSearch($resource) {
        var resourceUrl =  'api/_search/trash-bins/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
