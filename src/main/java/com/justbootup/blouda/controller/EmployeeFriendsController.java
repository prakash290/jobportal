package com.justbootup.blouda.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justbootup.blouda.serviceApi.IEmployeeFriendsRequest;

@Controller
public class EmployeeFriendsController {
	
	@Autowired
	IEmployeeFriendsRequest employeeFriendRequest;
	
	@RequestMapping(value="/getFriendsList",method=RequestMethod.POST)	
	public @ResponseBody JSONObject getAllFriendsList(@RequestBody JSONObject queryParams){
		
		return employeeFriendRequest.getFriendsList(queryParams);
		
	}	
	
	@RequestMapping(value="/sendFriendRequest",method=RequestMethod.POST)	
	public @ResponseBody JSONObject sendFriendRequest(@RequestBody JSONObject employeeList){				
		return employeeFriendRequest.sendFriendRequest(employeeList);		
	}
	
	@RequestMapping(value="/getRequestedFriendsCount",method=RequestMethod.POST)	
	public @ResponseBody JSONObject getRequestedFriendsCount(@RequestBody JSONObject currentEmploye){				
		return employeeFriendRequest.getRequestedFriendsCount(currentEmploye);		
	}
	
	@RequestMapping(value="/getRequestedFriendsList",method=RequestMethod.POST)	
	public @ResponseBody JSONObject getRequestedFriendsList(@RequestBody JSONObject currentEmployee){				
		return employeeFriendRequest.getRequestedFriends(currentEmployee);		
	}
	
	@RequestMapping(value="/updateFriendRequest",method=RequestMethod.POST)	
	public @ResponseBody JSONObject updateFriendRequest(@RequestBody JSONObject employeeFriends){				
		return employeeFriendRequest.updateFriendRequest(employeeFriends);		
	}
	
	@RequestMapping(value="/showFriendsList",method=RequestMethod.POST)	
	public @ResponseBody JSONObject showFriendsList(@RequestBody JSONObject friendsList){				
		return employeeFriendRequest.showFriends(friendsList);		
	}
	
	@RequestMapping(value="/updateEmployeeFriendsRestriction",method=RequestMethod.POST)	
	public @ResponseBody JSONObject updateEmployeeFriendsRestriction(@RequestBody JSONObject plan){				
		return employeeFriendRequest.updateEmployeeFriendsRestriction(plan);		
	}
	
	@RequestMapping(value="/sendInmail1",method=RequestMethod.POST)	
	public @ResponseBody JSONObject sendInmail1(@RequestBody JSONObject mailDetails){		
		return employeeFriendRequest.sendInmail(mailDetails);
	}
	
	
}
