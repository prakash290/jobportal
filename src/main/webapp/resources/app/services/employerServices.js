bcloud.factory('employerServices',[ '$http','$q', '$cookieStore',
	function($http,$q,$cookieStore){

		var selectedElements={};

		return{

			// Get all industry Types 			
			get_industry_types: function(){
				industries= $http.get('/blouda/getAllIndustries');			 
				return industries;
			},

			// Get all Locations 
			get_locations: function(){			
				locations= $http.get('/blouda/getAllLocations');			
				return locations;
			},

			// Get all Roles
			get_roles: function(){			
				roles= $http.get('/blouda/getAllRoles');			
				return roles;
			},

			// Get all Experiences
			get_experiences: function(){			
				experiences= $http.get('/blouda/getAllExperiences');			
				return experiences;
			},			

			// Get all industries based on search criteria		 
			search_industry: function(search_criteria){
				console.log(search_criteria);
				var defer=$q.defer();			
				$http.post('/blouda/getSearchIndustry',search_criteria)
				.success(function(data){
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			},

			// Get all location based on search criteria
			search_location: function(search_criteria){
				var defer=$q.defer();					
				$http.post('/blouda/getSearchLocation',search_criteria)
				.success(function(data){
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});
				return defer.promise;
			},

			// Get all role based on search criteria
			search_role: function(search_criteria){
				var defer=$q.defer();					
				$http.post('/blouda/getSearchRole',search_criteria)
				.success(function(data){
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});
				return defer.promise;
			},

			// Get all experience based on search criteria
			search_experience: function(search_criteria){
				var defer=$q.defer();					
				$http.post('/blouda/getSearchExperience',search_criteria)
				.success(function(data){
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});
				return defer.promise;
			},

			// Onclick selected elements from chart
			publicizingdatas: function(values){
				$cookieStore.put('selectedElements',values);
				selectedElements=values;								
			},

			// Return the selected elements
			getpublicizingdatas: function(){
				return selectedElements;
			},	

			// Set the show list
			setShowList : function(){
				$cookieStore.put('selectedElements',selectedElements);
			},

			getShowList : function(){
				return $cookieStore.get('selectedElements');
			},

			//Get List
			getEmployeeProfiles : function(){	

				return $http.post('/blouda/getEmployeeslist',this.getShowList());
			},

			//Get List for Pagination
			getEmployeeProfileswithPagination : function(selectedElementswithPagination){	
				
				var defer=$q.defer();					
				$http.post('/blouda/getEmployeeslistPage',selectedElementswithPagination)
				.success(function(data){
					defer.resolve(data);					
				}).error(function(data){
					defer.reject(data);
				});
				return defer.promise;	

			},

			//Get List for Canvas Chart data
			getEmployeeProfilesforChartData : function(selectedElementswithPagination){	
				
				var defer=$q.defer();					
				$http.post('/blouda/getEmployeesChartData',selectedElementswithPagination)
				.success(function(data){
					console.log("success");
					defer.resolve(data);
				}).error(function(data,status){
					console.log("error");
					defer.reject(status);
				});
				return defer.promise;	

			},

			getImage : function(){
				var defer=$q.defer();					
				$http.get('/blouda/getRChart')
				.success(function(data){
					console.log("success");
					defer.resolve(data);
				}).error(function(data,status){
					console.log("error");
					defer.reject(status);
				});
				return defer.promise;
			},
			getImageinbytes : function(){
				var defer=$q.defer();					
				
				$http({
				    url: "getRChart", 
				    method: "GET",				    
 				}).success(function(data){					
					defer.resolve(data);
				}).error(function(data,status){
					console.log("error");
					defer.reject(status);
				});	
				return defer.promise;
			},
		}
}]);
