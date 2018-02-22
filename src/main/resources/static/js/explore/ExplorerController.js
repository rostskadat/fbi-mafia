(function() {
    'use strict';

    /**
     * The ExplorerController allows to explore the Mafia organization
     */
    var module = angular.module('fbi-mafia.explorer');

    module.controller('ExplorerController', [ '$scope', '$location', '$routeParams', '$uibModal', 'ExplorerService',
            'ExplorerConstants', 'FileSaver', 'Blob', ExplorerController ]);
    
    module.directive('stDateRange', ['$timeout', function ($timeout) {
        return {
            restrict: 'E',
            require: '^stTable',
            scope: {
                before: '=',
                after: '='
            },
            templateUrl: 'stDateRange.html',

            link: function (scope, element, attr, table) {

                var inputs = element.find('input');
                var inputBefore = angular.element(inputs[0]);
                var inputAfter = angular.element(inputs[1]);
                var predicateName = attr.predicate;


                [inputBefore, inputAfter].forEach(function (input) {

                    input.bind('blur', function () {


                        var query = {};

                        if (!scope.isBeforeOpen && !scope.isAfterOpen) {

                            if (scope.before) {
                                query.before = scope.before;
                            }

                            if (scope.after) {
                                query.after = scope.after;
                            }

                            scope.$apply(function () {
                                table.search(query, predicateName);
                            })
                        }
                    });
                });

                function open(before) {
                    return function ($event) {
                        $event.preventDefault();
                        $event.stopPropagation();

                        if (before) {
                            scope.isBeforeOpen = true;
                        } else {
                            scope.isAfterOpen = true;
                        }
                    }
                }

                scope.openBefore = open(true);
                scope.openAfter = open();
            }
        }
    }]);
    
    module.directive('stNumberRange', ['$timeout', function ($timeout) {
        return {
            restrict: 'E',
            require: '^stTable',
            scope: {
                lower: '=',
                higher: '='
            },
            templateUrl: 'stNumberRange.html',
            link: function (scope, element, attr, table) {
                var inputs = element.find('input');
                var inputLower = angular.element(inputs[0]);
                var inputHigher = angular.element(inputs[1]);
                var predicateName = attr.predicate;

                [inputLower, inputHigher].forEach(function (input, index) {

                    input.bind('blur', function () {
                        var query = {};

                        if (scope.lower) {
                            query.lower = scope.lower;
                        }

                        if (scope.higher) {
                            query.higher = scope.higher;
                        }

                        scope.$apply(function () {
                            table.search(query, predicateName)
                        });
                    });
                });
            }
        };
    }]);
    
    module.directive('fileItemName', ['$timeout', function ($timeout) {
        return {
            restrict: 'E',
            require: '^stTable',
            scope: {
                fileItem: '=',
                click: '&'
            },
            templateUrl: 'fileItemNameDirective.html',
//            link: function (scope, element, attr, table) {
//                var inputs = element.find('input');
//                var inputLower = angular.element(inputs[0]);
//                var inputHigher = angular.element(inputs[1]);
//                var predicateName = attr.predicate;
//
//                [inputLower, inputHigher].forEach(function (input, index) {
//
//                    input.bind('blur', function () {
//                        var query = {};
//
//                        if (scope.lower) {
//                            query.lower = scope.lower;
//                        }
//
//                        if (scope.higher) {
//                            query.higher = scope.higher;
//                        }
//
//                        scope.$apply(function () {
//                            table.search(query, predicateName)
//                        });
//                    });
//                });
//            }
        };
    }]);
    
    module.directive('myDirective', function() {
        return {
          require: 'ngModel',
          link: function(scope, element, attrs, ngModelController) {
            ngModelController.$parsers.push(function(data) {
              //convert data from view format to model format
              return data; //converted
            });

            ngModelController.$formatters.push(function(data) {
              //convert data from model format to view format
              return data; //converted
            });
          }
        }
      });

    /**
     * ExplorerController Provider
     * 
     * @constructor
     * @ngdoc object
     * @memberof fbi-mafia.explorer
     * @name ExplorerController
     * @scope
     * @requires $scope {service} controller scope
     * @requires $location {service} $location
     * @requires $routeParams {service} $routeParams
     * @requires $uibModal {service} $uibModal
     * @requires explorerService {service} explorerService
     * @requires ExplorerConstants {service} ExplorerConstants
     */
    function ExplorerController($scope, $location, $routeParams, $uibModal, explorerService, ExplorerConstants,
            FileSaver, Blob) {

        var vm = this;

        vm.scope = $scope;
        $scope.controller = vm;
        $scope.fileItems = [];
        $scope.treeItems = [];
        $scope.path = '';
        $scope.pathArray = [];
        $scope.view = 'content';

        vm.clickFileItem = clickFileItem;
        vm.refreshView = refreshView;
        vm.toggleFolderItem = toggleFolderItem;
        vm.getBreadcrumb = getBreadcrumb;
        vm.openUploadDialog = openUploadDialog;
        vm.downloadFileItems = downloadFileItems;
        vm.openCopyToDialog = openCopyToDialog;
        vm.openMoveToDialog = openMoveToDialog;
        vm.closeCopyMoveToDialog = closeCopyMoveToDialog;
        vm.copyMoveFileItems = copyMoveFileItems;
        vm.openDeleteDialog = openDeleteDialog;
        vm.closeDeleteDialog = closeDeleteDialog;
        vm.deleteFileItems = deleteFileItems;
        vm.openRenameDialog = openRenameDialog;
        vm.closeRenameDialog = closeRenameDialog;
        vm.renameFileItems = renameFileItems;
        vm.openPropertiesDialog = openPropertiesDialog;
        vm.closePropertiesDialog = closePropertiesDialog;
        vm.getFlexPanelHeight = getFlexPanelHeight;
        vm.rightClick = rightClick;
        vm.selectAllRows = selectAllRows;
        vm.clearSelectedRows = clearSelectedRows;
        vm.toggleRowSelection = toggleRowSelection;
        vm.switchView = switchView;

        $scope.treeViewGrid = {
            enableSorting : false,
            enableFiltering : false,
            enableColumnMenus : false,
            showTreeExpandNoChildren : true,
            showTreeRowHeader : true,

            columnDefs : [ {
                name : 'name',
                cellTemplate : 'folderItemName.html'
            }, ],
            data : 'treeItems'
        }

        $scope.treeViewGrid.onRegisterApi = function(gridApi) {
            $scope.treeViewGridApi = gridApi;
            $scope.treeViewGridApi.treeBase.on.rowExpanded($scope, _expandTreeRow);
        };

        $scope.test = function(fileItem) {
            console.log('Opening ' + fileItem.name);
        };
        
        $scope.fileItemsGrid = {
            exporterMenuCsv : false,
            exporterMenuPdf : false,
            enableFiltering : true,
            enableSorting : true,
            enableExpandable : false,
            enableRowSelection : true,
            enableSelectAll : true,
            multiSelect : true,
            expandableRowTemplate : 'properties.html',
            rowTemplate : 'fileItem.html',
            columnDefs : ExplorerConstants.COLUMN_DEFS,
            data : 'fileItems',
        };

        $scope.fileItemsGrid.onRegisterApi = function(gridApi) {
            $scope.fileItemsGridApi = gridApi;
        };

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#getFlexPanelHeight
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description This method returns the height of the panel containing the grids... Should be in the html, or?
         */
        function getFlexPanelHeight() {
            var footerHeight = angular.element(".wfs-footer")[0].offsetHeight;
            var offsetTop = angular.element(".wfs-content")[0].offsetTop;
            return {
                "height" : $(window).height() - offsetTop - footerHeight - 50
            }
        }

        /*
         * The first thing is to watch the 'path' parameter in the URL. This drives the update of all other parameters
         * in the application.
         */
        $scope.$watch('routeParams', function() {
            refreshView();
        });

        $scope.status = {
            isopen : false
        };
        function rightClick($event, row) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.status.isopen = !$scope.status.isopen;
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#getBreadcrumb
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description This function returns the path for a specific breadcrumb link
         * @param {number}
         *            index the depth of the link
         * @param {string}
         *            name the name of the leaf folder
         */
        function getBreadcrumb(index, name) {
            if (index == 0) {
                return name;
            }
            return $scope.pathArray.slice(0, index).join("/") + "/" + name;
        }

        /*
         * This private method is called when the user click to expand a node on the tree grid.
         */
        function _expandTreeRow(row) {
            var entity = row.entity;
            var path = entity.absolutePath;
            var $$hashKey = entity.$$hashKey;
            var treeItems = $scope.treeItems;
            if (!entity.nodeLoaded) {
                // retrieve the children of this node.
                explorerService.tree(path, function(response) {
                    for (var i = 0; i < treeItems.length; i++) {
                        if (treeItems[i].$$hashKey == $$hashKey) {
                            // and insert them at the right place in the tree
                            treeItems.splice.apply(treeItems, [ i + 1, 0 ].concat(response.data));
                            entity.nodeLoaded = true;
                            return;
                        }
                    }
                });
            }
        }

        function clickFileItem(fileItem) {
            if (!fileItem) {
                return;
            }
            
            if (fileItem.fileItemType == 'FOLDER' || fileItem.fileItemType == 'SYMLINK') {
                refreshView(fileItem.absolutePath);
            } else if (fileItem.fileItemType == 'FILE') {
                downloadFileItems(undefined, fileItem.absolutePath)
            }
        }
        
        /**
         * This method will refresh the view.
         */
        function refreshView(path) {
            if (path) {
                $scope.path = path
            } else if (!$scope.path) {
                $scope.path = "";
            }
            $scope.pathArray = $scope.path.split(/[/\\]/);
            _refreshTreeView($scope.path);
            if ($scope.view == 'details') {
                _refreshListView($scope.path);
            } else {
                _refreshContentView($scope.path);
            }
        }

        function toggleFolderItem(row) {
            $scope.treeViewGridApi.treeBase.toggleRowTreeState(row);
            if ($scope.view == 'details') {
                _refreshListView(row.entity.absolutePath);
            } else {
                _refreshContentView(row.entity.absolutePath);
            }

        }

        /*
         * This private method refresh the list-view by calling the list service
         */
        function _refreshListView(path) {
            var notify = Notify.notifyWait('Updating');
            explorerService.list(path, function(response) {
                $scope.fileItems = response.data;
                if (notify) {
                    notify.close();
                }
            }, function() {
                if (notify) {
                    notify.update('icon', 'fa fa-warning');
                    notify.update('type', 'warning');
                    notify.update('title', 'Failed to list ' + path);
                    notify.update('message', '');
                }
            });
            if ($scope.fileItemsGridApi) {
                $scope.fileItemsGridApi.selection.clearSelectedRows();
            }
        }

        /*
         * This private method refresh the content-view by calling the list service
         */
        function _refreshContentView(path) {
            var notify = Notify.notifyWait('Updating');
            explorerService.list(path, function(response) {
                $scope.fileItems = response.data;
                if (notify) {
                    notify.close();
                }
            }, function() {
                if (notify) {
                    notify.update('icon', 'fa fa-warning');
                    notify.update('type', 'warning');
                    notify.update('title', 'Failed to list ' + path);
                    notify.update('message', '');
                }
            });
        }

        /*
         * This method refresh the tree view by calling the tree service
         */
        function _refreshTreeView(path) {
            if (path == "") {
                explorerService.tree(path, function(response) {
                    $scope.treeItems = response.data;
                });
            } else {
                $scope.treeViewGridApi.grid.rows.forEach(function(row) {
                    if (row.entity.absolutePath == path) {
                        $scope.treeViewGridApi.treeBase.toggleRowTreeState(row);
                    }
                });
            }
        }

        /**
         * Create the FileDialog to upload several files...
         */
        function openUploadDialog($event, absolutePath) {
            $event.preventDefault();
            $event.stopPropagation();

            if (absolutePath === undefined) {
                absolutePath = $scope.path;
            }
            $.FileDialog({
                accept : "*",
                ok_button : "<i class='fa fa-upload'></i>&nbsp;Upload",
                title : "Upload file(s)",
            }).on('files.bs.filedialog', function($event) {
                var files = $event.files;
                _uploadFiles(absolutePath, files);
            });
        }

        function _uploadFiles(absolutePath, files) {
            explorerService.uploadMany(absolutePath, files, function(responses) {
                if (responses.length > 1) {
                    Notify.notifyInfo('Successfully uploaded ' + responses.length + ' files.');
                } else {
                    Notify.notifyInfo('Successfully uploaded \'' + responses[0].data.absolutePath + '\'.');
                }
                refreshView();
            }, function(response) {
                Notify.notifyWarning('Failed to upload \'' + response.data.absolutePath + '\'!');
                refreshView();
            });
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#openDeleteDialog
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Open the delete dialog.
         * @param {event}
         *            not used...
         * @param {object}
         *            row the ui-grid.row for which the delete dialog should be displayed
         */
        function openDeleteDialog($event, absolutePath) {
            $scope.pathsToDelete = [];

            if ($scope.fileItemsGridApi.selection.getSelectedCount() > 0) {
                // OK the user wants to delete many files...
                $scope.fileItemsGridApi.selection.getSelectedRows().forEach(function(row) {
                    $scope.pathsToDelete.push(row.absolutePath);
                });
            } else if (absolutePath !== undefined) {
                // OK the user wants to delete a specific file...
                $scope.pathsToDelete = [ absolutePath ];
            } else {
                Notify.notifyWarning('Please select the files to delete.');
                return;
            }
            $scope.deleteDialog = $uibModal.open({
                animation : true,
                ariaLabelledBy : 'modal-title',
                ariaDescribedBy : 'modal-body',
                templateUrl : 'js/explore/templates/DeleteDialog.html',
                appendTo : $('#dialogAnchor'),
                scope : $scope
            });
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#closeDeleteDialog
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Close the delete dialog.
         */
        function closeDeleteDialog() {
            $scope.deleteDialog.close();
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#deleteFileItems
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Close the delete dialog and delete the file.
         */
        function deleteFileItems() {
            $scope.deleteDialog.close();
            explorerService.deleteMany($scope.pathsToDelete, function(responses) {
                if (responses.length > 1) {
                    Notify.notifyInfo('Successfully deleted ' + responses.length + ' files.');
                } else {
                    Notify.notifyInfo('Successfully deleted \'' + responses[0].data.absolutePath + '\'.');
                }
                refreshView();
            }, function(response) {
                Notify.notifyWarning('Failed to delete \'' + response.data.absolutePath + '\'!');
                refreshView();
            });
        }

        function openEditDialog(fileItem) {
            Notify.notifyWarning('NOT IMPLENMENTED');
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#downloadFileItems
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description
         * @param {event}
         *            not used...
         * @param {object}
         */
        function downloadFileItems($event, absolutePath) {
            $scope.pathsToDownload = [];
            if ($scope.fileItemsGridApi && $scope.fileItemsGridApi.selection.getSelectedCount() > 0) {
                // OK the user wants to delete many files...
                $scope.fileItemsGridApi.selection.getSelectedRows().forEach(function(row) {
                    $scope.pathsToDownload.push(row.absolutePath);
                });
            } else if (absolutePath !== undefined) {
                // OK the user wants to delete a specific file...
                $scope.pathsToDownload = [ absolutePath ];
            } else {
                Notify.notifyWarning('Please select the files to delete.');
                return;
            }

            explorerService.download($scope.pathsToDownload, function(response) {
                var length = response.data.length;
                console.log("data.length=" + length);
                var data = new Blob([ response.data ], {
                    type : response.headers('Content-Type')
                });
                FileSaver.saveAs(data, response.headers('Content-disposition').split('=')[1], true);
            }, function(response) {
                Notify.notifyWarning('Failed to delete \'' + response.data.absolutePath + '\'!');
            });
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#openCopyToDialog
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description
         * @param {event}
         *            not used...
         */
        function openCopyToDialog($event) {
            $scope.copyMove = {};
            $scope.copyMove.action = 'Copy';
            _openCopyMoveToDialog($event);
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#openMoveToDialog
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description
         * @param {event}
         *            not used...
         */
        function openMoveToDialog($event) {
            $scope.copyMove = {};
            $scope.copyMove.action = 'Move';
            _openCopyMoveToDialog($event);
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#_openCopyMoveToDialog
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description
         * @param {event}
         *            not used...
         */
        function _openCopyMoveToDialog($event) {
            $scope.copyMoveFileItem = undefined;
            if ($scope.fileItemsGridApi.selection.getSelectedCount() == 1) {
                // OK the user wants to delete many files...
                $scope.copyMoveFileItem = $scope.fileItemsGridApi.selection.getSelectedRows()[0];
            } else {
                Notify.notifyWarning('Please select exactly one file.');
                return;
            }
            $scope.copyMoveDialog = $uibModal.open({
                animation : true,
                ariaLabelledBy : 'modal-title',
                ariaDescribedBy : 'modal-body',
                templateUrl : 'js/explore/templates/CopyMoveToDialog.html',
                appendTo : $('#dialogAnchor'),
                scope : $scope
            });
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#closeCopyMoveToDialog
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description
         * @param {event}
         *            not used...
         */
        function closeCopyMoveToDialog() {
            $scope.copyMoveDialog.close();
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#copyMoveFileItems
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description
         * @param {event}
         *            not used...
         */
        function copyMoveFileItems() {
            $scope.copyMoveDialog.close();
            var absolutePath = $scope.copyMoveFileItem.absolutePath;
            var newAbsolutePath = $scope.copyMoveFileItem.newAbsolutePath;
            if (newAbsolutePath && newAbsolutePath.localeCompare('') != 0
                    && absolutePath.localeCompare(newAbsolutePath) != 0) {
                var action = ($scope.copyMove.action == 'Copy') ? 'COPY' : 'MOVE';
                explorerService.move(absolutePath, '', newAbsolutePath, action, function(response) {
                    Notify.notifyInfo('Successfully copy/move \'' + absolutePath + '\'.');
                    refreshView();
                }, function(response) {
                    Notify.notifyWarning('Failed to copy/move \'' + absolutePath + '\'!');
                    refreshView();
                });
            } else {
                Notify.notifyWarning('Invalid new name \'' + newAbsolutePath + '\'!');
            }

        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#openRenameDialog
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Open the rename dialog.
         * @param {event}
         *            not used...
         * @param {object}
         *            row the ui-grid.row for which the rename dialog should be displayed
         */
        function openRenameDialog($event, row) {
            $scope.renameFileItem = undefined;
            if ($scope.fileItemsGridApi.selection.getSelectedCount() == 1) {
                $scope.renameFileItem = $scope.fileItemsGridApi.selection.getSelectedRows()[0];
            } else if (row !== undefined) {
                $scope.renameFileItem = row.entity;
            } else {
                Notify.notifyWarning('Please select exactly one file.');
                return;
            }

            $scope.renameDialog = $uibModal.open({
                animation : true,
                ariaLabelledBy : 'modal-title',
                ariaDescribedBy : 'modal-body',
                templateUrl : 'js/explore/templates/RenameDialog.html',
                appendTo : $('#dialogAnchor'),
                scope : $scope
            });
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#closeRenameDialog
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Close the rename dialog.
         */
        function closeRenameDialog() {
            $scope.renameDialog.close();
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#renameFileItems
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Close the rename dialog and rename the file.
         */
        function renameFileItems() {
            $scope.renameDialog.close();
            var name = $scope.renameFileItem.name;
            var newName = $scope.renameFileItem.newName;
            if (newName && newName.localeCompare('') != 0 && name.localeCompare(newName) != 0) {
                explorerService.move($scope.renameFileItem.absolutePath, $scope.renameFileItem.parent, newName, 'MOVE',
                        function(response) {
                            Notify.notifyInfo('Successfully renamed \'' + response.data.absolutePath + '\'.');
                            refreshView();
                        }, function(response) {
                            Notify.notifyWarning('Failed to rename \'' + response.data.absolutePath + '\'!');
                            refreshView();
                        });
            } else {
                Notify.notifyWarning('Invalid new name for \'' + name + '\'!');
            }
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#getMatchingFilenames
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description This method returns a promise used in the typeahead for renaming dialog
         */
        $scope.getMatchingFilenames = function(path, hint) {
            if (hint) {
                return explorerService.getMatchingFilenames(path, hint, function(response) {
                    return response.data.map(function(item) {
                        return item.name;
                    });
                });
            }
        };

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#getMatchingAbsolutePaths
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description This method returns a promise used in the typeahead for copy/move dialog
         */
        $scope.getMatchingAbsolutePaths = function(path, hint) {
            if (hint) {
                return explorerService.getMatchingFilenames(path, hint, function(response) {
                    return response.data.map(function(item) {
                        return item.absolutePath;
                    });
                });
            }
        };

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#openPropertiesDialog
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Open the properties dialog.
         * @param {event}
         *            not used...
         * @param {object}
         *            row the ui-grid.row for which the property dialog should be displayed
         */
        function openPropertiesDialog($event, row) {
            $scope.propertiesFileItem = undefined;
            if ($scope.fileItemsGridApi.selection.getSelectedCount() == 1) {
                $scope.propertiesFileItem = $scope.fileItemsGridApi.selection.getSelectedRows()[0];
            } else if (row !== undefined) {
                $scope.propertiesFileItem = row.entity;
            } else {
                Notify.notifyWarning('Please select exactly one file.');
                return;
            }

            $scope.propertiesDialog = $uibModal.open({
                animation : true,
                ariaLabelledBy : 'modal-title',
                ariaDescribedBy : 'modal-body',
                templateUrl : 'js/explore/templates/PropertiesDialog.html',
                appendTo : $('#dialogAnchor'),
                scope : $scope
            });
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#closePropertiesDialog
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Close the properties dialog.
         */
        function closePropertiesDialog() {
            $scope.propertiesDialog.close();
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#selectAllRows
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Selects all rows.
         */
        function selectAllRows() {
            $scope.fileItemsGridApi.selection.selectAllRows();
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#clearSelectedRows
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Unselects all rows
         */
        function clearSelectedRows() {
            $scope.fileItemsGridApi.selection.clearSelectedRows();
        }

        /**
         * @ngdoc function
         * @name fbi-mafia.explorer:ExplorerController#toggleRowSelection
         * @methodOf fbi-mafia.explorer:ExplorerController
         * @description Toggles data row as selected or unselected
         */
        function toggleRowSelection() {
            $scope.fileItemsGridApi.grid.rows.forEach(function(row) {
                $scope.fileItemsGridApi.selection.toggleRowSelection(row.entity);
            });
        }

        function switchView(newView) {
            $scope.view = newView;
        }
    }

})();
