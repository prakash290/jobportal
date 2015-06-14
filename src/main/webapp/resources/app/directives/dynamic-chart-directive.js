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


bcloud.directive('sampledir',function(){
	var searchIcon = angular.element('<span class="glyphicon glyphicon-search" aria-hidden="true" ng-show="Onshowmouseover"></span>');
	//var searchIcon = angular.element("<div>PPP</div>");		
	return{
		restrict:"A",
		compile : function(tE,Tar){

				tE.bind('mouseout',function(){
					console.log("MouseOut");
					searchIcon.remove();
					//input.removeAttr( "ng-click" );
				});
			return function(scope,el,attrs){

				el.bind('mouseover',function(){
					console.log("MouseOvered");
					el.append(searchIcon);					
				});

			}			
		},
		controller : function($scope){
				$scope.sample = function(){
					console.log("clik");
				};
			}
	}
});

bcloud.directive('addCourse',function($compile){
	return{
		restrict:"E",
		template:"<span><button class='btn btn-info' style='margin-top:20px;' >Add Course</button></span>",
		scope:true,
		replace:true,
		link : function(scope,el,attrs){

				el.bind('click',function(){						
					scope.count++;

					if(scope.count > 1)
					{
						var previousElement = angular.element(document.getElementById('currentcount'+(scope.count-1)));
						console.log(previousElement);
					}	

					var x = angular.element(document.getElementById('course'));
					var newq = $compile("<course count ="+scope.count+"></course>")(scope);
					var length = angular.element(".addCourseValue").length;
					console.log(length);
					x.append(newq);
					if(length == 2)
					{
						el.remove();
					}		
										
				});

		},			
		
		controller : function($scope){
				$scope.sample = function(){
					console.log("clik");
				};
			}
	}
});

bcloud.directive('course',function($templateCache){
	
	var label = angular.element('<div>Added</div>');
	return{
		restrict:"E",
		//template:"<div class='addCourseValue' style='margin-bottom:10px;'>added</div>",
		templateUrl:'resources/views/addCourse.html',
		replace:true,
		scope:{
			name :'=',
			
		},
		link : function(scope,element,attr){
			scope.employeecourses=[];
			
			scope.count = attr.count;
			if(scope.count>1)
			{
				var previousElement = angular.element(document.getElementById('count'+(scope.count-1)));
				console.log(previousElement);
				previousElement.remove();
			}

			scope.saveCourse = function(){
				console.log(scope.employeecourses);
			};

			/*scope.namess=["dd","aa"];
			scope.add = function(nam){
				console.log(nam);
				scope.namess.push(nam);
			};*/	
		},		

	}
});


bcloud.directive('updatecourse',function($compile){
	return{
		restrict :'E',
		scope:{
			updateCourse : '&'
		},
		replace:true,
		templateUrl : 'resources/views/employee/addCourseForUpdate.html',
		link:function (scope,element,attribute) {			
			 scope.length = angular.element(".courses").length;
			console.log(scope.length);
			if(length == 1)
			{
				scope.toggleCourse=false;
			}
			$compile (element.attr('ng-model','ddd'+scope.length) ) (scope);
			scope.updateCourse();
		}

	}
});


bcloud.directive('showHide',function($compile){
	return {
		scope:{
			preview :'=preview'
		},
		restrict : 'A',		
		link : function(scope,element,attribute){
			console.log(scope.preview);
			
		}
	
	}
});

bcloud.directive('dynamicTemplate',function($compile,$templateCache){
	return {
		scope:{
			template : "="
		},
		restrict : 'E',		
		link : function(scope,element,attribute){
			alert(scope.template)
			var ele = $templateCache.get(scope.template);
			alert(ele);
		}
	
	}
});

