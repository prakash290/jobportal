package com.justbootup.blouda.dao;

import java.awt.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import com.justbootup.blouda.util.MongodbConnection;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

@Repository
public class EmployeeMailServiceDao {
	
	MongodbConnection mongodb = new MongodbConnection();
	
	@SuppressWarnings("unchecked")
	public JSONObject sendInmail(JSONObject sendEmail){
		
		JSONObject result = new JSONObject();
		
		try {	
			
			MongoClient mongoclient = null;	
			mongoclient = mongodb.connect();
			DB db = mongoclient.getDB("jobportal");
			DBCollection collection = db.getCollection("employeeconnections");
			
			String senderemail = sendEmail.get("email").toString();
			
			// fetch friend request restrictions from mongodb.
			BasicDBObject isAllow = getEmployeeCollectionRestriction(senderemail);
			if(isAllow.getBoolean("allow"))
			{
				BasicDBObject query = new BasicDBObject();
				query.append("email",senderemail);		
				
				sendEmail.remove("email");
				
				Date currentDate = new Date( );
			    SimpleDateFormat currenDateFormat = new SimpleDateFormat ("dd-MM-yyyy hh:mm:ss");
			    
			    sendEmail.put("sendAt",currenDateFormat.format(currentDate));
			    String id = RandomStringUtils.randomAlphanumeric(20);
			    long millisStart = Calendar.getInstance().getTimeInMillis();
			    id=id+millisStart;
			    System.out.println("Id is : "+id);
			    sendEmail.put("id",id);
			    
			    System.out.println("query is : "+query);
			    System.out.println("object is : "+sendEmail);
			    
				BasicDBObject updateQuery = new BasicDBObject();
				
				updateQuery.append("sendMails", sendEmail);	
								
				JSONObject copyofSE = (JSONObject)sendEmail.clone();
				
				copyofSE.put("reply",false);		
				
				updateQuery.append("inMails", copyofSE);
				
				// This update for sender
				collection.update(query, new BasicDBObject().append("$push",updateQuery));
				
				// This part is update to receiver
				query.append("email",sendEmail.get("to"));
				
				sendEmail.put("from",senderemail);	
				sendEmail.remove("to");	
				
				BasicDBObject updateInmailQuery = new BasicDBObject();
				
				updateInmailQuery.append("inMails", sendEmail);
				collection.update(query, new BasicDBObject().append("$push",updateInmailQuery));		
				
				updateinmailCount(senderemail);
				
				result.put("status","OK");				
			}   
			else
			{
				result.put("status","failed");
				result.put("proversion", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status","FAILED");
			System.out.println("Exception raised in sendInmail method");
		}
		
		return null;
		
	}
	
	public BasicDBObject getInboxMails(JSONObject employee)
	{
		BasicDBObject result = new BasicDBObject();
		try {			
			MongodbConnection mongo = new MongodbConnection();
			MongoClient mongoclient = null;
			mongoclient = mongo.connect();			
			DB db = mongoclient.getDB("jobportal");			
			DBCollection collection = db.getCollection("employeeconnections");
			
			BasicDBObject query = new BasicDBObject();
			query.append("email",employee.get("email"));
			query.append("reply", new BasicDBObject().append("$ne",false));
			
			BasicDBObject match = new BasicDBObject();
			match.append("$match", query);
			
			BasicDBObject unwind = new BasicDBObject();
			unwind.append("$unwind", "$inMails");
			
			BasicDBObject unwindquery= new BasicDBObject();
			unwindquery.append("inMails.reply", new BasicDBObject().append("$ne",false));
			
			BasicDBObject secondmatch = new BasicDBObject();
			secondmatch.append("$match", unwindquery);
			
			BasicDBObject projection = new BasicDBObject();	
			projection.append("inMails",1);
			
			ArrayList<DBObject> pipeline = new ArrayList<DBObject>();
			pipeline.add(match);
			pipeline.add(unwind);
			pipeline.add(secondmatch);			
			System.out.println(match);
			System.out.println(unwind);
			System.out.println(secondmatch);
			System.out.println(projection);
			pipeline.add(new BasicDBObject().append("$project",projection));
			
			AggregationOutput out = collection.aggregate(pipeline);
			
			ArrayList<Object> list = new ArrayList<Object>();
	        for (final DBObject result12 : out.results()) {
	            list.add(result12.get("inMails"));           
	        }
	        System.out.println(list);
			//result = (BasicDBObject) collection.findOne(query,new BasicDBObject().append("_id", 0).append("inMails", 1));			
			result.append("inMails", list);
			System.out.println(result);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Above Exception is raised in getInboxMails");
		}
		return result;
	}
	
	public BasicDBObject getsendMails(JSONObject employee)
	{
		BasicDBObject result = new BasicDBObject();
		try {			
			MongodbConnection mongo = new MongodbConnection();
			MongoClient mongoclient = null;
			mongoclient = mongo.connect();			
			DB db = mongoclient.getDB("jobportal");			
			DBCollection collection = db.getCollection("employeeconnections");
			
			BasicDBObject query = new BasicDBObject();
			query.append("email",employee.get("email"));
			result = (BasicDBObject) collection.findOne(query,new BasicDBObject().append("_id", 0).append("sendMails", 1));		
				
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Above Exception is raised in getInboxMails");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject sendReply(JSONObject employee)
	{
		JSONObject status = new JSONObject();
		try 
		{
			MongodbConnection mongo = new MongodbConnection();
			MongoClient mongoclient = null;
			mongoclient = mongo.connect();			
			DB db = mongoclient.getDB("jobportal");			
			DBCollection collection = db.getCollection("employeeconnections");
			
			String senderemail = employee.get("email").toString();
			String receiveremail = employee.get("to").toString();
			
			String id = employee.get("msgid").toString();			
			
			BasicDBObject idquery = new BasicDBObject();
			idquery.append("id",id);
			
			BasicDBObject query = new BasicDBObject();
			query.append("email",senderemail);
			query.append("inMails", new BasicDBObject().append("$elemMatch", idquery));
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			Date Date = new Date();
			String currentDate = simpleDateFormat.format(Date);
			
			employee.remove("email");
			employee.remove("id"); 
			employee.put("sendAt",currentDate);
			
			BasicDBObject updateQueryValue = new BasicDBObject();
			updateQueryValue.append("inMails.$.replys", employee);
			
			System.out.println("query i s: "+query);
			System.out.println("update query is : "+updateQueryValue);
			
			BasicDBObject updateQuery = new BasicDBObject();			
			updateQuery.append("$push", updateQueryValue);
			updateQuery.append("$set", new BasicDBObject().append("inMails.$.reply", true));
			
			// This update for sender
			collection.update(query, updateQuery);
			
			// This update for receiver			
			query.append("email",receiveremail);
			collection.update(query, updateQuery);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception is raised in sendReply method");
		}
		return null;		
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
				System.out.println("totalinmails count is : "+restriction.getInt("totalinmails"));
				
				if(restriction.getInt("usedinmails")<=restriction.getInt("totalinmails"))
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
	
	private void updateinmailCount(String email){
		
		try {
			
			MongoClient mongoclient = null;	
			mongoclient = mongodb.connect();
			DB db = mongoclient.getDB("jobportal");
			DBCollection collection = db.getCollection("employeeconnections");
			
			BasicDBObject query = new BasicDBObject();
			query.append("email", email);
			
			BasicDBObject incrementQuery = new BasicDBObject();			
			incrementQuery.append("restriction.remaininginmails", -1);
			incrementQuery.append("restriction.usedinmails", 1);
			
			BasicDBObject UpdateQuery = new BasicDBObject();			
			
			UpdateQuery.append("$inc", incrementQuery);
			
			DBObject object = collection.findAndModify(query, null, null, false, UpdateQuery, true, false);
			
			System.out.println(object);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("The above exception is raised in updateinmailCount method");
		}
	};
	private int getDifferenceDays(Date startDate, Date endDate) {
		int daysdiff=0;
		long diff = endDate.getTime() - startDate.getTime();
		long diffDays = diff / (24 * 60 * 60 * 1000)+1;
		daysdiff = (int) diffDays;
		return daysdiff;
	}
	
}
