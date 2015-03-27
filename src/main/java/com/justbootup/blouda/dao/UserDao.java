package com.justbootup.blouda.dao;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.justbootup.blouda.domainObjects.User;

@Repository
public class UserDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<JSONObject> getAllUsers()
	{
		System.out.println("user Dao Called");
		return mongoTemplate.findAll(JSONObject.class,"User");
	}
	public boolean saveUser(JSONObject json)
	{
		mongoTemplate.insert(json,"User");
	
		return true;
	}
	
}
