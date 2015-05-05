package com.justbootup.blouda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justbootup.blouda.dao.CommonThingsForAllDao;
import com.justbootup.blouda.serviceApi.ICommonThingsForAll;
import com.mongodb.BasicDBObject;

@Service
public class CommonThingsForAllServices implements ICommonThingsForAll{
	
	@Autowired
	private CommonThingsForAllDao commonThings;
	
	@Override
	public void getAllCompaniesName() {		
		commonThings.getAllCompaniesName();
	}

	@Override
	public void getAllLocations() {		
		commonThings.getAllLocations();
	}

	@Override
	public void getAllSkills() {	
		commonThings.getAllSkills();
	}

	@Override
	public void getAllIndustryTypes() {
				
	}

	@Override
	public BasicDBObject friendSearchViewData() {		
		return commonThings.friendSearchViewData();
	}

}
