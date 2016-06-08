'use strict';

describe('Controller Tests', function() {

    describe('TrashBin Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTrashBin;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTrashBin = jasmine.createSpy('MockTrashBin');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'TrashBin': MockTrashBin
            };
            createController = function() {
                $injector.get('$controller')("TrashBinDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'devopsApp:trashBinUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
