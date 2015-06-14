package com.justbootup.blouda.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.justbootup.blouda.service.EmployeeMailService;
import com.mongodb.BasicDBObject;

@RestController
public class EmployeeMailController {
	@Autowired
	private EmployeeMailService employeeemailservice;
	
	@RequestMapping(value="/sendInmail",method=RequestMethod.POST)	
	public @ResponseBody JSONObject sendInmail(@RequestBody JSONObject mailDetails){		
		return employeeemailservice.sendInmail(mailDetails);
	}
	
	@RequestMapping(value="/getInboxMails",method=RequestMethod.POST)	
	public @ResponseBody BasicDBObject getInboxMails(@RequestBody JSONObject employee){		
		return employeeemailservice.getInboxMails(employee);
	}
	
	@RequestMapping(value="/getsendMails",method=RequestMethod.POST)	
	public @ResponseBody BasicDBObject getsendMails(@RequestBody JSONObject employee){		
		return employeeemailservice.getsendMails(employee);
	}
	
	@RequestMapping(value="/reply",method=RequestMethod.POST)	
	public @ResponseBody JSONObject sendreply(@RequestBody JSONObject employee){		
		return employeeemailservice.sendReply(employee);
	}
	
	
}
