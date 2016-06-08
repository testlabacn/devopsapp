(function() {
    'use strict';

    angular
        .module('devopsApp')
        .controller('WasteCollectionDetailController', WasteCollectionDetailController);

    WasteCollectionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'WasteCollection', 'Truck', 'TrashBin'];

    function WasteCollectionDetailController($scope, $rootScope, $stateParams, entity, WasteCollection, Truck, TrashBin) {
        var vm = this;

        vm.wasteCollection = entity;

        var unsubscribe = $rootScope.$on('devopsApp:wasteCollectionUpdate', function(event, result) {
            vm.wasteCollection = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
