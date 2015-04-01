package com.justbootup.blouda.serviceApi;

import java.net.UnknownHostException;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.justbootup.blouda.domainObjects.User;

public interface IEmployeeService {
	
	public List<JSONObject> getAllUsers();
	public JSONObject saveUser(JSONObject user);
	public JSONObject employeeUpdateProfile(JSONObject jsonEmployeeProfile) throws UnknownHostException;	

}
