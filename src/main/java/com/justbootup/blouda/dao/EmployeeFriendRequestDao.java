package com.justbootup.blouda.dao;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		JSONObject result = new JSONObject();
		try {
			//QueryForSendFriendRequest(employeeList);
			result = executeQuery( QueryForSendFriendRequest(employeeList));
			if(!result.containsKey("error"))
			{
				ArrayList<Object> checkingdata = (ArrayList<Object> ) result.get("data");
				if(checkingdata.isEmpty())
				{
					result.clear();
					result.put("status","OK");
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
			
			System.out.println("Return Result is : "+result);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Raised in sendFriendRequest Method in EmployeeFriendRequestDao class ");
		}
		
		
		return result;
	}
	
	
	public JSONObject getRequestedFriends(JSONObject currentEmployee)
	{
		
		return null;
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
			System.out.println(queryforfriendscount);
			result = executeQuery( queryforfriendscount);
			System.out.println("Before Result is : "+result);
			if(!result.containsKey("error"))
			{
				ArrayList<Object> checkingdata = (ArrayList<Object> ) result.get("data");
				if(!checkingdata.isEmpty())
				{

					ArrayList<Object> data = (ArrayList<Object> ) result.get("data");
					if(!data.isEmpty())
					{
						for(int i=0;i<data.size();i++)
						{
							result.clear();
							ArrayList<Object> innerObject = (ArrayList<Object>) data.get(i);
							result.put("count",innerObject.toString());
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
	private StringBuffer prepareQueryForGetFriends(JSONObject queryParams)
	{		
		StringBuffer queryForFriendRequest = new StringBuffer();		
		try{	
			
			HashMap<String, Object> forIteration = queryParams;
			
			boolean isCompanyExists=false;
			if(forIteration.containsKey("company"))
			{
				//match(com:employer:company {name:"justbootup"})-[r:working_in]-(comemployee:employee)
				queryForFriendRequest.append("match ( `"+forIteration.get("company")+"` :employer:company { name : \""+forIteration.get("company")+"\" } )");
				queryForFriendRequest.append("- [:working_in] - ( companyemployee:employee ) with companyemployee ");
				isCompanyExists=true;
			}
			
			String email = (String) forIteration.get("email");
			forIteration.remove("email");
			
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
			
			//match(prakash:employee {email:"prakash@justbootup.com"})-[r:working_in]->(com:employer:company ) where not (e)-[:working_in]->(com) return e
			// Finally we append candidate employee company
			queryForFriendRequest.append("match (currentemployee:employee { email :\""+email+"\" } ) - [ :working_in ] -> (company:employer:company) where not ( "+employeeNode+") - [:working_in] ->(company)");
			
			//with allemployee,emp where not (allemployee)-[:friend]-(emp) return allemployee
			queryForFriendRequest.append("with "+employeeNode+" , currentemployee where not ("+employeeNode+") - [:friend] - ( currentemployee ) return "+employeeNode+".email");
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
				
				ArrayList<String> essentialFields = new ArrayList<String>();
				essentialFields.add("fullname");				
				essentialFields.add("currentlocation");
				essentialFields.add("experience");
				essentialFields.add("skillset");
				essentialFields.add("email");
				//essentialFields.add("currentcompanydetails");
				
				ArrayList<JSONObject> resultdata = new ArrayList<JSONObject>();		
				for(DBObject employee : employeeDetails)
				{
					JSONObject employeeinfo = new JSONObject();
					for(String essentialField:essentialFields)
					{
						if(employee.containsKey(essentialField))
						{
							employeeinfo.put(essentialField, employee.get(essentialField));
						}
						else
						{
							// Add N/A field
							employeeinfo.put(essentialField, "N//A");
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
