(function() {
    'use strict';

    angular
        .module('devopsApp')
        .controller('TruckDeleteController',TruckDeleteController);

    TruckDeleteController.$inject = ['$uibModalInstance', 'entity', 'Truck'];

    function TruckDeleteController($uibModalInstance, entity, Truck) {
        var vm = this;

        vm.truck = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Truck.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
