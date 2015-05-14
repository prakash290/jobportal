package com.justbootup.blouda.serviceApi;

import java.net.UnknownHostException;
import java.util.List;
import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.BasicDBObject;

public interface IEmployeeService {
	
	public List<JSONObject> getAllUsers();
	public net.sf.json.JSONObject saveUser(MultipartFile file,net.sf.json.JSONObject employeeDetails);
	public JSONObject employeeUpdateProfile(JSONObject jsonEmployeeProfile) throws UnknownHostException;	
	public BasicDBObject getEmployeeProfile(JSONObject employeeSession) throws UnknownHostException;
	public BasicDBObject emloyeeLogin(JSONObject employeeCredentials) throws UnknownHostException;
	public BasicDBObject emloyeeLinkedInLogin(JSONObject employeeLinkedInCredentials) throws UnknownHostException;
	public JSONObject generateLinkedInAccessToken(JSONObject cookiecredentials);
}
