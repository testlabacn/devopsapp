(function() {
    'use strict';

    angular
        .module('devopsApp')
        .controller('TrashBinDialogController', TrashBinDialogController);

    TrashBinDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'TrashBin'];

    function TrashBinDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, TrashBin) {
        var vm = this;

        vm.trashBin = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.trashBin.id !== null) {
                TrashBin.update(vm.trashBin, onSaveSuccess, onSaveError);
            } else {
                TrashBin.save(vm.trashBin, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('devopsApp:trashBinUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setBarCode = function ($file, trashBin) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        trashBin.barCode = base64Data;
                        trashBin.barCodeContentType = $file.type;
                    });
                });
            }
        };

    }
})();
