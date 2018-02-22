(function(window) {
    'use strict';

    // attach Notify as a property of window
    var Notify = window.Notify || (window.Notify = {});

    //
    // These are the default for all notifications.
    // 
    $.notifyDefaults({
        placement : {
            from : "top",
            align : "center"
        },
        animate : {
            enter : 'animated fadeInDown',
            exit : 'animated fadeOut'
        }
    });

    var notifyWait = function(action) {
        return $.notify({
            icon : 'fa fa-spin fa-spinner',
            title : '<strong>' + action + '</strong>',
            message : 'Please stand by...'
        });
    }

    /*
     * This function notify at the Info level.
     */
    var notifyInfo = function(message) {
        $.notify({
            title : '<i class="fa fa-info"></i>&nbsp;' + message,
            message : '',
        }, {
            type : 'info',
            delay : 1000,
        });
    }

    /*
     * This function notify at the Warning level.
     */
    var notifyWarning = function(message) {
        $.notify({
            title : '<i class="fa fa-warning"></i>&nbsp;' + message,
            message : '',
        }, {
            type : 'warning',
            delay : 4000,
        });
    }

    function publishExternalAPI(Notify) {
        angular.extend(Notify, {
            'notifyWait' : notifyWait,
            'notifyInfo' : notifyInfo,
            'notifyWarning' : notifyWarning
        });
    }

    publishExternalAPI(Notify);

})(window);
