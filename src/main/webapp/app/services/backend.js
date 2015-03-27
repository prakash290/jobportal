bcloud.factory('backend',[ '$http','$q','requestNotificationChannel', function($http,$q,requestNotificationChannel){
	
	var data1='';
	var data2='';
	var publicizingdata=[];
	var result={};
	var indus_type=[];
	var role=[];

	return{
		
		pass_attribute: function(val){
			return $http.post('/_ah/api/useranalytics/v1/chartgeneration',val);
		},	

		// Get all industry Types 			
		get_industry_types: function(){
			industries= $http.get('/BloudA/_ah/api/useranalytics/v1/allIndustries');			 
			return industries;
		},

		// Get all Locations 
		get_locations: function(){			
			locations= $http.get('/BloudA/_ah/api/useranalytics/v1/allLocations');			
			return locations;
		},

		// Get all Roles
		get_roles: function(){			
			roles= $http.get('/BloudA/_ah/api/useranalytics/v1/allRoles');			
			return roles;
		},

		// Get all Experiences
		get_experiences: function(){			
			experiences= $http.get('/BloudA/_ah/api/useranalytics/v1/allExperiences');			
			return experiences;
		},
		

		// Get all industries based on search criteria		 
		search_industry: function(search_criteria){
			console.log(search_criteria);
			var defer=$q.defer();			
			$http.post('/BloudA/_ah/api/useranalytics/v1/searchIndustry',search_criteria)
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
			$http.post('/BloudA/_ah/api/useranalytics/v1/searchLocation',search_criteria)
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
			$http.post('/BloudA/_ah/api/useranalytics/v1/searchRole',search_criteria)
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
			$http.post('/BloudA/_ah/api/useranalytics/v1/searchExperience',search_criteria)
			.success(function(data){
				defer.resolve(data);
			}).error(function(data){
				defer.reject(data);
			});
			return defer.promise;
		},


		dynamicquery: function(queryval){			
			data2=$http.post('/_ah/api/useranalytics/v1/dynamicdata',{type : queryval});			
			return data2;
		},		
		dynamicquery1: function(queryval){				
			var defer=$q.defer();					
			$http.post('/_ah/api/useranalytics/v1/dynamicdata',{type : queryval})
			.success(function(data){
				defer.resolve(data);
			}).error(function(data){
				defer.reject(data);
			});
			return defer.promise;
		},

		publicizingdatas: function(values){			
			result=values;
			console.log(result);					
		},	
		getpublicizingdatas: function(){
			return result;
		},	
		getpublicdata: function(){
			var defer=$q.defer();					
			$http.post('/_ah/api/useranalytics/v1/dy',result)
			.success(function(data){
				defer.resolve(data);
			}).error(function(data){
				defer.reject(data);
			});
			return defer.promise;			
		},
							
		totalcount: function(){			
			var defer=$q.defer();					
			$http.get('/_ah/api/useranalytics/v1/count')
			.success(function(data){
				defer.resolve(data);
			}).error(function(data){
				defer.reject(data);
			});
			return defer.promise;
		},
		pagination: function(begin,end){
			var defer=$q.defer();					
			$http.post('/_ah/api/useranalytics/v1/filterpage',{begin:begin,end:end})
			.success(function(data){				
				defer.resolve(data);
			}).error(function(data){
				defer.reject(data);
			});
			return defer.promise;
		},
		getuserprofile: function(){
			var defer=$q.defer();					
			$http.get('/BloudA/_ah/api/useranalytics/v1/userprofile')
			.success(function(data){				
				defer.resolve(data);
			}).error(function(data){
				defer.reject(data);
			});
			return defer.promise;
		}

	}

}]);