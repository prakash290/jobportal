<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<html ng-app="bcloud">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="app/css/bootstrap/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="app/css/style.css">    
    <link rel="stylesheet" type="text/css" href="app/css/c3.css">      
    <link rel="stylesheet" type="text/css" href="app/css/ng-tags-input.bootstrap.css">   
    <link rel="stylesheet" type="text/css" href="app/css/ng-tags-input.css">   
    <link rel="stylesheet" type="text/css" href="app/css/angular-tagger.css"> 
    <script type="text/javascript" src="app/js/jquery-2.1.3.js" ></script>
    <script type="text/javascript" src="app/js/d3.min.js" ></script>
    <script type="text/javascript" src="app/js/c3.js" ></script>       
    <script type="text/javascript" src="app/js/angular.js" ></script>
    <script type="text/javascript" src="app/js/angular-cookies.js" ></script>   
     <script type="text/javascript" src="app/js/angular-animate.js" ></script>  
    <script type="text/javascript" src="app/plugins/ui-bootstrap.js" ></script>    
    <script type="text/javascript" src="app/js/angular-route.js" ></script>
    <script type="text/javascript" src="app/js/ng-tags-input.js" ></script>
    <script type="text/javascript" src="app/js/angular-tagger.js" ></script>
    <script type="text/javascript" src="app/js/bootstrap.js" ></script>
    <script type="text/javascript" src="app/app-module.js" ></script>  
    <script type="text/javascript" src="app/services/backend.js" ></script>
    <script type="text/javascript" src="app/services/notification.js" ></script>

    <script type="text/javascript" src="app/controllers/controller.js" ></script> 
    <script type="text/javascript" src="app/directives/dynamic-chart-directive.js" ></script>  
    <script type="text/javascript" src="app/directives/front-chart-directive.js" ></script>   
    <script type="text/javascript" src="app/js/angular-chart.js" ></script>    
    <script type="text/javascript" src="app/js/canvasjs.js" ></script>   
    
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
      	<li><a href="#/register">Register</a></li>
      	<li><a href="#/analytics">Analytics</a></li>   
        <li><a href="#/hireme">Hire People</a></li>      
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
