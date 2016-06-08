(function() {
    'use strict';

    angular
        .module('devopsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('waste-collection', {
            parent: 'entity',
            url: '/waste-collection?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'WasteCollections'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/waste-collection/waste-collections.html',
                    controller: 'WasteCollectionController',
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
        .state('waste-collection-detail', {
            parent: 'entity',
            url: '/waste-collection/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'WasteCollection'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/waste-collection/waste-collection-detail.html',
                    controller: 'WasteCollectionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'WasteCollection', function($stateParams, WasteCollection) {
                    return WasteCollection.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('waste-collection.new', {
            parent: 'waste-collection',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/waste-collection/waste-collection-dialog.html',
                    controller: 'WasteCollectionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                serverTimestamp: null,
                                truckTimestamp: null,
                                wasteLevel: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('waste-collection', null, { reload: true });
                }, function() {
                    $state.go('waste-collection');
                });
            }]
        })
        .state('waste-collection.edit', {
            parent: 'waste-collection',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/waste-collection/waste-collection-dialog.html',
                    controller: 'WasteCollectionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['WasteCollection', function(WasteCollection) {
                            return WasteCollection.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('waste-collection', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('waste-collection.delete', {
            parent: 'waste-collection',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/waste-collection/waste-collection-delete-dialog.html',
                    controller: 'WasteCollectionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['WasteCollection', function(WasteCollection) {
                            return WasteCollection.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('waste-collection', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
