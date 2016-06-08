(function() {
    'use strict';

    angular
        .module('devopsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('truck', {
            parent: 'entity',
            url: '/truck?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Trucks'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/truck/trucks.html',
                    controller: 'TruckController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('truck-detail', {
            parent: 'entity',
            url: '/truck/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Truck'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/truck/truck-detail.html',
                    controller: 'TruckDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Truck', function($stateParams, Truck) {
                    return Truck.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('truck.new', {
            parent: 'truck',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/truck/truck-dialog.html',
                    controller: 'TruckDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                truckCode: null,
                                plate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('truck', null, { reload: true });
                }, function() {
                    $state.go('truck');
                });
            }]
        })
        .state('truck.edit', {
            parent: 'truck',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/truck/truck-dialog.html',
                    controller: 'TruckDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Truck', function(Truck) {
                            return Truck.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('truck', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('truck.delete', {
            parent: 'truck',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/truck/truck-delete-dialog.html',
                    controller: 'TruckDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Truck', function(Truck) {
                            return Truck.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('truck', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
