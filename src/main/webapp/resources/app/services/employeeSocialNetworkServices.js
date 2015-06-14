bcloud.factory('employeeSocialNetworkServices',[ '$http','$q','authentication','$cookieStore', 
	function($http,$q,authentication,$cookieStore){
		var newUser = null;
		return {
				getLinkedInLoginUrl:function(){				
					var defer=$q.defer();			
					$http.get('/blouda/getLinkedInLoginUrl')
					.success(function(data){										
						defer.resolve(data);
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
				},
				getGoogleLoginUrl:function(){				
					var defer=$q.defer();			
					$http.get('/blouda/getGoogleLoginUrl')
					.success(function(data){										
						defer.resolve(data);
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
				},
				getEmployeeSocialStatus : function(){
					var defer=$q.defer();			
					$http.get('/blouda/SocialNetworkDetails')
					.success(function(data){										
						defer.resolve(data);
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
				},
				createNewEmployeeusingSocial : function(employeeCredentials){
					var defer=$q.defer();			
					$http.post('/blouda/createNewSocialAccount',employeeCredentials)
					.success(function(data){										
						defer.resolve(data);
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
				} 
		}
}]);

