package com.justbootup.blouda.dao;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.justbootup.blouda.util.MongodbConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

@Repository
public class EmployeeDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<JSONObject> getAllUsers()
	{
		System.out.println("user Dao Called");
		return mongoTemplate.findAll(JSONObject.class,"User");
	}
	
	@SuppressWarnings("unchecked")
	public net.sf.json.JSONObject saveUser(MultipartFile resume,net.sf.json.JSONObject employeeDetails)
	{
		try {
			if(employeeDetails.get("linkedinemail") == null) 
			{
			System.out.println("Before Inserting Data is : "+employeeDetails);
			ObjectId id = new ObjectId();
			System.out.println("Inserted Id value is : "+id);
			employeeDetails.put("_id", id);	
			System.out.println(resume.getContentType());
			JSONObject employeeFileDetails = saveResume(resume, employeeDetails);			
			
			employeeDetails.put("resumeDetails", employeeFileDetails);
			
			mongoTemplate.insert(employeeDetails,"employee");
			}
			else
			{
				System.out.println("Before Inserting Data is with linked in email : "+employeeDetails);
				MongodbConnection mongo = new MongodbConnection();
				MongoClient mongoclient = null;
				mongoclient = mongo.connect();
				
				DB db = mongoclient.getDB("jobportal");
				
				DBCollection collection = db.getCollection("employee");
				
				DBCollection thirdpartycollection = db.getCollection("thirdpartyemployee");
				
				String linkedinemail = (String) employeeDetails.get("linkedinemail");
				
				employeeDetails.remove("linkedinemail");
				
				ObjectId id = new ObjectId();
				System.out.println("Inserted Id value is : "+id);
				employeeDetails.put("_id", id);	
			
				
				BasicDBObject employee = new BasicDBObject(employeeDetails);
								
				collection.insert(employee);
				
				
				BasicDBObject thirdparty = new BasicDBObject();
				thirdparty.append("linkedinemail", linkedinemail);
				thirdpartycollection.insert(thirdparty);			
				
			}
			
		} catch (Exception e) {
			
			employeeDetails.put("error", "failure");
            return employeeDetails;
		}
		
		
		return employeeDetails;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject employeeUpdateProfile(JSONObject jsonEmployeeProfile) throws UnknownHostException{
		
		MongodbConnection mongo = new MongodbConnection();
		MongoClient mongoclient = null;
		mongoclient = mongo.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("employee");
		
		String email = (String) jsonEmployeeProfile.get("useremail");
		jsonEmployeeProfile.remove("useremail");
		
		// Query condition for email
		BasicDBObject condition = new BasicDBObject();
		condition.append("email", email);	
		
		// update condition
		BasicDBObject updateQuery = new BasicDBObject();
		updateQuery.append("$set",jsonEmployeeProfile);
		System.out.println(updateQuery);		
		
		WriteResult result =collection.update(condition, updateQuery, true, false);
		
		JSONObject returnStatus = new JSONObject();
		if(result.isUpdateOfExisting())
		{
			returnStatus.put("email", email);
		}
		else
		{
			returnStatus.put("update", false);
		}		
		
		return returnStatus;
	}
	
	@SuppressWarnings("unchecked")
	public BasicDBObject getEmployeeProfile(JSONObject employeeSession) throws UnknownHostException	
	{		
		MongodbConnection mongo = new MongodbConnection();
		MongoClient mongoclient = null;
		mongoclient = mongo.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("employee");
		
		BasicDBObject condition = new BasicDBObject(employeeSession);
		
		DBCursor cursor = collection.find(condition);		
		
		 BasicDBObject userProfile = new BasicDBObject();
		 
		try {
		   while(cursor.hasNext()) {
			   userProfile = (BasicDBObject) cursor.next();		      
		   }
		} finally {
		   cursor.close();
		}
		
		
		return userProfile;
	}
	
	public BasicDBObject employeeLogin(JSONObject employeeCredentials) throws UnknownHostException
	{
		
		MongodbConnection mongo = new MongodbConnection();
		MongoClient mongoclient = null;
		mongoclient = mongo.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("employee");
		
		BasicDBObject credentials = new BasicDBObject(employeeCredentials);
		
		DBObject object = collection.findOne(credentials);
		
		if(object != null)
		{
			credentials.remove("password");
			credentials.append("login", true);
		}
		else
		{
			credentials.put("login", false);
		}
		System.out.println(credentials);
		
		return credentials;
	}
	
	public BasicDBObject emloyeeLinkedInLogin(JSONObject employeeLinkedInCredentials) throws UnknownHostException
	{
		
		MongodbConnection mongo = new MongodbConnection();
		MongoClient mongoclient = null;
		mongoclient = mongo.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("thirdpartyemployee");
		
		BasicDBObject credentials = new BasicDBObject(employeeLinkedInCredentials);
		
		DBObject object = collection.findOne(credentials);
		
		if(object != null)
		{
			credentials.append("email",credentials.get("linkedinemail"));
			credentials.remove("linkedinemail");
			credentials.append("isUserExists", true);
		}
		else
		{
			credentials.put("isUserExists", false);
		}
		System.out.println(credentials);
		
		return credentials;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject saveResume(MultipartFile file,net.sf.json.JSONObject userDetails) throws Exception 
	{	
		
		JSONObject employeeFileDetails = new JSONObject();
		
		String emailID = userDetails.getString("email");
		String industryType = userDetails.getString("industrytype");
		final String rootDirectory = "D:\\resumes\\"+industryType;
		
		File createDirectory = new File(rootDirectory);		
		
		
		if (!createDirectory.exists()) 		        
		    createDirectory.mkdir();
		
		String fileName = file.getOriginalFilename();
		String extension=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		
		String modifiedFileName = fileName.substring(0,fileName.lastIndexOf(".")).concat("-"+emailID).concat(extension);
		
		employeeFileDetails.put("FileName",modifiedFileName);
		
		String modifiedFileNamewithDirectory = rootDirectory.concat("\\"+fileName.substring(0,fileName.lastIndexOf("."))).concat("-"+emailID).concat(extension);
		System.out.println("Modified file Name is : "+modifiedFileNamewithDirectory);
		
		employeeFileDetails.put("FilePath", modifiedFileNamewithDirectory);
		// Create File
		File saveResume = new File(modifiedFileNamewithDirectory);
		file.transferTo(saveResume);
		
		File checking = new File(modifiedFileNamewithDirectory);
		if(checking.isFile())
		{
			System.out.println("Successfully created");
		}
		else
		{
			System.out.println("Failed to create");
		}
		return employeeFileDetails;
	}

	
}
