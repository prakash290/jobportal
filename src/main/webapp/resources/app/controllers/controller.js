bcloud.controller('parentCtrl',['$scope','authentication','employerServices','$location','friendRequestServices','$cookieStore','$cookies','employeeServices',
  function($scope,authentication,employerServices,$location,friendRequestServices,$cookieStore,$cookies,employeeServices){



/*var socket = new WebSocket("wss://localhost:8443/blouda/myHandler");
console.log(socket);*/
 var sock = new SockJS('https://localhost:8443/blouda/myHandler');
   sock.onopen = function() {
       //console.log('open');
   };
   sock.onmessage = function(e) {
       //console.log('message', e.data);
   };
   sock.onclose = function() {
       //console.log('close');
   };

 /*var sock2 = new SockJS('/blouda/notify');
 
   var stompClient = Stomp.over(sock2);

    stompClient.connect({}, function(frame) {
      console.log("stomClient is connected");
    });
*/
    /*var socket = new SockJS("/blouda/hello");
 var stompClient = Stomp.over(socket);

 // callback function to be called when stomp client is connected to server (see Note 2)
 var connectCallback = function() {
      alert("connected!");
      stompClient.subscribe('/topic/greetings', function(greeting){
           console.log("connected successfully");
      });
 }; 

 // callback function to be called when stomp client could not connect to server (see Note 3)
 var errorCallback = function(error) {
      // display the error's message header:
      alert(error.headers.message);
 };

 // Connect as guest (Note 4)
 stompClient.connect("guest", "guest", connectCallback, errorCallback);*/

  $scope.query={
		industry_type:[],
		prefered_location:[],
		role:[],
		experience:[]
	};

  $scope.getRequestedFriendCount = function(){
    $scope.currentemployee = {
      "email":authentication.getEmployee()
    };
    friendRequestServices.getRequestedFriendsCount($scope.currentemployee).then(function(data){
      $scope.count = data.count;
    });
  } 

  $scope.publishEvent={
    industry_type:"",
    prefered_location:"",
    role:"",
    experience:""
  };

  $scope.checkEmployeeLoggedIn = function(){  
  console.log("Inside checkEmployeeLoggedIn");  
    $scope.loginStatus = authentication.isEmployeeLoggedIn();
   if(angular.isUndefined($scope.loginStatus))
   {      
      $scope.loginStatus = false;
      $scope.isEmployeeLoggedIn = false;
   }
   else
   {
      $scope.getRequestedFriendCount();
      $scope.loggedUserEmail = authentication.getEmployee();
      console.log("$scope.loggedUserEmail : "+$scope.loggedUserEmail);
      $scope.isEmployeeLoggedIn = true;
   }    
  };
$scope.checkEmployeeLoggedIn();
console.log("$scope.isEmployeeLoggedIn" +$scope.isEmployeeLoggedIn);
  $scope.logout = function(){
    authentication.logoutEmployee();
    $scope.checkEmployeeLoggedIn();
    employeeServices.clearAllCookies();
    $location.path('/login');
  };

  $scope.showList = function(){
    // To store selected values
    employerServices.setShowList();
    $location.path('/employerSearchResult');
  }; 

  $scope.$watch(function() { return $cookies.authentication; }, function(old,newValue) {
        $scope.checkEmployeeLoggedIn();
        if(old != newValue)
        {
          if(angular.isUndefined ($cookies.authentication))
            {
              console.log("Scope is watching ");
              $scope.checkEmployeeLoggedIn();              
            }
        }
        
    });


}]);

bcloud.controller('homeCtrl',['$scope','$http','employerServices','employeeSocialNetworkServices','$window',
  function($scope,$http,employerServices,employeesocialservices,$window){  
  
   $scope.notify = function( message) {
   console.log(message);
  
    $scope.initiator = false;
    };

    $scope.reconnect = function() {
        setTimeout($scope.initSockets, 10000);
    };

    $scope.initSockets = function() {
        var sock2 = new SockJS('/blouda/notify');    
        var stompClient = Stomp.over(sock2);
        console.log(stompClient);
        stompClient.connect({}, function() {
            stompClient.subscribe("/topic/notify", $scope.notify);
        });
        stompClient.onclose = $scope.reconnect;
    };

  $scope.initSockets();

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

    $scope.linkedIn = function(){

      employeesocialservices.getLinkedInLoginUrl().then(function(data){
        console.log(data);     
         $window.open('https://www.google.com', '_blank');
      
      });        
  }
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
  console.log("auth token is : "+authentication.isEmployeeLoggedIn());
  if( !angular.isUndefined (authentication.isEmployeeLoggedIn()) && !angular.isUndefined (authentication.getEmployeeProfile()) ) 
  {
    var profile = authentication.getEmployeeProfile();      
    $scope.fullname = profile.fullname;              
  } 

  $scope.registerUser = function (){

    var priority = {
      "subscribeuser" : false,
      "subscriberplan" : "",
      "subscriber_fromdate" : "",
      "subsriber_expireddate" : "",
    }

    $scope.user = {
        "email" : authentication.isEmployeeLoggedIn(),
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
     console.log(file);
    if(angular.isUndefined(file))
    {
      $scope.user.resume=false;
    }
    else
     {
      $scope.user.resume = file;
     } 
     console.log($scope.user);
    
    employeeServices.insertEmployee($scope.user).then(function(data){      
      $location.path('/profile');
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


bcloud.controller('profileCtrl',['$scope','employeeServices','$location','$rootScope','authentication',  
  function($scope, employeeServices,$location,$rootScope,authentication){
    var loggedInEmployeeProfile = authentication.getEmployeeProfile();

    if(!angular.isUndefined(loggedInEmployeeProfile))
    {
      $scope.headline = loggedInEmployeeProfile.headline;
    }

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
        "headline" : $scope.headline,
        "currentcompanydetails" : $scope.currentcompanydetails,
        "previouscompanydetails" : $scope.previouscompanydetails,
        "email" : authentication.getEmployee()
        };

        console.log($scope.employeeprofile);

        employeeServices.employeeProfileUpdate($scope.employeeprofile)
        .then(function(data){          
          $location.path('/home');
         });

    };

}]);


bcloud.controller('LoginCtrl',['$scope','$auth','$location','employeeServices','$rootScope','$facebook','$http','authentication','friendRequestServices','employeeSocialNetworkServices',
  function($scope, $auth,$location,employeeServices,$rootScope,$facebook,$http,authentication,friendRequestServices,employeesocialservices) {
    
    $scope.loginAlert = false;
    $scope.alerts = [
    { type: 'danger', msg: 'Sorry! UserName or Password is wrong.' },    
                  ];

    $scope.closeAlert = function(index) {
      $scope.loginAlert = false;
    };
  
    $scope.newAccount = function(){      
      $scope.signup = true;
      //$location.path("/newregister");
    };
    employeeServices.getCommonEmployeeProfile();

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
          $scope.$parent.checkEmployeeLoggedIn();
          $scope.getRequestedFriendCount();
          employeeServices.storeEmployeeProfile();
          $location.path('/home');          
        }
        
      });
    };

    $scope.linkedinMsg = {};
    $scope.showLinkedinLogin = true;
    $scope.showEmailForm = true;
    
     console.log("$scope.status is : "+$scope.status); 
  
    $scope.$on("fb.auth.login",function(){
      console.log($facebook.getAuthResponse());  
       $scope.getFBProfile();
       $scope.facebookLoginCheck();    
    });
    $scope.loginToggle = function() {
      $scope.status = $facebook.isConnected();
      if($scope.status) {
          $scope.facebookLoginCheck();    
         //$scope.getFBProfile();      
      } else {
        $facebook.login();       
      }
    };
    $scope.getFBProfile = function(){
      $facebook.api('/me').then(function(user) {         
          user = {
            "email":user.email
          }
          console.log(user); 
          employeeServices.setSocialAPIResult(user);
          $location.path("/employeeAuth");
        }); 
    }
    $scope.facebookLoginCheck = function(){
      employeeServices.generatefbtoken ($facebook.getAuthResponse())
        .then(function(fbdata){
            if(fbdata.newEmployee)
            {
              authentication.loginEmployee(fbdata);
              $location.path("/home");
            }
            else
            {
              
              console.log("success");
              employeeServices.setSocialAPIResult(fbdata);
              $location.path("/employeeAuth");
            }
        }); 
    }
    $scope.getFriends = function() {
      if(!$scope.status) return;
      $facebook.cachedApi('/me/friends').then(function(friends) {
        console.log(friends);
        $scope.friends = friends.data;
      });
    };
    $scope.linkedIn = function(){
      employeesocialservices.getLinkedInLoginUrl().then(function(data){
        console.log(data);
        window.location.replace(data.loginurl);       
      });
    };
    $scope.googleAPI = function(){     
    employeesocialservices.getGoogleLoginUrl().then(function(data){
        console.log(data);
       window.location.replace(data.loginurl);
    });
  };

  }]);


bcloud.controller('employeeSignUpCtrl',['$scope','$location','authentication','employeeServices','employeeSocialNetworkServices',
  function($scope,$location,authentication,employeeServices,employeesocialservices){
    
    $scope.signUpAlert = false;
    $scope.alerts = [
    { type: 'danger' },    
                  ];

    $scope.closeAlert = function(index) {
      $scope.signUpAlert = false;
    };


    $scope.signUp = function(){
      $scope.signupEmployee = {
        "email" : $scope.newemployee.email,
        "password" : $scope.newemployee.password
      };
      var signUpStatus = employeeServices.employeeSignUp($scope.signupEmployee);
      signUpStatus.then(function(data){
        if(angular.isUndefined(data.newEmployee))
        {
          authentication.loginEmployee(data);
          authentication.setnewEmployeeProfile(data);
          $location.path('/employeeRegister');
        }
        else
        {
           $scope.signUpAlert = true; 
           $scope.newemployee = "";
        }
        
      });
    };

    $scope.$on("fb.auth.login",function(){
      console.log($facebook.getAuthResponse());  
       $scope.getFBProfile();
       $scope.facebookSignUpCheck();    
    });
    $scope.loginToggle = function() {
      $scope.status = $facebook.isConnected();
      if($scope.status) {
          $scope.facebookSignUpCheck();    
         //$scope.getFBProfile();      
      } else {
        $facebook.login();       
      }
    };
    $scope.getFBProfile = function(){
      $facebook.api('/me').then(function(user) {         
          user = {
            "email":user.email
          }
          console.log(user); 
          employeeServices.setSocialAPIResult(user);
          $location.path("/employeeAuth");
        }); 
    }
    $scope.facebookSignUpCheck = function(){
      employeeServices.generatefbtoken ($facebook.getAuthResponse())
        .then(function(fbdata){
            if(fbdata.newEmployee)
            {
              authentication.loginEmployee(fbdata);
              $location.path("/home");
            }
            else
            {              
              console.log("success");
              employeeServices.setSocialAPIResult(fbdata);
              $location.path("/employeeAuth");
            }
        }); 
    }
    
    $scope.linkedIn = function(){
      employeesocialservices.getLinkedInLoginUrl().then(function(data){
        console.log(data);
        window.location.replace(data.loginurl);       
      });
    };
    $scope.googleAPI = function(){     
    employeesocialservices.getGoogleLoginUrl().then(function(data){
        console.log(data);
       window.location.replace(data.loginurl);
    });
    };
}]);

bcloud.controller('socialAPIStatusCtrl',['$scope','$location','employeeServices','getAPIStatus',
  function($scope,$location,employeeServices,getAPIStatus){
    console.log(getAPIStatus);
    // Accessing Globaly so we define the result to services.
    employeeServices.setSocialAPIResult(getAPIStatus);
    $location.path("/employeeAuth");
}]);

bcloud.controller('employeeAuthCtrl',['$scope','$location','employeeServices','employeeSocialNetworkServices','authentication',
  function($scope,$location,employeeServices,employeeSocialNetworkServices,authentication){
    $scope.socialEmployee = employeeServices.getSocialAPIResult();
    console.log("Employee Auth Controllers : "+$scope.socialEmployee);
    
    $scope.createSocialAccount = function(){
      console.log($scope.socialEmployee);
      var employeeCredentials = {
        "email":$scope.socialEmployee.email,
        "password":$scope.socialEmployee.password
      };
      employeeSocialNetworkServices.createNewEmployeeusingSocial(employeeCredentials)
      .then(function(successData){
         if(!angular.equals({},successData))
         {
              console.log(successData);
              authentication.loginEmployee(successData);              
              authentication.setEmployeeProfile(successData);
              employeeServices.storeSocialEmployeeProfile(successData);
              $location.path('/employeeUpdateProfile');              
         }
      });
    };

}]);

bcloud.controller('employeeLoginRedirectCtrl',['$scope','$location','authentication','getLoggedInEmpployee',
  function($scope,$location,authentication,getLoggedInEmpployee){
    console.log(getLoggedInEmpployee);
    // Accessing Globaly so we define the result to services.
    authentication.loginEmployee(getLoggedInEmpployee);
    $location.path("/home");
}]);

bcloud.controller('employeeProfileUpdateCtrl',['$scope','$location','authentication','$compile','getEmployeeProfile','$compile','$parse','$timeout','$q','employeeServices','$rootScope','$modal','$templateCache',
  function($scope,$location,authentication,$compile,getEmployeeProfile,$compile,$parse,$timeout,$q,employeeServices,$rootScope,$modal,$templateCache){
    $scope.emlployeeProfileStatus ={};
    $scope.copyofemployeeprofile={};
    $scope.addemlployeeProfileStatus = getEmployeeProfile.profilestatus;
    
    angular.copy(getEmployeeProfile,$scope.copyofemployeeprofile);

    $scope.newProfileStatus = {};
    angular.forEach($scope.addemlployeeProfileStatus, function(values) {          
            angular.forEach(values, function(key,v) {
            $scope.emlployeeProfileStatus[v] = key;
          });
     });    

    $scope.employeePersonalDetails = getEmployeeProfile.personaldetails;
      
    $scope.employeeEducationDetails = {};
      
    $scope.employeeEducationDetails.educationdetails = getEmployeeProfile.educationdetails;

    $scope.employeeProfilesummary = getEmployeeProfile.profilesummary;

    $scope.employeeProfessionalDetails = getEmployeeProfile.professionaldetails;
     
    $scope.employeementtype = getEmployeeProfile.employeementtype;  
      
    $scope.academicprojectdetails = getEmployeeProfile.academicprojectdetails;
    $scope.academicprojectdetailsmainsection =false;
    if($scope.academicprojectdetails)
    {
      $scope.academicprojectdetailsmainsection =true;
    }
    $scope.industrytype = getEmployeeProfile.currentindustrytype;

    $scope.isexistsIndustryType = function(){

     if(angular.isUndefined($scope.industrytype))
       {
        return true;
       } 
      else
      {
        return false;
      }
    };
    if($scope.academicprojectdetails)
    {
      if(!angular.isUndefined(getEmployeeProfile.academicprojectdetails))
      {
        //$scope.employeeProfessionalDetails.fresher.projectdetails;
         
        $scope.employeeProjectdetailsObj = getEmployeeProfile.academicprojectdetails;          

        $timeout(function(){
          $scope.employeeProjectdetails = [];
           var i=0;
           angular.forEach($scope.employeeProjectdetailsObj, function(value, key) {          
            $scope.project = {};
            $scope.project[key] = value;            
            $scope.employeeProjectdetails.push($scope.project);
             $scope["projectPreviewMode"+i]=true;
            i++;
          }); 
           console.log($scope.employeeProjectdetails);

           if($scope.employeeProjectdetails.length < 3)
            {
              $scope.toggleProject = true;
            }
        });
      }
    }
    if ($scope.employeeProfessionalDetails)
    {
      console.log(angular.isUndefined(getEmployeeProfile.professionaldetails.currentcompany));
      $scope.employeeCurrentcompany = getEmployeeProfile.professionaldetails.currentcompany;
       $scope.employeeCompanydetails = []; 
      if(!angular.isUndefined(getEmployeeProfile.professionaldetails.currentcompany))
      {
        console.log($scope.employeeCurrentcompany);
        $scope.employeeCompanydetailsObj = getEmployeeProfile.professionaldetails.previouscompanydetails;        
        $timeout(function(){          
          var i=0;
           angular.forEach($scope.employeeCompanydetailsObj, function(value, key) {          
            $scope.company = {};
            $scope.company[key] = value;   

            $scope.employeeCompanydetails.push($scope.company);
            console.log($scope.employeeCompanydetails);
            $scope["previouscompanyPreviewMode"+i]=true;
            i++;
          }); 

           if($scope.employeeCompanydetails.length < 3)
            {
              $scope.toggleCompany = true;
            }
        });

      }
    }

    $scope.employeeOtherCourses = [];
    $timeout(function(){          
          $scope.employeeOtherCoursesObj = getEmployeeProfile.othercourses;
          var i=0;
           angular.forEach($scope.employeeOtherCoursesObj, function(value, key) {          
            $scope.course = {};
            $scope.course[key] = value;            
            $scope.employeeOtherCourses.push($scope.course);
            $scope["coursePreviewMode"+i]=true;
            i++;
          }); 

          if($scope.employeeOtherCourses.length < 3)
          {
            $scope.toggleCourse = true;
          }
     }); 
   
    if($scope.emlployeeProfileStatus.profileimage)
    {
      //Get Profile Image
      employeeServices.getProfileImage().then(function(data){          
         $scope.employeeprofileimage = data.image;
      });
    }
    $scope.addCourseforUpdate = function(){      
      var courseLength = $scope.employeeOtherCourses.length;         
      var elem = '<form class="form-horizontal newcourses" name="employeeregister"> <div class="form-group"> <label for="Full Name" class="col-sm-3 control-label" style="text-align:left">Course Name</label> <div class="col-sm-4"> <input type="text" class="form-control" ng-model="employeeUpdateCourse.course'+courseLength+'.course" name="fullname" placeholder="Course"> </div> </div> <div class="form-group"> <label for="exp" class="col-sm-3 control-label" style="text-align:left">From</label> <!-- This is for normal User --> <div class="col-sm-4"> <select class="form-control" ng-model="employeeUpdateCourse.course'+courseLength+'.from" name="currentlocation"> <option value="" selected>Choose</option> <option>2002</option> </select> </div> </div> <div class="form-group"> <label for="exp" class="col-sm-3 control-label" style="text-align:left">To</label> <!-- This is for normal User --> <div class="col-sm-4"> <select class="form-control" ng-model="employeeUpdateCourse.course'+courseLength+'.to"> <option value="" selected>Choose</option> <option>2002</option> </select> </div> </div> <button class="btn btn-info" type="button" ng-click="updateCourse()">Save</button> <button class="btn btn-danger" type="button" ng-click="cancelCourse()">Cancel</button> </form>';
      var courseelement = $compile(elem) ($scope);
      console.log(courseelement);
      angular.element(document.getElementById('spaceforUpdateCourse')).append(courseelement);
     $scope.toggleCourse = false;
    }; 
    $scope.updateCourse = function(){     
      console.log($scope.employeeUpdateCourse);
      var length = $scope.employeeOtherCourses.length;
      var baseObjname = "othercourses.course";
      $scope.newprofileObject={};      
      baseObjname = baseObjname+length;
      $scope.newprofileObject[baseObjname] = $scope.employeeUpdateCourse["course"+length];
     $scope.newprofileObject.email = authentication.getEmployee();
     console.log($scope.newprofileObject);
     
      var updateResult = employeeServices.updateEmployeeProfile($scope.newprofileObject);
      updateResult.then(function(data){
        angular.forEach($scope.employeeUpdateCourse, function(value, key) {          
            console.log($scope.employeeOtherCourses.length);
            var length = $scope.employeeOtherCourses.length;
             $scope.newCourse ={};
              var objKey = "course"+length;
              $scope.newCourse[objKey] = value;
              $scope.employeeOtherCourses.push($scope.newCourse)
              $scope["coursePreviewMode"+length]=true;
              if($scope.employeeOtherCourses.length <= 3)
              {
                 $scope.toggleCourse = true;
                 $scope.employeeUpdateCourse = {};
                 angular.element(".newcourses").remove();     
              }
            
          });
    });
  };

    $scope.cancelCourse = function(){
           $scope.toggleCourse = true;
           $scope.employeeUpdateCourse = {};
           angular.element(".newcourses").remove(); 
    };

    // For Projects
    $scope.updatenewProject = function(){

      var projectLength = angular.element(".projects").length;         

      var newprojectelem = '<form class="form-horizontal newproject"> <!-- Project Details --> <div class="form-group"> <label for="inputPassword3" class="col-sm-2 control-label" style="text-align:left">Project Name</label> <div class="col-sm-5"> <input type="text" class="form-control" ng-model="employeeUpdateProject.project'+projectLength+'.name" placeholder="Project Name" name="projectname" > </div> </div> <!-- Project Cagetory --> <div class="form-group"> <label for="exp" class="col-sm-2 control-label" style="text-align:left">Project Category</label> <div class="col-sm-5"> <select class="form-control" ng-model="employeeUpdateProject.project'+projectLength+'.category"> <option>Select</option> <option>IT</option> <option>MECH</option> <option>ACCOUNTS</option> <option>BPO</option> <option>MEDICAL</option> <option>HR</option> </select> </div> </div> <!-- Skills Used --> <div class="form-group"> <label for="skill sets" class="col-sm-2 control-label" style="text-align:left">Skill Sets</label> <div class="col-sm-5"> <tagger options="options" ng-model="employeeUpdateProject.project'+projectLength+'.skillset" placeholder="Give your Skill sets"/> </div> </div> <!--Project Description --> <div class="form-group"> <label for="inputPassword3" class="col-sm-2 control-label" style="text-align:left">Project Description</label> <div class="col-sm-5"> <textarea class="form-control" rows="3"  ng-model="employeeUpdateProject.project'+projectLength+'.description" placeholder="Profile Summary"></textarea> </div> </div> <button class="btn btn-info" type="button" ng-click="updateProject()">Save</button> <button class="btn btn-danger" type="button" ng-click="cancelProject()">Cancel</button> </form>';

      var compilednewprojectelement = $compile(newprojectelem) ($scope);      
      var skillset = 'employeeUpdateProject.project'+projectLength+'.skillset';
      var model = $parse(skillset);
      model.assign($scope, []); 
      
      angular.element(document.getElementById('spaceforUpdateprojects')).append(compilednewprojectelement);
      $scope.toggleProject = false;
    }

    $scope.updateProject = function(){

      var length = $scope.employeeProjectdetails.length;
      var baseObjname = "academicprojectdetails.project";
      $scope.newprofileObject={};      
      baseObjname = baseObjname+length;     
      $scope.newprofileObject[baseObjname] = $scope.employeeUpdateProject["project"+length];
     $scope.newprofileObject.email = authentication.getEmployee();
     
      var updateResult = employeeServices.updateEmployeeProfile($scope.newprofileObject);
      updateResult.then(function(data){

         console.log($scope.employeeProjectdetails.length); 
        angular.forEach($scope.employeeUpdateProject, function(value, key) {          
            $scope.newProject ={};
             var length = $scope.employeeProjectdetails.length;            
              var objKey = "project"+length;
              $scope.newProject[objKey] = value;
              $scope.employeeProjectdetails.push($scope.newProject)
              $scope["projectPreviewMode"+length]=true;
            
          if($scope.employeeProjectdetails.length <= 3)
          {
             $scope.toggleProject = true;
             $scope.employeeUpdateProject = {};
             angular.element(".newproject").remove();     
          }
        });

      });   
    };


    $scope.cancelProject = function(){
       $scope.toggleProject = true;
       $scope.employeeUpdateProject = {};
       angular.element(".newproject").remove();
    }

    $scope.getCount = function(){      
      return angular.element(".companies").length;
    };

    // For Companies
    $scope.updatenewCompany = function(){

      var companyLength = angular.element(".companies").length;         

      var newcompanydom = '<form class="form-horizontal newcompany"> <div class="form-group"> <label for="inputPassword3" class="col-sm-2 control-label" style="text-align:left">Company Name</label> <div class="col-sm-5"> <input type="text" class="form-control" ng-model="employeeUpdateCompany.previouscompany'+companyLength+'.name" placeholder="Company Name"> </div> </div> <!-- Desigantion --> <div class="form-group"> <label for="inputPassword3" class="col-sm-2 control-label" style="text-align:left">Designation</label> <div class="col-sm-5"> <input type="text" class="form-control" ng-model="employeeUpdateCompany.previouscompany'+companyLength+'.role"  placeholder="Designation" name="resumetitle"> </div> </div> <!-- Date Picker for From Date --> <div class="form-group"> <label for="exp" class="col-sm-2 control-label" style="text-align:left">From Date</label> <div class="col-sm-3"> <select class="form-control" ng-model="employeeUpdateCompany.previouscompany'+companyLength+'.frommonth" > <option>Month</option> <option>1</option> <option>2</option> <option>3</option> </select> </div> <div class="col-sm-3"> <select class="form-control" ng-model="employeeUpdateCompany.previouscompany'+companyLength+'.fromyear" > <option>Year</option> <option>2001</option> <option>2002</option> <option>2003</option> </select> </div> </div> <!-- Date Picker for To Date --> <div class="form-group"> <label for="exp" class="col-sm-2 control-label" style="text-align:left">To Date</label> <div class="col-sm-3"> <select class="form-control"ng-model="employeeUpdateCompany.previouscompany'+companyLength+'.tomonth"> <option>Month</option> <option>1</option> <option>2</option> <option>3</option> </select> </div> <div class="col-sm-3"> <select class="form-control" ng-model="employeeUpdateCompany.previouscompany'+companyLength+'.toyear"> <option>Year</option> <option>2001</option> <option>2002</option> <option>2003</option> </select> </div> </div> <!-- Job Profile --> <div class="form-group"> <label for="exp" class="col-sm-2 control-label" style="text-align:left">Job Profile</label> <div class="col-sm-5"> <textarea class="form-control" rows="3" ng-model="employeeUpdateCompany.previouscompany'+companyLength+'.description"></textarea> </div> </div> <button class="btn btn-info" type="button" ng-click="updateCompany()">Save</button> <button class="btn btn-danger" type="button" ng-click="cancelCompany()">Cancel</button> </form>';

      var compilednewcompanyelement = $compile(newcompanydom) ($scope);      
      
      angular.element(document.getElementById('spaceforUpdatecompany')).append(compilednewcompanyelement);
      $scope.toggleCompany = false;
    }

    $scope.updateCompany = function(){
      console.log($scope.employeeUpdateCompany);
      var length = $scope.employeeCompanydetails.length;
      var baseObjname = "professionaldetails.previouscompanydetails.previouscompany";
      $scope.newprofileObject={};      
      baseObjname = baseObjname+length;
      $scope.newprofileObject[baseObjname] = $scope.employeeUpdateCompany["previouscompany"+length];
     $scope.newprofileObject.email = authentication.getEmployee();
     
      var updateResult = employeeServices.updateEmployeeProfile($scope.newprofileObject);
      updateResult.then(function(data){
        angular.forEach($scope.employeeUpdateCompany, function(value, key) {          
            console.log($scope.employeeCompanydetails.length);
            var length = $scope.employeeCompanydetails.length;
             $scope.newCompany ={};
              var objKey = "previouscompany"+length;
              $scope.newCompany[objKey] = value;
              $scope.employeeCompanydetails.push($scope.newCompany)
              $scope["previouscompanyPreviewMode"+length]=true;
              if($scope.employeeCompanydetails.length <= 3)
              {
                 $scope.toggleCompany = true;
                 $scope.employeeUpdateCompany = {};
                 angular.element(".newcompany").remove();     
              }              
              if("previouscompanydetails" in $scope.copyofemployeeprofile.professionaldetails)            
              {
                  var obj = angular.copy(value);
                  $scope.copyofemployeeprofile.professionaldetails.previouscompanydetails[objKey] = obj;
              }
              else{
                $scope.copyofemployeeprofile.professionaldetails["previouscompanydetails"]={};                  
                var obj = angular.copy(value);
                $scope.copyofemployeeprofile.professionaldetails.previouscompanydetails[objKey] = obj;              
              }
          });
      }); 

      
    }

    $scope.cancelCompany = function(){
       $scope.toggleCompany = true;
       $scope.employeeUpdateCompany = {};
       angular.element(".newcompany").remove();
    }
    $scope.showEditButton = function(Section){ 
      $scope[Section] = true;
    }
    $scope.hideEditButton = function(Section){
      $scope[Section] = false;
    }

  // This is for previous company only
    $scope.showPCEditButton = function(Section){ 
      var pcmodel = Section+this.val;
     $scope[pcmodel] = true; 
    }

    $scope.hidePCEditButton = function(Section){ 
    var pcmodel = Section+this.val;
     $scope[pcmodel] = false;
    }

    // This is for coures only  
    //showArrayEditButton  
    $scope.showcourseEditButton = function(Section){
    var pcmodel = Section+this.val;
     $scope[pcmodel] = true; 
    }

    $scope.hidecourseEditButton = function(Section){ 
    var pcmodel = Section+this.val;
     $scope[pcmodel] = false;
    }
    // First Param is Toggle The Preview Mode and Edit Mode
    // Second Param is Passing an Object for angularjs watch
    // Third Param is Scope Object name for Assign to $watch dynamicallay
    // Fourth Param is Update Section

    $scope.togglePreviewMode = function(Section,obj,Objname,UpdateButtonToggle){
       
      $scope[Section] = false;      

      $scope.watch(obj,Objname,UpdateButtonToggle);
      $scope[UpdateButtonToggle] = false;
    }
    $scope.togglePCPreviewMode = function(Section,obj,Objname,UpdateButtonToggle){
       
      $scope[Section+this.val] = false; 
      var UpdateButtonToggle = "isEligibleemployeepreviouscompany"+this.val;     
      console.log(Section+this.val + " , "+$scope[Section+this.val]);
      //$scope.watch($scope.employeeCompanydetails,'employeeCompanydetails','isEligibleemployeeProfile');
      $scope.watch($scope.employeeCompanydetails,'employeeCompanydetails',UpdateButtonToggle);

      $scope[UpdateButtonToggle] = false;
    }

    $scope.togglePreviewModeForArrays = function(Section,obj,Arrayname,objname,UpdateButtonToggle){
       
      $scope[Section+this.val] = false; 
      var UpdateButtonToggle = UpdateButtonToggle+this.val;  
      console.log(UpdateButtonToggle);
      ArrayObjname = Arrayname+"["+this.val+"] . "+objname+this.val;   
      $scope.watch(obj,ArrayObjname,UpdateButtonToggle);
      $scope[UpdateButtonToggle] = false;
    }
    $scope.isEligibleUpdate = function(UpdateButton){
      $scope[UpdateButton] = true;      
    }
    
    // First Param is switch to preview mode
    // Second Param is preview mode scope object name
    // Third Param is copyofemployeeprofile scope object name which is used to reset the old value.

    $scope.cancelEmpProfile = function(Section,CancelObjectName,CopyObjectName){      
      $scope[Section] = true;
     
     if(CopyObjectName.indexOf(".") != -1) {
         var split = CopyObjectName.split('.');
         var splitlength = split.length;
         console.log(splitlength);
         for(var i=0;i<splitlength;i++)
         {
            console.log(split[i]);
            if(i==0)
            {
              data= $scope.copyofemployeeprofile[split[i]];       
            }  
            else
            {
              data = data[split[i]];
            }
            if( (i+1) == splitlength)
            {
              angular.copy(data,$scope[CancelObjectName]);  
            }
         }      
      }
      else
      {
        angular.copy($scope.copyofemployeeprofile[CancelObjectName],$scope[CancelObjectName]);  
      }
    };

    $scope.cancelEmpProfileforEducation = function(Section,CancelObjectName,CopyObjectName){      
      $scope[Section] = true;
      console.log($scope.copyofemployeeprofile.educationdetails);
      var source = $scope.copyofemployeeprofile.educationdetails[CancelObjectName];
      var desitination = $scope.employeeEducationDetails.educationdetails[CancelObjectName];
      console.log(source);
      console.log(desitination);      
      angular.copy(source,desitination);        
    };
    $scope.cancelEmpProfileofpreviouscompany = function(Section,ObjectDetail,ArrayofObjectName,ArrayName){
      
      $scope[Section+this.val] = true; 
      $scope.updateObject = {};
      $scope.previouscompanyDetails= $scope.copyofemployeeprofile.professionaldetails.previouscompanydetails;                      
      $scope.updateObject[ArrayofObjectName+this.val] = $scope.previouscompanyDetails[ArrayofObjectName+this.val];
      angular.copy($scope.updateObject,$scope[ArrayName] [this.val]);      
     
    };
    
    $scope.cancelEmpProfileforArrays = function(Section,ObjectDetail,ArrayofObjectName,ArrayName){
      $scope[Section+this.val] = true; 
      $scope.updateObject = {};
      $scope.previouscompanyDetails= $scope.copyofemployeeprofile.professionaldetails.previouscompanydetails;                
      $scope.updateObject[ArrayofObjectName+this.val] = $scope.previouscompanyDetails[ArrayofObjectName+this.val];
      $scope[ArrayofObjectName] = ObjectDetail;
      console.log($scope.updateObject);
      $scope[ArrayName] [this.val] = $scope.updateObject;
      console.log($scope[ArrayName] [this.val]);
    };

    $scope.updateEmployeeProfile= function(updateSection,updateObject,Mode){
      
      $scope.profileObject = {};
      $scope.profileObject = updateObject; 
      console.log($scope.profileObject);
      $scope.profileObject.email = authentication.getEmployee();
      var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){
        console.log(data);
        $scope[Mode] = true;
      });
      
    };

    $scope.updateEmployeeProfileForEducation= function(updateSection,updateObject,Mode,educationType){
      
       if("educationdetails" in $scope.copyofemployeeprofile) 
       {
          if(educationType in $scope.copyofemployeeprofile.educationdetails)
          { 
            var desitination ={}
            desitination[educationType] = $scope.copyofemployeeprofile.educationdetails[educationType];
            $scope.profileObject = {};
            $scope.profileObject = updateObject;           
            $scope.profileObject.email = authentication.getEmployee();
            $scope.profileObject.olddata = desitination; 
          }
          else
          {
            $scope.profileObject = {};
            $scope.profileObject = updateObject;           
            $scope.profileObject.email = authentication.getEmployee();
            
          }
                       
       }
       else
       {
          $scope.copyofemployeeprofile.educationdetails={};
          $scope.profileObject = {};
          $scope.profileObject = updateObject; 
          console.log($scope.profileObject);
          $scope.profileObject.email = authentication.getEmployee();
       }
       
     var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){
        console.log(data);
        $scope[Mode] = true;
        if(angular.isUndefined($scope.copyofemployeeprofile.educationdetails[educationType]) )
        {
          $scope.copyofemployeeprofile.educationdetails[educationType] = "";
        }
        console.log($scope.copyofemployeeprofile.educationdetails[educationType]);
        var obj = angular.copy(updateObject.educationdetails[educationType]);
        console.log(obj);
        $scope.copyofemployeeprofile.educationdetails[educationType] = obj;
      });
      
    };

    $scope.updateEmployeeProfileForCurrentCompany = function(updateSection,updateObject,Mode){
      //professionaldetails
      //currentcompany
      $scope.profileObject = {};
      $scope.currentcompany = {};
      $scope.currentcompany.currentcompany = updateObject;     
      $scope.profileObject.professionaldetails = $scope.currentcompany; 

      if( !(updateObject.name ==  $scope.copyofemployeeprofile.professionaldetails.currentcompany.name ))
      {
        console.log( $scope.copyofemployeeprofile.professionaldetails.currentcompany);
        var currentcompany = {};
        currentcompany.currentcompany = $scope.copyofemployeeprofile.professionaldetails.currentcompany;
        $scope.profileObject.olddata = currentcompany;
      }
      $scope.profileObject.email = authentication.getEmployee();
      console.log($scope.profileObject);

      var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){
        console.log(data);
        $scope[Mode] = true;
        if( !(updateObject.name ==  $scope.copyofemployeeprofile.professionaldetails.currentcompany.name ))
        {
          console.log($scope.copyofemployeeprofile.professionaldetails.currentcompany);        
          angular.copy(updateObject,$scope.copyofemployeeprofile.professionaldetails.currentcompany);
          console.log($scope.copyofemployeeprofile.professionaldetails.currentcompany);
        }

      });
    };
    
    $scope.updateEmployeePCProfile = function(updateSection,updateObject,mode,index){
      var companyLength = $scope.employeeCompanydetails.length;
      var previousObjkey = updateSection + this.val;
      var mode = mode + this.val;     
      $scope.updatedcompany={};
      $scope.updatedcompany[previousObjkey] = updateObject
      $scope[mode] = true;
      // $scope.employeeCompanydetails.splice(index, 1);
        //$scope.employeeCompanydetails.push( $scope.updatedcompany);
      $scope.employeeCompanydetails[index] = $scope.updatedcompany;
      
      
    }

    $scope.updateEmployeeProfileforCourse = function(updateSection,updateObject,mode,index,baseObjname){
      var courseLength =  $scope.employeeOtherCourses.length;
      var previousObjkey = updateSection + this.val;
      var mode = mode + this.val;
      delete updateObject.index;
      $scope.profileObject = {};
      $scope.profileObject[baseObjname + this.val] =updateObject; 
      $scope.profileObject.email = authentication.getEmployee();
      var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){
        console.log(data);        
        $scope.updatedcourse={};
        $scope.updatedcourse[previousObjkey] = updateObject
        $scope[mode] = true;
         // $scope.employeeCompanydetails.splice(index, 1);
        //$scope.employeeCompanydetails.push( $scope.updatedcompany);
        $scope.employeeOtherCourses[index] = $scope.updatedcourse;
      }); 
      

    }

    $scope.updateEmployeeProfileforProject = function(updateSection,updateObject,mode,index,baseObjname){
      var previousObjkey = updateSection + this.val;
      var mode = mode + this.val;
      
      $scope.profileObject = {};
      $scope.projectobject ={} ;
      $scope.projectobject["project"+this.val]=updateObject;
      $scope.profileObject.academicprojectdetails=$scope.projectobject;
      $scope.profileObject.email = authentication.getEmployee();
      
      console.log($scope.profileObject);
      var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){
        console.log(data);        
        $scope.updatedcompany={};
        $scope.updatedcompany[previousObjkey] = updateObject
        $scope[mode] = true;
         // $scope.employeeCompanydetails.splice(index, 1);
        //$scope.employeeCompanydetails.push( $scope.updatedcompany);
        $scope.employeeProjectdetails[index] = $scope.updatedcompany;
      }); 
      

    }

    $scope.updateEmployeeProfileofpreviouscompany = function(updateSection,updateObject,mode,index,baseObjname){
      var companyLength = angular.element(".companies").length;
      var previousObjkey = updateSection + this.val;
      $scope.previouscompanyDetails= $scope.copyofemployeeprofile.professionaldetails.previouscompanydetails;
      var olddata = {};
      var baseObjname = "professionaldetails.previouscompanydetails.previouscompany";      
      olddata[baseObjname+this.val] =  $scope.previouscompanyDetails["previouscompany"+this.val];
      console.log(olddata);
      var mode = mode + this.val;
      $scope.profileObject = {};
      $scope.profileObject[baseObjname + this.val] =updateObject; 
      $scope.profileObject.email = authentication.getEmployee();
      $scope.profileObject.olddata = olddata;
      var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){
        console.log(data);        
        $scope.updatedcompany={};
        $scope.updatedcompany[previousObjkey] = updateObject
        $scope[mode] = true;
        $scope.employeeCompanydetails[index] = $scope.updatedcompany;
        console.log(updateObject);
        console.log($scope.copyofemployeeprofile.professionaldetails.previouscompanydetails["previouscompany"+this.val]);
        $scope.previouscompanyDetails= $scope.copyofemployeeprofile.professionaldetails.previouscompanydetails;                      
        angular.copy(updateObject, $scope.copyofemployeeprofile.professionaldetails.previouscompanydetails["previouscompany"+index])
      }); 
      

    }

    $scope.updateEmployeeProfileforArray = function(updateSection,updateObject,mode,index,baseObjname){
      var companyLength = angular.element(".companies").length;
      var previousObjkey = updateSection + this.val;
      var mode = mode + this.val;

      $scope.profileObject = {};
      $scope.profileObject[baseObjname + this.val] =updateObject; 
      $scope.profileObject.email = authentication.getEmployee();
      var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){
        console.log(data);        
        $scope.updatedcompany={};
        $scope.updatedcompany[previousObjkey] = updateObject
        $scope[mode] = true;
         // $scope.employeeCompanydetails.splice(index, 1);
        //$scope.employeeCompanydetails.push( $scope.updatedcompany);
        $scope.employeeCompanydetails[index] = $scope.updatedcompany;
      }); 
      

    }

   $scope.value = 1;

  $scope.incrementValue = function(increment) {
    console.log($scope.value);
    $scope.value += increment;
  };

    $scope.watch = function(obj,Objname,UpdateButtonToggle){      
      var original =angular.copy(obj);
       $scope.watchs = [];
    $scope.watchs.push($scope.$watch(Objname,function(v){  
         if(angular.equals(original,v))
          {
            
            $scope[UpdateButtonToggle] = true;
           
          }         
          else
          {
            $scope[UpdateButtonToggle] = false;            
          }
      },true));
     

    }; // End of watch function


     // Image Crop Functions Start

      var ProfileImage = angular.element('#profile_imageforupdate');
      $scope.openFile = function(pimindex){
        profileimageindex = pimindex; 
        var inputtag = angular.element("<input type='file' id='fileInput'>");
        inputtag.on('change',handleFileSelect);
        inputtag[0].click();
      };

        $scope.myImage='';
        $scope.myCroppedImage='';
        $scope.profile_imageCaption = true;
        var handleFileSelect=function(evt) {

          var file=evt.currentTarget.files[0];
          var reader = new FileReader();
          reader.onload = function (evt) {
            $scope.$apply(function($scope){
              $scope.myImage=evt.target.result;
              $rootScope.image = $scope.myImage;
              $scope.open();
            });
          };
          reader.readAsDataURL(file);
        };
        
        var employeeimage = new Blob();
        $scope.animationsEnabled = true;

        $scope.open = function (size) {

          var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'myModalContent.html',
            controller: 'ModalInstanceCtrl',
            size: 'lg',
            resolve: {
              image: function () {
                return $rootScope.image;
              }
            }        
          });

          modalInstance.result.then(function (selectedImage) {
            var img = {
              "image":selectedImage
            } 
             employeeimage = dataURItoBlob(selectedImage);
             if(employeeimage.size != 0)
            {    
              $scope.employeeProfile = {};    
              $scope.employeeProfile.profileimage = employeeimage;
              $scope.employeeProfile.email = authentication.getEmployee();
              var profileImageResult = employeeServices.updateEmployeeProfileImage($scope.employeeProfile);
              profileImageResult.then(function(data){
                console.log(data);
                $scope.base = selectedImage; 
                $scope.employeeprofileimage = selectedImage; 
                ProfileImage = angular.element(document.getElementById("dynamicProfileImage"))
                ProfileImage.removeAttr('ng-src'); 
                console.log(ProfileImage);                   
                $compile(ProfileImage.attr('ng-src',selectedImage)) ($scope);
                $scope.profile_imageCaption=false;
                $scope.dynamicprofileimage = true;
                angular.element(".newprofileimage").remove(); 
                $scope.addemlployeeProfileStatus[profileimageindex]["profileimage"] = true;
    

              });
            } 
            

          }, function () {
            console.log('Modal dismissed at: ' + new Date());
          });
        };

    // End of image crop 


    // Iniatize Template Object for two binding

    // Create a Template Name and Object Name as same Name

   $scope.opentemplate = function(templatename,index){
      $scope[templatename] = {};   
      $scope[templatename].index=index;    
      console.log($scope[templatename]);    
      if(templatename == "academicprojectdetails")
      {
        var projectskillset = "academicprojectdetails.skillset";
        model = $parse(projectskillset);
        model.assign($scope,[]);
      }
      var element = angular.element(document.getElementById("spacefornewprofilecontents"));

      //var getTemplate = $templateCache.get(templatename+".html");
      var classname = "."+templatename;
      var templatename = templatename+".html";     
      console.log(templatename);

      if ($templateCache.get(templatename)) {
        if(!$scope.templateView)
        {
          $scope.templateView = true;
        }
         $scope.template = templatename; 
      }
    };
    $scope.check = function(){
      alert("Checked");
    };
    


  $scope.skills = [
          {id:1, name:'Markdown'},
          {id:2, name:'HTML'},
          {id:3, name:'PHP'},
          {id:4, name:'JavaScript'}
        ];
      
    $scope.skillset = [
          'Markdown',
          'HTML',
          'PHP',
          'JavaScript'
        ];

    $scope.employeeSkill = []; 
    $scope.isemployeeSkillReady = false;
    $scope.keyskillAdd = true;
  
    $scope.addEmployeeSkill = function(ObjectName,ObjectDetails){
      console.log(ObjectDetails);
      var index = $scope.skillset.indexOf(ObjectDetails.employeeskillname);
      $scope.employeeSkill.push(ObjectDetails.employeeskillname);
      $scope.isemployeeSkillReady = true;
      $scope.skillset.splice(index,1);
      $scope.keyskillAdd = false;
      $scope[ObjectName] = {};
      console.log($scope.employeeSkill);   
      if($scope.employeeSkill.length>3)
      {
        angular.element(".addSkills").remove();
      }
      /*var skill = {
        "name" : $scope.employeeskillname,
        "type" : $scope.skilltype
      };*/
     // $scope.employeeSkill.push(skill);
      
    };

   // For Profile Array 
    $scope.addEmployeeProfileArray = function(baseObjname,ObjectDetail,ArrayName,mode,MainProfileModeName,ClassNameforClose,ChildObjectName){
      var mainindex = ObjectDetail.index;
      console.log(mainindex);
      delete ObjectDetail.index;
      var length =  0;
      var previousObjkey = baseObjname + length;
      var mode = mode+length;
      $scope.profileObject = {};
      $scope.profileObject[baseObjname + length] =ObjectDetail; 
      $scope.profileObject.email = authentication.getEmployee();
      var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){                
        $scope.childObject={};        
        previousObjkey = ChildObjectName + length;
        $scope.childObject[previousObjkey] = ObjectDetail;
        $scope[mode] = true;                
        
        $scope[ArrayName][0]=$scope.childObject;
        console.log($scope[ArrayName].length);
        $scope.emlployeeProfileStatus[MainProfileModeName] = true; 
        $scope.addemlployeeProfileStatus[mainindex][MainProfileModeName] = true;    
        angular.element("."+ClassNameforClose).remove(); 

      }); 
    };


    //For Profile Objects  
    //updateSection,updateObject,MainProfileModeName,MainProfileObjectName,MainProfilePreviewMode,ClassNameforClose
    $scope.addEmployeeProfileObject = function(updateSection,updateObject,MainProfileModeName,MainProfileObjectName,MainProfilePreviewMode,ClassNameforClose){
        var mainindex = updateObject.index;
        delete updateObject.index;
        console.log(updateSection);
        console.log(updateObject);
        $scope.profileObject = {};
        $scope.profileObject[updateSection] =updateObject; 
        $scope.profileObject.email = authentication.getEmployee();
        var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
        updateResult.then(function(data){
          $scope.copyofemployeeprofile[updateSection] = updateObject;  
          console.log($scope.copyofemployeeprofile[updateSection]);          
          $scope[MainProfileObjectName]=updateObject; 
          $scope[MainProfilePreviewMode] = true;  
          $scope.addemlployeeProfileStatus[mainindex][MainProfileModeName] = true;
          $scope.emlployeeProfileStatus[MainProfileModeName] = true;
          angular.element("."+ClassNameforClose).remove();    
        });
    };

      $scope.addEmployeeProfileProject = function(updateSection,updateObject,mode,index,baseObjname){
      var mainindex = updateObject.index;
      delete updateObject.index;
      var previousObjkey = updateSection + '0';
      var mode = mode + this.val;
      $scope.profileObject = {};
      $scope.projectobject ={} ;
      $scope.projectobject.project0=updateObject;
      if(angular.isUndefined($scope.employeementtype))
      {
        $scope.profileObject.employeementtype="fresher";
      }
      $scope.profileObject.academicprojectdetails=$scope.projectobject;
      $scope.profileObject.email = authentication.getEmployee();
      var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){             
        $scope.updatedcompany={};
        $scope.updatedcompany[previousObjkey] = updateObject
        $scope[mode] = true;
        $scope.projectPreviewMode0=true;
        angular.element(".newacademicprojectdetails").remove();
        $scope.toggleProject = true;
        $scope.employeeProjectdetails = [];
        $scope.emlployeeProfileStatus.academicprojectdetails = true;
        $scope.employeeProjectdetails.push($scope.updatedcompany);
        $scope.academicprojectdetailsmainsection = true;
        console.log($scope.addemlployeeProfileStatus[mainindex]);
        $scope.addemlployeeProfileStatus[mainindex]["academicprojectdetails"] = true;
        
      }); 
      

    }
    $scope.addEmployeeProfessional = function(updateSection,updateObject,MainProfileModeName,MainProfileObjectName,MainProfilePreviewMode,ClassNameforClose){     
      
      var mainindex = updateObject.index;
      delete updateObject.index;         
      $scope.profileObject = {};
      $scope.currentcompany = {};
      $scope.currentcompany["currentcompany"] = updateObject;
      $scope.profileObject["professionaldetails"] =$scope.currentcompany;
      $scope.profileObject.email = authentication.getEmployee();
      $scope.profileObject.employeementtype = "experiencer";
      $scope.employeementtype = "experiencer";
      console.log($scope.profileObject);
      var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){
         $scope.employeeProfessionalDetails = true;
        $scope.employeeCurrentcompany = updateObject;   
        $scope.toggleCompany = true;    
        $scope.employeeCompanydetails=[]; 
        var currentcompanyObj = {};
        angular.copy($scope.currentcompany,currentcompanyObj);
        console.log(currentcompanyObj);
        $scope.copyofemployeeprofile["professionaldetails"] = currentcompanyObj;
        console.log($scope.copyofemployeeprofile);
        $scope.emlployeeProfileStatus["professionaldetails"] = true;
        $scope.addemlployeeProfileStatus[mainindex]["professionaldetails"] = true;      
        angular.element(".newprofessionaldetails").remove();
        $location.path("/employeeUpdateProfile");
      });
    };
    // For Add Personal Details
    $scope.addEmployeePersonalDetails = function(updateSection,updateObject,MainProfileModeName,MainProfileObjectName,ClassNameforClose){

      $scope.profileObject = {};
      var index = updateObject.index;      
      delete updateObject.index;

      $scope.profileObject[updateSection] =updateObject; 
      $scope.profileObject.email = authentication.getEmployee();
      var updateResult = employeeServices.updateEmployeeProfile($scope.profileObject);
      updateResult.then(function(data){  
        $scope[MainProfileObjectName] = updateObject;
        console.log($scope.copyofemployeeprofile);        
        $scope.name="prakash";
        $scope.copyofemployeeprofile["personaldetails"] = updateObject;
        $scope.emlployeeProfileStatus[MainProfileModeName] = true;
        $scope.addemlployeeProfileStatus[index][MainProfileModeName] = true;
        angular.element("."+ClassNameforClose).remove();    

      }); 

    };

    $scope.addEmployeeProfileResume = function(updateSection,updateObject,MainProfileModeName,ClassNameforClose){
        
        var mainindex = updateObject.index;
        delete updateObject.index;        

        $scope.employeeResume = updateObject;
        if(angular.isUndefined($scope.employeeResume.currentindustrytype))

        {
          $scope.employeeResume.currentindustrytype = $scope.industrytype;
        }

        var updateResult = employeeServices.updateEmployeeProfileResume($scope.employeeResume);
        updateResult.then(function(data){  
         $scope.emlployeeProfileStatus[MainProfileModeName] = true; 
        $scope.addemlployeeProfileStatus[mainindex][MainProfileModeName] = true;               
          angular.element("."+ClassNameforClose).remove();    
        }); 
    };

    $scope.addkeySkill = function(){
      $scope.keyskillAdd = true;
    }

  $scope.options = ["Text", "Markdown", "HTML", "PHP", "Python",
                  "Java", "JavaScript", "Ruby", "VHDL",
                  "Verilog"];
  $scope.tags = [];


  $scope.checkUndefined = function(Object){
    if (angular.isDefined(Object))
    {
      return true;
    }
    else
    {
      return false;
    }
  };
 
   // class for close the template
   $scope.unloadTemplate=function(scopeobject){
    $scope.templateView = false;
   };

    $scope.years = [];

    var count = 1990;
    for(var i=0;i<25;i++)
    {
      $scope.year = {"id":i,"year":count++};
      $scope.years.push($scope.year);
    }

}]);


bcloud.controller('employeeregisterCtrl',['$scope','$http','employeeServices','authentication','$location','$timeout','$modal','$log','$rootScope','$compile','$templateCache','$parse',
  function($scope,$http,employeeServices,authentication,$location,$timeout,$modal,$log,$rootScope,$compile,$templateCache,$parse){
  
    $scope.years = [];

    var count = 1990;
    for(var i=0;i<25;i++)
    {
      $scope.year = {"id":i,"year":count++};
      $scope.years.push($scope.year);
    }
    
     if( !angular.isUndefined (authentication.isEmployeeLoggedIn()) && !angular.isUndefined (authentication.getEmployeeProfile()) ) 
    {
      var profile = authentication.getEmployeeProfile();      
      $scope.fullname = profile.fullname;              
    } 
   
    $scope.addProfileCourse = function(){
      
      var appendElement =  angular.element(document.getElementById('spaceforregistercourse'));
     
      var courseCount = angular.element(".registercoursecount").length;
    
      var courseElement = $compile('<div class="col-md-12 registercoursecount" > <div class="form-group"> \n <label for="Full Name" class="control-label" style="text-align:left;width:100px;">Course Name</label> <input type="text" class="form-control" ng-model="employeeInformation.othercourses.course'+courseCount+'.course" placeholder="Enter course name"> </div> <div class="form-group"> <label for="from">From</label> <select class="form-control" ng-model="employeeInformation.othercourses.course'+courseCount+'.from" name="currentlocatio"> <option>2001</option> </select> </div> <div class="form-group"> <label for="from">To</label> <select class="form-control" ng-model="employeeInformation.othercourses.course'+courseCount+'.to" name="currentlocation"> <option>2002</option> </select> </div> </div>') ($scope);
    
      $templateCache.put('employeeProfileCourse.html',courseElement);
      var ele =  $templateCache.get('employeeProfileCourse.html')
      console.log(ele); 
      console.log(courseCount);
      appendElement.append(courseElement);
      if(courseCount == 2)
      {
        angular.element(".adds").remove();
      }
    };

    

    $scope.updateExperienceType = function(){     
     
        if($scope.employeeInformation.employeementtype == "experiencer")
        {

          if("keyskills" in $scope.employeeInformation)
          {
            /*console.log("experiencer keyskills truepart");
            delete $scope.employeeInformation.keyskills;
            console.log($scope.employeeInformation.keyskills);
            var skillset = 'employeeInformation.keyskills';
            var model = $parse(skillset);
            model.assign($scope, []);*/
            $scope.employeeInformation.keyskills = [];
          }
          else
          {
            console.log("experiencer keyskills elsepart");
            console.log($scope.employeeInformation.keyskills);
            var skillset = 'employeeInformation.keyskills';
            var model = $parse(skillset);
            model.assign($scope, []);
            $scope.employeeInformation.keyskills = [];
          }
          

          if( ('fresherdetails' in $scope.employeeInformation)) 
             {                
                delete $scope.employeeInformation.fresherdetails;                
             }                      
        }
        else if($scope.employeeInformation.employeementtype == "fresher")
        { 
          if("keyskills" in $scope.employeeInformation)
          {
            /*console.log("fresher keyskills truepart");
            delete $scope.employeeInformation.keyskills;
            console.log($scope.employeeInformation.keyskills);
            var skillset = 'employeeInformation.keyskills';
            var model = $parse(skillset);
            model.assign($scope, []);*/
            $scope.employeeInformation.keyskills = [];
          }
          else
          {
            console.log("fresher keyskills elsepart");
            console.log($scope.employeeInformation.keyskills);
            var skillset = 'employeeInformation.keyskills';
            var model = $parse(skillset);
            model.assign($scope, []);
            $scope.employeeInformation.keyskills = [];
          }

            if( ('experiencedetails' in $scope.employeeInformation)) 
             {                
                delete $scope.employeeInformation.experiencedetails;                
             }            
        }
       }
    
    // Image Crop Functions Start

      var ProfileImage = angular.element('#profile_image');
      $scope.openFile = function(){
        var inputtag = angular.element("<input type='file' id='fileInput'>");
        inputtag.on('change',handleFileSelect);
        inputtag[0].click();
      };

        $scope.myImage='';
        $scope.myCroppedImage='';
        $scope.profile_imageCaption = true;
        var handleFileSelect=function(evt) {

          var file=evt.currentTarget.files[0];
          var reader = new FileReader();
          reader.onload = function (evt) {
            $scope.$apply(function($scope){
              $scope.myImage=evt.target.result;
              $rootScope.image = $scope.myImage;
              $scope.open();
            });
          };
          reader.readAsDataURL(file);
        };
        
        var employeeimage = new Blob();
        $scope.animationsEnabled = true;

        $scope.open = function (size) {

          var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'myModalContent.html',
            controller: 'ModalInstanceCtrl',
            size: 'lg',
            resolve: {
              image: function () {
                return $rootScope.image;
              }
            }        
          });

          modalInstance.result.then(function (selectedImage) {
            var img = {
              "image":selectedImage
            } 
             employeeimage = dataURItoBlob(selectedImage);
            
            $scope.base = selectedImage;  
            ProfileImage.removeAttr('src');                    
            $compile(ProfileImage.attr('ng-src',selectedImage)) ($scope);
            $scope.profile_imageCaption=false;
          }, function () {
            $log.info('Modal dismissed at: ' + new Date());
          });
        };

    // End of image crop 

      $scope.registerUser = function (){      

      $scope.employeeInformation.email = authentication.isEmployeeLoggedIn();      var file = $scope.myFile;
    
      if(!angular.isUndefined(file))      
      {
          $scope.employeeInformation.resume = file;
      } 
      console.log("Prfofile Image : "+employeeimage);
      console.log(employeeimage.size);
      if(employeeimage.size != 0)
      {        
        $scope.employeeInformation.profileimage = employeeimage;
      }   
      console.log($scope.employeeInformation);
    
    employeeServices.insertEmployee($scope.employeeInformation).then(function(data){      
      //$location.path('/profile');
      $location.path('/employeeProfile');      
    });
  };
    $scope.reset = function () {    
      $scope.user = '';
    };

    $scope.options = ["Text", "Markdown", "HTML", "PHP", "Python",
                    "Java", "JavaScript", "Ruby", "VHDL",
                    "Verilog", "C#", "C/C++"
                    ]; 
}]);

bcloud.controller('employeeProfileCtrl',['$scope','employeeServices','$location','$rootScope','authentication', '$compile','getAuthEmpployeeProfile','$parse',
  function($scope, employeeServices,$location,$rootScope,authentication,$compile,getAuthEmpployeeProfile,$parse){
    
    var loggedInEmployeeProfile = authentication.getEmployeeProfile();
   
    if(!angular.isUndefined(getAuthEmpployeeProfile))
    {
      console.log();

      $scope.newempolyee ={
        "email" : authentication.isEmployeeLoggedIn(),
        "employeementtype" : getAuthEmpployeeProfile.employeementtype
      };

      if(getAuthEmpployeeProfile.employeementtype == "fresher")
      {
            var skillset = 'employeeProfile.academicprojectdetails.project0.skillset';
            var model = $parse(skillset);
            model.assign($scope, []); 
      }

    }  

    if(!angular.isUndefined(loggedInEmployeeProfile))
    {
      $scope.headline = loggedInEmployeeProfile.headline;
    }


    $scope.addPreviousCompany = function(){
      
      var appendElement =  angular.element(document.getElementById('spaceforpreviouscompany'));
     
      var previouscompanycount = angular.element(".PreviousCompany").length;
      
      var companyCount = previouscompanycount+1;

      var previouscompanydom = '<span class="PreviousCompany"><hr> <h4><u>Previous Company</u></h4><br> <div class="form-group"> <label for="inputPassword3" class="col-sm-2 control-label" style="text-align:left">Company Name</label> <div class="col-sm-5"> <input type="text" class="form-control" ng-model="employeeProfile.professionaldetails.previouscompanydetails.previouscompany'+previouscompanycount+'.name" placeholder="Previous Company Name"> </div> </div> <!-- Desigantion --> <div class="form-group"> <label for="inputPassword3" class="col-sm-2 control-label" style="text-align:left">Designation</label> <div class="col-sm-5"> <input type="text" class="form-control" ng-model="employeeProfile.professionaldetails.previouscompanydetails.previouscompany'+previouscompanycount+'.role"  placeholder="Designation" name="resumetitle" > </div> </div> <!-- Date Picker for From Date --> <div class="form-group"> <label for="exp" class="col-sm-2 control-label" style="text-align:left">From Date</label> <div class="col-sm-3"> <select class="form-control" ng-model="employeeProfile.professionaldetails.previouscompanydetails.previouscompany'+previouscompanycount+'.frommonth" > <option>Month</option> <option>1</option> <option>2</option> <option>3</option> </select> </div> <div class="col-sm-3"> <select class="form-control" ng-model="employeeProfile.professionaldetails.previouscompanydetails.previouscompany'+previouscompanycount+'.fromyear" > <option>Year</option> <option>2001</option> <option>2002</option> <option>2003</option> </select> </div> </div> <!-- Date Picker for To Date --> <div class="form-group"> <label for="exp" class="col-sm-2 control-label" style="text-align:left">To Date</label> <div class="col-sm-3"> <select class="form-control"ng-model="employeeProfile.professionaldetails.previouscompanydetails.previouscompany'+previouscompanycount+'.tomonth"> <option>Month</option> <option>1</option> <option>2</option> <option>3</option> </select> </div> <div class="col-sm-3"> <select class="form-control" ng-model="employeeProfile.professionaldetails.previouscompanydetails.previouscompany'+previouscompanycount+'.toyear"> <option>Year</option> <option>2001</option> <option>2002</option> <option>2003</option> </select> </div> </div> <!-- Job Profile --> <div class="form-group"> <label for="exp" class="col-sm-2 control-label" style="text-align:left">Job Profile</label> <div class="col-sm-5"> <textarea class="form-control" rows="3" ng-model="employeeProfile.professionaldetails.previouscompanydetails.previouscompany'+previouscompanycount+'.description"></textarea> </div> </div> </span>';

      var courseElement = $compile(previouscompanydom)($scope);
      appendElement.append(courseElement);
      if(previouscompanycount == 2)
      {
        angular.element(".addPreviousCompany").remove();
      }
    };


    $scope.addProjects = function(){

      var appendElement =  angular.element(document.getElementById('spaceforprojects'));
     
      var projectCount = angular.element(".projects").length;
      
      projectCount++;

      var projectdom = '<span class="projects"> <div class="form-group"> <label for="inputPassword3" class="col-sm-2 control-label" style="text-align:left">Project Name</label> <div class="col-sm-5"> <input type="text" class="form-control"ng-model="employeeProfile.academicprojectdetails.project'+projectCount+'.name" placeholder="Project Name" name="resumetitle" > </div> </div> <!-- Project Cagetory --> <div class="form-group"> <label for="exp" class="col-sm-2 control-label" style="text-align:left">Project Category</label> <div class="col-sm-5"> <select class="form-control" ng-model="employeeProfile.academicprojectdetails.project'+projectCount+'.category" > <option>Select</option> <option>IT</option> <option>MECH</option> <option>ACCOUNTS</option> <option>BPO</option> <option>MEDICAL</option> <option>HR</option> </select> </div> </div> <!-- Skills Used --> <div class="form-group"> <label for="skill sets" class="col-sm-2 control-label" style="text-align:left">Skill Sets</label> <div class="col-sm-5"> <tagger ng-model="employeeProfile.academicprojectdetails.project'+projectCount+'.skillset"  options="options" placeholder="Give your Skill sets"/> </div> </div> <!--Project Description --> <div class="form-group"> <label for="inputPassword3" class="col-sm-2 control-label" style="text-align:left">Project Description</label> <div class="col-sm-5"> <textarea class="form-control" rows="3" ng-model="employeeProfile.academicprojectdetails.project'+projectCount+'.description" placeholder="Profile Summary"></textarea> </div> </div> </span>';

      var project = $compile(projectdom)($scope);
      var skillset = 'employeeProfile.academicprojectdetails.project'+projectCount+'.skillset';
      var model = $parse(skillset);
      model.assign($scope, []); 
      appendElement.append(project);
      if(projectCount == 2)
      {
        angular.element(".addProjectButton").remove();
      }
    }

     $scope.savemyprofile = function(){

      $scope.employeeProfile.email = authentication.getEmployee();
      $scope.employeeProfile.employeementtype = getAuthEmpployeeProfile.employeementtype;
      console.log($scope.employeeProfile);
      employeeServices.employeeProfileUpdate($scope.employeeProfile)
        .then(function(data){          
          $location.path('/home');
         });
    };

    $scope.options = ["Text", "Markdown", "HTML", "PHP", "Python",
                    "Java", "JavaScript", "Ruby", "VHDL",
                    "Verilog", "C#", "C/C++"
                    ]; 

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

bcloud.controller('firendSearchCtrl',['$scope','$compile','friendRequestServices','authentication','getCommonThingsforFriendSearch','employeeServices','$modal','$timeout',
  function($scope,$compile,friendRequestServices,authentication,getCommonThingsforFriendSearch,employeeServices,$modal,$timeout){

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

      var profile = employeeServices.getCommonEmployeeProfile();

      $scope.request.email=authentication.getEmployee();
      if(!angular.isUndefined(profile))
      {
         if(!angular.isUndefined(profile.employeementtype))
            $scope.request.employeementtype = profile.employeementtype;
      };
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
    $scope.friendslistQueryParam = {
      "employee":emailId,
      "email":authentication.getEmployee()
    };           
    console.log($scope.friendslistQueryParam);
    friendRequestServices.sendFriendRequest($scope.friendslistQueryParam).then(function(data){
          if(data.status == "OK")
          {
            $scope.friendsList.splice(index, 1);
          }
          else if (data.proversion)
          {
            //Call Proversion Model
            $scope.open();
          } 
    });
  };

  // Employee Connection Proversion 
     $scope.open = function () {
      var modalInstance = $modal.open({       
        templateUrl: 'connectionproversion.html',
        controller: 'connectionproversionCtrl',
        size: "lg",
        backdrop:false       
      });

      modalInstance.result.then(function (plan) {       
      }, function () {
        console.log('Modal dismissed at: ' + new Date());
      });

    };
}]);

bcloud.controller('friendRequestListCtrl',['$scope','authentication','friendRequestServices',
  function($scope,authentication,friendRequestServices){
  
  $scope.currentEmployeeFriendList = {      
      "email":authentication.getEmployee()
    }; 
  friendRequestServices.getRequestedFriendsList($scope.currentEmployeeFriendList).then(function(data){    
    if(!angular.equals({},data))
          {
           $scope.friendRequestedList = data.result;
            $scope.resultoffriendsListfail= false;
            $scope.resultoffriendsListtrue = true;
          }
          else
          {
            $scope.resultoffriendsListtrue = false;
            $scope.resultoffriendsListfail = true;
          }
  });

  $scope.confirmRequest = function(employeeEmail,index,status){              
    console.log(employeeEmail,index,status);
    updatefriendslist = {
      "email":authentication.getEmployee(),
      "employee":employeeEmail,
      "status":status
    }
    friendRequestServices.updateFriendRequest(updatefriendslist).then(function(data){
      if(!angular.equals({},data))
          {    
            if(data.status == "OK")
              {
                $scope.friendRequestedList.splice(index, 1);
                console.log($scope.friendRequestedList);
                console.log($scope.friendRequestedList.length);
                if($scope.friendRequestedList.length<=0)
                {                  
                  $scope.resultoffriendsListtrue = false;
                  $scope.resultoffriendsListfail = true;
                }
                $scope.$parent.getRequestedFriendCount();
              }       
          }
          else
          {
           
          }
    });
  }

}]);

bcloud.controller('friendsListCtrl',['$scope','friendRequestServices','showFriendsList','$modal',
  function($scope,friendRequestServices,showFriendsList,$modal){
    console.log("!angular.equals({},showFriendsList) "+!angular.equals({},showFriendsList));
    console.log("angular.equals({},showFriendsList)" +angular.equals({},showFriendsList));
    if(!angular.equals({},showFriendsList))
    {
      $scope.employeefriendsListview=true;
      $scope.employeeFriends = showFriendsList.result;
    }
    else
    {
      $scope.employeefriendsListview=false;
    }

    $scope.sendMail = function(receiverEmailId){
      $scope.sendmail = {
        "to":receiverEmailId
      };
      $scope.inmail();
    };

    // Mail Controller
    $scope.inmail = function () {
      var modalInstance = $modal.open({       
        templateUrl: 'inmail.html',
        controller: 'inmailCtrl',        
        backdrop:false,
        resolve: {
              sendmail: function () {
                return $scope.sendmail;
              }
            }
      });

      modalInstance.result.then(function () {       
      }, function () {
        console.log('Modal dismissed at: ' + new Date());
      });

    };

}]);

bcloud.controller('mailCtrl',['$scope','mailService','authentication','getinboxmails','getsendMails',
  function($scope,mailService,authentication,getinboxmails,getsendMails){
    $scope.inmails = getinboxmails.inMails;
    $scope.sendmails = getsendMails.sendMails;

    $scope.reply = {};
    $scope.ma = {};
    $scope.reply = function(id){
      $scope.ma.reply[id] = true;
    };

    $scope.sendreply = function(id,to){
      var employee = {};
      employee.email = authentication.getEmployee();
      employee.to = to;
      employee.msgid = id;
      employee.message = $scope.reply.message;
      console.log(employee);
      var result = mailService.sendreplys(employee);
      result.then(function(data){
        controller.log(data);
      });
    };

    $scope.cancelreply = function(id){
      $scope.ma.reply[id] = false;
    };
      $scope.check = function(){
    alert($scope.inmail.name);
  };
  $scope.inmail = {};
  $scope.inboxConfig = {
        "replyMsg":false,
        "inboxpreviewmode":true
  };

    // For Directive

    $scope.tabcontent = true;

    $scope.toggleContent = function(){
      $scope.tabcontent = !$scope.tabcontent;
    };
   
     $scope.openMail = function(id,index){    
        $scope.readmail = filtered[index];
        $scope.inboxConfig.inboxpreviewmode = !$scope.inboxConfig.inboxpreviewmode;
     };
     $scope.sendreplyMsg = function(id,to){

      var employee = {};
      employee.email = authentication.getEmployee();
      employee.to = to;
      employee.msgid = id;
      employee.message = $scope.reply.replymessage;
      console.log(employee);       
      var result = mailService.sendreplys(employee);
      result.then(function(data){
        console.log(data);
        $scope.inboxConfig.replyMsg = false;
      });
    };

}]);

bcloud.controller('ModalInstanceCtrl', function ($scope, $modalInstance, $timeout, image) {
  
    var img = new Image(); 
    var imgPlace = angular.element('<div class="imgPlace">');
    img.onload = function(){
     
    };

    img.src = image; 

 

    $scope.changeImage = function(){
        var newElement = angular.element('<input type="file" class="modalFileinput"/></div>');
        newElement[0].click();
        newElement.on('change',handleFileSelect);      
    }
 
    console.log("image : "+image);
    $scope.ok = function () {    
      $modalInstance.close($scope.myCroppedImage);
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };

    $scope.myImage='';
    $scope.myCroppedImage='';

     var handleFileSelect=function(evt) {    
      var file=evt.currentTarget.files[0];
      console.log(file);
      var reader = new FileReader();
      reader.onload = function (evt) {
        $scope.$apply(function($scope){
          $scope.myImage=evt.target.result;
        });
      };
      reader.readAsDataURL(file);
    };
    /* This is for inside modal
      $timeout(function() {
      var x = angular.element(document.querySelector('.modalFileinput'));
      console.log(x);
      x.bind('change',handleFileSelect);

      }/* no delay here );*/

    $scope.myImage = image;
});


bcloud.controller('connectionproversionCtrl', function ($scope, $modalInstance,friendRequestServices,authentication) {

  $scope.ok = function (plan) {
      var plan = {
        "plan":plan,
        "email":authentication.getEmployee()
       } 
    var request = friendRequestServices.updateConnectionProPlan(plan);
   request.then(function(data){
      $modalInstance.close();
   });
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

  $scope.add = function(){
    alert("Function Added");
  };

});

bcloud.controller('inmailCtrl', function ($scope, $modalInstance,friendRequestServices,sendmail,authentication) {

  $scope.sendmail = sendmail; 

  $scope.sendInmail = function(){
    $scope.sendmail.email  = authentication.getEmployee();
     var mailStatus = friendRequestServices.sendInmail($scope.sendmail);
    mailStatus.then(function(data){
      $modalInstance.close();
   });
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

  $scope.date = function(date){
    var day = moment(date, "DD-MM-YYYY HH:mm:ss").format("MM-DD-YYYY HH:mm:ss");
    console.log(day);
    console.log(Date.parse(day));
    return Date.parse(day);
  };


});

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

function convertToInt(id){
    return parseInt(id, 10);
};

function dataURItoBlob(dataURI) {
    var byteString = atob(dataURI.split(',')[1]);
    var ab = new ArrayBuffer(byteString.length);
    var ia = new Uint8Array(ab);  
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    return new Blob([ab], { type: 'image/jpeg' });
}

bcloud.filter('fromNow', function($filter) {
  return function(date) {
    console.log(date);
    date =parseInt(date);
    moment.locale('en');  
   console.log(moment.unix(date).fromNow());

   return moment.unix(date).fromNow();
  }
});

bcloud.filter('displayDate', function($filter) {
  return function(date) {  
    date =parseInt(date);
    moment.locale('en'); 

   return moment.unix(date).format("DD-MM-YYYY hh:mm:ss:a");
  }
});

bcloud.filter('orderObjectBy', function() {
  return function(items, field, reverse) {
   filtered = [];
    angular.forEach(items, function(item) {
      filtered.push(item);
    });
    filtered.sort(function (a, b) {
     
      return (a[field] > b[field] ? 1 : -1);
    });
    if(reverse) filtered.reverse();
    console.log(filtered);
    return filtered;
  };
});

