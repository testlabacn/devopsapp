(function() {
    'use strict';
    angular
        .module('devopsApp')
        .factory('Truck', Truck);

    Truck.$inject = ['$resource'];

    function Truck ($resource) {
        var resourceUrl =  'api/trucks/:id';

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
