(function() {
    'use strict';

    angular
        .module('devopsApp')
        .controller('TruckDialogController', TruckDialogController);

    TruckDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Truck'];

    function TruckDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Truck) {
        var vm = this;

        vm.truck = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.truck.id !== null) {
                Truck.update(vm.truck, onSaveSuccess, onSaveError);
            } else {
                Truck.save(vm.truck, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('devopsApp:truckUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
