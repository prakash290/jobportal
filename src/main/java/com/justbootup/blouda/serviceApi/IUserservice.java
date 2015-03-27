package com.justbootup.blouda.serviceApi;

import java.util.List;

import org.json.simple.JSONObject;

import com.justbootup.blouda.domainObjects.User;

public interface IUserservice {
	
	public List<JSONObject> getAllUsers();
	public boolean saveUser(JSONObject user);

}
