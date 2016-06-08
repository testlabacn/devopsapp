'use strict';

describe('Controller Tests', function() {

    describe('WasteCollection Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockWasteCollection, MockTruck, MockTrashBin;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockWasteCollection = jasmine.createSpy('MockWasteCollection');
            MockTruck = jasmine.createSpy('MockTruck');
            MockTrashBin = jasmine.createSpy('MockTrashBin');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'WasteCollection': MockWasteCollection,
                'Truck': MockTruck,
                'TrashBin': MockTrashBin
            };
            createController = function() {
                $injector.get('$controller')("WasteCollectionDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'devopsApp:wasteCollectionUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
