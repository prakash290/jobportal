<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Hello App Engine</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Employer Account Create</title>
  <link rel="stylesheet" type="text/css" href="css/bootstrap/css/bootstrap.css">
  <link rel="stylesheet" type="text/css" href="css/jquery.bxslider.css">
  <link rel="stylesheet" type="text/css" href="css/style.css">
  <style type="text/css">
    .navbar-header {
    float: left;
    padding: 15px;
    text-align: center;
    width: 100%;
  }
  .navbar-brand {float:none;}
  #linkedlogin{
    display: none;
    }
    .well{
      background-color: white;
      padding-bottom: 30px;
    }
  </style>
    <script type="text/javascript" src="resources/app/js/jquery-2.1.3.js"></script>
   
    <script type="text/javascript" src="https://platform.linkedin.com/in.js">
     api_key:75xcotynliiowz
     authorize:true
     onLoad: OnLinkedInFrameworkLoad
     </script>     
     <script type="text/javascript">
    
    function OnLinkedInFrameworkLoad() {
    IN.Event.on(IN, "auth", OnLinkedInAuth);        
    }
   function OnLinkedInAuth() {
    IN.API.Profile("me")
    .fields([
            'id', 'firstName', 'lastName', 'positions:(title,company)'])
    .result(function(result) {
      console.log('inside onLinkedInLogin=============>');  
      console.log(result.values[0]); 
      var positions = result.values[0].positions;
      var positionCount = positions._total;;
      for(var i = 0; i < positionCount; i++) {
      var company = positions.values[i].company;
      var title = positions.values[i].title;
      console.log(company.name);
      loaddata(company.name);
    }
    list();
    })
    .error(function(err) {
      alert(err);
    });
    hide();
    show(); 
     
    }
    function onLinkedInLogin() {
            IN.API.Profile("me")
            .fields(["id", "firstName", "lastName", "pictureUrl", "publicProfileUrl", "industry", "location", "headline"])
            .result(function (result) {
                var firstName = result.values[0].firstName;
                var lastName = result.values[0].lastName;              
            })
         }       
    </script>
    <script type="text/javascript">
    function show(){      
      $("#linkedlogin").show();
     }  
     function hide(){
      $("#loginmessage").hide();
     }
     var company=[];
     function loaddata(data){
      company.push(data);      
     }
     function list(){      
      createcompany(company);
     }
     function createcompany(data){   
      console.log(data);
      for(var i=0;i<data.length;i++)
      {
        $("#sample").append('<option value="'+data[i]+'">'+data[i]+'</option>');
      }

     }
    </script>
  </head>

<body style="background-color:#f4f4f4;">
    <header>
      <nav class="navbar navbar-default navbar-static-top">
        <div class="container">
          <!-- Brand and toggle get grouped for better mobile display -->
          <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="index.html">BloudA</a>
          </div>
        </div><!-- /.container-fluid -->
      </nav>
    </header>

    <div class="container-fluid">
      <div class="container">
        <div class="row">
          <div class="col-md-8 col-md-offset-2">  <br>            
              <div class="well">
                <h2 class="text-center">User Review</h2>
                <div ><p class="text-center">Please login in linkedin to write your review</p>
                  <div class="col-md-4 col-md-offset-4" style="padding-bottom:40px;">
                      <script type="in/Login">       
                      </script>
                      &nbsp;<br/>
                  </div>    
                </div>
               <form id="linkedlogin">
                  <div class="form-group">
                    <label for="exampleInputEmail1">Title of Review</label>
                    <input type="text" class="form-control" id="exampleInputEmail1" placeholder="Review Title">
                  </div>
                  <div class="form-group">
                    <label for="exampleInputEmail1">Pros</label>
                    <textarea class="form-control" rows="3" placeholder="Advice to Management"></textarea>
                  </div>
                  <div class="form-group">
                    <label for="exampleInputEmail1">Cons</label>
                    <textarea class="form-control" rows="3" placeholder="Advice to Management"></textarea>
                  </div>
                  <div class="form-group">
                    <label for="exampleInputEmail1">Advice to Management</label>
                    <textarea class="form-control" rows="3" placeholder="Advice to Management"></textarea>
                  </div>
                  <select class="form-control" id="sample">
                      
                  </select><br>
                  
                  <button type="button" class="btn btn-primary">Submit Review</button>
               </form>
              </div>
          </div>
        </div>
      </div>
    </div>
    
 <script type="text/javascript" src="js/jquery-2.1.3.js"></script>
<script type="text/javascript" src="css/bootstrap/js/bootstrap.js"></script>
<script type="text/javascript" src="js/jquery.bxslider.js"></script>
<script type="text/javascript" src="js/script.js"></script>
    
  </body>
</html>