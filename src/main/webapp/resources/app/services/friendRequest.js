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
			},
			sendFriendRequest:function(friendRequest){
				var defer=$q.defer();			
				$http.post('/blouda/sendFriendRequest',friendRequest)
				.success(function(data){										
					defer.resolve(data);					
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			},
			getRequestedFriendsCount:function(currentEmployeeForFriend){
				var defer=$q.defer();			
				$http.post('/blouda/getRequestedFriendsCount',currentEmployeeForFriend)
				.success(function(data){										
					defer.resolve(data);					
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			}

		}

}]);

