package com.justbootup.blouda.serviceApi;

import org.json.simple.JSONObject;


public interface IEmployeeFriendsRequest {

	public JSONObject getFriendsList(JSONObject queryParams);
	public JSONObject sendFriendRequest(JSONObject employeeList);
	public JSONObject getRequestedFriends(JSONObject currentemployee);
	public JSONObject getRequestedFriendsCount(JSONObject currentEmploye);
	
}
