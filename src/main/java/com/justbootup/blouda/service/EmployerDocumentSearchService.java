package com.justbootup.blouda.service;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justbootup.blouda.dao.EmployerDocumentSearchDao;
import com.justbootup.blouda.serviceApi.IEmployerDocumentSearchService;

@Service
public class EmployerDocumentSearchService implements IEmployerDocumentSearchService {

	@Autowired
	EmployerDocumentSearchDao employerDocSearchDao;
	
	@Override
	public void getDocuments() {		
		employerDocSearchDao.getDocuments();		
	}

	@Override
	public LinkedHashMap<String, Object> getWholeSearchDocuments(JSONObject keywords) {
		
		return employerDocSearchDao.getWholeSearchDocuments(keywords);
	}

	@Override
	public LinkedHashMap<String, Object> getMultikeywordSearchDocument(JSONObject multiSearchKeyword) {
	
		return employerDocSearchDao.getMultikeywordSearchDocument(multiSearchKeyword);
	}

	@Override
	public HashMap<String,Object> getEmployeeResume(JSONObject selectedEmployee) throws Exception{
		
		return employerDocSearchDao.getEmployeeResume(selectedEmployee);
	}
	

}
