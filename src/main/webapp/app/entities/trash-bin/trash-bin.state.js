(function() {
    'use strict';

    angular
        .module('devopsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('trash-bin', {
            parent: 'entity',
            url: '/trash-bin?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'TrashBins'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/trash-bin/trash-bins.html',
                    controller: 'TrashBinController',
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
        .state('trash-bin-detail', {
            parent: 'entity',
            url: '/trash-bin/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'TrashBin'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/trash-bin/trash-bin-detail.html',
                    controller: 'TrashBinDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'TrashBin', function($stateParams, TrashBin) {
                    return TrashBin.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('trash-bin.new', {
            parent: 'trash-bin',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trash-bin/trash-bin-dialog.html',
                    controller: 'TrashBinDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                trashBinCode: null,
                                latitute: null,
                                longitude: null,
                                barCode: null,
                                barCodeContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('trash-bin', null, { reload: true });
                }, function() {
                    $state.go('trash-bin');
                });
            }]
        })
        .state('trash-bin.edit', {
            parent: 'trash-bin',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trash-bin/trash-bin-dialog.html',
                    controller: 'TrashBinDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TrashBin', function(TrashBin) {
                            return TrashBin.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('trash-bin', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('trash-bin.delete', {
            parent: 'trash-bin',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trash-bin/trash-bin-delete-dialog.html',
                    controller: 'TrashBinDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['TrashBin', function(TrashBin) {
                            return TrashBin.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('trash-bin', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
