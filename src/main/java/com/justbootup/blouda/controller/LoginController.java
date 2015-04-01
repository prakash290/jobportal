package com.justbootup.blouda.controller;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.justbootup.blouda.serviceApi.IEmployeeService;

@Controller
public class LoginController {

	@Autowired
	private IEmployeeService userService;
	
	
	/*@RequestMapping("/")
	public String userLogin()
	{
		System.out.println("login page");
		return "login";
	}
	*/
	@RequestMapping(value="/welcome", method = RequestMethod.GET)  
	 public String executeSecurity(ModelMap model, Principal principal ) {  
	   
	  String name = principal.getName();  
	  model.addAttribute("user", name);  
	  
	  return "welcome";  
	   
	 }  
	
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		
		
		return "login";
 
	}
	
	@RequestMapping(value="/loginfailed", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {
 
		model.addAttribute("error", "true");
		return "login";
 
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {
 
		return "login";
 
	}
	
	
	
}
