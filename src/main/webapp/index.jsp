<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<html ng-app="bcloud">
  <head>
  
   <base href="/blouda/">
   
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="resources/app/css/bootstrap/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="resources/app/css/style.css">    
    <link rel="stylesheet" type="text/css" href="resources/app/css/c3.css">    
     
    <link rel="stylesheet" type="text/css" href="resources/app/css/angular-tagger.css"> 
    <link rel="stylesheet" type="text/css" href="resources/app/css/bootstrap-social.css"> 
   
    <script type="text/javascript" src="resources/app/js/jquery-2.1.3.js" ></script>
    <script type="text/javascript" src="resources/app/js/d3.min.js" ></script>
    <script type="text/javascript" src="resources/app/js/c3.js" ></script>       
    <script type="text/javascript" src="resources/app/js/angular.js" ></script>
    <script type="text/javascript" src="resources/app/js/angular-cookies.js" ></script>   
     <script type="text/javascript" src="resources/app/js/angular-animate.js" ></script>  
    <script type="text/javascript" src="resources/app/js/ui-bootstrap-tpls-0.12.1.js" ></script>    
    <script type="text/javascript" src="resources/app/js/angular-route.js" ></script>   
    <script type="text/javascript" src="resources/app/js/angular-tagger.js" ></script>
    <script type="text/javascript" src="resources/app/js/satellizer.js" ></script>
    <script type="text/javascript" src="resources/app/js/bootstrap.js" ></script>
    <script type="text/javascript" src="resources/app/js/ngStorage.js" ></script>
    <script type="text/javascript" src="resources/app/js/angular-sanitize.js" ></script>
     <script type="text/javascript" src="resources/app/js/plugins/ngFacebook.js" ></script> 


    <script type="text/javascript" src="resources/app/app-module.js" ></script>  

    <script type="text/javascript" src="resources/app/js/plugins/google-plus-signin.js" ></script>

    <script type="text/javascript" src="resources/app/services/backend.js" ></script>
    <script type="text/javascript" src="resources/app/services/employeeServices.js" ></script>
    <script type="text/javascript" src="resources/app/services/employerServices.js" ></script>
    <script type="text/javascript" src="resources/app/services/employerDocServices.js" ></script>
    <script type="text/javascript" src="resources/app/services/notification.js" ></script>

    <script type="text/javascript" src="resources/app/controllers/controller.js" ></script> 
    <script type="text/javascript" src="resources/app/directives/dynamic-chart-directive.js" ></script>  
    <script type="text/javascript" src="resources/app/directives/front-chart-directive.js" ></script>
    <script type="text/javascript" src="resources/app/js/browserUtil.js" ></script> 
    <script type="text/javascript" src="resources/app/js/angular-linkedin-login.js" ></script>

    <script type="text/javascript" src="resources/app/js/angular-chart.js" ></script> 
    <script type="text/javascript" src="resources/app/services/authentication.js" ></script>
    <script type="text/javascript" src="resources/app/js/canvasjs.js" ></script>     
    <script type="text/javascript" src="resources/app/js/angular-tagger.js" ></script>   
    
    <title>Hello App Engine</title>
  </head>
  <body ng-controller="parent">
  
  		<nav class="navbar navbar-default">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#/home">BloudA</a>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">      
      
      <ul class="nav navbar-nav navbar-right">
        <li><a href="#/home">Home</a></li>
      	<li><a href="#/newregister" ng-show="!isEmployeeLoggedIn()">Register</a></li>
      	<li><a href="#/analytics">Analytics</a></li>   
        <li><a href="#/hireme">Hire People</a></li>   
        <li><a href="#/login" ng-show="!isEmployeeLoggedIn()"> Login</a></li>    
        <li><a href="" ng-click="logout()" ng-show="isEmployeeLoggedIn()">{{loggedUserEmail}}</a></li> 
        <li><a href="" ng-click="logout()" ng-show="isEmployeeLoggedIn()">Logout</a></li>           
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>

  <div class="container">
  <div class="row">  	
  		<div class="ng-view"></div>  	
  </div>
  </div>
    
  </body>
</html>
