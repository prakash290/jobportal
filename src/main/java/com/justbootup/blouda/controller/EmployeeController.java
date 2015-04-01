package com.justbootup.blouda.controller;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import net.sf.mongodb_jdbc_driver.MongoDbDriver;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justbootup.blouda.serviceApi.IEmployeeService;


@Controller
public class EmployeeController {
	
	@Autowired
	private IEmployeeService employeeService;
	
	@RequestMapping(value="/employee",method=RequestMethod.GET)
	public String employeePage(HttpServletRequest request) throws SQLException
	{
		/*MongoDbDriver gh = new MongoDbDriver();
		String URL_SPEC = "jdbc:mongodb://localhost:27017/jobportal";
		Connection con = gh.connect(URL_SPEC, null);
		System.out.println(con);*/
		return "candidate";
	}

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/employeeCreate",method=RequestMethod.POST)
	public @ResponseBody JSONObject employeeSave(@RequestBody JSONObject jsonEmployee) throws SQLException
	{
		JSONObject user = employeeService.saveUser(jsonEmployee);
		JSONObject userObject = new JSONObject();
		userObject.put("user",user);
		return userObject;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/employeeProfile",method=RequestMethod.POST)
	public @ResponseBody JSONObject employeeUpdateProfile(@RequestBody JSONObject jsonEmployeeProfile) throws SQLException, UnknownHostException
	{
		System.out.println(jsonEmployeeProfile);
		JSONObject status = employeeService.employeeUpdateProfile(jsonEmployeeProfile);
		
		return status;
	}
	
  
	
	
}
