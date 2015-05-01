bcloud.factory('authentication',['$cookieStore',function($cookieStore){

	var authtoken = $cookieStore.get("authentication");

	return{
			loginEmployee : function(employeeDetails){
				console.log(employeeDetails);
				$cookieStore.put('authentication',employeeDetails.email);	  				
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
				return authtoken;
			},
			isEmployeeLoggedIn :function(){
				return authtoken;
			}
	}

}]);