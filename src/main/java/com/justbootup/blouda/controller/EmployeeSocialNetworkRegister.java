package com.justbootup.blouda.controller;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.justbootup.blouda.serviceApi.IEmployeeSocialRegister;

@Controller
public class EmployeeSocialNetworkRegister {
	
	@Autowired
	IEmployeeSocialRegister employeeSocialNetworks;
	JSONObject socialNetworkResult = new JSONObject();		
	JSONObject parseEmployeeEmail = new JSONObject();
	
	@RequestMapping(value="/getLinkedInLoginUrl",method=RequestMethod.GET)	
	public @ResponseBody JSONObject getLinkedInLoginUrl(){		
		return employeeSocialNetworks.getLinkedInLoginUrl();		
	}
	
	
	@RequestMapping(value="/linkedInCallback",method=RequestMethod.GET)	
	public String verfiyLinkedUser(@RequestParam HashMap<String, String> authorizationParams){
		System.out.println("Social Network Result is : "+socialNetworkResult);
		socialNetworkResult = employeeSocialNetworks.createNewLinkedInEmployee(authorizationParams);	
		parseEmployeeEmail  = employeeSocialNetworks.employeeCredentials(socialNetworkResult);
		JSONObject isEmployeeExists = employeeSocialNetworks.checkisEmployeeExists(parseEmployeeEmail,socialNetworkResult);
		String Url = "";
		if((boolean) isEmployeeExists.get("isEmployeeExists"))
		{
			Url = "loginRedirect";
		}
		else
		{
			Url = "socialAPIStatus";
		}
		return "redirect:/#/"+Url;
	}
	
	@RequestMapping(value="/getGoogleLoginUrl",method=RequestMethod.GET)	
	public @ResponseBody JSONObject getGoogleLoginUrl(){		
		return employeeSocialNetworks.getGoogleLoginUrl();		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/googleAPICallback",method=RequestMethod.GET)	
	public String verfiyGoogleUser(@RequestParam HashMap<String, String> googleauthorizationParams){	
		System.out.println("Social Network Result is : "+socialNetworkResult);
		socialNetworkResult =  employeeSocialNetworks.createNewEmployeeUsingGoogle(googleauthorizationParams);
		socialNetworkResult.put("socialnetworkname", "google");
		parseEmployeeEmail  = employeeSocialNetworks.employeeCredentials(socialNetworkResult);
		JSONObject isEmployeeExists = employeeSocialNetworks.checkisEmployeeExists(parseEmployeeEmail,socialNetworkResult);
		String Url = "";
		if((boolean) isEmployeeExists.get("isEmployeeExists"))
		{
			Url = "loginRedirect";
		}
		else
		{
			Url = "socialAPIStatus";
		}
		return "redirect:/#/"+Url;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/emloyeefaceBookLogin",method=RequestMethod.POST)
	public @ResponseBody JSONObject emloyeefacebookLogin(@RequestBody JSONObject facebookDetails) throws Exception
	{	
		System.out.println("FacebookDetails is : "+facebookDetails);
		socialNetworkResult = employeeSocialNetworks.generateFacebooklongliveToken(facebookDetails);
		socialNetworkResult.put("socialnetworkname", "facebook");
		parseEmployeeEmail  = employeeSocialNetworks.employeeCredentials(socialNetworkResult);
		JSONObject isEmployeeExists = employeeSocialNetworks.checkisEmployeeExists(parseEmployeeEmail,socialNetworkResult);
		if((boolean) isEmployeeExists.get("isEmployeeExists"))
		{
			parseEmployeeEmail.put("newEmployee", true);
			return parseEmployeeEmail;
		}
		else
		{
			parseEmployeeEmail.put("newEmployee", false);
			return parseEmployeeEmail;
		}				
	}	
	
	@RequestMapping(value="/SocialNetworkDetails",method=RequestMethod.GET)
	public @ResponseBody JSONObject socialNetworkgetEmployeeCredential() throws Exception
	{		
		return parseEmployeeEmail;
	}
	
	@RequestMapping(value="/createNewSocialAccount",method=RequestMethod.POST)
	public @ResponseBody JSONObject createEmployeeUsingSocial(@RequestBody JSONObject employeeCredentials) throws Exception
	{			
		return employeeSocialNetworks.createNewEmployeeUsingSocialNetwork(employeeCredentials, socialNetworkResult);
	}
	
}	

