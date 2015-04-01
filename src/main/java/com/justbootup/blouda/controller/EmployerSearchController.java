/**
 * 
 */
package com.justbootup.blouda.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.justbootup.blouda.serviceApi.IEmployerSearchService;

/**
 * @author admin
 *
 */

@Controller
public class EmployerSearchController {
	
	@Autowired
	private IEmployerSearchService employerSearchService;
	
	@RequestMapping(value = "/getAllIndustries",method=RequestMethod.GET)
	public JSONObject getAllIndustries(){
		
		JSONObject json=new JSONObject();
		json.put("allIndustriesList", employerSearchService.getAllIndustries());
		System.out.println(json);
		return json;			
	}
	
}


