bcloud.controller('parent',function($scope){
	
	$scope.query={
		industry_type:[],
		prefered_location:[],
		role:[],
		experience:[]
	};

  $scope.publishEvent={
    industry_type:"",
    prefered_location:"",
    role:"",
    experience:""
  };
  $scope.$watch('query',function(o,v){    
  },true);
});

bcloud.controller('homeCtrl',['$scope','backend','$http',function($scope,backend,$http){
	  $scope.home="I am coming from home ctrl";
	  $scope.filteredTodos = [];
	  $scope.itemsPerPage = 5;
	  $scope.currentPage = 1;


  $scope.makeTodos = function() {
    $scope.todos = [];
    for (var i=1;i<=1000;i++) {
      $scope.todos.push({ text:'todo '+i, done:false});
    }
  };

  $scope.figureOutTodosToDisplay = function() {
    var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
    var end = begin + $scope.itemsPerPage;    
    //backend.pagination(begin,end);
    $scope.filteredTodos = $scope.todos.slice(begin, end);
  };

  $scope.makeTodos(); 
  $scope.figureOutTodosToDisplay();

  $scope.pageChanged = function() {
    $scope.figureOutTodosToDisplay();
  };	
$scope.data=[];
$scope.wholedata=[];


 userprofile=backend.getuserprofile();
 userprofile.then(function(data){
 	
 	for(var i=0;i<data.length;i++)
 	{
 		$scope.wholedata.push(data[i]); 		
 		$scope.data.push(data[i].data);
 	}
 	
 });

}]);

bcloud.controller('registerCtrl',['$scope','$http',function($scope,$http){
	
}]);

bcloud.controller('analyticsCtrl',
	['$scope','industry_types','locations','roles','experiences','backend','requestNotificationChannel',
	function($scope,industry_types,locations,roles,experiences,backend,requestNotificationChannel){	
	 
	   $scope.industries=jsontoarray(industry_types);   
     $scope.locations=jsontoarray(locations);
     $scope.roles=jsontoarray(roles); 
     $scope.experiences=jsontoarray(experiences); 
     $scope.query=$scope.$parent.query;
     $scope.remove=function(index,attributename){        	    
       $scope.$parent.query[attributename].splice(index,1); 
       angular.forEach($scope.$parent.publishEvent, function(value, key) {
            if(key == attributename)
            {
              $scope.$parent.publishEvent[key]=false;
            }
            else
            {
              $scope.$parent.publishEvent[key]=true;
            }             
       });     
        requestNotificationChannel.dataUpdated();	 
     }

     $scope.search_query={
      industry_type :"search_industry()",
      prefered_location:"search_location()",
      role:"search_role()",
      experience:"search_experience()"
     }

  $scope.reDrawChart=function(){
    angular.forEach($scope.query, function(value, key) {
            console.log(key + ': ' + value.length);
            if(value.length > 0)
            {
              console.log($scope.search_query[key]);
              $scope.search_query[key];        
              $scope.$parent.publishEvent[key]=false;
              console.log("Method Passing is : "+key + " : " + value)
             
            } 
            else
            {
               $scope.$parent.publishEvent[key]=true;
            }
      });

      requestNotificationChannel.dataUpdated();
     
  };   
  
}]);

bcloud.controller('hiremeCtrl',['$scope','$http','requestNotificationChannel',function($scope,$http,requestNotificationChannel){
   
   $scope.industype = ['IT', 'Accounts Jobs','BPO Jobs','Mobile Jobs'];
   var query={};   

   $scope.findresume=function(){

   	$scope.skills=emptycheck($scope.skills);
   	$scope.experience=emptycheck($scope.experience);
   	$scope.salarytype=emptycheck($scope.salarytype);
   		var query={
   			indus_type : $scope.industrytype,
   			skillset : $scope.skills,
   			experience : $scope.experience,
   			current_salary : $scope.salarytype
   		}

   		$http.post('/_ah/api/useranalytics/v1/searching',query);
   				
   		console.log(query);
   };

}]);

function emptycheck(value){
	if(value == '')
		return undefined;
	else
		return value;
}

function jsontoarray(jsonobject){
	var arr=[];
			for (var key in jsonobject) {
		        arr.push([ key , jsonobject[key]]);
		    }
	return arr;		    
}


function unloaddata(){
	chart2.unload({
        ids: 'coimbatore'
    });
    chart2.unload({
        ids: 'bangalore'
    });
}


function loaddata(){

}