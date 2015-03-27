/**
 * 
 */
package com.justbootup.blouda.service;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justbootup.blouda.dao.EmployerSearchDao;
import com.justbootup.blouda.serviceApi.IEmployerSearchService;

/**
 * @author admin
 *
 */
@Service
public class EmployerSearchService implements IEmployerSearchService {

	@Autowired
	private EmployerSearchDao employerSearchDao;

	@Override
	public List getAllIndustries() {

		return employerSearchDao.getAllIndustries();
	}

}
