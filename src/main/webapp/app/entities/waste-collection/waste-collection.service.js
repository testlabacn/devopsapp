(function() {
    'use strict';
    angular
        .module('devopsApp')
        .factory('WasteCollection', WasteCollection);

    WasteCollection.$inject = ['$resource', 'DateUtils'];

    function WasteCollection ($resource, DateUtils) {
        var resourceUrl =  'api/waste-collections/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.serverTimestamp = DateUtils.convertDateTimeFromServer(data.serverTimestamp);
                        data.truckTimestamp = DateUtils.convertDateTimeFromServer(data.truckTimestamp);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
