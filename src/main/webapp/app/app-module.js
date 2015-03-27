var bcloud=angular.module('bcloud',['ngRoute','ui.bootstrap','ngAnimate']);

bcloud.config(function($routeProvider){
	
	$routeProvider
	
	.when('/home',{
		templateUrl : 'views/home.html',
		controller : 'homeCtrl'
	})
	
	.when('/register',{
		templateUrl : 'views/register.html',
		controller : 'registerCtrl'
	})
	
	.when('/analytics',{
		templateUrl : 'views/analytics.html',
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
		templateUrl : 'views/hireme.html',
		controller : 'hiremeCtrl'
	})
	
	.otherwise({
		redirectTo: '/home'
	});
});