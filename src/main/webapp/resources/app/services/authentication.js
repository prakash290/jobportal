bcloud.factory('authentication',['$cookieStore',function($cookieStore){

	var authtoken = $cookieStore.get("authentication");

	return{

			loginUser : function(user){				
				if(user.username=="admin" && user.password=="admin")
		  			{
					  $cookieStore.put('authentication',true);	  				
		  			  authtoken=$cookieStore.get('authentication');	  			
		  			}
		  		else
		  			{
		  			 $cookieStore.put('authentication',false);
		  			authtoken=$cookieStore.get('authentication');			  			  			
		  			}
				 return authtoken;	
			},

			getUser : function(){
				return authtoken;
			},

			logoutUser : function(){
				$cookieStore.put('authentication',false);
		  		authtoken=$cookieStore.get('authentication');
		  		return authtoken;
			},
			newUser : function(userObject){
				console.log("This is set be cookie of userobject id : "+userObject.user._id.timeSecond);
				$cookieStore.put('authentication',true);	  				
		  		authtoken=$cookieStore.get('authentication');
			}
	}

}]);