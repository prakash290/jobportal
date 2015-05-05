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
	public @ResponseBody String getAllFriendsList(@RequestBody JSONObject queryParams){
		employeeFriendRequest.getFriendsList(queryParams);
		return "Sample";		
	}
}
