/**
 * 
 */
package com.justbootup.blouda.dao;

import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;

import org.bson.NewBSONDecoder;
import org.hibernate.validator.constraints.Length;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.rosuda.REngine.REXPMismatchException;

import com.justbootup.blouda.util.*;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


/**
 * @author Prakash.A
 * Mar 19, 2015
 * HelperClass.java
 */
public class EmployerSearchHelperClass {
	
	MongodbConnection mongodb=new MongodbConnection();
	
	
	/**
	 *  Common method for groupby operation except experience ( Range Related Fields )
	 * 
	 * @param attributeName
	 * @param attributeValue
	 * @return Aggregation GroupBy Object
	 */
	public DBObject groupBy(String attributeName)
	{
				
		BasicDBObject matchValue=new BasicDBObject("_id", "$"+attributeName);
		matchValue.put("count",new BasicDBObject("$sum", 1));		
		DBObject group= new BasicDBObject("$group", matchValue);	
		
		return group;		
	}
	
	@SuppressWarnings({ "unchecked" })
	public JSONObject groupByResult(Object attributeName,String attributeType)
	{
		MongoClient mongoclient = null;
		
		JSONObject groupByResults = new JSONObject();
		
		try{
			
			mongoclient = mongodb.connect();
			
			DB db = mongoclient.getDB("jobportal");
			
			DBCollection collection = db.getCollection("employeeuser");
			
			DBObject groupBy = null;
			List<DBObject> pipeline = null;
			
			if(attributeType.equals("String"))
			{
				// build the $projection operation
				JSONObject field=new JSONObject();
				field.put(attributeName, 1);
				field.put("_id",0 );			
				DBObject project = new BasicDBObject("$project", field );
				
				// Now the $group operation
				BasicDBObject groupFields  = new BasicDBObject("_id", "$"+attributeName);
				groupFields.put("count",new BasicDBObject("$sum", 1));				
				groupBy = new BasicDBObject("$group", groupFields);
				
				
				// run aggregation
				pipeline = Arrays.asList(project, groupBy );
				
			}
			else
			{
				
				// build the $projection operation
				JSONObject field=new JSONObject();
				field.put(attributeName, 1);
				field.put("_id",0 );			
				DBObject project = new BasicDBObject("$project", field );
				
				// Unwind for Array Field
				DBObject unwind = new BasicDBObject("$unwind", "$"+attributeName );				
				
				// Now the $group operation
				BasicDBObject groupFields  = new BasicDBObject("_id", "$"+attributeName);
				groupFields.put("count",new BasicDBObject("$sum", 1));				
				groupBy = new BasicDBObject("$group", groupFields);
				
				// run aggregation
				pipeline = Arrays.asList(project, unwind, groupBy);				
				
			}
			
			AggregationOutput results = collection.aggregate(pipeline);
			Object[] values = new Object[2];
			
			for (DBObject result : results.results()) {
				int i=0;
				for(String keys:result.keySet())
				{
					values[i]=result.get(keys);					
					i++;
				}
				groupByResults.put(values[0], values[1]);				
			}
			
			System.out.println(groupByResults);
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
	
		return groupByResults;
				
	}
	
	
	@SuppressWarnings("unchecked")
	public BasicDBObject queryGeneration(JSONObject queryParams){
		
	
		HashMap< String, Object> forIteration = new  HashMap<String, Object>(queryParams);		
		
		//This hashmap object is used for hold an mongodb schemas. Like { field :value , field : [arrayValue], field : [ {nested object }]  }
		HashMap<String, String> attributeTypes=new HashMap<String,String>();
		
		attributeTypes.put("industry_type", "fieldValue");
		attributeTypes.put("prefered_location", "arrayValue");
		attributeTypes.put("role", "fieldValue");
		attributeTypes.put("experience", "rangeValue");
		
		
		BasicDBObject eachFieldQuery = new BasicDBObject();
		
		
		for (Map.Entry<String, Object> entry : forIteration.entrySet()) {		
		 
			String Keyname=	entry.getKey();			
			String atype=attributeTypes.get(Keyname);			
			switch (atype) {
			case "fieldValue":
			{
							
				ArrayList<String> particularArray = (ArrayList<String>) forIteration.get(Keyname);
				
				if(particularArray.size() > 1) // This is for OR condition 1 Attribute with Multiple Values
				{
					
					// Form like this $or: [ {"role" : "Medical"}, {"role" : "Accountant"} ]			
					org.json.simple.JSONArray orOperator = new org.json.simple.JSONArray();
					for(int i=0; i<particularArray.size(); i++)
					{
						JSONObject obj = new JSONObject();
						obj.put(Keyname,particularArray.get(i));
						orOperator.add(obj);
					}
					
					eachFieldQuery.append("$or",orOperator);
				}
				else if(particularArray.size() == 1)
				{
					// Form like this {"employeer_type" : "Fresher"} single object						
					eachFieldQuery.append(Keyname, particularArray.get(0));
				}
				else
				{
					// This is empty array object so we omit this attribute
				}
				
				
				
				break;
			}	
			case "arrayValue":
			{
				
				ArrayList<String> particularArray = (ArrayList<String>) forIteration.get(Keyname);				
				if(particularArray.size() > 1) // This is for OR condition 1 Attribute with Multiple Values
				{
					
					// Form like this "field_name" :  { $in: [ "coimbatore", "delhi" ] }			
					JSONObject inArray = new JSONObject();
					org.json.simple.JSONArray orOperator = new org.json.simple.JSONArray();
					for(int i=0; i<particularArray.size(); i++)
					{				
						orOperator.add(particularArray.get(i));
					}
					inArray.put("$in",orOperator);						
					eachFieldQuery.append(Keyname, inArray);
								
				}
				else if(particularArray.size() == 1)
				{
					// Form like this {"employeer_type" : "Fresher"} single object							
					eachFieldQuery.append(Keyname, particularArray.get(0));
				}
				else
				{
					// This is empty array object so we omit this attribute
				}			
				
				
				
				
				break;
			}
			case "rangeValue":
			{
				
				ArrayList<String> particularArray = (ArrayList<String>) forIteration.get(Keyname);
				
				if(particularArray.size() > 1) // This is for OR condition 1 Attribute with Multiple Values
				{
					
					// Form like this "field_name" :{ $or: [ {"experience" : { $gt : 0, $lte: 2 } }, {"experience" : { $lt : 2, $gt: 5 } } ]}			
					org.json.simple.JSONArray orOperator = new org.json.simple.JSONArray();
					
					for(int i=0; i<particularArray.size(); i++)
					{				
						String range=(String) particularArray.get(i);		
						JSONObject obj = new JSONObject();
						
						String[] spliter = range.split(",");				
						
						JSONObject ranges = new JSONObject();
						ranges.put("$gt", Integer.parseInt(spliter[0]));
						ranges.put("$lte", Integer.parseInt(spliter[1]));			   
						
						obj.put(Keyname, ranges);		
						
						orOperator.add(obj);
					}					
							
					eachFieldQuery.append("$or", orOperator);
				}
				else if(particularArray.size() == 1)
				{
					// Form like this {"employeer_type" : "Fresher"} single object			
					String range = (String) particularArray.get(0);
					String[] spliter = range.split(",");
					
					// for {$gt:0,$lte:2}			
					JSONObject ranges = new JSONObject();
					ranges.put("$gt", Integer.parseInt(spliter[0]));
					ranges.put("$lte", Integer.parseInt(spliter[1]));						
					
					eachFieldQuery.append(Keyname, ranges);
				}
				else
				{
					// This is empty array object so we omit this attribute
				}	
				
				
				
				break;	
			}	
			default:
				break;
			}
			
		}	
		
		BasicDBObject finalGroupByQuery = new BasicDBObject("$match", eachFieldQuery);			
		
		return finalGroupByQuery;
	}
	
	
	@SuppressWarnings({ "unchecked" })
	public JSONObject SearchgroupByResult(Object attributeName,String attributeType,BasicDBObject match)
	{
		MongoClient mongoclient = null;
		
		JSONObject groupByResults = new JSONObject();
		
		try{
			
			mongoclient = mongodb.connect();
			
			DB db = mongoclient.getDB("jobportal");
			
			DBCollection collection = db.getCollection("employeeuser");
			
			DBObject groupBy = null;
			List<DBObject> pipeline = null;
			
			if(attributeType.equals("String"))
			{
				// build the $projection operation
				JSONObject field=new JSONObject();
				field.put(attributeName, 1);
				field.put("_id",0 );				
				
				// Now the $group operation
				BasicDBObject groupFields  = new BasicDBObject("_id", "$"+attributeName);
				groupFields.put("count",new BasicDBObject("$sum", 1));				
				groupBy = new BasicDBObject("$group", groupFields);
				
				System.out.println("groupBy is : "+groupBy+","+match);			
				// run aggregation
				pipeline = Arrays.asList(match, groupBy );
				
			}
			else
			{
				
				
				// Unwind for Array Field
				DBObject unwind = new BasicDBObject("$unwind", "$"+attributeName );		
				
				// Now the $group operation
				BasicDBObject groupFields  = new BasicDBObject("_id", "$"+attributeName);
				groupFields.put("count",new BasicDBObject("$sum", 1));				
				groupBy = new BasicDBObject("$group", groupFields);
				
				// run aggregation
				pipeline = Arrays.asList(match,unwind, groupBy);				
				
			}
			
			AggregationOutput results = collection.aggregate(pipeline);
			Object[] values = new Object[2];
			
			for (DBObject result : results.results()) {
				
				int i=0;
				for(String keys:result.keySet())
				{
					values[i]=result.get(keys);					
					i++;
				}
				groupByResults.put(values[0], values[1]);				
			}
			
			System.out.println(groupByResults);
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
	
		return groupByResults;
				
	}
	
	
	public BasicDBObject getEmployeeInfos(JSONObject allSearchCriteria) throws UnknownHostException
	{
		MongoClient mongoclient = null;
		MongodbConnection mongodb = new MongodbConnection();
		
		mongoclient = mongodb.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("employeeuser");
		
		BasicDBObject queryParams = queryGenerationforemployee(allSearchCriteria);		
		
		
		DBCursor cursor = collection.find(queryParams);		
		List<DBObject> returnObject = cursor.toArray();
		BasicDBObject employeeProfiles = new BasicDBObject();
		employeeProfiles.append("result", returnObject); 		
		return employeeProfiles;
	}
	
	
	public BasicDBObject getEmployeeInfoPage(JSONObject allSearchCriteriaPage) throws Exception
	{
		MongoClient mongoclient = null;
		MongodbConnection mongodb = new MongodbConnection();
		
		mongoclient = mongodb.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("employeeuser");
		
		int beginning = (int) allSearchCriteriaPage.get("begin");
		int end = (int) allSearchCriteriaPage.get("end");
		
		allSearchCriteriaPage.remove("begin");
		allSearchCriteriaPage.remove("end");
		
		BasicDBObject queryParams = queryGenerationforemployee(allSearchCriteriaPage);		
		
		
		DBCursor cursor = collection.find(queryParams).skip(beginning).limit(end);
		//new RServe().readImagefile();
		
		List<DBObject> employeeProfilesPage = cursor.toArray();
		
		BasicDBObject finalemployeeProfiles = new BasicDBObject();
		finalemployeeProfiles.append("result", employeeProfilesPage); 			
		return finalemployeeProfiles;
	}
	
	
	@SuppressWarnings("unchecked")
	public BasicDBObject queryGenerationforemployee(JSONObject queryParams){
		
	
		HashMap< String, Object> forIteration = new  HashMap<String, Object>(queryParams);
		
		
		//This hashmap object is used for hold an mongodb schemas. Like { field :value , field : [arrayValue], field : [ {nested object }]  }
		HashMap<String, String> attributeTypes=new HashMap<String,String>();
		
		attributeTypes.put("industry_type", "fieldValue");
		attributeTypes.put("prefered_location", "arrayValue");
		attributeTypes.put("role", "fieldValue");
		attributeTypes.put("experience", "rangeValue");
		
		
		BasicDBObject eachFieldQuery = new BasicDBObject();
		
		
		for (Map.Entry<String, Object> entry : forIteration.entrySet()) {		
		 
			String Keyname=	entry.getKey();			
			String atype=attributeTypes.get(Keyname);			
			switch (atype) {
			case "fieldValue":
			{
							
				ArrayList<String> particularArray = (ArrayList<String>) forIteration.get(Keyname);
				
				if(particularArray.size() > 1) // This is for OR condition 1 Attribute with Multiple Values
				{
					
					// Form like this $or: [ {"role" : "Medical"}, {"role" : "Accountant"} ]			
					org.json.simple.JSONArray orOperator = new org.json.simple.JSONArray();
					for(int i=0; i<particularArray.size(); i++)
					{
						JSONObject obj = new JSONObject();
						obj.put(Keyname,particularArray.get(i));
						orOperator.add(obj);
					}
					
					eachFieldQuery.append("$or",orOperator);
				}
				else if(particularArray.size() == 1)
				{
					// Form like this {"employeer_type" : "Fresher"} single object						
					eachFieldQuery.append(Keyname, particularArray.get(0));
				}
				else
				{
					// This is empty array object so we omit this attribute
				}
				
				
				
				break;
			}	
			case "arrayValue":
			{
				
				ArrayList<String> particularArray = (ArrayList<String>) forIteration.get(Keyname);				
				if(particularArray.size() > 1) // This is for OR condition 1 Attribute with Multiple Values
				{
					
					// Form like this "field_name" :  { $in: [ "coimbatore", "delhi" ] }			
					JSONObject inArray = new JSONObject();
					org.json.simple.JSONArray orOperator = new org.json.simple.JSONArray();
					for(int i=0; i<particularArray.size(); i++)
					{				
						orOperator.add(particularArray.get(i));
					}
					inArray.put("$in",orOperator);						
					eachFieldQuery.append(Keyname, inArray);
								
				}
				else if(particularArray.size() == 1)
				{
					// Form like this {"employeer_type" : "Fresher"} single object							
					eachFieldQuery.append(Keyname, particularArray.get(0));
				}
				else
				{
					// This is empty array object so we omit this attribute
				}			
				
				
				
				
				break;
			}
			case "rangeValue":
			{
				
				ArrayList<String> particularArray = (ArrayList<String>) forIteration.get(Keyname);
				
				if(particularArray.size() > 1) // This is for OR condition 1 Attribute with Multiple Values
				{
					
					// Form like this "field_name" :{ $or: [ {"experience" : { $gt : 0, $lte: 2 } }, {"experience" : { $lt : 2, $gt: 5 } } ]}			
					org.json.simple.JSONArray orOperator = new org.json.simple.JSONArray();
					
					for(int i=0; i<particularArray.size(); i++)
					{				
						String range=(String) particularArray.get(i);		
						JSONObject obj = new JSONObject();
						
						String[] spliter = range.split(",");				
						
						JSONObject ranges = new JSONObject();
						ranges.put("$gt", Integer.parseInt(spliter[0]));
						ranges.put("$lte", Integer.parseInt(spliter[1]));			   
						
						obj.put(Keyname, ranges);		
						
						orOperator.add(obj);
					}					
							
					eachFieldQuery.append("$or", orOperator);
				}
				else if(particularArray.size() == 1)
				{
					// Form like this {"employeer_type" : "Fresher"} single object			
					String range = (String) particularArray.get(0);
					String[] spliter = range.split(",");
					
					// for {$gt:0,$lte:2}			
					JSONObject ranges = new JSONObject();
					ranges.put("$gt", Integer.parseInt(spliter[0]));
					ranges.put("$lte", Integer.parseInt(spliter[1]));						
					
					eachFieldQuery.append(Keyname, ranges);
				}
				else
				{
					// This is empty array object so we omit this attribute
				}	
				
				
				
				break;	
			}	
			default:
				break;
			}
			
		}				
		
		return eachFieldQuery;
	}
	
	/**
	 * 
	 * @param userprofiles
	 * @return JSON data of userprofile. This json fomat is for CANVAS chart.
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public JSONArray chartJson( JSONArray allProfiles ) {
		
		// UserProfile Chart Attributes 
		ArrayList<String> profileDetails=new ArrayList<String>();
		profileDetails.add("sslc_coursename");
		profileDetails.add("hsc_coursename");
		profileDetails.add("ug_coursename");
		profileDetails.add("pg_coursename");
		
		JSONArray chartFormated=new JSONArray();
		
		for(int i=0;i<allProfiles.size();i++)
		{
			
			HashMap<Object, Object> user=(JSONObject) allProfiles.get(i);			
			
			
			JSONArray singleprofileData=new JSONArray();
			JSONObject finalProfile=new JSONObject();
			
			finalProfile.put("name",user.get("name"));
			finalProfile.put("userid",user.get("user_id ") );
			
			// Setting X axis interval  
			int interval=2;
			
			for(String singledetails:profileDetails)
			{
				for (Entry<Object, Object> entry : user.entrySet()) {				
					if(entry.getKey().equals(singledetails))
					  {
						   JSONObject dataObj=new JSONObject();
						   JSONArray yAxisDuration=new JSONArray();
						   String keyname=singledetails.substring(0,singledetails.indexOf("_") + 1);						   
						   dataObj.put("x",interval);
						   interval=interval+2;
						   yAxisDuration.add(Integer.valueOf((String) user.get(keyname+"from")));
						   yAxisDuration.add(Integer.valueOf((String) user.get(keyname+"to")));
						   dataObj.put("y",yAxisDuration);	
						   dataObj.put("label",user.get(keyname+"coursename"));
						   singleprofileData.add(dataObj);
					  }						    
				}			
			}
			finalProfile.put("data",singleprofileData);	
			
			chartFormated.add(finalProfile);
			
		}
		
		return chartFormated;
	}
	
	
	/**
	 * 
	 * @param userprofiles
	 * @return JSON data of userprofile. This json fomat is for CANVAS chart.
	 * @throws REXPMismatchException 
	 * 
	 */
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public JSONArray chartJsonConversion( ArrayList allProfiles ) throws Exception {
		
		//System.out.println(allProfiles);
		// UserProfile Chart Attributes 
		ArrayList<String> profileAttributes = new ArrayList<String>();		
		profileAttributes.add("education");
		profileAttributes.add("workingexperience");	
		
		ArrayList<String> educationFields = new ArrayList<String>();
		educationFields.add("ug_coursename");
		educationFields.add("pg_coursename");
		educationFields.add("doctorate");
		
		ArrayList<String> workingexperienceFields = new ArrayList<String>();
		workingexperienceFields.add("current_companyname");
		workingexperienceFields.add("previous1_companyname");
		workingexperienceFields.add("previous2_companyname");
		workingexperienceFields.add("previous3_companyname");		
		
		
		JSONArray chartFormated=new JSONArray();
		
		for(int i=0;i<allProfiles.size();i++)
		{
			BasicDBObject singleObjectfromArray = new BasicDBObject();
			singleObjectfromArray = (BasicDBObject) allProfiles.get(i);
			HashMap<String,Object> user=singleObjectfromArray;			
			
			JSONArray singleprofileData=new JSONArray();
			JSONObject finalProfile=new JSONObject();
			//All employee Details : { "result" : [ { "_id" : { "$oid" : "5513991ef2ef27427f5638e3"} , "name" : "Arun" , "industry_type" : "IT" , "prefered_location" : [ "bangalore"] , "skill_set" : [ "PHP" , "Web Designer"] , "employeer_type" : "Fresher" , "role" : "Software Developer" , "education" : [ { "ug_course_name" : "B.E[C.SC]" , "ug_from" : 2000.0 , "ug_to" : 2004.0} , { "pg_course_name" : "M.E[C.SC]" , "pg_from" : 2005.0 , "pg_to" : 2008.0}] , "workingexperience" : [ { "currentcompany_name" : "JustBootUp" , "JoinDate" : "01-01-2015"}]} , { "_id" : { "$oid" : "55220d0a5742c00411b35e3b"} , "name" : "Mohan" , "industry_type" : "IT" , "prefered_location" : [ "chennai" , "bangalore"] , "skill_set" : [ "spring" , "java" , "hibernate"] , "employeer_type" : "Experience" , "experience" : 3.0 , "role" : "Software Developer" , "education" : [ { "ug_coursename" : "B.Sc[IT]" , "ug_from" : 2008.0 , "ug_to" : 2011.0}] , "workingexperience" : [ { "current_companyname" : "JustBootUp" , "JoinDate" : "2015"} , { "previous1_companyname" : "kambaa" , "previous1_from" : "2014" , "previous1_to" : "2015"}]} , { "_id" : { "$oid" : "55220cfe5742c00411b35e3a"} , "name" : "Prem" , "industry_type" : "IT" , "prefered_location" : [ "coimbatore" , "delhi" , "kerala"] , "skill_set" : [ "java" , "angularjs" , "j2ee" , "bigdata" , "hadoop" , "Spark"] , "employeer_type" : "Experience" , "experience" : 5.0 , "role" : "Manager" , "education" : [ { "ug_coursename" : "B.E(C.Sc)" , "ug_from" : 2004.0 , "ug_to" : 2008.0} , { "pg_coursename" : "M.E" , "pg_from" : 2009.0 , "pg_to" : 2012.0}] , "workingexperience" : [ { "currentcompany_name" : "JustBootUp" , "JoinDate" : "2015"} , { "previous1_companyname" : "Robert Bosch" , "previous1_from" : "2013" , "previous1_to" : "2014"}]} , { "_id" : { "$oid" : "55220ca45742c00411b35e39"} , "name" : "Prakash" , "industry_type" : "IT" , "prefered_location" : [ "coimbatore" , "chennai" , "bangalore"] , "skill_set" : [ "java" , "angularjs" , "j2ee"] , "employeer_type" : "Experience" , "experience" : 2.0 , "role" : "Software Developer" , "education" : [ { "ug_coursename" : "B.Sc[IT]" , "ug_from" : 2008.0 , "ug_to" : 2011.0}] , "workingexperience" : [ { "current_companyname" : "JustBootUp" , "JoinDate" : "2015"} , { "previous1_companyname" : "kambaa" , "previous1_from" : "2014" , "previous1_to" : "2015"}]}]}

			
			finalProfile.put("name",user.get("name"));			
			finalProfile.put("role", user.get("role"));
			finalProfile.put("experience", user.get("experience"));
			finalProfile.put("prefered_location", user.get("prefered_location"));
			finalProfile.put("skill_set", user.get("skill_set"));
			
			// Setting X axis interval  
			int interval=2;
			
			// profileAttributes
			for(String singleAttribute:profileAttributes)
			{
				// Entry for loop
				for (Entry<String, Object> entry : user.entrySet()) {	
					
					// This is to match chart attribute fields.
					if(entry.getKey().equals(singleAttribute))
					  {
						//	This loop for converting education object to chart data
						if(singleAttribute.equals("education"))
						{						
							BasicDBList singleAttributeList = new BasicDBList();
							singleAttributeList = (BasicDBList) user.get(singleAttribute);					
							
							for(int inner=0; inner<singleAttributeList.size(); inner++)
							{
								BasicDBObject singleObject = (BasicDBObject) singleAttributeList.get(inner);
								
								// This is for education field iteration
								for (int education = 0; education<educationFields.size(); education++)
								{
									//	To check the key is presented in particular object
									if( singleObject.containsKey(educationFields.get(education)) )
									{
										JSONObject dataObj = new JSONObject();
										String educationFieldName = educationFields.get(education);
										JSONArray yAxisDuration=new JSONArray();
										String keyname=educationFieldName.substring(0,educationFieldName.indexOf("_") + 1);											
										dataObj.put("x",interval);
										interval=interval+2;										
										yAxisDuration.add( new Double((double) singleObject.get(keyname+"from")).intValue() );
										yAxisDuration.add( new Double((double) singleObject.get(keyname+"to")).intValue() );
										dataObj.put("y",yAxisDuration);	
										dataObj.put("label",singleObject.get(keyname+"coursename"));
										singleprofileData.add(dataObj);
									}
									//System.out.println("Education Data is : "+singleprofileData);
									
								}							
								
							}
						 } // Ending education field	
						
						 else if(singleAttribute.equals("workingexperience"))
						 {
							 BasicDBList singleAttributeList = new BasicDBList();
								singleAttributeList = (BasicDBList) user.get(singleAttribute);					
								
								for(int inner=0; inner<singleAttributeList.size(); inner++)
								{
									BasicDBObject singleObject = (BasicDBObject) singleAttributeList.get(inner);
									
									// This is for education field iteration
									for (int experience = 0; experience<workingexperienceFields.size(); experience++)
									{
										//	To check the key is presented in particular object
										if( singleObject.containsKey(workingexperienceFields.get(experience)) )
										{
											JSONObject dataObj = new JSONObject();
											String experienceFieldName = workingexperienceFields.get(experience);
											JSONArray yAxisDuration=new JSONArray();
											String keyname=experienceFieldName.substring(0,experienceFieldName.indexOf("_") + 1);	
											dataObj.put("x",interval);
											interval=interval+2;
											if(!keyname.equals("current_"))
											{
												yAxisDuration.add( Integer.valueOf((String) singleObject.get(keyname+"from")) );
												yAxisDuration.add( Integer.valueOf((String) singleObject.get(keyname+"to")) );
											}
											else
											{
												yAxisDuration.add( Integer.valueOf((String) singleObject.get("JoinDate")) );
												yAxisDuration.add( new Integer(2015) );
											}
											dataObj.put("y",yAxisDuration);	
											dataObj.put("label",singleObject.get(keyname+"companyname"));
											singleprofileData.add(dataObj);
										}
										//System.out.println("Experience Data is : "+singleprofileData);
										
									}							
									
								} 								
						 }// Ending experience field
							 
						 } // Ending of chart attribute fields							
						
					  }	// Ending of entry for loop	    
				} // Ending of profileAttributes	
			
			// To check whether data is presented or not
			if(singleprofileData.size() > 0)
			{
				finalProfile.put("data",singleprofileData);	
				chartFormated.add(finalProfile);
			}
			else
			{
				
			}
			
			
			}// Ending of first for loop			
			
			
			System.out.println(chartFormated);
			
		return chartFormated;
	}
	
	
	
}
