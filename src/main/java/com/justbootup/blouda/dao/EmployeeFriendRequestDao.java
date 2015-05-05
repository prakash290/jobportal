package com.justbootup.blouda.dao;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeFriendRequestDao {
	
	public void getFriendsList(JSONObject queryParams){
		System.out.println("Query params is : "+queryParams);
	}
	
}
