package com.justbootup.blouda.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justbootup.blouda.serviceApi.IEmployeeSocialRegister;

@Controller
public class EmployeeSocialNetworkRegister {
	
	@Autowired
	IEmployeeSocialRegister employeeSocialNetworks;
	
	@RequestMapping(value="/getLinkedInLoginUrl",method=RequestMethod.GET)	
	public @ResponseBody JSONObject getLinkedInLoginUrl(){
		
		return employeeSocialNetworks.getLinkedInLoginUrl();
		
	}
}	