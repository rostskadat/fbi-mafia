(function() {
    'use strict';

    var module = angular.module('fbi-mafia.filter', []);

    module.filter('size', [ '$filter', FilterFileSize ]);
    module.filter('friendlyDate', [ FilterFileLastModifiedTime ]);
    module.filter('fileType', [ FilterFileType ]);
    

    /**
     * This filter allows to display a number as a user friendly size in bytes
     * @param $filter a reference to angular filter (to get the original number filter).
     * @returns a string with a indicator of the size unit. 
     */
    function FilterFileSize($filter) {
        return function(input, fractionSize) {
            if (input < 1024) {
                return "1 KB";
            } else if (input < 1024 * 1024) {
                return $filter('number')((input / 1024), fractionSize) + " KB";
            } else if (input < 1024 * 1024 * 1024) {
                return $filter('number')((input / (1024 * 1024)), fractionSize) + " MB";
            } else {
                return $filter('number')((input / (1024 * 1024 * 1024)), fractionSize) + " GB";
            }
        };
    }

    /**
     * This function uses the <a href="https://momentjs.com/">momentJs</a> library to display nicely formated date
     * @returns
     */
    function FilterFileLastModifiedTime() {
        return function(input) {
            return moment(input).fromNow();
        }
    }

    function FilterFileType() {
        return function(input) {
            return (input === 'FILE') ? 'FILE + mimeType' : ''; 
        }
    }

})();
