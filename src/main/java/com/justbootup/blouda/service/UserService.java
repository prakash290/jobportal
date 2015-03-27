package com.justbootup.blouda.service;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justbootup.blouda.dao.UserDao;
import com.justbootup.blouda.domainObjects.User;
import com.justbootup.blouda.serviceApi.IUserservice;

@Service
public class UserService implements IUserservice {
	
	@Autowired
	private UserDao userDao;

	@Override
	public List<JSONObject> getAllUsers() {
		
		return userDao.getAllUsers();
	}

	@Override
	public boolean saveUser(JSONObject json) {
		
		return userDao.saveUser(json);
	}

	

	
}
