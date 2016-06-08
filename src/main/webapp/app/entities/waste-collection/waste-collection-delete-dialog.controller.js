(function() {
    'use strict';

    angular
        .module('devopsApp')
        .controller('WasteCollectionDeleteController',WasteCollectionDeleteController);

    WasteCollectionDeleteController.$inject = ['$uibModalInstance', 'entity', 'WasteCollection'];

    function WasteCollectionDeleteController($uibModalInstance, entity, WasteCollection) {
        var vm = this;

        vm.wasteCollection = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            WasteCollection.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
