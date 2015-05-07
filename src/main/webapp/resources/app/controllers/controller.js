bcloud.controller('parentCtrl',['$scope','authentication','employerServices','$location','friendRequestServices',
  function($scope,authentication,employerServices,$location,friendRequestServices){
	
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

  $scope.loginUser = "";

  $scope.isEmployeeLoggedIn = function(){
    $scope.loginStatus = authentication.isEmployeeLoggedIn();
   if(angular.isUndefined($scope.loginStatus))
   {      
      $scope.loginStatus = false;
   }
   else
   {
      $scope.loggedUserEmail = authentication.getEmployee();
   }
    return $scope.loginStatus;
  };

  $scope.logout = function(){
    authentication.logoutEmployee();
    $location.path('/login');
  };

  $scope.showList = function(){

    // To store selected values
    employerServices.setShowList();
    $location.path('/employerSearchResult');

  };
  $scope.count = 0;  
}]);

bcloud.controller('homeCtrl',['$scope','employerServices','getRequestedFriendsCount',
  function($scope,employerServices,getRequestedFriendsCount){
  var data = getRequestedFriendsCount;
  console.log(data);
   $scope.displayImg = false;

   $scope.getImage = function(){
      console.log("Inside Controller");
      $scope.image = null;
      employerServices.getImageinbytes().then(function(data){      
      $scope.image = data.imagepath; 
      console.log($scope.image);     
    }); 
      return $scope.image;
    };


}]);

bcloud.controller('registerCtrl',['$scope','$http',function($scope,$http){
	
}]);


bcloud.controller('analyticsCtrl',['$scope','industry_types','locations','roles','experiences','requestNotificationChannel',
	function($scope,industry_types,locations,roles,experiences,requestNotificationChannel){	
	 
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


bcloud.controller('newRegisterCtrl',['$scope','$http','employeeServices','authentication','$location','$timeout',
  function($scope,$http,employeeServices,authentication,$location,$timeout){
 
  if( angular.isUndefined (authentication.isEmployeeLoggedIn()) ) 
  {
          $scope.emailform = true;
  }
  else
  {
          $scope.emailform = false;
              employeeServices.getUserProfile().then(function(data){

                $scope.fullname = data.fullname;
                $scope.currentlocation = data.currentlocation;
                $scope.industrytype = data.industrytype;
              
          });
  } 

  $scope.registerUser = function (){

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
        "industrytype" : $scope.industrytype,
        "experience" : $scope.experience,
        "skillset" : $scope.tags,
        "basiceducation" : $scope.basiceducation,
        "mastereduction" : $scope.mastereduction,
        "doctorateeducation" : $scope.doctorateeducation,
        "othercourse1" : $scope.othercourse1,
        "othercourse2" : $scope.othercourse2,
        "othercourse3" : $scope.othercourse3,
        "resume":$scope.myFile
    };    

     var file = $scope.myFile;
   
     console.log($scope.user);
    $scope.newuser = null;
    employeeServices.insertEmployee($scope.user).then(function(data){
      $scope.newuser = data;
      employeeServices.newUserDetails($scope.newuser);
      
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

  // This function is for third party user registeration 
  $scope.thirdPartyUser = function(){
  
      var priority = {
      "subscribeuser" : false,
      "subscriberplan" : "",
      "subscriber_fromdate" : "",
      "subsriber_expireddate" : "",
        }

        console.log(authentication.isEmployeeLoggedIn());

      $scope.thirdpartyemployee = {
          "useremail" : authentication.isEmployeeLoggedIn(),          
          "fullname" : $scope.fullname,
          "priority" : priority,
          "currentlocation": $scope.currentlocation,
          "phonenumber" : $scope.phonenumber,
          "industrytype" : $scope.industrytype,
          "experience" : parseInt($scope.experience),
          "skillset" : $scope.tags,
          "basiceducation" : $scope.basiceducation,
          "mastereduction" : $scope.mastereduction,
          "doctorateeducation" : $scope.doctorateeducation,
          "othercourse1" : $scope.othercourse1,
          "othercourse2" : $scope.othercourse2,
          "othercourse3" : $scope.othercourse3,
              };
              console.log($scope.thirdpartyemployee);
          employeeServices.insertThirdPartyEmployee($scope.thirdpartyemployee).then(function(data){
          
              if(!angular.isUndefined(data.email))
              {
                $location.path('/profile');
              }
          });        

  };//End of third Party User functions



}]);


bcloud.controller('profileCtrl',['$scope','employeeServices','$location','$rootScope','authentication',
  function($scope, employeeServices,$location,$rootScope,authentication){
    
     $scope.savemyprofile = function(){

        $scope.currentcompanydetails = {
          "current_industry" : $scope.current_industry,
          "role":$scope.role,
          "current_company_name" : $scope.current_company_name,
          "current_designation" : $scope.current_designation,
          "salary_lakhs" : parseInt($scope.salary_lakhs),
          "salary_thousands" : parseInt($scope.salary_thousands),
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
        "useremail" : authentication.getEmployee()
        };

        console.log($scope.employeeprofile);

        employeeServices.employeeProfileUpdate($scope.employeeprofile)
        .then(function(data){
          alert("Completed Profile Details");
          $location.path('/home');
        });

    };

}]);


bcloud.controller('LoginCtrl',['$scope','$auth','$location','employeeServices','$rootScope','$facebook',
  function($scope, $auth,$location,employeeServices,$rootScope,$facebook) {
    
    $scope.loginAlert = false;
    $scope.alerts = [
    { type: 'danger', msg: 'Sorry! UserName or Password is wrong.' },    
                  ];

    $scope.closeAlert = function(index) {
      $scope.loginAlert = false;
    };
  
    $scope.newAccount = function(){      
      $location.path("/newregister");
    };

    $scope.login = function(){
      var loginStatus = employeeServices.employeeLogin($scope.employee);
      loginStatus.then(function(data){
        if(!data.login)
        {
          $scope.employee = "";
          $scope.loginAlert = true;
        }
        else
        {
          $location.path('/home');
        }
        
      });
    };

    $scope.linkedinMsg = {};
    $scope.showLinkedinLogin = true;
    $scope.showEmailForm = true;
            
    $scope.linkedinProfileDataCallback = function(linkedindata){
          console.log('profileDataCallback',linkedindata);

          $scope.linkedIn = {
              "linkedinemail" : linkedindata.user_email
          }

          // To check if user email is already registerd with us.
          var isUserExists = employeeServices.linkedInLogin($scope.linkedIn)

          isUserExists.then(function(data){
            alert(data.isUserExists);
            if(!data.isUserExists)
            {
                      $scope.linkedInUser = {
                        "email" : linkedindata.user_email,  
                        "linkedinemail" : linkedindata.user_email,              
                        "fullname" : linkedindata.first_name+" "+linkedindata.last_name,                
                        "currentlocation": linkedindata.location,
                        "industrytype" : linkedindata.industry               
                      };   

                       employeeServices.insertEmployee($scope.linkedInUser).then(function(newLinkedInUserdata){
                          $scope.newuser = newLinkedInUserdata;
                          //$rootScope.globalscope = $scope.newuser;
                          
                          //employeeServices.newLinkedInUser($scope.newuser);
                          
                          if(!angular.isUndefined($scope.newuser.user._id.timeSecond))
                        {              
                          $location.path('/newregister');
                        }
                        });
            } // End If Loop

            else
            {
              $location.path('/home');
            }

          }); // End of promises       

    }; // End of linkedInProfileDataFunction



     $scope.$on('event:google-plus-signin-success', function (event, authResult) {
          // User successfully authorized the G+ App!
          console.log(+event);
          console.log(authResult);
          console.log('Signed in!');
          $scope.getUserInfo();

    });
    $scope.$on('event:google-plus-signin-failure', function (event, authResult) {
          // User has not authorized the G+ App!
          console.log('Not signed into Google Plus.');
    });

    // When callback is received, process user info.
    $scope.userInfoCallback = function(userInfo) {
        console.log(userInfo);
        $scope.$apply(function() {
            $scope.processUserInfo(userInfo);
        });
    };
 
    // Request user info.
    $scope.getUserInfo = function() {
        gapi.client.request(
            {
                'path':'/plus/v1/people/me',
                'method':'GET',
                'callback': $scope.userInfoCallback
            }
        );
    };


    $scope.$on('fb.auth.authResponseChange', function() {
      $scope.status = $facebook.isConnected();
      if($scope.status) {
        $facebook.api('/me').then(function(user) {
          console.log(user);
          $scope.getFriends();
          $scope.user = user;
        });
      }
    });

    $scope.loginToggle = function() {
      if($scope.status) {
        $facebook.logout();
      } else {
        $facebook.login();
      }
    };

    $scope.getFriends = function() {
      if(!$scope.status) return;
      $facebook.cachedApi('/me/friends').then(function(friends) {
        console.log(friends);
        $scope.friends = friends.data;
      });
    };


  }]);



bcloud.controller('employerSearchResultCtrl',['$scope','$http','employerServices','getEmployeeProfiles',
  function($scope,$http,employerServices,getEmployeeProfiles){
    $scope.employeeProfiles = '';

    var data = getEmployeeProfiles;

    $scope.totalcount=data.result.length;  
    
     $scope.selectedvalue = employerServices.getShowList();

     $scope.searchAttributes = employerServices.getShowList();

     $scope.iterate = employerServices.getShowList();      
     console.log($scope.iterate);

     $scope.itemsPerPage = 3;
     $scope.currentPage = 1;

     $scope.$watch('itemsPerPage',function(newValue,oldValue){
        if(newValue != oldValue)
        {
           $scope.figureOutTodosToDisplay();
        }
     },true);
    
    $scope.figureOutTodosToDisplay = function() {
      console.log("itemsPerPage is : "+$scope.itemsPerPage);
    var begin = (($scope.currentPage - 1) * $scope.itemsPerPage);
    var end = parseInt(begin) + parseInt($scope.itemsPerPage);
    
    $scope.selectedvalue.begin = begin;
    $scope.selectedvalue.end = (end-begin);  

    $scope.isEmployeeProfileReady= false;

    $scope.displayProfiles = employerServices.getEmployeeProfileswithPagination($scope.selectedvalue);

    $scope.displayProfiles.then(function(data){
      var profiles=employerServices.getEmployeeProfilesforChartData($scope.selectedvalue);
      profiles.then(function(data){
      
          $scope.wholedata=[];
          $scope.resultdata=data.result;

        for(var i=0;i<$scope.resultdata.length;i++)
          {
            $scope.wholedata.push($scope.resultdata[i]);            
          }          
          $scope.isEmployeeProfileReady=true;
      });
      $scope.employeeProfiles = [];     
      $scope.employeeProfiles = data.result;
      console.log(data.result);     
    }).catch(function(data){
     
    });


    $scope.mySortFunction = function(item) {
      if(isNaN(item[$scope.sortExpression]))
        return item[$scope.sortExpression];
      return parseInt(item[$scope.sortExpression]);
    }
    
  };

  $scope.figureOutTodosToDisplay();

  $scope.pageChanged = function() {
    $scope.figureOutTodosToDisplay();
  }; 
  
}]);


bcloud.controller('docSearchCtrl',['$scope','employeeDocServices',
  function($scope,employeeDocServices){

    $scope.wholeDoc = function(){
      console.log($scope.search);
      employeeDocServices.searchWholeDoc($scope.search)
      .then(function(data){
        $scope.noresultOfAdvancedSearch = false;
        $scope.result=data;

        if($scope.result.hasOwnProperty("noresult")){                         
          $scope.noresultOfAdvancedSearch = true;                
        }
        else if ($scope.result.hasOwnProperty("result")){
          $scope.employees = data.result; 
        }  
        else if ($scope.result.hasOwnProperty("error")){
                // Change location to 404 for 
        } 
        else{
            
        }            
        
      });
    };

    $scope.multipleSearch = function(){
       
        employeeDocServices.multifieldSearch($scope.multikeyword)
        .then(function(data){

        $scope.noresultOfmultipleSearch = false;
        $scope.multiresult=data;

        if($scope.multiresult.hasOwnProperty("noresult")){                         
          $scope.noresultOfmultipleSearch = true;                
        }
        else if ($scope.multiresult.hasOwnProperty("result")){
          $scope.employeelist = data.result; 
        }  
        else if ($scope.multiresult.hasOwnProperty("error")){
                // Change location to 404 for 
        } 
        else{
            
        } 
      });
    };

    $scope.getResume = function(email){
      var resumeQuery = {
        "email":email
      };
      employeeDocServices.getResume(resumeQuery)
        .then(function(data){        
          console.log("Successfully Downloaded");
        });
    };

}]);


bcloud.controller('firendSearchCtrl',['$scope','$compile','friendRequestServices','authentication','getCommonThingsforFriendSearch',
  function($scope,$compile,friendRequestServices,authentication,getCommonThingsforFriendSearch){

  $scope.wholecompanies=getCommonThingsforFriendSearch.wholecompanies;
  $scope.wholelocations=getCommonThingsforFriendSearch.wholelocations;
  $scope.wholeskills=getCommonThingsforFriendSearch.wholeskills;

  $scope.searchFriend = function(){
      $scope.request={};
      angular.forEach($scope.friendRequest, function(value, key) {
            angular.forEach(value,function(val,ke){
              if(ke == "name")
              {            
                $scope.request[key]=val;     
              }
            });
      });   
      $scope.request.email=authentication.getEmployee();
      console.log($scope.request);
      friendRequestServices.getFriendsList($scope.request).then(function(data){
          if(!angular.equals({},data))
          {
            $scope.friendsList = data.result;
            $scope.noresultview = false;
            $scope.resultview = true;
          }
          else
          {
            $scope.resultview = false;
            $scope.noresultview = true;
          }
      });      
  };

  $scope.sendRequest = function(emailId,index){
    alert(index);    
    $scope.friendslistQueryParam = {
      "employee":emailId,
      "email":authentication.getEmployee()
    };           
    console.log($scope.friendslistQueryParam);
    friendRequestServices.sendFriendRequest($scope.friendslistQueryParam).then(function(data){
          if(data.status == "OK")
          {

          }
    });
  }


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


