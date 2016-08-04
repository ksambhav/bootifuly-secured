angular.module('uaa').controller('LoginCtrl',function($scope,$http,$cookies){
	console.log('login controller');
	$scope.cok = $cookies.get('XSRF-TOKEN');
	console.log($scope.cok);

});