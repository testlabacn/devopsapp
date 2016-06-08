(function() {
    'use strict';
    angular
        .module('devopsApp')
        .factory('TrashBin', TrashBin);

    TrashBin.$inject = ['$resource'];

    function TrashBin ($resource) {
        var resourceUrl =  'api/trash-bins/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
