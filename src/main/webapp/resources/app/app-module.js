var bcloud=angular.module('bcloud',['ngRoute','ui.bootstrap','ngAnimate','tagger','ngCookies']);

bcloud.config(function($routeProvider){
	
	$routeProvider
	
	.when('/home',{
		templateUrl : 'resources/views/home.html',
		controller : 'homeCtrl'
	})

	.when('/newregister',{
		templateUrl : 'resources/views/newregister.html',
		controller : 'newRegisterCtrl'
	})

	.when('/profile',{
		templateUrl : 'resources/views/profile.html',
		controller : 'profileCtrl'		
	})
	
	.when('/register',{
		templateUrl : 'resources/views/register.html',
		controller : 'registerCtrl'
	})
	
	.when('/analytics',{
		templateUrl : 'resources/views/analytics.html',
		controller : 'analyticsCtrl',
		resolve : {			
			industry_types: function(backend,$q){
				var defer=$q.defer();
				backend.get_industry_types().success(function(data,status,config){
					defer.resolve(data);
				}).error(function(data,status,config){
					defer.reject(data);
				});

				return defer.promise;
			},
			locations: function(backend,$q){
				var defer=$q.defer();
				backend.get_locations().success(function(data,status,config){
					defer.resolve(data);
				}).error(function(data,status,config){
					defer.reject(data);
				});

				return defer.promise;
			},
			roles: function(backend, $q){
				var defer=$q.defer();
				backend.get_roles().success(function(data,status,config){
					defer.resolve(data);
				}).error(function(data,status,config){
					defer.reject(data);
				});

				return defer.promise;
			},
			experiences: function(backend, $q){
				var defer=$q.defer();
				backend.get_experiences().success(function(data,status,config){
					defer.resolve(data);
				}).error(function(data,status,config){
					defer.reject(data);
				});

				return defer.promise;
			}

		}
	})

	.when('/hireme',{
		templateUrl : 'resources/views/hireme.html',
		controller : 'hiremeCtrl'
	})
		
	.otherwise({
		redirectTo: '/home'
	});
});