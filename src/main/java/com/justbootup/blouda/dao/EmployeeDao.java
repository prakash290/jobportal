package com.justbootup.blouda.dao;

import java.net.UnknownHostException;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoDataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.justbootup.blouda.domainObjects.User;
import com.justbootup.blouda.util.MongodbConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
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
	
	public JSONObject saveUser(JSONObject json)
	{
		try {
			ObjectId id = new ObjectId();
			System.out.println("Inserted Id value is : "+id);
			json.put("_id", id);
			
			mongoTemplate.insert(json,"employee");
			
		} catch (Exception e) {
			
			json.put("error", "failure");
            return json;
		}
		
		
		return json;
	}
	
	public JSONObject employeeUpdateProfile(JSONObject jsonEmployeeProfile) throws UnknownHostException{
		
		MongodbConnection mongo = new MongodbConnection();
		MongoClient mongoclient = null;
		mongoclient = mongo.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("employee");
		
		// Query condition for email
		BasicDBObject condition = new BasicDBObject();
		condition.append("email", jsonEmployeeProfile.get("useremail"));
		jsonEmployeeProfile.remove("useremail");
		
		// update condition
		BasicDBObject updateQuery = new BasicDBObject();
		updateQuery.append("$set",jsonEmployeeProfile);
		System.out.println(updateQuery);		
		
		WriteResult result =collection.update(condition, updateQuery, true, false);
		
		JSONObject returnStatus = new JSONObject();
		if(result.isUpdateOfExisting())
		{
			returnStatus.put("update", true);
		}
		else
		{
			returnStatus.put("update", false);
		}
		
		/*String employeeemail = (String) jsonEmployeeProfile.get("useremail");
		jsonEmployeeProfile.remove("useremail");	
				
		
		mongoTemplate.updateFirst(new Query(Criteria.where("email").is(employeeemail)), Update.update("employee",jsonEmployeeProfile), JSONObject.class);*/
		return returnStatus;
	}
	
}
