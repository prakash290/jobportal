package com.justbootup.blouda.service;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justbootup.blouda.dao.EmployeeSocialRegisterDao;
import com.justbootup.blouda.serviceApi.IEmployeeSocialRegister;


@Service
public class EmployeeSocialRegisterService implements IEmployeeSocialRegister{

	@Autowired
	EmployeeSocialRegisterDao employeeSocialNetworkDao;
	
	@Override
	public JSONObject getLinkedInLoginUrl() {	
		return employeeSocialNetworkDao.getLinkedInLoginUrl();
	}

	@Override
	public JSONObject createNewLinkedInEmployee(HashMap<String, String> linkedParams) {		
		return employeeSocialNetworkDao.createNewLinkedinEmployee(linkedParams);
	}

	@Override
	public JSONObject getGoogleLoginUrl() {		
		return employeeSocialNetworkDao.getGoogleLoginUrl();
	}

	@Override
	public JSONObject createNewEmployeeUsingGoogle(HashMap<String, String> googleauthorizationParams) {		
		return employeeSocialNetworkDao.createNewEmployeeUsingGoogle(googleauthorizationParams);
	}

	@Override
	public JSONObject generateFacebooklongliveToken(JSONObject facebookDetails) throws Exception {		
		return employeeSocialNetworkDao.generateFacebooklongliveToken(facebookDetails);
	}

	@Override
	public JSONObject employeeCredentials(JSONObject socialEmployeeObject){		
		return employeeSocialNetworkDao.employeeEmail(socialEmployeeObject);
	}

	@Override
	public JSONObject createNewEmployeeUsingSocialNetwork(JSONObject newEmployee,JSONObject socialNetworkDetails) {		
		return employeeSocialNetworkDao.createNewEmployeeUsingSocialNetworks(newEmployee, socialNetworkDetails);
	}

	@Override
	public JSONObject checkisEmployeeExists(JSONObject employeeEmail, JSONObject updateSocialCredentials) {			
		return employeeSocialNetworkDao.checkEmployeeExists(employeeEmail,updateSocialCredentials);
	}
	
}
