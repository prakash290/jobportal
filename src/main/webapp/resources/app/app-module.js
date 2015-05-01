var bcloud=angular.module('bcloud',['ngRoute','ui.bootstrap','ngAnimate','tagger','ngCookies','satellizer','ngStorage','ngFacebook','ngSanitize']);

bcloud.config(function($routeProvider,$locationProvider){
	//$locationProvider.html5Mode(true);
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
 	

bcloud.config(function($authProvider) {

    $authProvider.facebook({
      clientId: '624059410963642'
    });

    $authProvider.google({
      clientId: '631036554609-v5hm2amv4pvico3asfi97f54sc51ji4o.apps.googleusercontent.com'
    });

    $authProvider.github({
      clientId: '0ba2600b1dbdb756688b'
    });

    $authProvider.linkedin({
      clientId: '75xcotynliiowz'
    });

    $authProvider.yahoo({
      clientId: 'dj0yJmk9dkNGM0RTOHpOM0ZsJmQ9WVdrOVlVTm9hVk0wTkRRbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD0wMA--'
    });

    $authProvider.live({
      clientId: '000000004C12E68D'
    });

    $authProvider.twitter({
      url: '/auth/twitter'
    });

    $authProvider.oauth2({
      name: 'foursquare',
      url: '/auth/foursquare',
      redirectUri: window.location.origin,
      clientId: 'MTCEJ3NGW2PNNB31WOSBFDSAD4MTHYVAZ1UKIULXZ2CVFC2K',
      authorizationEndpoint: 'https://foursquare.com/oauth2/authenticate',
    });

  });


