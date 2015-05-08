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
			},			
			getRequestedFriendsList:function(currentEmployeeFriendList){
				var defer=$q.defer();			
				$http.post('/blouda/getRequestedFriendsList',currentEmployeeFriendList)
				.success(function(data){										
					defer.resolve(data);					
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			},
			updateFriendRequest:function(updatefriendslist){
				var defer=$q.defer();			
				$http.post('/blouda/updateFriendRequest',updatefriendslist)
				.success(function(data){										
					defer.resolve(data);					
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			},
			showFriendsList:function(getfriendslist){
				var defer=$q.defer();			
				$http.post('/blouda/showFriendsList',getfriendslist)
				.success(function(data){										
					defer.resolve(data);					
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
			}
		}

}]);

