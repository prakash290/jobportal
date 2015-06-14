package com.justbootup.blouda.controller;

import java.io.File;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.justbootup.blouda.dao.EmployeeDao;
import com.justbootup.blouda.serviceApi.IEmployeeBuildGraph;
import com.justbootup.blouda.serviceApi.IEmployeeService;
import com.justbootup.blouda.util.SocialNetworkProperties;
import com.mongodb.BasicDBObject;


@Controller
public class EmployeeController {
	
	@Autowired
	 IEmployeeService employeeService;
	
	@Autowired
	SocialNetworkProperties prop;
	
	@Autowired
	 IEmployeeBuildGraph employeeBuildGraphService;
	
	 @Autowired
	 SimpMessagingTemplate template;
	 
	 String WEBSOCKET_TOPIC = "/topic/notify";
	 
	 public void notifyClients() throws Throwable {
	        template.convertAndSend(WEBSOCKET_TOPIC, new Date());	       
	    }
	 
	 @SuppressWarnings("unchecked")
		@RequestMapping(value="/web",method=RequestMethod.GET)
		public @ResponseBody JSONObject emp() throws Throwable
		{
			JSONObject obj = new JSONObject();
			//new EmployeeBulidNodeDao().createEmployeeNode();		
			obj.put("name","sample");
			notifyClients();
			return obj;
		}
	 
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/employee",method=RequestMethod.GET)
	public @ResponseBody JSONObject employeePage(HttpServletRequest request) throws SQLException
	{
		JSONObject obj = new JSONObject();
		//new EmployeeBulidNodeDao().createEmployeeNode();		
		obj.put("name","sample");
		return obj;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/callback",method=RequestMethod.GET)
	public @ResponseBody JSONObject employeePage1(HttpServletRequest request) throws SQLException
	{
		
		String state = request.getParameter("state");
		String code = request.getParameter("code");
		System.out.println("state is : "+request.getParameter("state"));
		System.out.println("code is : "+request.getParameter("code"));
		JSONObject obj = new JSONObject();
		new EmployeeDao().generateoauth2Token(state,code);
		
		
		//new EmployeeBulidNodeDao().createEmployeeNode();
		obj.put("name","sample");
		return obj;
	}

		
	@RequestMapping(value="/newEmployeeRegister",method=RequestMethod.POST)	
	public @ResponseBody JSONObject newEmployeeRegister(@RequestBody JSONObject newEmployee) throws SQLException, Exception
	{
		JSONObject employee = new JSONObject();
		employee = employeeService.newEmployeeRegistration(newEmployee);		
		return employee;
	}
	
	@RequestMapping(value="/employeeCreate",method=RequestMethod.POST)	
	public @ResponseBody net.sf.json.JSONObject employeeSave(@RequestParam(value="file",required=false) MultipartFile file,@RequestParam(value="profileimage",required=false) MultipartFile profileimage,@RequestParam("userDetails") String employeeDetails) throws SQLException, Exception
	{
		System.out.println(employeeDetails);
		net.sf.json.JSONObject jsonEmployeeDetails = net.sf.json.JSONObject.fromObject(employeeDetails);	
		net.sf.json.JSONObject employee = new net.sf.json.JSONObject();	
		
		employee.put("user",employeeService.saveUser(file,jsonEmployeeDetails,profileimage));			
		employeeBuildGraphService.createEmployeeNode(jsonEmployeeDetails);
		return employee;
	}
	

	@RequestMapping(value="/employeeProfile",method=RequestMethod.POST)
	public @ResponseBody JSONObject employeeUpdateProfile(@RequestBody JSONObject jsonEmployeeProfile) throws SQLException, UnknownHostException
	{
		JSONObject forEmployeeGraphNode = (JSONObject) jsonEmployeeProfile.clone();
		System.out.println(forEmployeeGraphNode);
		JSONObject status = employeeService.employeeUpdateProfile(jsonEmployeeProfile);	
		System.out.println(status);
		if(!status.containsKey("update") && jsonEmployeeProfile.get("employeementtype").equals("experiencer"))
		{			
			employeeBuildGraphService.createEmployeeProfileNode(forEmployeeGraphNode);
		}
		System.out.println("Inside Controller after updated Profile is : "+forEmployeeGraphNode);
		
		return status;
	}
	

	@RequestMapping(value="/getemployeeProfile",method=RequestMethod.POST)
	public @ResponseBody BasicDBObject getEmployeeProfile(@RequestBody JSONObject employeeSession) throws SQLException, UnknownHostException
	{
		BasicDBObject employeeProfile = employeeService.getEmployeeProfile(employeeSession);
		
		return employeeProfile;
	}
	
	@RequestMapping(value="/getemployeeProfileforUpdate",method=RequestMethod.POST)
	public @ResponseBody HashMap<String, Object> getEmployeeProfileforUpdate(@RequestBody JSONObject employee) throws SQLException, UnknownHostException
	{
		HashMap<String, Object> employeeProfile = employeeService.getEmployeeProfileForUpdate(employee);		
		return employeeProfile;
	}
	
  
	@RequestMapping(value="/employeeLogin",method=RequestMethod.POST)
	public @ResponseBody BasicDBObject employeeLogin(@RequestBody JSONObject employeeCredentials) throws SQLException, UnknownHostException
	{
		BasicDBObject employeeProfile = employeeService.emloyeeLogin(employeeCredentials);
		
		return employeeProfile;
	}
	
	@RequestMapping(value="/emloyeeLinkedInLogin",method=RequestMethod.POST)
	public @ResponseBody BasicDBObject emloyeeLinkedInLogin(@RequestBody JSONObject employeeLinkedInCredentials) throws SQLException, UnknownHostException
	{
		BasicDBObject employeeLinkedIn = employeeService.emloyeeLinkedInLogin(employeeLinkedInCredentials);		
		return employeeLinkedIn;
	}
  
	@RequestMapping(value="/emloyeefaceBookLogin1",method=RequestMethod.POST)
	public @ResponseBody BasicDBObject emloyeefacebookLogin(@RequestBody JSONObject facebookDetails) throws Exception
	{
		//BasicDBObject employeeLinkedIn = employeeService.emloyeeLinkedInLogin(facebookDetails);
		BasicDBObject sample = new BasicDBObject();
		sample.put("dsfsdf","sadfdsa");
		new EmployeeDao().generateFacebooklongliveToken(facebookDetails);
		return sample;
	}
	
	
	@RequestMapping(value="/newEmployeeGoogleLogin")
	public @ResponseBody BasicDBObject employeeGoogleLogin(@RequestParam Map<String,String> allRequestParams) throws Exception
	{
		System.out.println(allRequestParams);		
		//BasicDBObject employeeLinkedIn = employeeService.emloyeeLinkedInLogin(facebookDetails);
		BasicDBObject sample = new BasicDBObject();
		sample.put("dsfsdf","sadfdsa");
		
		new EmployeeDao().googleOauth2Token(allRequestParams);
		return sample;
	}
	
	@RequestMapping(value="/getEmployeeProfileImage",method=RequestMethod.POST)
	public @ResponseBody JSONObject getEmployeeProfileImage(@RequestBody JSONObject employee) throws Exception
	{		
		return employeeService.getEmployeeProfileImage(employee);
	}
	
	@RequestMapping(value="/updateEmployeeProfiles",method=RequestMethod.POST)
	public @ResponseBody JSONObject updateEmployeeProfiles(@RequestBody JSONObject employeeProfiles) throws Exception
	{	
		System.out.println(employeeProfiles);
		String jsonEmployee = employeeProfiles.toJSONString();		
		JSONObject result = employeeService.updateEmployeeProfiles(employeeProfiles);
		if(result.get("status").toString().equals("success"))
		{
			// Create Node for Updated Fields
			net.sf.json.JSONObject employee = net.sf.json.JSONObject.fromObject(jsonEmployee);
			System.out.println("net sf json object is : "+employee);
			employeeBuildGraphService.updateEmployeeProfileNode(employee);
		}
		return result; 
	}
	

	@RequestMapping(value="/updateEmployeeProfileImage",method=RequestMethod.POST)	
	public @ResponseBody JSONObject updateEmployeeProfileImage(@RequestParam(value="profileimage",required=false) MultipartFile profileimage,@RequestParam("email") String employee) throws SQLException, Exception
	{		
		net.sf.json.JSONObject employeeEmail = new net.sf.json.JSONObject();
		employeeEmail.put("email",employee);
		return employeeService.updateEmployeeProfileImage(employeeEmail, profileimage);
	}
	
	@RequestMapping(value="/updateEmployeeProfileResume",method=RequestMethod.POST)	
	public @ResponseBody JSONObject updateEmployeeProfileResume(@RequestParam(value="profileresume",required=false) MultipartFile profileresume,@RequestParam("userobject") String employee) throws SQLException, Exception
	{	
		
		net.sf.json.JSONObject employeeEmail = net.sf.json.JSONObject.fromObject(employee);			
		return employeeService.updateEmployeeProfileResume(employeeEmail,profileresume);
	}
	
}
