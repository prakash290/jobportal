bcloud.factory('friendRequestServices',[ '$http','$q',
	function($http,$q){

		return{

			getFriendsList:function(queryParams){
				var defer=$q.defer();			
				$http.post('/blouda/getFriendsList',queryParams)
				.success(function(data){										
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;			
			}

		}

}]);

