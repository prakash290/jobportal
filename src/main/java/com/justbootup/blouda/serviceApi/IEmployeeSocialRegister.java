package com.justbootup.blouda.serviceApi;

import java.util.HashMap;

import org.json.simple.JSONObject;

public interface IEmployeeSocialRegister {

	public JSONObject getLinkedInLoginUrl();	
	public JSONObject createNewLinkedInEmployee(HashMap<String, String> authorizationParams);
	public JSONObject getGoogleLoginUrl();
	public JSONObject createNewEmployeeUsingGoogle(HashMap<String, String> authorizationParams);
	public JSONObject generateFacebooklongliveToken(JSONObject facebookDetails) throws Exception;
	
	public JSONObject employeeCredentials(JSONObject passEmailForGettingCredentials);
	public JSONObject createNewEmployeeUsingSocialNetwork(JSONObject newEmployeeDetails, JSONObject socialNetworkDetails);
	
	public JSONObject checkisEmployeeExists(JSONObject employeeEmail,JSONObject updateSocialCredentials);
	
}
