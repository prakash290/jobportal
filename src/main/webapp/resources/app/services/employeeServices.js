bcloud.factory('employeeServices',[ '$http','$q','authentication','$cookieStore', 
	function($http,$q,authentication,$cookieStore){
		var newUser = null;
		return {

			insertEmployee : function (userDetails) {
				
				 var formdata = new FormData();
				 console.log(userDetails.resume);
				 var name=userDetails;
				 
				 console.log(angular.toJson(name));

				 formdata.append('data',angular.toJson(name));
        		formdata.append('file', userDetails.resume);        		        		      		
				var defer=$q.defer();			
				/*$http.post('/blouda/employeeCreate',formdata,
				{
					 transformRequest: function(data, headersGetterFunction) {
        			return data; // do nothing! FormData is very good!
    				}
					  headers: {
					    'Content-Type': 'multipart/form-data' 
					  }

				})*/
				$http({
				    method: 'POST',				   
				    url: '/blouda/employeeCreate',
				    headers: {'Content-Type': undefined},
				    data: { resume: userDetails.resume, name:name  }	
				    ,
				    transformRequest: function(data, headersGetterFunction) {
				        var formData = new FormData();
               			 formData.append("userDetails", angular.toJson(data.name));
		               	formData.append("file",data.resume);
                	
                			return formData; 
                	}		
				})
				.success(function(data){
					authentication.newUser(data);					
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});	

		
			return defer.promise;
			},

			newUserDetails : function(user){				
				if(user != 'empty' )
				{	
				newUser = user;
				console.log(newUser);
				}
				else
				{
				
				}
				return newUser;
			},
			getNewUser : function(){

				newUser = this.newUserDetails("empty");											
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
			getUserProfile : function(){
				var defer=$q.defer();

				var useremailid = 
				{ "email" : $cookieStore.get('authentication')};


				$http.post('/blouda/getemployeeProfile',useremailid)
				.success(function(data){										
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});		
				return defer.promise;
			},
			insertThirdPartyEmployee : function(employeeDetails){
				var defer=$q.defer();			
				$http.post('/blouda/employeeProfile',employeeDetails)
				.success(function(data){										
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			},
			employeeLogin : function(employeeCredentials){

				var defer=$q.defer();			
				$http.post('/blouda/employeeLogin',employeeCredentials)
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
			linkedInLogin : function(linkedinuser){

				var defer=$q.defer();			
				$http.post('/blouda/emloyeeLinkedInLogin',linkedinuser)
				.success(function(data){
				if(data.isUserExists)
				{
					authentication.loginEmployee(data);
				}										
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;

			},

		}
}]);

