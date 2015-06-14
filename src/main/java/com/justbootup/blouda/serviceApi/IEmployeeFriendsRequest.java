package com.justbootup.blouda.serviceApi;

import org.json.simple.JSONObject;

public interface IEmployeeFriendsRequest {

	public JSONObject getFriendsList(JSONObject queryParams);
	public JSONObject sendFriendRequest(JSONObject employeeList);
	public JSONObject getRequestedFriends(JSONObject currentemployee);
	public JSONObject getRequestedFriendsCount(JSONObject currentEmploye);
	public JSONObject updateFriendRequest(JSONObject employeeFriends);
	public JSONObject showFriends(JSONObject friendsList);
	public JSONObject updateEmployeeFriendsRestriction(JSONObject plan);
	public JSONObject sendInmail(JSONObject sendmail);
	
}
