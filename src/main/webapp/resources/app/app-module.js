var bcloud=angular.module('bcloud',['ngRoute','ui.bootstrap','ngAnimate','tagger','ngCookies','satellizer','ngStorage','ngFacebook','ngSanitize','angular.chosen','ngImgCrop','yaru22.angular-timeago']);

bcloud.config(function($routeProvider,$locationProvider){
	//$locationProvider.html5Mode(true);
	$routeProvider

	.when('/home',{
		templateUrl : 'resources/views/home.html',
		controller : 'homeCtrl',			
	})

	.when('/newregister',{
		templateUrl : 'resources/views/newregister.html',
		controller : 'newRegisterCtrl'
	})

	.when('/profile',{
		templateUrl : 'resources/views/profile.html',
		controller : 'profileCtrl'		
	})

	.when('/employeeLogin',{
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

	.when('/employeeSignUp',{
		templateUrl : 'resources/views/employeesignup.html',
		controller : 'employeeSignUpCtrl'
	})

	.when('/friendRequestList',{
		templateUrl : 'resources/views/friendRequestList.html',
		controller : 'friendRequestListCtrl'
	})

	.when('/employeeMail',{
		templateUrl : 'resources/views/employee/mail.html',
		controller : 'mailCtrl',
		resolve:{
				getinboxmails:function($http,$q,authentication){
					employee={
						"email" : authentication.getEmployee()
					}
					var defer=$q.defer();			
					$http.post('/blouda/getInboxMails',employee)
					.success(function(data){
					if(data.login)
					{
						authentication.loginEmployee(data);
					}										
						defer.resolve(data);
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
				},
				getsendMails:function($http,$q,authentication){
					employee={
						"email" : authentication.getEmployee()
					}
					var defer=$q.defer();			
					$http.post('/blouda/getsendMails',employee)
					.success(function(data){
					if(data.login)
					{
						authentication.loginEmployee(data);
					}										
						defer.resolve(data);
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
				}			
		}
	})

	.when('/friendsList',{
		templateUrl : 'resources/views/friends.html',
		controller : 'friendsListCtrl',
		resolve : {
			showFriendsList:function(authentication,$q,$http){
				if(angular.isUndefined (authentication.getEmployee() ) | authentication.getEmployee() != "null")
				{
					employee={
						"email" : authentication.getEmployee()
					}
					var defer=$q.defer();			
					$http.post('/blouda/showFriendsList',employee)
					.success(function(data){										
						defer.resolve(data);					
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
				}				
			}
		}
	})

	.when('/socialAPIStatus',{
		template : 'Please wait ...',
		controller : 'socialAPIStatusCtrl',
		resolve : {
			getAPIStatus:function(employeeSocialNetworkServices,$q,$http){
					var defer=$q.defer();			
					$http.get('/blouda/SocialNetworkDetails')
					.success(function(data){										
						defer.resolve(data);					
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
								
			}
		} 			
	})

	.when('/employeeAuth',{
		templateUrl : 'resources/views/employeeCredentials.html',
		controller : 'employeeAuthCtrl'
	})

	.when('/employeeUpdateProfile',{
		templateUrl : 'resources/views/employee/employeeUpdateProfile.html',
		controller : 'employeeProfileUpdateCtrl',
		resolve : {
			getEmployeeProfile : function($q,$http,authentication){
					var defer=$q.defer();			
					console.log(authentication.getEmployee());
					var employee = {
						"email":authentication.getEmployee()
					};
					$http.post('/blouda/getemployeeProfileforUpdate',employee)
					.success(function(data){										
						defer.resolve(data);					
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
			}
		}			
	})

	.when('/loginRedirect',{
		templateUrl : 'resources/views/loginRedirect.html',
		controller : 'employeeLoginRedirectCtrl',
		resolve : {
			getLoggedInEmpployee : function($q,$http){
					var defer=$q.defer();			
					$http.get('/blouda/SocialNetworkDetails')
					.success(function(data){										
						defer.resolve(data);					
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
			}
		}	
	})

	.when('/employeeRegister',{
		templateUrl : 'resources/views/employee/employeeregister.html',
		controller : 'employeeregisterCtrl'
	})

	.when('/employeeProfile',{
		templateUrl : 'resources/views/employee/employeeprofile.html',
		controller : 'employeeProfileCtrl',
		resolve : {
			getAuthEmpployeeProfile : function(authentication){
					
					return authentication.getnewEmployeeProfile();
			}
		}

	})
	.otherwise({
		redirectTo: '/home'
	});
});


bcloud.config(['$facebookProvider', function($facebookProvider) {
    $facebookProvider.setAppId('1603106119947122').setPermissions(['email','user_friends']);
  }]);

bcloud.run(['$rootScope', '$window','$templateCache', function($rootScope, $window, $templateCache) {
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

    $rootScope.$on("$routeChangeStart",function(event,current,next){

    });
    $rootScope.$on("$routeChangeSuccess",function(event,current,next){
    	
    });
    $rootScope.$on("$routeChangeError",function(event,current,next){
    	
    });

	$templateCache.put('employeeProfileCourse.html','<div class="col-md-12" > <div class="form-group"> \n <label for="Full Name" class="control-label" style="text-align:left;width:100px;">Course Name</label> <input type="text" class="form-control" ng-model="fullname" placeholder="Enter course name" required> </div> <div class="form-group"> <label for="from">From</label> <select class="form-control" ng-model="currentlocation" name="currentlocation" required> <option>2001</option> </select> </div> <div class="form-group"> <label for="from">To</label> <select class="form-control" ng-model="currentlocation" name="currentlocation" required> <option>2002</option> </select> </div> </div>');
    	

  }]);

bcloud.config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    $httpProvider.defaults.withCredentials = true;
    // $http.defaults.headers.post['X-CSRFToken'] = $cookies.csrftoken;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
   
}]);
