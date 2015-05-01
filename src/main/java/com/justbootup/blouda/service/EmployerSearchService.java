/**
 * 
 */
package com.justbootup.blouda.service;

import java.net.UnknownHostException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justbootup.blouda.dao.EmployerSearchDao;
import com.justbootup.blouda.serviceApi.IEmployerSearchService;
import com.mongodb.BasicDBObject;

/**
 * @author admin
 *
 */
@Service
public class EmployerSearchService implements IEmployerSearchService {

	@Autowired
	private EmployerSearchDao employerSearchDao;

	@Override
	public JSONObject getAllIndustries() throws UnknownHostException {
		
		return employerSearchDao.getAllIndustries();
	}

	@Override
	public JSONObject getAllLocations() throws UnknownHostException {
		
		return employerSearchDao.getAllLocations();
	}

	@Override
	public JSONObject getAllRoles() throws UnknownHostException {
		
		return employerSearchDao.getAllRoles();
	}

	@Override
	public JSONObject getAllExperiences() throws UnknownHostException {
		
		return employerSearchDao.getAllExperiences();
	}

	@Override
	public JSONObject getSearchIndustry(JSONObject searchCriteriaIndustry) throws UnknownHostException {
		
		return employerSearchDao.searchIndustry(searchCriteriaIndustry);
	}

	@Override
	public JSONObject getSearchLocation(JSONObject searchCriteriaLocation) throws UnknownHostException {		
		
		return employerSearchDao.searchLocation(searchCriteriaLocation);
	}

	@Override
	public JSONObject getSearchRole(JSONObject searchCriteriaRole) throws UnknownHostException {
		
		return employerSearchDao.searchRole(searchCriteriaRole);
	}

	@Override
	public JSONObject getSearchExperience(JSONObject searchCriteriaExperience) throws UnknownHostException {
		
		return employerSearchDao.searchExperience(searchCriteriaExperience);
	}

	@Override
	public BasicDBObject getEmployeeInfo(JSONObject allSearchCriteria) throws UnknownHostException {
		
		
		return employerSearchDao.getEmployeeInfo(allSearchCriteria);
	}

	@Override
	public BasicDBObject getEmployeeInfoPage(JSONObject allSearchCriteriaPage) throws Exception {
		
		return employerSearchDao.getEmployeeInfoPage(allSearchCriteriaPage);
	}

	@Override
	public BasicDBObject getEmployeeInfoforchart(JSONObject allSearchCriteriaforChart)	throws Exception {
		
		return employerSearchDao.getEmployeeInfoforchart(allSearchCriteriaforChart);
	}

	
	

}
