/**
 * 
 */
package com.justbootup.blouda.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author admin
 *
 */

@Repository
public class EmployerSearchDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public List getAllIndustries() {
		List<JSONObject> list = mongoTemplate.findAll(JSONObject.class,
				"employeeuser");
		
		
		return list;
	}

}
