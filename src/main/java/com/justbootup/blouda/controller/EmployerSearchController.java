/**
 * 
 */
package com.justbootup.blouda.controller;

import java.net.UnknownHostException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justbootup.blouda.serviceApi.IEmployerSearchService;
import com.justbootup.blouda.util.RServe;
import com.mongodb.BasicDBObject;

/**
 * @author admin
 *
 */
@SuppressWarnings("unchecked")
@Controller
public class EmployerSearchController {
	
	@Autowired
	private IEmployerSearchService employerSearchService;

	
	
	@RequestMapping(value = "/getAllIndustries",method=RequestMethod.GET)
	public @ResponseBody JSONObject allIndustries() throws UnknownHostException{
		
		JSONObject json= employerSearchService.getAllIndustries();		
		return json;			
	}	
	
	@RequestMapping(value = "/getAllLocations",method=RequestMethod.GET)
	public @ResponseBody JSONObject allLocations() throws UnknownHostException{
		
		JSONObject json = employerSearchService.getAllLocations();		
		return json;			
	}
	
	@RequestMapping(value = "/getAllRoles",method=RequestMethod.GET)
	public @ResponseBody JSONObject allRoles() throws UnknownHostException{
		
		JSONObject json = employerSearchService.getAllRoles();
	
		return json;			
	}
	
	@RequestMapping(value = "/getAllExperiences",method=RequestMethod.GET)
	public @ResponseBody JSONObject allExperiences() throws UnknownHostException{
		
		JSONObject json = employerSearchService.getAllExperiences();	
		return json;			
	}
	
	@RequestMapping(value = "/getSearchIndustry",method=RequestMethod.POST)
	public @ResponseBody JSONObject searchIndustry(@RequestBody JSONObject searchCriteriaIndustry) throws UnknownHostException{
		
		JSONObject json = employerSearchService.getSearchIndustry(searchCriteriaIndustry);							
		return json;			
	}
	
	@RequestMapping(value = "/getSearchLocation",method=RequestMethod.POST)
	public @ResponseBody JSONObject searchLocation(@RequestBody JSONObject searchCriteriaLocation) throws UnknownHostException{		
		
		JSONObject json=  employerSearchService.getSearchLocation(searchCriteriaLocation);	
		return json;			
	}
	
	@RequestMapping(value = "/getSearchRole",method=RequestMethod.POST)
	public @ResponseBody JSONObject searchRole(@RequestBody JSONObject searchCriteriaRole) throws UnknownHostException{
				
		JSONObject json =  employerSearchService.getSearchRole(searchCriteriaRole);			
		return json;			
	}
	
	@RequestMapping(value = "/getSearchExperience",method=RequestMethod.POST)
	public @ResponseBody JSONObject searchExperience(@RequestBody JSONObject searchCriteriaExperience) throws UnknownHostException{		
		
		JSONObject json = employerSearchService.getSearchExperience(searchCriteriaExperience);
		return json;			
	}
	
	
	@RequestMapping(value = "/getEmployeeslist",method=RequestMethod.POST)
	public @ResponseBody BasicDBObject getEmployeeslist(@RequestBody JSONObject allSearchCriteria) throws UnknownHostException{
		
		BasicDBObject userprofiles = employerSearchService.getEmployeeInfo(allSearchCriteria);	
		return userprofiles;			
	}
	
	@RequestMapping(value = "/getEmployeeslistPage",method=RequestMethod.POST)
	public @ResponseBody BasicDBObject getEmployeeslistPage(@RequestBody JSONObject allSearchCriteriaPage) throws Exception{
		
		BasicDBObject userprofilePage = employerSearchService.getEmployeeInfoPage(allSearchCriteriaPage);	
		return userprofilePage;			
	}
	
	@RequestMapping(value = "/getEmployeesChartData",method=RequestMethod.POST)
	public @ResponseBody BasicDBObject getEmployeesChartData(@RequestBody JSONObject allSearchCriteriaforChart) throws Exception{
		
		BasicDBObject userprofilePage = employerSearchService.getEmployeeInfoforchart(allSearchCriteriaforChart);	
		return userprofilePage;			
	}
	
	@RequestMapping(value = "/getRChart",method=RequestMethod.GET)
	public @ResponseBody BasicDBObject getRChart() throws Exception{
			BasicDBObject dbObject = new BasicDBObject();
			//response.setContentType("image/jpeg");
			//response.getOutputStream().write(new RServe().readImagefile());
			dbObject.put("imagepath",new RServe().readImagefile());
			return dbObject;
	}
	
	@RequestMapping(value = "/Rchart",method=RequestMethod.GET)
	public void Rchart(HttpServletResponse response) throws Exception{
			
			
			//response.setContentType("image/jpeg");
			//response.getOutputStream().write(new RServe().readImagefile());
		
			response.setContentType("image/jpeg");
			response.getOutputStream().write(new RServe().readImagefile());
		
			
			response.flushBuffer();
	}
	
}


