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

bcloud.directive("passwordVerify", function() {
    return {
        require: "ngModel",
        scope: {
            passwordVerify: '=',            
        },
        link: function(scope, element, attrs, ctrl) {        	
            scope.$watch(function() {
                var combined;
                
                if (scope.passwordVerify || ctrl.$viewValue) {
                   combined = scope.passwordVerify + '_' + ctrl.$viewValue; 
                }                                  
                return combined;
            }, function(value) {            	
                if (value) {
                    ctrl.$parsers.unshift(function(viewValue) {
                        var origin = scope.passwordVerify;
                        if (origin !== viewValue) {
                            ctrl.$setValidity("passwordVerify", false);
                            return undefined;
                        } else {
                            ctrl.$setValidity("passwordVerify", true);
                            return viewValue;
                        }
                    });
                }
            });

            scope.$watch('passwordVerify',function(){            	
            	console.log("Scope is watching");
            },true);
        }
    };
});

bcloud.directive("displayuserinfos",[function(){
	return{
		scope: {			
			resultdata:'=',			
		},
		restrict : 'E',
		link:function(scope,element,attrs){	
			console.log(scope.resultdata);	
		},
		template: '<div style="margin-top:40px;"><p>Name :{{resultdata.name}}</p><p>Role : {{resultdata.role}}</p><p>Experience : {{resultdata.experience}}</p><p>Preffered Location : <span ng-repeat="location in resultdata.prefered_location">{{location}}  </span><p>Skill set : <span ng-repeat="skills in resultdata.skill_set">{{skills}}  </span><div><button id="login-btn" class="btn btn-primary" style="margin-right:3px;">Schedule Interview</button><button id="login-btn" class="btn btn-primary">View Resume</button></div></div>',
		replace: true
	}

}]);

bcloud.directive('fileInput',['$parse',function($parse){
	return{
		restrict:"A",
		link:function(scope,element,attrs){
			element.bind('change',function(){
				$parse(attrs.fileInput)
				.assign(scope,element[0].files)				
				scope.$apply();
				console.log(scope.files);
			});
		}

	}
}]);

bcloud.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            
            element.bind('change', function(){
         var ext = $('#file').val().split(".").pop().toLowerCase();
        if($.inArray(ext, ["doc","pdf",'docx']) == -1) {
        	alert("Only doc pdf docx is allowed");
        	element.val(null);   	         	
        }else{
            scope.$apply(function(){
                    modelSetter(scope, element[0].files[0]);
                    console.log("scope value is : "+element[0].files[0]);
            });
            
        }
                
            });

        }
    };
}]);


bcloud.directive('addASpaceBetween', [function () {
        'use strict';
        return function (scope, element) {
            if(!scope.$last){
                element.after('&nbsp;');
            }
            else
            {
            	console.log("This is last element : "+scope.$last);
            }	
        }
    }
]);


bcloud.directive("hidedetails",[function(){
	return{		
		restrict : 'A',
		link:function(scope,element,attrs){	
			element.bind('click',function(){
				$(this).hide(1000);
			});
		}		
	}

}]);
