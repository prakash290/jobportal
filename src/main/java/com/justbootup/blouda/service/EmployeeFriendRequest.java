package com.justbootup.blouda.service;


import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;





import com.justbootup.blouda.dao.EmployeeFriendRequestDao;
import com.justbootup.blouda.serviceApi.IEmployeeFriendsRequest;

@Service
public class EmployeeFriendRequest implements IEmployeeFriendsRequest{
	
	@Autowired
	private EmployeeFriendRequestDao employerFriendRequestDao;
	
	@Override
	public JSONObject getFriendsList(JSONObject queryParams) {		
		return employerFriendRequestDao.getFriendsList(queryParams);
	}

	@Override
	public JSONObject sendFriendRequest(JSONObject employeeList) {		
		return employerFriendRequestDao.sendFriendRequest(employeeList);
	}

	@Override
	public JSONObject getRequestedFriends(JSONObject currentemployee) {		
		return employerFriendRequestDao.getRequestedFriends(currentemployee);
	}

	@Override
	public JSONObject getRequestedFriendsCount(JSONObject currentEmploye) {		
		return employerFriendRequestDao.getRequestedFriendsCount(currentEmploye);
	}

	@Override
	public JSONObject updateFriendRequest(JSONObject employeeFriends) {		
		return employerFriendRequestDao.updateFriendRequest(employeeFriends);
	}

	@Override
	public JSONObject showFriends(JSONObject friendsList) {		
		return employerFriendRequestDao.showFriendsList(friendsList);
	}
	
	

}
