package com.justbootup.blouda.service;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justbootup.blouda.dao.EmployeeBulidNodeDao;
import com.justbootup.blouda.serviceApi.IEmployeeBuildGraph;

@Service
public class EmployeeBuildGraph implements IEmployeeBuildGraph{

	@Autowired
	EmployeeBulidNodeDao employeebuildnode;
	
	
	public void createEmployeeNode(JSONObject employee) {	
		employeebuildnode.createEmployeeNode(employee);		
	}


	@Override
	public void createEmployeeProfileNode(org.json.simple.JSONObject employeeProfile) {		
		employeebuildnode.newEmployeeProfileNode(employeeProfile);
	}

}
