var bcloud=angular.module('bcloud',['ngRoute','ui.bootstrap','ngAnimate','tagger','ngCookies','satellizer','ngStorage','ngFacebook','ngSanitize']);

bcloud.config(function($routeProvider,$locationProvider){
	//$locationProvider.html5Mode(true);
	$routeProvider

	.when('/home',{
		templateUrl : 'resources/views/home.html',
		controller : 'homeCtrl',
		resolve:{
			getRequestedFriendsCount:function(authentication,$q,$http){
				if(authentication.getEmployee()!="null")
				{
					currentEmployeeForFriend = {
						"email":authentication.getEmployee()
					};
					var defer=$q.defer();			
					$http.post('/blouda/getRequestedFriendsCount',currentEmployeeForFriend)
					.success(function(data){										
						defer.resolve(data);					
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
				}
				else
				{
					data ={
						"count":0
					}
				}	
				
			}
		}	
	})

	.when('/newregister',{
		templateUrl : 'resources/views/newregister.html',
		controller : 'newRegisterCtrl'
	})

	.when('/profile',{
		templateUrl : 'resources/views/profile.html',
		controller : 'profileCtrl'		
	})

	.when('/login',{
		templateUrl : 'resources/views/newlogin.html',
		controller : 'LoginCtrl'
	})
	
	.when('/register',{
		templateUrl : 'resources/views/register.html',
		controller : 'registerCtrl'
	})

	.when('/employerSearchResult',{
		templateUrl : 'resources/views/result.html',
		controller : 'employerSearchResultCtrl',
		resolve : {
			getEmployeeProfiles : function(employerServices,$q){
						
				var defer=$q.defer();
				employerServices.getEmployeeProfiles().success(function(data,status,config){
					defer.resolve(data);
				}).error(function(data,status,config){
					defer.reject(data);
				});

				return defer.promise;
			}
		}
	})
	
	.when('/analytics',{
		templateUrl : 'resources/views/analytics.html',
		controller : 'analyticsCtrl',
		resolve : {			
			industry_types: function(employerServices,$q){
				var defer=$q.defer();
				employerServices.get_industry_types().success(function(data,status,config){
					defer.resolve(data);
				}).error(function(data,status,config){
					defer.reject(data);
				});

				return defer.promise;
			},
			locations: function(employerServices,$q){
				var defer=$q.defer();
				employerServices.get_locations().success(function(data,status,config){
					defer.resolve(data);
				}).error(function(data,status,config){
					defer.reject(data);
				});

				return defer.promise;
			},
			roles: function(employerServices, $q){
				var defer=$q.defer();
				employerServices.get_roles().success(function(data,status,config){
					defer.resolve(data);
				}).error(function(data,status,config){
					defer.reject(data);
				});

				return defer.promise;
			},
			experiences: function(employerServices, $q){
				var defer=$q.defer();
				employerServices.get_experiences().success(function(data,status,config){
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

	.when('/docsearch',{
		templateUrl : 'resources/views/documentsearch.html',
		controller : 'docSearchCtrl'
	})
	
	.when('/friendSearch',{
		templateUrl : 'resources/views/friendSearch.html',
		controller : 'firendSearchCtrl',
		resolve : {
			getCommonThingsforFriendSearch: function(backend, $q){
					var defer=$q.defer();
					backend.getcommonThingsforFriendRequest().success(function(data,status,config){
						defer.resolve(data);
					}).error(function(data,status,config){
						defer.reject(data);
					});
				return defer.promise;
				}
		}

	})

	.otherwise({
		redirectTo: '/home'
	});
});


bcloud.config(['$facebookProvider', function($facebookProvider) {
    $facebookProvider.setAppId('423450941147620').setPermissions(['email','user_friends']);
  }]);

bcloud.run(['$rootScope', '$window', function($rootScope, $window) {
    (function(d, s, id) {
      var js, fjs = d.getElementsByTagName(s)[0];
      if (d.getElementById(id)) return;
      js = d.createElement(s); js.id = id;
      js.src = "//connect.facebook.net/en_US/sdk.js";
      fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));
    $rootScope.$on('fb.load', function() {
      $window.dispatchEvent(new Event('fb.load'));
    });
  }]);
