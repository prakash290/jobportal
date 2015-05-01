package com.justbootup.blouda.controller;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.justbootup.blouda.dao.EmployeeBulidNodeDao;
import com.justbootup.blouda.serviceApi.IEmployeeBuildGraph;
import com.justbootup.blouda.serviceApi.IEmployeeService;
import com.mongodb.BasicDBObject;


@Controller
public class EmployeeController {
	
	@Autowired
	 IEmployeeService employeeService;
	
	@Autowired
	 IEmployeeBuildGraph employeeBuildGraphService;
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/employee",method=RequestMethod.GET)
	public @ResponseBody JSONObject employeePage(HttpServletRequest request) throws SQLException
	{
		JSONObject obj = new JSONObject();
		//new EmployeeBulidNodeDao().createEmployeeNode();
		obj.put("name","sample");
		return obj;
	}

		
	@RequestMapping(value="/employeeCreate",method=RequestMethod.POST)	
	public @ResponseBody net.sf.json.JSONObject employeeSave(@RequestParam(value="file") MultipartFile file,@RequestParam("userDetails") String employeeDetails) throws SQLException, Exception
	{
		
		net.sf.json.JSONObject jsonEmployeeDetails = net.sf.json.JSONObject.fromObject(employeeDetails);	
		net.sf.json.JSONObject employee = new net.sf.json.JSONObject();
		employee.put("user",employeeService.saveUser(file,jsonEmployeeDetails));			
		employeeBuildGraphService.createEmployeeNode(jsonEmployeeDetails);
		return employee;
	}
	

	@RequestMapping(value="/employeeProfile",method=RequestMethod.POST)
	public @ResponseBody JSONObject employeeUpdateProfile(@RequestBody JSONObject jsonEmployeeProfile) throws SQLException, UnknownHostException
	{
		JSONObject forEmployeeGraphNode = (JSONObject) jsonEmployeeProfile.clone();
		JSONObject status = employeeService.employeeUpdateProfile(jsonEmployeeProfile);		
		System.out.println("Inside Controller after updated Profile is : "+forEmployeeGraphNode);
		employeeBuildGraphService.createEmployeeProfileNode(forEmployeeGraphNode);
		return status;
	}
	

	@RequestMapping(value="/getemployeeProfile",method=RequestMethod.POST)
	public @ResponseBody BasicDBObject getEmployeeProfile(@RequestBody JSONObject employeeSession) throws SQLException, UnknownHostException
	{
		BasicDBObject employeeProfile = employeeService.getEmployeeProfile(employeeSession);
		
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
  
	
	
}
