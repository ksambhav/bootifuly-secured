angular.module('uaa', ['ui.router','ngAria','ngMessages', 'ngMaterial', 'ngMessages','ngCookies']);

angular.module('uaa').config(function($stateProvider, $urlRouterProvider, $httpProvider) {

    $stateProvider.state('login', {
        url: '/',
        templateUrl: 'partial/login/login.html',
        controller: 'LoginCtrl'
    });
    /* Add New States Above */
    $urlRouterProvider.otherwise('/');
    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

});

angular.module('uaa').run(function($rootScope) {

    $rootScope.safeApply = function(fn) {
        var phase = $rootScope.$$phase;
        if (phase === '$apply' || phase === '$digest') {
            if (fn && (typeof(fn) === 'function')) {
                fn();
            }
        } else {
            this.$apply(fn);
        }
    };

});
