package com.justbootup.blouda.service;

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
	
}
