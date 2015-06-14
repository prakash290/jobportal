bcloud.factory('employeeServices',[ '$http','$q','authentication','$cookieStore','$cookies', 
	function($http,$q,authentication,$cookieStore,$cookies){
		var newUser = null;
		return {

			insertEmployee : function (userDetails) {
				
				 var formdata = new FormData();
				 console.log(userDetails.resume);
				 var name=userDetails;
				 
				 console.log(angular.toJson(name));

				 formdata.append('data',angular.toJson(name));
        		formdata.append('file', userDetails.resume);        		        		      		
        		if(!angular.isUndefined(userDetails.profileimage))
		        {
					formdata.append("profileimage",userDetails.profileimage);		               	
		  		}
				var defer=$q.defer();				
				$http({
				    method: 'POST',				   
				    url: '/blouda/employeeCreate',
				    headers: {'Content-Type': undefined},
				    data: { resume: userDetails.resume, name:name,profileimage:userDetails.profileimage  }	
				    ,
				    transformRequest: function(data, headersGetterFunction) {
				        var formData = new FormData();
               			formData.append("userDetails", angular.toJson(data.name));
		               	formData.append("file",data.resume);
		               	if(!angular.isUndefined(data.profileimage))
		               	{
							formData.append("profileimage",data.profileimage);		               	
		               	}
                		return formData; 
                	}		
				})
				.success(function(data){
					authentication.newUser(data);
					authentication.setnewEmployeeProfile(data);	
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

			generateLinkedInAccessToken:function(employeeCredentials){
				
				var defer=$q.defer();			
				$http.post('/blouda/linkedInaccessToken',employeeCredentials)
				.success(function(data){
					console.log(data);
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			},
			generatefbtoken:function(fb){
				var defer=$q.defer();			
				$http.post('/blouda/emloyeefaceBookLogin',fb)
				.success(function(data){					
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			},
			getGoogleContacts:function(token)
			{	
				console.log(token);
				var tokenobj = {
					"access_token":token
				};

				var meurl="https://www.googleapis.com/plus/v1/people/me";
				var contacturl = "https://www.google.com/m8/feeds/contacts/default/full?alt=json";
				var feedurl ="https://www.google.com/m8/feeds/contacts/default/full?alt=json&access_token="+token;
				$http({
			        url: feedurl, 			        
			        method: "GET",
			        /*headers:{
			        	 'Authorization': 'Basic '+token,
			        }*/			        
			     }).success(function(data){
			        console.log("Success");
			     });

				var xhr = new XMLHttpRequest();
				var oauthToken = token;
				xhr.open('GET',
				  'https://www.google.com/m8/feeds/contacts/default/full?alt=json&access_token=' + encodeURIComponent(token));
				xhr.send();
				

			},
			setSocialAPIResult : function(employeeInfo){
				$cookieStore.put("loginUser",employeeInfo);				
			},
			getSocialAPIResult : function(){
				return $cookieStore.get("loginUser");;
			},
			checkFBEmployeeExists : function(){

			},
			createNewEmployee : function(employeeCredentials){
					var defer=$q.defer();			
					$http.post('/blouda/createNewSocialAccount',employeeCredentials)
					.success(function(data){										
						defer.resolve(data);
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
			},
			employeeSignUp : function(newEmployeeSignup){
					var defer=$q.defer();			
					$http.post('/blouda/newEmployeeRegister',newEmployeeSignup)
					.success(function(data){										
						defer.resolve(data);
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
			},
			getProfileImage : function(){
				var employee = 
				{ "email" : authentication.getEmployee()};

				var defer=$q.defer();			
					$http.post('/blouda/getEmployeeProfileImage',employee)
					.success(function(data){										
						defer.resolve(data);
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
			},
			updateEmployeeProfile : function(newProfile){
				var defer=$q.defer();			
				$http.post('/blouda/updateEmployeeProfiles',newProfile)
				.success(function(data){										
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			},
			updateEmployeeProfileImage : function(EmployeeProfileImage){
				var defer=$q.defer();				
				$http({
				    method: 'POST',				   
				    url: '/blouda/updateEmployeeProfileImage',
				    headers: {'Content-Type': undefined},
				    data: { email: EmployeeProfileImage.email,profileimage:EmployeeProfileImage.profileimage  }	
				    ,
				    transformRequest: function(data, headersGetterFunction) {
				        var formData = new FormData();
               			formData.append("email",data.email);
		               	if(!angular.isUndefined(data.profileimage))
		               	{
							formData.append("profileimage",data.profileimage);		               	
		               	}
                		return formData; 
                	}		
				})
				.success(function(data){					
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});	

		
				return defer.promise;
			},
			updateEmployeeProfileResume : function(EmployeeProfileResume)
			{
				EmployeeProfileResume.email = authentication.getEmployee();
				
				console.log(EmployeeProfileResume);
				var defer=$q.defer();				
				$http({
				    method: 'POST',				   
				    url: '/blouda/updateEmployeeProfileResume',
				    headers: {'Content-Type': undefined},
				    data: { userObject: angular.toJson(EmployeeProfileResume),profileresume:EmployeeProfileResume.myFile  },
				    transformRequest: function(data, headersGetterFunction) {
				        var formData = new FormData();
               			formData.append("userobject",data.userObject);
               			formData.append("profileresume",data.profileresume);
               			console.log(data.profileresume);
		               	if(!angular.isUndefined(data.userObject.myFile))
		               	{
									               	
		               	}
                		return formData; 
                	}		
				})
				.success(function(data){					
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});
				return defer.promise;
			},
			storeSocialEmployeeProfile : function(data){
				$cookieStore.put("commonEmployeeProfile",data);					
			},
			storeEmployeeProfile : function(){
					var defer=$q.defer();			
					console.log(authentication.getEmployee());
					var employee = {
						"email":authentication.getEmployee()
					};
					$http.post('/blouda/getemployeeProfileforUpdate',employee)
					.success(function(data){										
						defer.resolve(data);	
						$cookieStore.put("commonEmployeeProfile",data);				
					}).error(function(data){
						defer.reject(data);
					});			
					return defer.promise;
			},
			getCommonEmployeeProfile : function(){
				return $cookieStore.get("commonEmployeeProfile");
			},
			clearAllCookies : function(){
				angular.forEach($cookies, function (v, k) {
					    $cookieStore.remove(k);
				});
			}

		}
}]);



