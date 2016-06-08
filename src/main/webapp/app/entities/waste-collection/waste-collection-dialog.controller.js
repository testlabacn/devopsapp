(function() {
    'use strict';

    angular
        .module('devopsApp')
        .controller('WasteCollectionDialogController', WasteCollectionDialogController);

    WasteCollectionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'WasteCollection', 'Truck', 'TrashBin'];

    function WasteCollectionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, WasteCollection, Truck, TrashBin) {
        var vm = this;

        vm.wasteCollection = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.trucks = Truck.query();
        vm.trashbins = TrashBin.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.wasteCollection.id !== null) {
                WasteCollection.update(vm.wasteCollection, onSaveSuccess, onSaveError);
            } else {
                WasteCollection.save(vm.wasteCollection, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('devopsApp:wasteCollectionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.serverTimestamp = false;
        vm.datePickerOpenStatus.truckTimestamp = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
