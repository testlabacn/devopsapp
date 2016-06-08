(function() {
    'use strict';

    angular
        .module('devopsApp')
        .controller('TrashBinDeleteController',TrashBinDeleteController);

    TrashBinDeleteController.$inject = ['$uibModalInstance', 'entity', 'TrashBin'];

    function TrashBinDeleteController($uibModalInstance, entity, TrashBin) {
        var vm = this;

        vm.trashBin = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            TrashBin.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
