package com.justbootup.blouda.dao;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.justbootup.blouda.util.MongodbConnection;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

@Repository
public class EmployeeFriendRequestDao {
	
	@Autowired
	RestTemplate restTemplate;
	
	MongodbConnection mongodb = new MongodbConnection();
	
	@SuppressWarnings("unchecked")
	public JSONObject getFriendsList(JSONObject queryParams){
		System.out.println("Query params is : "+queryParams);
		StringBuffer query = prepareQueryForGetFriends(queryParams);
		JSONObject result = executeQuery(query);
		JSONObject employeeList = new JSONObject();
		BasicDBList getFriendDetailsFromMongoDB = new BasicDBList();
		if(!result.containsKey("error"))
		{
			ArrayList<Object> checkingdata = (ArrayList<Object> ) result.get("data");
			
			if(!checkingdata.isEmpty())
			{
				System.out.println("Result from rest api is : "+result);
				
				ArrayList<Object> data = (ArrayList<Object> ) result.get("data");
				if(!data.isEmpty())
				{
					for(int i=0;i<data.size();i++)
					{						
						ArrayList<Object> innerObject = (ArrayList<Object>) data.get(i);
						getFriendDetailsFromMongoDB.add(innerObject.get(0));				
					}
				}			
				// call mongodb to get user details
				employeeList = getEmployeeDetails(getFriendDetailsFromMongoDB);
				
			}
			else
			{
				System.out.println("Something went wrong");
			}
		}
		else
		{
			
		}
		
		
		return employeeList;
	}
	
	
	@SuppressWarnings("unchecked")
	public JSONObject sendFriendRequest(JSONObject employeeList)
	{
		String email = employeeList.get("email").toString();
		JSONObject result = new JSONObject();
		try {
			
			// fetch friend request restrictions from mongodb.
			BasicDBObject isAllow = getEmployeeCollectionRestriction(email);				
			
			if(isAllow.getBoolean("allow"))
			{	
						
				//QueryForSendFriendRequest(employeeList);
				result = executeQuery( QueryForSendFriendRequest(employeeList));
				if(!result.containsKey("error"))
				{
					ArrayList<Object> checkingdata = (ArrayList<Object> ) result.get("data");
					if(checkingdata.isEmpty())
					{
						result.clear();
						result.put("status","OK");
						updateFriendRequestCount(email);						
					}
					else
					{
						result.put("status","failed");
						System.out.println("Some Error happen in sendFriendRequest Method");
					}
				}
				else
				{
					// Error is happend.
					result.put("status","failed");
				}
			}
			
			else
			{
				result.put("status","failed");
				result.put("proversion", true);
			}
		
			
			System.out.println("Return Result is : "+result);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Raised in sendFriendRequest Method in EmployeeFriendRequestDao class ");
		}
		
		
		return result;
	}
	
	@SuppressWarnings("unchecked")	
	public JSONObject getRequestedFriends(JSONObject currentEmployee)
	{
		
		JSONObject result = new JSONObject();
		JSONObject friendDetais = new JSONObject();
		
		BasicDBList findFrienddetails = new BasicDBList();
		
		StringBuffer queryforfriendscount = new StringBuffer();
		try {
			
				//match(e:employee {email:"shankar@gmail.com"})<-[r:friend {status:"pending"}]-(e1:employee) return e1
				queryforfriendscount.append("match (currentuser:employee { email : \""+currentEmployee.get("email")+"\" } ) ");
				queryforfriendscount.append("<- [r:friend {status:\"pending\"}] - (allemployee:employee) return allemployee.email");
				System.out.println(queryforfriendscount);
				result = executeQuery( queryforfriendscount);
				System.out.println("Before Result is : "+result);
				if(!result.containsKey("error"))
				{
					//{"data":[["prakash@justbootup.com"]],"columns":["allemployee.email"]}
					ArrayList<Object> checkingdata = (ArrayList<Object> ) result.get("data");
					if(!checkingdata.isEmpty())
					{
	
						ArrayList<Object> data = (ArrayList<Object> )  result.get("data");
						if(!data.isEmpty())
						{
							for(int i=0;i<data.size();i++)
							{	
								ArrayList<Object> innerObject = (ArrayList<Object>) data.get(i);
								findFrienddetails.add(innerObject.get(0));
							}
						}	
						friendDetais = getEmployeeDetails(findFrienddetails);
					}
					else
					{	
						
					}
				}
				else
				{
					// Error is happend.
					
				}
			
			System.out.println("Return Result from friends count method is : "+friendDetais);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Raised in getFriendscount method of EmployeeFriendRequestDao class ");
		}
		return friendDetais;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getRequestedFriendsCount(JSONObject currentEmploye)
	{
		JSONObject result = new JSONObject();
		StringBuffer queryforfriendscount = new StringBuffer();
		try {
			//match(e:employee {email:"shankar@gmail.com"})<-[r:friend {status:"pending"}]-(e1:employee) return e1
			queryforfriendscount.append("match (currentuser:employee { email : \""+currentEmploye.get("email")+"\" } ) ");
			queryforfriendscount.append("<- [r:friend {status:\"pending\"}] - (allemployee:employee) return count(allemployee)");			
			result = executeQuery( queryforfriendscount);			
			if(!result.containsKey("error"))
			{
				//{"data":[[1]],"columns":["count(allemployee)"]}
				// {"data":[["shankar@gmail.com"],["prabha@kambaa.com"]],"columns":["allemployee.email"]}

				ArrayList<Object> checkingdata = (ArrayList<Object> ) result.get("data");
				if(!checkingdata.isEmpty())
				{

					ArrayList<Object> data = (ArrayList<Object> ) checkingdata.get(0);
					if(!data.isEmpty())
					{
						for(int i=0;i<data.size();i++)
						{
							result.clear();							
							result.put("count",data.get(i));
						}
					}			
					
				}
				else
				{					
					result.clear();
					result.put("count","0");
					
				}
			}
			else
			{
				// Error is happend.
				result.put("status","failed");
			}
			
			System.out.println("Return Result from friends count method is : "+result);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Raised in getFriendscount method of EmployeeFriendRequestDao class ");
		}
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public JSONObject updateFriendRequest(JSONObject employeeFriends)
	{
		
		JSONObject result = new JSONObject();
		StringBuffer query = new StringBuffer();
		
		try {
			
			//match (e:employee {email:"shankar@gmail.com"})<-[r:friend]-(em:employee {email:"prakash@justbootup.com"}) set r.status='rejected' return r
			query.append("match ( user :employee { email : \""+employeeFriends.get("email")+"\" } ) ");
			query.append("<-[r:friend] - ( otheremployee :employee { email : \""+employeeFriends.get("employee")+"\" } ) ");
			query.append("set r.status = \""+employeeFriends.get("status")+"\"");
			
			result = executeQuery(query);
			
			if(!result.containsKey("error"))
			{
				result.clear();
				result.put("status","OK");
			}
			else
			{
				result.clear();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Raised in updateFriendRequest of EmployeeFriendRequestDao class ");
		}
		return result;
	}
	
	public JSONObject updateEmployeeFriendsRestriction(JSONObject plan)
	{
		JSONObject status = new JSONObject();
		try {
			
			int incrementValue = getIncrementValue(plan.get("plan").toString());
			
			MongoClient mongoclient = null;	
			mongoclient = mongodb.connect();
			DB db = mongoclient.getDB("jobportal");
			DBCollection collection = db.getCollection("employeeconnections");
			
			BasicDBObject query = new BasicDBObject();
			query.append("email", plan.get("email"));
			
			BasicDBObject incrementQuery = new BasicDBObject();			
			incrementQuery.append("restriction.totalfriendrequest", incrementValue);
			incrementQuery.append("restriction.totalinmails", incrementValue);
			incrementQuery.append("restriction.remainingfriendrequest", incrementValue);
			incrementQuery.append("restriction.remaininginmails", incrementValue);	
			
			
			BasicDBObject UpdateQuery = new BasicDBObject();			
			
			UpdateQuery.append("$inc", incrementQuery);
			
			DBObject object = collection.findAndModify(query, null, null, false, UpdateQuery, true, false);
			
			System.out.println(object);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("The Above Exception is raised in updateEmployeeFriendsRestriction method");
		}
		return null;
	}
	
	private int getIncrementValue(String planname)
	{
		int result = 0;
		try {
			
			HashMap<String, Integer> plans = new HashMap<String, Integer>();
			plans.put("plan1", 10);
			plans.put("plan2", 30);
			plans.put("plan3", 50);
			result = plans.get(planname);	
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception raised in getIncrementValue method");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject sendInmail(JSONObject sendEmail){
		
		JSONObject result = new JSONObject();
		
		try {	
			
			MongoClient mongoclient = null;	
			mongoclient = mongodb.connect();
			DB db = mongoclient.getDB("jobportal");
			DBCollection collection = db.getCollection("employeeconnections");
			
			String senderemail = sendEmail.get("email").toString();
			BasicDBObject query = new BasicDBObject();
			query.append("email",senderemail);		
			
			sendEmail.remove("email");
			
			Date currentDate = new Date( );
		    SimpleDateFormat currenDateFormat = new SimpleDateFormat ("dd-MM-yyyy hh:mm:ss");
		    
		    sendEmail.put("sendAt",currenDateFormat.format(currentDate));
		    ObjectId id = new ObjectId();
		    System.out.println(id);
		    sendEmail.put("id",id);
		    
		    System.out.println("query is : "+query);
		    System.out.println("object is : "+sendEmail);
		    
			BasicDBObject updateQuery = new BasicDBObject();
			
			updateQuery.append("sendMails", sendEmail);			
			
			// This update for sender
			collection.update(query, new BasicDBObject().append("$push",updateQuery));
			
			// This part is update to receiver
			query.append("email",sendEmail.get("to"));
			
			sendEmail.put("from",senderemail);	
			sendEmail.remove("to");	
			
			BasicDBObject updateInmailQuery = new BasicDBObject();
			
			updateInmailQuery.append("inMails", sendEmail);
			collection.update(query, new BasicDBObject().append("$push",updateInmailQuery));		   
		      
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception raised in sendInmail method");
		}
		
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject showFriendsList(JSONObject friendsList)
	{
		JSONObject result = new JSONObject();
		StringBuffer query = new StringBuffer();
		BasicDBList getFriendsinfos = new BasicDBList();
		JSONObject friendsInfos = new JSONObject();
		try {
			
			//match ( user :employee { email : "shankar@gmail.com" } ) -[r:friend {status:'confirmed'}] - ( otheremployee :employee ) return otheremployee
			query.append("match ( :employee { email : \""+friendsList.get("email")+"\" } ) ");
			query.append("- [r:friend { status : \"confirmed\"} ] - (friends:employee) return friends.email");
			System.out.println(query);
			result = executeQuery(query);
			
			if(!result.containsKey("error"))
			{
				//{"data":[["prakash@justbootup.com"]],"columns":["allemployee.email"]}
				ArrayList<Object> checkingdata = (ArrayList<Object> ) result.get("data");
				if(!checkingdata.isEmpty())
				{

					ArrayList<Object> data = (ArrayList<Object> )  result.get("data");
					if(!data.isEmpty())
					{
						for(int i=0;i<data.size();i++)
						{	
							ArrayList<Object> innerObject = (ArrayList<Object>) data.get(i);
							getFriendsinfos.add(innerObject.get(0));
						}
					}	
					friendsInfos = getEmployeeDetails(getFriendsinfos);
					
				}
				else
				{	
					
				}
			}
			else
			{
				// Error is happend.
				
			}
			
			System.out.println(friendsInfos);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Excepton Raised in showFriendsList of EmployerFriendRequestDao class");
		}
		return friendsInfos;
	}
	
	@SuppressWarnings("unchecked")
	private StringBuffer prepareQueryForGetFriends(JSONObject queryParams)
	{	
		System.out.println();
		StringBuffer queryForFriendRequest = new StringBuffer();		
		try{	
			
			HashMap<String, Object> forIteration = queryParams;
			String email = (String) forIteration.get("email");
			forIteration.remove("email");
			
			System.out.println(forIteration.size()+"dsf");
			if(forIteration.size() !=1)
			{
			
				boolean isCompanyExists=false;
				if(forIteration.containsKey("company"))
				{
					//match(com:employer:company {name:"justbootup"})-[r:working_in]-(comemployee:employee)
					queryForFriendRequest.append("match ( `"+forIteration.get("company")+"` :employer:company { name : \""+forIteration.get("company")+"\" } )");
					queryForFriendRequest.append("- [:working_in] - ( companyemployee:employee ) with companyemployee ");
					isCompanyExists=true;
				}
				
				
				
				if(forIteration.containsKey("employeementtype"))
					forIteration.remove("employeementtype");
				
				String employeeNode =null;
				if(isCompanyExists)
				{
					employeeNode = "companyemployee";				
				}
				else
				{
					employeeNode = "allemployee : employee";
				}
				
				
				for(String key:forIteration.keySet())
				{	
					
					if(!key.equals("company"))
					{
						if( employeeNode.equals("companyemployee") )
						{	
							queryForFriendRequest.append("match ("+employeeNode+" ) - [ :having_"+key+"] -> ( :commonNode:"+key+" { name : \""+forIteration.get(key).toString().toLowerCase()+"\" } ) with "+employeeNode+" ");
						}				
						
						else if( employeeNode.equals("allemployee : employee"))
						{
							queryForFriendRequest.append("match ("+employeeNode+" ) - [ :having_"+key+"] -> ( :commonNode:"+key+" { name : \""+forIteration.get(key).toString().toLowerCase()+"\" } ) with allemployee ");
							employeeNode = "allemployee";
						}			
						
					}
					
				}
				
				if(queryParams.containsKey("employeementtype") )
				{	
					if(queryParams.get("employeementtype").equals("experiencer"))
					{
						//match(prakash:employee {email:"prakash@justbootup.com"})-[r:working_in]->(com:employer:company ) where not (e)-[:working_in]->(com) return e
						// Finally we append candidate employee company
						queryForFriendRequest.append("match (currentemployee:employee { email :\""+email+"\" } ) - [ :working_in ] -> (company:employer:company) where not ( "+employeeNode+") - [:working_in] ->(company)");
						
						//with allemployee,emp where not (allemployee)-[:friend]-(emp) return allemployee					
						queryForFriendRequest.append("with "+employeeNode+" , currentemployee where not ("+employeeNode+") - [:friend] - ( currentemployee ) return "+employeeNode+".email");
						
					}	
				}
				
				
				queryForFriendRequest.append("match (currentemployee:employee { email :\""+email+"\" } )"); 
				queryForFriendRequest.append(" , currentemployee where not ("+employeeNode+") - [:friend] - ( currentemployee ) with "+employeeNode+" where not "+employeeNode+".email=\""+email+"\" return "+employeeNode+".email");
				
			}
			else
			{
				queryForFriendRequest.append("match (currentemployee:employee { email :\""+email+"\" } )"); 
				queryForFriendRequest.append("match (allemployee:employee)"); 
				queryForFriendRequest.append("where not ( allemployee ) - [:friend] - ( currentemployee ) with allemployee where not allemployee.email=\""+email+"\" return allemployee.email");
				
			}
				System.out.println("Final query for friend request is : "+queryForFriendRequest);			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception Raised in prepareQueryForFriedRequest in employeefriendRequestdao class");
		}
		
		return queryForFriendRequest;		
	}
		
	
	private StringBuffer QueryForSendFriendRequest(JSONObject employeeList)
	{
		//merge(e:employee {email:'prakash@justbootup.com'}) merge(e1:employee {email:"prabha@kambaa.com"}) merge(e)-[r:friend {status:"pending"}]->(e1)
		StringBuffer sendRequest = new StringBuffer();
		sendRequest.append("merge( user :employee { email : \""+employeeList.get("email")+"\" } )");
		sendRequest.append("merge( otheremployee :employee { email : \""+employeeList.get("employee")+"\" } )");
		sendRequest.append("merge(user)-[r:friend { status : \"pending\" }] -> (otheremployee)");
		System.out.println("Friend Request Query is : "+sendRequest);
		return sendRequest;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private JSONObject getEmployeeDetails(BasicDBList listofemails)
	{
		MongoClient mongoclient = null;		
		JSONObject employeeList = new JSONObject();
		try{
			
				mongoclient = mongodb.connect();
				DB db = mongoclient.getDB("jobportal");
				DBCollection collection = db.getCollection("employee");
				
				BasicDBObject listOfEmployee = new BasicDBObject();
				listOfEmployee.put("$in", listofemails);
				System.out.println(listOfEmployee);
				
				BasicDBObject query = new BasicDBObject();
				query.append("email",listOfEmployee);
				
				BasicDBObject projection = new BasicDBObject();
				projection.append("_id", 0);			
							
				DBCursor cursor = collection.find(query);
				List<DBObject> employeeDetails = cursor.toArray();			
				
				
				final ArrayList<String> essentialFields = new ArrayList<String>();
				essentialFields.add("email");
				essentialFields.add("personaldetails");
				essentialFields.add("professionaldetails");				
				essentialFields.add("employeementtype");
				
				
				final ArrayList<String> personaldetails = new ArrayList<String>();		
				personaldetails.add("currentlocation");
				personaldetails.add("fullname");
				
				final ArrayList<String> professionalcurrentcompanydetails = new ArrayList<String>();		
				professionalcurrentcompanydetails.add("name");	
				
				
				ArrayList<JSONObject> resultdata = new ArrayList<JSONObject>();		
				for(DBObject employee : employeeDetails)
				{
					JSONObject employeeinfo = new JSONObject();
					for(String essentialField:essentialFields)
					{
						if(employee.containsKey(essentialField))
						{
							if(essentialField.equals("personaldetails"))
							{
								BasicDBObject personaldetailsObj = new BasicDBObject();
								personaldetailsObj = (BasicDBObject) employee.get(essentialField);
								for(String personal:personaldetails)
								{
									employeeinfo.put(personal, personaldetailsObj.get(personal));
								}
							}
							else if(essentialField.equals("professionaldetails"))
							{
								BasicDBObject professionaldetailsObj = new BasicDBObject();
								professionaldetailsObj = (BasicDBObject) employee.get(essentialField);
								BasicDBObject currentcompanydetails = new BasicDBObject();
								currentcompanydetails = (BasicDBObject) professionaldetailsObj.get("currentcompany");
								
								for(String currentcompany:professionalcurrentcompanydetails)
								{
									employeeinfo.put("currentcompanyname", currentcompanydetails.get(currentcompany));
								}								
							}
							else
							{
								employeeinfo.put(essentialField, employee.get(essentialField));
							}
							
						}
						else
						{
							// Add N/A field
							employeeinfo.put(essentialField, "N/A");
						}
					}
					if(employee.containsKey("currentcompanydetails"))
					{
						BasicDBObject gettingcompanyInfo = (BasicDBObject) employee.get("currentcompanydetails");
						employeeinfo.put("current_company_name",gettingcompanyInfo.get("current_company_name"));						
					}
					
					resultdata.add(employeeinfo);
				 }
				employeeList.put("result",resultdata);				
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception Raised in getEmployeeDetails from EmployeeFriendRequestDao class");
		}
		finally{
			if(mongoclient != null)
				mongoclient.close();
		}
		return employeeList;
	}
	
	
	/**
	 * 	
	 * @param employee
	 * @return status 
	 * 
	 */
	private BasicDBObject getEmployeeCollectionRestriction(String email){
		
		BasicDBObject result = new BasicDBObject();
		MongoClient mongoclient = null;	
		try
		{
			mongoclient = mongodb.connect();
			DB db = mongoclient.getDB("jobportal");
			DBCollection empcollection = db.getCollection("employee");
			
			BasicDBObject query = new BasicDBObject();
			query.append("email",email);
			
			BasicDBObject fields = new BasicDBObject();
			fields.append("_id",0);
			fields.append("employeeSession.lastLoggedIn", 1);
			
			DBObject employeeSession = empcollection.findOne(query, fields);
			
			 
			BasicDBObject session = new BasicDBObject();
			 
			session = (BasicDBObject) employeeSession.get("employeeSession");
			 
			 String currentdate = (String) session.get("lastLoggedIn");
			 
			 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			 Date tempDate=simpleDateFormat.parse(currentdate);
			 SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy");           
			 currentdate = outputDateFormat.format(tempDate);			 
			    
			DBCollection connection = db.getCollection("employeeconnections");
			
			DBObject employeeRestriction = connection.findOne(query, new BasicDBObject().append("_id",0).append("restriction", 1));
			
			BasicDBObject restriction = new BasicDBObject();
			 
			restriction = (BasicDBObject) employeeRestriction.get("restriction");
			
			System.out.println(restriction);
			String restrictionstartdate = (String) restriction.get("startdate");
			
			Date startDate = outputDateFormat.parse(restrictionstartdate);
			Date endDate = outputDateFormat.parse(currentdate);
			//Date endDate = outputDateFormat.parse("15-07-2015");
			int differentDays = getDifferenceDays(startDate,endDate);
			
			
			System.out.println(differentDays);
			if(differentDays > 30)
			{
				int module = differentDays % 30;
				System.out.println("module is "+module);
				
				System.out.println("minus values is "+(differentDays-module));
				int diff = differentDays-module;
				Calendar cal = Calendar.getInstance();    
				cal.setTime( outputDateFormat.parse(restrictionstartdate));    
				cal.add( Calendar.DATE, diff );    
				String convertedDate=outputDateFormat.format(cal.getTime());    
				System.out.println("Date increase by "+diff+".."+convertedDate);
				
				//FindAndModify for update query				
				BasicDBObject resetRestriction = new BasicDBObject();
				BasicDBObject newrestriction = new BasicDBObject();
				newrestriction.append("startdate",convertedDate);
				newrestriction.append("totalfriendrequest", 10);	
				newrestriction.append("usedfriendrequest",0);
				newrestriction.append("remainingfriendrequest",10);
				newrestriction.append("totalinmails", 10);
				newrestriction.append("usedlinmails",0);
				newrestriction.append("remaininginmails",10);				
				resetRestriction.append("restriction", newrestriction);
				
				BasicDBObject UpdateQuery = new BasicDBObject();			
				
				UpdateQuery.append("$push",new BasicDBObject().append("restrictiondetails",employeeRestriction));
				UpdateQuery.append("$set",resetRestriction);
				
				DBObject object = connection.findAndModify(query, null, null, false, UpdateQuery, true, false);
				
				if(object != null)
				{
					result.append("allow", true);
				}
				else
				{
					result.append("allow", false);
				}
				
			}
			else
			{
				System.out.println("totalfriendrequest count is : "+restriction.getInt("totalfriendrequest"));
				
				if(restriction.getInt("usedfriendrequest")<=restriction.getInt("totalfriendrequest"))
				{					
					result.append("allow", true);					
				}
				else
				{
					result.append("allow", false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("The above exception is raised in getEmployeeCollectionRestriction method");
		}
		
		return result;
	}
	
	private void updateFriendRequestCount(String email){
		
		try {
			
			MongoClient mongoclient = null;	
			mongoclient = mongodb.connect();
			DB db = mongoclient.getDB("jobportal");
			DBCollection collection = db.getCollection("employeeconnections");
			
			BasicDBObject query = new BasicDBObject();
			query.append("email", email);
			
			BasicDBObject incrementQuery = new BasicDBObject();			
			incrementQuery.append("restriction.remainingfriendrequest", -1);
			incrementQuery.append("restriction.usedfriendrequest", 1);
			
			BasicDBObject UpdateQuery = new BasicDBObject();			
			
			UpdateQuery.append("$inc", incrementQuery);
			
			DBObject object = collection.findAndModify(query, null, null, false, UpdateQuery, true, false);
			
			System.out.println(object);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("The above exception is raised in updateFriendRequestCount method");
		}
	};
	private int getDifferenceDays(Date startDate, Date endDate) {
		int daysdiff=0;
		long diff = endDate.getTime() - startDate.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000)+1;
		daysdiff = (int) diffDays;
		return daysdiff;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject executeQuery(StringBuffer queryParams){
		
		JSONObject nodeQueryResult = new JSONObject();
		
		try{
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);		
			
			JSONObject sss = new JSONObject();					
			sss.put("query",queryParams);
			HttpEntity<JSONObject> request = new HttpEntity<JSONObject>(sss,headers);
			
			URI uri1 = new URI("http://localhost:7474/db/data/cypher");			
	       
			ResponseEntity<JSONObject> response = restTemplate.exchange(uri1, HttpMethod.POST, request, JSONObject.class);
			
	        
	        if(HttpStatus.OK == response.getStatusCode())
	        {
	        	// Request Successfully completed	
	        	nodeQueryResult = response.getBody();
	        	
	        }
	        else
	        {
	        	// Some Error is made	
	        	nodeQueryResult.put("error", "some error is made");
	        }
			
		}
		catch(Exception e)
		{
			//nodeQueryResult.put("error", "some error is made");
			nodeQueryResult.clear();
			nodeQueryResult.put("error", "some error is made");
			System.out.println("Exception thrown in executeQuery method of employeebulidnodedao layer");
			e.printStackTrace();
		}
		
		return nodeQueryResult;
	}

	
}
