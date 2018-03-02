(function() {
    'use strict';

    /**
     * This controller allows to set the proper language
     */
    var module = angular.module('fbi-mafia.I18n', [ 'pascalprecht.translate' ]);
    module.config([ '$translateProvider', I18nConfigure ]);
    module.run([ '$rootScope', '$translatePartialLoader', '$translate', 'I18nService', I18nRun ]);
    module.factory('I18nService', [ '$translate', 'PreferencesService', I18nService ]);
    module.controller('I18nController', [ '$translatePartialLoader', 'I18nService', I18nController ]);
    module.directive('i18nSelect', I18nSelectDirective);

    function I18nConfigure($translateProvider) {
        $translateProvider.useSanitizeValueStrategy('sanitize');
        $translateProvider.determinePreferredLanguage();
        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate : 'i18n/{part}/{lang}.json'
        });
    }

    function I18nRun($rootScope, $translatePartialLoader, $translate, I18nService) {
        var preferredLanguage = I18nService.getDefaultLanguage();
        console.log("[I18nRun]: Setting language to " + preferredLanguage);
        $translatePartialLoader.addPart('cosaNostra');
        $translate.use(preferredLanguage);
        $translate.refresh();
        $rootScope.$on('$translatePartialLoaderStructureChanged', function() {
            $translate.refresh();
        });
    }

    function I18nService($translate, PreferencesService) {
        var service = {
            _init : function() {

            },

            setLanguage : function(language) {
                if (language) {
                    $translate.use(language);
                    PreferencesService.set('preferredLanguage', language);
                }
            },

            getDefaultLanguage : function() {
                var defaultLanguage = $translate.preferredLanguage();
                var preferredLanguage = PreferencesService.get('preferredLanguage');
                if (preferredLanguage == null) {
                    // only take the language part, not the local...
                    preferredLanguage = defaultLanguage != null ? defaultLanguage.split("_")[0] : 'en';
                    PreferencesService.set('preferredLanguage', preferredLanguage);
                }
                return preferredLanguage;
            }
        }
        service._init();
        return service;
    }

    function I18nController($translatePartialLoader, I18nService) {

        $translatePartialLoader.addPart('cosaNostra');

        var self = this;
        self.setLanguage = setLanguage;
        self.error = false;

        function setLanguage(language) {
            I18nService.setLanguage(language);
        }
    }

    function I18nSelectDirective(I18nService) {
        return {
            restrict : 'E',
            templateUrl : 'js/i18n/i18n-select.html'
        };
    }
})();
