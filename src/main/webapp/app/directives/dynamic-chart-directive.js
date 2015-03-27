bcloud.directive("chart123",['$compile',function($compile){
	return{	
		scope:true,
		restrict:'A',
		link:function(scope,element,attrs){
			element.bind("onchange",function(){
				angular.element(document.getElementById('sample'))
				.append("<h2>Hi i am working</h2>");
				/*angular.element(document.getElementById('dd')).remove(); */
				
				alert("ddd");
			});
		},
		template:'<div id="sample">Sample Test</div>',	
		
	}
}]);

bcloud.directive("userprofile",[function(){
	return{
		scope: {
			userprofile: '=',
			data: '='
		},
		restrict : 'E',
		link:function(scope,element,attrs){		
		var chart = new CanvasJS.Chart("chartContainer",
			{
				title:{
					text: "User Name",
				},
				exportEnabled: true,
				axisY: {
					includeZero:false,
					title: "Year vise Details",
					interval: 2,
					valueFormatString: "#"
				}, 
				axisX: {
					interval:2,
					title: "User Profile",					
				},

				data: [
				{
					type: "rangeBar",
					showInLegend: false,
					yValueFormatString: "#",									
					dataPoints:scope.userprofile 
				}
				]
			});
	      chart.render();
		}
	}

}]);

bcloud.directive("userprofile1",[function(){
	return{
		scope: {
			userprofile: '=',
			data: '=',
						
		},
		restrict : 'E',
		link:function(scope,element,attrs){			
		scope.dataAttributeChartID = 'chartid' + Math.floor(Math.random() * 1000000001);
		angular.element(element).attr('id', scope.dataAttributeChartID);            
				
		var chart = new CanvasJS.Chart(scope.dataAttributeChartID,
			{
				title:{
					text: attrs.wholedata,
				},
				exportEnabled: true,
				axisY: {
					includeZero:false,
					title: "Year vise Details",
					interval: 2,
					valueFormatString: "#"
				}, 
				axisX: {
					interval:2,
					title: "User Profile",					
				},

				data: [
				{
					type: "rangeBar",
					showInLegend: false,
					yValueFormatString: "#",									
					dataPoints:scope.data 
				}
				]
			});
	      chart.render();
		},
		template: '<div id="dataAttributeChartID" style="height: 300px;"></div>',
		replace: true
	}

}]);