<!DOCTYPE html>
<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
  </head>

  <body>
    <div class="container">
      <h2>Candidate</h2>
      <div class="panel panel-primary">
   <div class="panel-heading">
      <h3 class="panel-title">Candidate</h3>
   </div>
   <div class="panel-body">
     
  
      <div class="panel-group" id="accordion">
        <div class="panel panel-primary">
          <div class="panel-heading">
            <h4 class="panel-title">
              <a data-toggle="collapse" data-parent="#accordion" href="#collapse1">Personal Info</a>
            </h4>
          </div>
          <div id="collapse1" class="panel-collapse collapse in">
            <div class="panel-body"> <div class="row">
    <div class="col-sm-6">
    <div class="row">
    <div class="form-group">
      <label for="fullname" class="col-sm-2 control-label">Full Name<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <input type="text" class="form-control" id="fullname" 
            placeholder="Enter First Name">
      </div>
   </div>
   </div>
   <div class="row">
   <div class="form-group">
      <label for="lastname" class="col-sm-2 control-label">City<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
          <select class="form-control" id="city">
  <option>Select</option>
  <option>Coimbatore</option>
   <option>Chennai</option>

</select>
      </div>
   </div>
   </div>
   <br>
     <div class="row">
   <div class="form-group">
      <label for="mailId" class="col-sm-2 control-label">Mail Id<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <input type="text" class="form-control" id="mailId" 
            placeholder="Enter Your MailId">
      </div>
   </div>
   </div>
  
  
   
    </div>
     <div class="col-sm-6">
     
      <div class="row">
    <div class="form-group">
      <label for="Mobile No" class="col-sm-2 control-label">Mobile No<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <input type="text" class="form-control" id="mobileNo" 
            placeholder="Enter your Mobile No">
      </div>
   </div>
   </div>
   <div class="row">
   <div class="form-group">
      <label for="password" class="col-sm-2 control-label">Password<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <input type="password" class="form-control" id="password" 
            placeholder="Enter your Password">
      </div>
   </div>
  
  
  
   </div>
    <br>
     <div class="row">
   <div class="form-group">
      <label for="confirmpassword" class="col-sm-2 control-label"> Confirm Password<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <input type="password" class="form-control" id="confirmpassword" 
            placeholder="Enter confirm password">
      </div>
   </div>
   </div>
   <!-- <p align="right">
    <a href="educational" ><input type="button" value="Next"  class="btn btn-primary"/></a>
    </p> -->
     </div>
    </div></div>
          </div>
        </div>
        <div class="panel panel-primary">
          <div class="panel-heading">
            <h4 class="panel-title">
              <a data-toggle="collapse" data-parent="#accordion" href="#collapse2">Educational Info</a>
            </h4>
          </div>
          <div id="collapse2" class="panel-collapse collapse in">
            <div class="panel-body">   <div class="row">
    <div class="col-sm-6">
    <div class="row">
    <div class="form-group">
      <label for="education" class="col-sm-2 control-label">Basic Education<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <select class="form-control">
     <option>Select</option>
  <option>Not Pursuing Graduation</option>
  <option>B.E</option>
  <option>B.Sc</option>
  <option>B.A</option>
  <option>B.Com</option>
</select>
      </div>
   </div>
   </div>
   <div class="row">
   <div class="form-group">
      <label for="education" class="col-sm-2 control-label">Masters Education<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <select class="form-control">
     <option>Select</option>
 
  <option>M.E</option>
  <option>M.Sc</option>
  <option>M.A</option>
  <option>M.Com</option>
</select>
      </div>
   </div>
   </div>
     
  
  
     <div class="row">
    <div class="form-group">
      <label for="Mobile No" class="col-sm-2 control-label">Doctorate Education<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <select class="form-control">
  <option>Select</option>
  <option>Ph.D</option>
  <option>M.Phil</option>
  <option>Others</option>

</select>
      </div>
   </div>
   </div>
  
   
    </div>
     <div class="col-sm-6">
     
    <div class="row">
    
    <div class="form-group">
      <label for="certificate1" class="col-sm-2 control-label"> Certificate Course<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <input type="text" class="form-control" id="certificate1" 
            placeholder="Enter Certifcate Course Name">
      </div>
   </div>
   
    </div>
    <div class="row">
    
    <div class="form-group">
      <label for="certificate2" class="col-sm-2 control-label"> Certificate Course<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <input type="text" class="form-control" id="certificate2" 
            placeholder="Enter Certifcate Course Name">
      </div>
   </div>
    </div>
   <div class="row">
   <div class="form-group">
      <label for="certificate3" class="col-sm-2 control-label"> Certificate Course<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <input type="text" class="form-control" id="certificate3" 
            placeholder="Enter Certifcate Course Name">
      </div>
   </div>
   </div>
   
   
<!--    <p align="right">
   <a href="employee" ><input type="button" value="Previous"  class="btn btn-primary"/></a>
    <a href="profession" ><input type="button" value="Next"  class="btn btn-primary"/></a>
    </p> -->
     </div>
    </div></div>
          </div>
        </div>
        <div class="panel panel-primary">
          <div class="panel-heading">
            <h4 class="panel-title">
              <a data-toggle="collapse" data-parent="#accordion" href="#collapse3">Profession Info</a>
            </h4>
          </div>
          <div id="collapse3" class="panel-collapse collapse in">
            <div class="panel-body">   <div class="row">
    <div class="col-sm-6">
    <div class="row">
    <div class="form-group">
      <label for="exp" class="col-sm-2 control-label"><span><font style="color:red">*</font></span>Years Of Exp</label>
      <div class="col-sm-10">
        <select class="form-control">
  <option>Select</option>
  <option>Fresher</option>
  <option>1</option>
  <option>2</option>
  <option>3</option>
 <option>4</option>
  <option>5</option>
</select>
      </div>
   </div>
   </div>
   <div class="row">
   <div class="form-group">
      <label for="skills" class="col-sm-2 control-label"><span><font style="color:red">*</font></span>Key Skills</label>
      <div class="col-sm-10">
         <input type="text" class="form-control" id="skills" 
            placeholder="Enter Your Skills">
      </div>
   </div>
   </div>
     <div class="row">
   <div class="form-group">
      <label for="salary" class="col-sm-2 control-label">Salary<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <input type="text" class="form-control" id="salary" 
            placeholder="Enter Your Salary">
      </div>
   </div>
   </div>
   <br>
   <div class="row">
   <div class="form-group">
      <label for="resume" class="col-sm-2 control-label">Resume<span><font style="color:red">*</font></span></label>
      <div class="col-sm-10">
         <input type="file"  id="resume" 
            >
      </div>
   </div>
  </div>
   <br>
  
   
    </div>
     <div class="col-sm-6">
     
      
   
     
  
     </div>
    </div></div>
          </div>
        </div>
      </div> 
    </div>
    <div class="row">
   
       <div style="margin-left:300px">
      <input type="submit" id="empSave" value="Register" class="btn btn-success"/>
      <input type="reset" value="Cancel" class="btn btn-danger"/>
     </div>
  
  </div>
 </div>
</div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
    <script>
    
  $(document).ready(function(){  
	  
	 
	$("#empSave").click(function(){  
    var form_data = {
            'fullname': $('#fullname').val(),
            'city': $('#city').val()
        };
    $.ajax({
		  url: "http://localhost:8080/blouda/employeeCreate",
		 // dataType:'json',
		  contentType: "application/json",
		  type: 'POST',
	      data : JSON.stringify(form_data),
		  // data : { field1: "hello", field2 : "hello2"},
		  success: function()
		  {
			  alert("success");
		  },
		  error: function()
		  {
			  
			  alert("ërror");
		  }
		 
		 
		});  //ajax end
	});// click event end
  });
    </script>
  </body>
</html>
