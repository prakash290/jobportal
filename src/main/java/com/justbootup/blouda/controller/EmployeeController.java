package com.justbootup.blouda.controller;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justbootup.blouda.serviceApi.IUserservice;


@Controller
public class EmployeeController {
	
	@Autowired
	private IUserservice userService;
	
	@RequestMapping(value="/employee",method=RequestMethod.GET)
	public String employeePage(HttpServletRequest request)
	{
		return "candidate";
	}

	
	@RequestMapping(value="/employeeCreate",method=RequestMethod.POST)
	public @ResponseBody String employeeSave(@RequestBody JSONObject jsonEmployee)
	{
		System.out.println(jsonEmployee);
		userService.saveUser(jsonEmployee);
		return "success";
	}
	

	
	
}
