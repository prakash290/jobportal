bcloud.factory('mailService',[ '$http','$q', 
	function($http,$q){

		return{
			
			sendreplys:function(employee){
				var defer=$q.defer();			
				$http.post('/blouda/reply',employee)
				.success(function(data){
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			},
		}

}]);		
