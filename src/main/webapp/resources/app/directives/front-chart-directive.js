bcloud.directive('industry',function(employerServices,$compile,requestNotificationChannel){

	return{
		restrict : 'EA',		
		scope: {
			chartdata: '=',							
		},	
		replace: true,			
		link: function($scope,element,attribute){
			   $scope.cliked=false;
				$scope.dataAttributeChartID = 'chartid' + Math.floor(Math.random() * 1000000001);
			    angular.element(element).attr('id', $scope.dataAttributeChartID);            
				var data=$scope.chartdata;		

				$scope.configuration={
				data: {
				        columns: data,
				        type:attribute.charttype,				       
				        onclick: function(d,i){	
				        publish={
				        	"indus_type" : d.name				        	
				        }						
						if($scope.$parent.query.industry_type.indexOf(d.name) == -1){						  
						  $scope.$parent.query.industry_type.push(d.name);
						}
						$scope.$apply();					
				       
				        employerServices.publicizingdatas($scope.$parent.query);
				       // requestNotificationChannel.dataUpdated();				        			        	      			          			        				        	
				        }
    				}
				};				 
			$scope.configuration.bindto = '#' + $scope.dataAttributeChartID;                   
            
          	chart1=c3.generate($scope.configuration);
          	console.log("Attribute objectid : ");
										
		},

		controller: function($scope,requestNotificationChannel){
			var onDataUpdatedHandler=function(){
				if($scope.$parent.publishEvent.industry_type)
				{

			  	search_criteria=employerServices.getpublicizingdatas();			  	

				console.log(search_criteria);
			  	sorteddata=employerServices.search_industry(search_criteria);

			  	sorteddata.then(function(results){

			  	chart1result=jsontoarray(results);
			  	console.log(chart1result);			  	

			  		if(chart1result != '')
			  		{
			  			for(var h=0;h<$scope.chartdata.length;h++)
							{
								var singlearray=$scope.chartdata[h];
								chart1.unload({
								        ids: singlearray[0]
								    });					
							}

					    chart1.load({
					    	columns:chart1result
					    });
					 }   
			  	});
				
			    }			    
			}
			requestNotificationChannel.onDataUpdated($scope, onDataUpdatedHandler);
		}

	}

});

bcloud.directive('location',function(employerServices,$compile,requestNotificationChannel){

	return{
		restrict : 'EA',		
		scope: {
			chartdata: '=',								
		},	
		replace: true,			
		link: function($scope,element,attribute){
			var data=$scope.chartdata;
			$scope.cliked=false;
			$scope.dataAttributeChartID = 'chartid' + Math.floor(Math.random() * 1000000001);
			 angular.element(element).attr('id', $scope.dataAttributeChartID);            
			$scope.configuration={
				data: {
				        columns: data,
				        type:attribute.charttype,				       
				        onclick: function(d,i){
				       	
				        if($scope.$parent.query.prefered_location.indexOf(d.name) == -1){						  
						  $scope.$parent.query.prefered_location.push(d.name);
						}

						$scope.$apply();				        
				        employerServices.publicizingdatas($scope.$parent.query);
				        //requestNotificationChannel.dataUpdated();				        
				        }
    				}
				};	
			 $scope.configuration.bindto = '#' + $scope.dataAttributeChartID;
           	chart2=c3.generate($scope.configuration);
          									
		},

		controller: function($scope,requestNotificationChannel,employerServices){
			var onDataUpdatedHandler=function(){				
				if($scope.$parent.publishEvent.prefered_location)
				{	
					result=employerServices.getpublicizingdatas();
					newlocation=employerServices.search_location(result);
					newlocation.then(function(results){
					chart2result=jsontoarray(results);
					if(chart2result != '')
					{
						for(var h=0;h<$scope.chartdata.length;h++)
							{
								var singlearray=$scope.chartdata[h];
								chart2.unload({
								        ids: singlearray[0]
								    });					
							}
					    chart2.load({
					    	columns: chart2result
					    });
					    
					}

					});
					
					
				}				
			}
			requestNotificationChannel.onDataUpdated($scope, onDataUpdatedHandler);
		}

	}

});

bcloud.directive('role',function(employerServices,$compile,requestNotificationChannel){

	return{
		restrict : 'EA',		
		scope: {
			chartdata: '=',								
		},	
		replace: true,			
		link: function($scope,element,attribute){
			var data=$scope.chartdata;
			$scope.cliked=false;
			$scope.dataAttributeChartID = 'chartid' + Math.floor(Math.random() * 1000000001);
			 angular.element(element).attr('id', $scope.dataAttributeChartID);            
			$scope.configuration={
				data: {
				        columns: data,
				        type:attribute.charttype,				       
				        onclick: function(d,i){
				       		
				        if($scope.$parent.query.role.indexOf(d.name) == -1){						  
						  $scope.$parent.query.role.push(d.name);
						}

						$scope.$apply();				        
				        employerServices.publicizingdatas($scope.$parent.query);				        
				        //requestNotificationChannel.dataUpdated();				        
				        }
    				}
				};	
			 $scope.configuration.bindto = '#' + $scope.dataAttributeChartID;
           	chart3=c3.generate($scope.configuration);
          									
		},

		controller: function($scope,requestNotificationChannel,employerServices){
			var onDataUpdatedHandler=function(){				
				if($scope.$parent.publishEvent.role)
				{	
					result=employerServices.getpublicizingdatas();
					newlocation=employerServices.search_role(result);
					newlocation.then(function(results){
					chart3result=jsontoarray(results);
					if(chart3result != '')
					{
						for(var h=0;h<$scope.chartdata.length;h++)
							{
								var singlearray=$scope.chartdata[h];
								chart3.unload({
								        ids: singlearray[0]
								    });					
							}
					    chart3.load({
					    	columns: chart3result
					    });
					    
					}

					});
					
					
				}				
			}
			requestNotificationChannel.onDataUpdated($scope, onDataUpdatedHandler);
		}

	}

});

bcloud.directive('experience',function(employerServices,$compile,requestNotificationChannel){

	return{
		restrict : 'EA',		
		scope: {
			chartdata: '=',								
		},	
		replace: false,			
		link: function($scope,element,attribute){
			var data=$scope.chartdata;
			$scope.cliked=false;
			$scope.dataAttributeChartID = 'chartid' + Math.floor(Math.random() * 1000000001);
			 angular.element(element).attr('id', $scope.dataAttributeChartID);            
			$scope.configuration={
				data: {
				        columns: data,
				        type:attribute.charttype,				       
				        onclick: function(d,i){				       	
				        if($scope.$parent.query.experience.indexOf(d.name) == -1){						  
						  $scope.$parent.query.experience.push(d.name);
						}

						$scope.$apply();				        
				        employerServices.publicizingdatas($scope.$parent.query);
				        //requestNotificationChannel.dataUpdated();				        
				        }
    				}
				};	
			 //$scope.configuration.bindto = '#' + $scope.dataAttributeChartID;
           	$scope.configuration.bindto="experience";
           	chart4=c3.generate($scope.configuration);
          									
		},
		controller: function($scope,requestNotificationChannel,employerServices){
			var onDataUpdatedHandler=function(){				
				if($scope.$parent.publishEvent.experience)
				{	
					result=employerServices.getpublicizingdatas();
					newlocation=employerServices.search_experience(result);
					newlocation.then(function(results){
					chart4result=jsontoarray(results);
					if(chart4result != '')
					{
						for(var h=0;h<$scope.chartdata.length;h++)
							{
								var singlearray=$scope.chartdata[h];
								chart4.unload({
								        ids: singlearray[0]
								    });					
							}
					    chart4.load({
					    	columns: chart4result
					    });
					    
					}

					});
					
					
				}				
			}
			requestNotificationChannel.onDataUpdated($scope, onDataUpdatedHandler);
		}

	}

});