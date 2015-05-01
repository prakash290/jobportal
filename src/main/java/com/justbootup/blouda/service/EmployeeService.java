package com.justbootup.blouda.service;

import java.net.UnknownHostException;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.justbootup.blouda.dao.EmployeeDao;
import com.justbootup.blouda.serviceApi.IEmployeeService;
import com.mongodb.BasicDBObject;

@Service
public class EmployeeService implements IEmployeeService {
	
	@Autowired
	private EmployeeDao employeeDao;

	@Override
	public List<JSONObject> getAllUsers() {
		
		return employeeDao.getAllUsers();
	}

	@Override
	public net.sf.json.JSONObject saveUser(MultipartFile resume,net.sf.json.JSONObject employeeDetails) {
		
		return employeeDao.saveUser(resume,employeeDetails);
	}

	@Override
	public JSONObject employeeUpdateProfile(JSONObject jsonEmployeeProfile) throws UnknownHostException {
		
		return employeeDao.employeeUpdateProfile(jsonEmployeeProfile);
	}

	@Override
	public BasicDBObject getEmployeeProfile(JSONObject employeeSession) throws UnknownHostException {
		
		return employeeDao.getEmployeeProfile(employeeSession);
	}

	@Override
	public BasicDBObject emloyeeLogin(JSONObject employeeCredentials) throws UnknownHostException {
	
		return employeeDao.employeeLogin(employeeCredentials);
	}

	@Override
	public BasicDBObject emloyeeLinkedInLogin(JSONObject employeeLinkedInCredentials) throws UnknownHostException {
		
		return employeeDao.emloyeeLinkedInLogin(employeeLinkedInCredentials);
	}

	

	
}
