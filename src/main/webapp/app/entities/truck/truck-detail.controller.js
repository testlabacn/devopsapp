(function() {
    'use strict';

    angular
        .module('devopsApp')
        .controller('TruckDetailController', TruckDetailController);

    TruckDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Truck'];

    function TruckDetailController($scope, $rootScope, $stateParams, entity, Truck) {
        var vm = this;

        vm.truck = entity;

        var unsubscribe = $rootScope.$on('devopsApp:truckUpdate', function(event, result) {
            vm.truck = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
