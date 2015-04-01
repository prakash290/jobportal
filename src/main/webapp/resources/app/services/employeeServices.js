bcloud.factory('employeeServices',[ '$http','$q','authentication', 
	function($http,$q,authentication){

	var newUser = null;

		return {

			insertEmployee : function (userDetails) {										
				var defer=$q.defer();			
				$http.post('/blouda/employeeCreate',userDetails)
				.success(function(data){									
					authentication.newUser(data);					
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
			return defer.promise;
			},

			newUserDetails : function(user){
				newUser = user;
				return newUser;
			},
			getNewUser : function(){				
				return newUser;
			},
			employeeProfileUpdate : function(employeeProfile){
				var defer=$q.defer();			
				$http.post('/blouda/employeeProfile',employeeProfile)
				.success(function(data){										
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			},

		}
}]);