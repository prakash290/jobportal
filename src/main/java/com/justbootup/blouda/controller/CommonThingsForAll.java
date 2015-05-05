package com.justbootup.blouda.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justbootup.blouda.serviceApi.ICommonThingsForAll;
import com.mongodb.BasicDBObject;

@Controller
public class CommonThingsForAll {
	
	@Autowired
	private ICommonThingsForAll commonThings;
	
	/**
	 * This api returns value is used for drop down list of friend search page.
	 * 
	 * @return
	 */
	@RequestMapping(value="/getAllForFriendsRequest",method=RequestMethod.GET)
	public @ResponseBody BasicDBObject friendSearchViewData(){
	
		BasicDBObject common = commonThings.friendSearchViewData();		
		return common;
	}
}

