angular.module('sso', ['ui.router','ngAria','ngMessages', 'ngMaterial', 'ngMessages','ngCookies']);

angular.module('sso').config(function($stateProvider, $urlRouterProvider,$httpProvider) {

    $stateProvider.state('home', {
        url: '/home',
        templateUrl: 'partial/home/home.html',
        controller : 'HomeCtrl',
        resolve : {
        	user : function($http){
        		return $http.get('/me').then(function(success){
        			console.log(success.data);
        		},function(error){
        			console.error(error);
        			alert('Shit Happens');
        		});
        	}
        }
    });
    /* Add New States Above */
    $urlRouterProvider.otherwise('/home');
    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
});

angular.module('sso').run(function($rootScope) {

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
