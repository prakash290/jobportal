/**
 * 
 */
package com.justbootup.blouda.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.justbootup.blouda.util.MongodbConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

/**
 * @author admin
 *
 */

@Repository
public class EmployerSearchDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	EmployerSearchHelperClass queryGenerator = new EmployerSearchHelperClass();
	
	/**
	 * @return allIndutries JSONObject
	 * 
	 */	
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public JSONObject getAllIndustries(){
		
		JSONObject allIndutries = new JSONObject();		
		allIndutries=queryGenerator.groupByResult("industry_type", "String");		
		return allIndutries;		
		
	}
	
	/**
	 * @return allLocations JSONObject
	 * 
	 */	
	public JSONObject getAllLocations(){		
		
		JSONObject allLocations = new JSONObject();		
		allLocations=queryGenerator.groupByResult("prefered_location", "Array");		
		return allLocations;
		
	}
	
	/**
	 * @return allRoles JSONObject
	 * 
	 */	
	public JSONObject getAllRoles(){
		
		JSONObject allRoles = new JSONObject();		
		allRoles=queryGenerator.groupByResult("role", "String");		
		return allRoles;
		
	}
	
	/**
	 * @return allExperience JSONObject
	 * 
	 */	
	@SuppressWarnings({ "unchecked" })
	public JSONObject getAllExperiences()
	
	{		
		MongoClient mongoclient = null;
		MongodbConnection mongodb = new MongodbConnection();
		JSONObject Experiences = new JSONObject();
		
		try{
			
			mongoclient = mongodb.connect();
			
			DB db = mongoclient.getDB("jobportal");
			
			DBCollection collection = db.getCollection("employeeuser");
			
			HashMap<Integer, Integer> ExperienceRanges = new HashMap<Integer, Integer>();
			
			// Ranges for > 0 and < 2 is (0,2)
			
			ExperienceRanges.put(0,2);
			ExperienceRanges.put(2,4);
			ExperienceRanges.put(5,10);
		
			for (Map.Entry<Integer, Integer> entry : ExperienceRanges.entrySet())
			{
			   JSONObject rangeQuery = new JSONObject();
			   JSONObject ranges = new JSONObject();
			   ranges.put("$gt", entry.getKey());
			   ranges.put("$lte", entry.getValue());
			   rangeQuery.put("experience", ranges);
			  			   
			   int result = collection.find(new BasicDBObject(rangeQuery)).count();
			   if(result > 0)
			   Experiences.put(entry.getKey()+","+entry.getValue(), result);
			}
			
		}
		catch(Exception e)
		{
			System.out.println("Exception Raised In HelperClass - groupBy");
			e.printStackTrace();
		}
		finally
		{
			if(mongoclient != null)
			{
				mongoclient.close();
			}
			
		}
	
		return Experiences;
		
	}
	
	/**
	 * @return 
	 * 
	 */
	public JSONObject searchIndustry(JSONObject searchCriteriaIndustry){	
			
		searchCriteriaIndustry.remove("industry_type");
		BasicDBObject match = queryGenerator.queryGeneration(searchCriteriaIndustry);
		System.out.println("DBObject is : "+match);
		JSONObject matchedIndustries = queryGenerator.SearchgroupByResult("industry_type", "String",match);
		System.out.println("Final matched Industries : "+matchedIndustries);
		return matchedIndustries;
	}
	
	
	/**
	 * 
	 */
	public JSONObject searchLocation(JSONObject searchCriteriaLocation){
		
		searchCriteriaLocation.remove("prefered_location");
		
		BasicDBObject match = queryGenerator.queryGeneration(searchCriteriaLocation);
		System.out.println("DBObject is : "+match);
		JSONObject matchedLocations = queryGenerator.SearchgroupByResult("prefered_location", "Array",match);
		System.out.println("Final matched Locations : "+matchedLocations);
		return matchedLocations;	
					
	}
	
	/**
	 * 
	 */
	public JSONObject searchRole(JSONObject searchCriteriaRole){
		
		searchCriteriaRole.remove("role");
		BasicDBObject match = queryGenerator.queryGeneration(searchCriteriaRole);
		System.out.println("DBObject is : "+match);
		JSONObject matchedRoles = queryGenerator.SearchgroupByResult("role", "String",match);
		System.out.println("Final matched Roles : "+matchedRoles);
		return matchedRoles;						
	}
	
	/**
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	public JSONObject searchExperience(JSONObject searchCriteriaExperience){
		
		MongoClient mongoclient = null;
		MongodbConnection mongodb = new MongodbConnection();
		JSONObject Experiences = new JSONObject();		
		
		searchCriteriaExperience.remove("experience");
		BasicDBObject Finalquey = queryGenerator.queryGenerationforemployee(searchCriteriaExperience);
	
		try{
			
			mongoclient = mongodb.connect();
			
			DB db = mongoclient.getDB("jobportal");
			
			DBCollection collection = db.getCollection("employeeuser");
			
			HashMap<Integer, Integer> ExperienceRanges = new HashMap<Integer, Integer>();
			
			// Ranges for > 0 and < 2 is (0,2)
			
			ExperienceRanges.put(0,2);
			ExperienceRanges.put(2,4);
			ExperienceRanges.put(5,10);
		
			for (Map.Entry<Integer, Integer> entry : ExperienceRanges.entrySet())
			{
			   JSONObject ranges = new JSONObject();
			   ranges.put("$gt", entry.getKey());
			   ranges.put("$lte", entry.getValue());
			   
			   Finalquey.append("experience", ranges); 
			   System.out.println("Final quey is : "+Finalquey);
			   
			   int result = collection.find(Finalquey).count();
			   if(result > 0)
			   Experiences.put(entry.getKey()+","+entry.getValue(), result);
			}
			
		}
		catch(Exception e)
		{
			System.out.println("Exception Raised In HelperClass - groupBy");
			e.printStackTrace();
		}
		finally
		{
			if(mongoclient != null)
			{
				mongoclient.close();
			}
			
		}
		return Experiences;

		
						
	}
	
	public BasicDBObject getEmployeeInfo(JSONObject allSearchCriteria) throws UnknownHostException{
						
		BasicDBObject employeeProfiles = queryGenerator.getEmployeeInfos(allSearchCriteria);
		System.out.println("All employee Details : "+employeeProfiles);	
		
		return employeeProfiles;				
	}
	
	public BasicDBObject getEmployeeInfoPage(JSONObject allSearchCriteriaPage) throws Exception{
		
		BasicDBObject employeeProfilesPage = queryGenerator.getEmployeeInfoPage(allSearchCriteriaPage);		
		return employeeProfilesPage;				
	}
	
	/**
	 * @throws UnknownHostException 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public BasicDBObject getEmployeeInfoforchart(JSONObject allSearchCriteriaforChart) throws Exception{
		
		BasicDBObject employeeProfilesChartData = queryGenerator.getEmployeeInfoPage(allSearchCriteriaforChart);		
		
		ArrayList arrayList=(ArrayList) employeeProfilesChartData.get("result");
		
		
		// Converting CANVAS JSON format
        JSONArray chartformat = queryGenerator.chartJsonConversion(arrayList);
        BasicDBObject result =new BasicDBObject().append("result",chartformat);
		
		//{ "result" : [ { "_id" : { "$oid" : "5513991cf2ef27427f5638e2"} , "name" : "karthi" , "industry_type" : "Accounts" , "prefered_location" : [ "coimbatore"] , "description" : "2 years experience in accounts" , "employeer_type" : "Experience" , "experience" : 2.0 , "role" : "Accountant"} , { "_id" : { "$oid" : "55139920f2ef27427f5638e4"} , "name" : "Rahul" , "industry_type" : "Accounts" , "prefered_location" : [ "chennai"] , "description" : "Tally known" , "employeer_type" : "Fresher" , "role" : "Accountant"} , { "_id" : { "$oid" : "5513a2ecf2ef27427f5638e7"} , "name" : "Banu" , "industry_type" : "Accounts" , "prefered_location" : [ "delhi"] , "description" : "Tally known" , "employeer_type" : "Fresher" , "role" : "Medical"}]}
		
		return result;						
	}
	
	

}
