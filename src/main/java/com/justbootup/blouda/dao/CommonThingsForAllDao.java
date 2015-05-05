package com.justbootup.blouda.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.justbootup.blouda.util.MongodbConnection;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

@Repository
public class CommonThingsForAllDao {
	
	MongodbConnection mongodb=new MongodbConnection();
	
	public BasicDBObject getAllCompaniesName(){		
		return getCommonThings("wholecompanies");	
	}
	
	public BasicDBObject getAllLocations(){		
		return getCommonThings("wholelocations");	
	}
	
	public BasicDBObject getAllSkills(){		
		return getCommonThings("wholeskills");	
	}
	
	private BasicDBObject getCommonThings(String name)
	{
		MongoClient mongoclient = null;
		BasicDBObject commonThings = new BasicDBObject();
		try
		{
			mongoclient = mongodb.connect();
			DB db = mongoclient.getDB("jobportal");
			DBCollection collection = db.getCollection("commonforall");
			
			BasicDBObject queryofAllCompanies = new BasicDBObject();
			queryofAllCompanies.put("name", name);			
			DBCursor cursor = collection.find(queryofAllCompanies);
			List<DBObject> employeeProfilesPage = cursor.toArray();			
			commonThings = (BasicDBObject) employeeProfilesPage.get(0);
			System.out.println(commonThings.get(name));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception Raised in getAllCompanies Method of CommnThingsForAllDao");
		}		
		finally{
			
		}
		return commonThings;
	}
	
	public BasicDBObject friendSearchViewData()
	{
		ArrayList<String> commonThings = new ArrayList<String>();
		commonThings.add("wholecompanies");
		commonThings.add("wholelocations");
		commonThings.add("wholeskills");
		
		return getfriendSearchViewData(commonThings);
	}
	private BasicDBObject getfriendSearchViewData(List<String> names)
	{
		MongoClient mongoclient = null;
		BasicDBObject commonThings = new BasicDBObject();
		try
		{
			mongoclient = mongodb.connect();
			DB db = mongoclient.getDB("jobportal");
			DBCollection collection = db.getCollection("commonforall");
			for(String name:names)
			{	
				BasicDBObject queryofAllCompanies = new BasicDBObject();
				queryofAllCompanies.put("name", name);			
				BasicDBObject projection = new BasicDBObject();
				projection.put(name, 1);
				projection.put("_id",0);
				DBCursor cursor = collection.find(queryofAllCompanies,projection);
				List<DBObject> employeeProfilesPage = cursor.toArray();		
				convertToAngularJSOption ((BasicDBList) employeeProfilesPage.get(0).get(name));				
				commonThings.append(name,  convertToAngularJSOption ((BasicDBList) employeeProfilesPage.get(0).get(name)));
				//commonThings.append(name,employeeProfilesPage.get(0).get(name));
			}
			
			System.out.println(commonThings);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception Raised in getAllCompanies Method of CommnThingsForAllDao");
		}		
		finally{
			
		}
		
		return commonThings;
	}
	
	private BasicDBList convertToAngularJSOption(BasicDBList list)
	{
		// convert this format for ng-options [{ id: 1, name: listname,}]
		BasicDBList converted = new BasicDBList();
		for(int i=0;i<list.size();i++)
		{
			BasicDBObject object = new BasicDBObject();
			object.put("id",i);
			object.put("name", list.get(i));
			converted.add(object);
		}
		return converted;
	}

}
