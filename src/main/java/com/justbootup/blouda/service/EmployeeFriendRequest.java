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
	public void getFriendsList(JSONObject queryParams) {		
		employerFriendRequestDao.getFriendsList(queryParams);
	}
	
	

}
