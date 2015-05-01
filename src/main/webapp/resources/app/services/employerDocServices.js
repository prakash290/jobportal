bcloud.factory('employeeDocServices',['$http','$q',
	function($http,$q){

		return{

			searchWholeDoc:function(search){
				var defer=$q.defer();			
				$http.post('/blouda/searchWholeDocument',search)
				.success(function(data){
					console.log(data);
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
				
			},
			multifieldSearch:function(multikeyword){
				var defer=$q.defer();			
				$http.post('/blouda/multikeywordDocSearch',multikeyword)
				.success(function(data){
					console.log(data);
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;
				
			},
			getResume:function(resumeQuery){
				var defer=$q.defer();			
				$http.post('/blouda/downloadResume1',resumeQuery,{responseType:'arraybuffer'})
				.success(function(data,status,headers){
					 var octetStreamMime = 'application/octet-stream';

                    headers = headers();
                    console.log(headers);
                     var filename = headers['filename'];
                     var contentType = headers['content-type'] || octetStreamMime;
                    	var blob = new Blob([data], { type:contentType });     
      					var downloadLink = angular.element('<a></a>');
                        downloadLink.attr('href',window.URL.createObjectURL(blob));
                        downloadLink.attr('download', filename);
      					downloadLink[0].click();
					defer.resolve(data);
				}).error(function(data){
					defer.reject(data);
				});			
				return defer.promise;				
			}
			
		}
}]);