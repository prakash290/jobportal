bcloud.factory('authentication',['$cookieStore',
	function($cookieStore){

	var authtoken = $cookieStore.get("authentication");
	return{
			loginEmployee : function(employeeDetails){

				if(!angular.isUndefined(employeeDetails.email))
				{	
					$cookieStore.put('authentication',employeeDetails.email);
				}				
				authtoken=$cookieStore.get('authentication');
			},
			logoutEmployee : function(){				
				$cookieStore.remove('authentication');
				authtoken=$cookieStore.get('authentication');
		  		return authtoken;
			},
			newUser : function(userObject){
				$cookieStore.put('authentication',userObject.user.email);	  				
		  		authtoken=$cookieStore.get('authentication');		  		
			},
			newLinkedInUser : function(linkedinuserObject){
				$cookieStore.put('authentication',linkedinuserObject.user.linkedinemail);	  				
		  		authtoken=$cookieStore.get('authentication');
			},
			getEmployee : function(){
				authtoken=$cookieStore.get('authentication');
				return authtoken;
			},
			isEmployeeLoggedIn :function(){
				authtoken=$cookieStore.get('authentication');				
				return authtoken;
			},
			setEmployeeProfile : function(profile){
				$cookieStore.put("empprofile",profile);				
			},
			getEmployeeProfile : function(){
				return $cookieStore.get("empprofile");
			},
			setnewEmployeeProfile : function(employee){
				$cookieStore.put("employeeprofile", employee.user);
			},
			getnewEmployeeProfile : function(){
				return $cookieStore.get("employeeprofile");
			}

	}

}]);
