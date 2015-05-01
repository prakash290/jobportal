/**
 * 
 */
package com.justbootup.blouda.serviceApi;

import java.net.UnknownHostException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;


/**
 * @author admin
 *
 */
public interface IEmployerSearchService {
	
	// These methods for inital page loading
	public JSONObject getAllIndustries() throws UnknownHostException;
	public JSONObject getAllLocations() throws UnknownHostException;
	public JSONObject getAllRoles() throws UnknownHostException;
	public JSONObject getAllExperiences() throws UnknownHostException;
	
	// These methods for sorting in inital page
	public JSONObject getSearchIndustry(JSONObject searchCriteria) throws UnknownHostException;
	public JSONObject getSearchLocation(JSONObject searchCriteria) throws UnknownHostException;
	public JSONObject getSearchRole(JSONObject searchCriteria) throws UnknownHostException;
	public JSONObject getSearchExperience(JSONObject searchCriteria) throws UnknownHostException;
	
	// These method for matched candidates
	public BasicDBObject getEmployeeInfo(JSONObject searchCriteria) throws UnknownHostException;
	public BasicDBObject getEmployeeInfoPage(JSONObject allSearchCriteriaPage) throws Exception;
	
	// This method for drawChart
	public BasicDBObject getEmployeeInfoforchart(JSONObject allSearchCriteriaPage) throws Exception;
}
