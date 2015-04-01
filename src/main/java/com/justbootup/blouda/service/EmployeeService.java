package com.justbootup.blouda.service;

import java.net.UnknownHostException;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justbootup.blouda.dao.EmployeeDao;
import com.justbootup.blouda.domainObjects.User;
import com.justbootup.blouda.serviceApi.IEmployeeService;

@Service
public class EmployeeService implements IEmployeeService {
	
	@Autowired
	private EmployeeDao userDao;

	@Override
	public List<JSONObject> getAllUsers() {
		
		return userDao.getAllUsers();
	}

	@Override
	public JSONObject saveUser(JSONObject json) {
		
		return userDao.saveUser(json);
	}

	@Override
	public JSONObject employeeUpdateProfile(JSONObject jsonEmployeeProfile) throws UnknownHostException {
		
		return userDao.employeeUpdateProfile(jsonEmployeeProfile);
	}

	

	
}
