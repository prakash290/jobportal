bcloud.controller('parent',['$scope','authentication',
  function($scope,authentication){
	
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

  $scope.isUserLoggedIn = function(){
    $scope.loginStatus = authentication.getUser();
   if(angular.isUndefined($scope.loginStatus))
   {
      $scope.loginStatus = false;
   }
    return $scope.loginStatus;
  };
}]);

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
	$scope.name = "sadfsdf";
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


bcloud.controller('newRegisterCtrl',['$scope','$http','employeeServices','authentication','$location',
  function($scope,$http,employeeServices,authentication,$location){
  
  $scope.registerUser = function (){

    var industrytype = checkIndustryType($scope.tags);  
    console.log(industrytype);

    var priority = {
      "subscribeuser" : false,
      "subscriberplan" : "",
      "subscriber_fromdate" : "",
      "subsriber_expireddate" : "",
    }

    $scope.user = {
    "email" : $scope.email,
    "password" : $scope.password,
    "fullname" : $scope.fullname,
    "priority" : priority,
    "currentlocation": $scope.currentlocation,
    "phonenumber" : $scope.phonenumber,
    "industrytype" : industrytype,
    "experience" : parseInt($scope.experience),
    "skillset" : $scope.tags,
    "basiceducation" : $scope.basiceducation,
    "mastereduction" : $scope.mastereduction,
    "doctorateeducation" : $scope.doctorateeducation,
    "othercourse1" : $scope.othercourse1,
    "othercourse2" : $scope.othercourse2,
    "othercourse3" : $scope.othercourse3 

    };    
    $scope.newuser = null;
    employeeServices.insertEmployee($scope.user).then(function(data){
      $scope.newuser = data;
      employeeServices.newUserDetails($scope.newuser);
      console.log($scope.newuser);
      if(!angular.isUndefined($scope.newuser.user._id.timeSecond))
    {
      $location.path('/profile');
    }
    });

  
    
  };

  $scope.reset = function () {    
    $scope.user = '';
  };

  $scope.options = ["Text", "Markdown", "HTML", "PHP", "Python",
                  "Java", "JavaScript", "Ruby", "VHDL",
                  "Verilog", "C#", "C/C++"];
  $scope.tags = [];

}]);


bcloud.controller('profileCtrl',['$scope','employeeServices','$location',
  function($scope, employeeServices,$location){
    $scope.alerts = [
                { type: 'success', msg: '' },    
              ];
    $scope.newAccountactivated = false;
      $scope.closeAlert = function(index) {
    $scope.newAccountactivated = false;
      };

    $scope.userobject = employeeServices.getNewUser();
    console.log( $scope.userobject);    
    $scope.alerts[0].msg = "HI '"+$scope.userobject.user.fullname+"'"+" Your account has been successfully created.";
    $scope.newAccountactivated = true;
   

    $scope.savemyprofile = function(){

        $scope.currentcompanydetails = {
          "current_industry" : $scope.current_industry,
          "role":$scope.role,
          "current_company_name" : $scope.current_company_name,
          "current_designation" : $scope.current_designation,
          "salary_lakhs" : $scope.salary_lakhs,
          "salary_thousands" : $scope.salary_thousands,
          "current_from_month" : $scope.current_from_month,
          "current_from_year" : $scope.current_from_year,
          "current_to_month" : $scope.current_to_month,
          "current_to_year" : $scope.current_to_year,
          "notice_period" : $scope.notice_period,
          "jobdescription" : $scope.jobdescription
       };

       $scope.previouscompanydetails = [$scope.previous1];
       $scope.employeeprofile = {
        "resumetitle" : $scope.resumetitle,
        "currentcompanydetails" : $scope.currentcompanydetails,
        "previouscompanydetails" : $scope.previouscompanydetails,
        "useremail" : $scope.userobject.user.email
        };

        console.log($scope.employeeprofile);

        employeeServices.employeeProfileUpdate($scope.employeeprofile)
        .then(function(data){
          alert("Completed Profile Details");
          $location.path('/home');
        });

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

function checkIndustryType(skillset){
  console.log(skillset);
    var options = ["Text", "Markdown", "HTML", "PHP", "Python",
                  "Java", "JavaScript", "Ruby", "VHDL",
                  "Verilog", "C#", "C/C++"];
    var industrytype = "";              
    for(var i=0; i<skillset.length; i++)
    {              
      if(options.indexOf(skillset[i]) != -1){
          industrytype = "IT";
        }
        else
        {
          industrytype = "other";
        }
    }  
    return industrytype;  
}


