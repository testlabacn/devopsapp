(function() {
    'use strict';

    angular
        .module('devopsApp')
        .controller('TrashBinDetailController', TrashBinDetailController);

    TrashBinDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'TrashBin'];

    function TrashBinDetailController($scope, $rootScope, $stateParams, DataUtils, entity, TrashBin) {
        var vm = this;

        vm.trashBin = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('devopsApp:trashBinUpdate', function(event, result) {
            vm.trashBin = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
