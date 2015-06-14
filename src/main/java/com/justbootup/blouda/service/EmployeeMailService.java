package com.justbootup.blouda.service;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justbootup.blouda.dao.EmployeeMailServiceDao;
import com.justbootup.blouda.serviceApi.IEmployeeEmail;
import com.mongodb.BasicDBObject;

@Service
public class EmployeeMailService implements IEmployeeEmail{
	
	@Autowired
	private EmployeeMailServiceDao employeemailservicedao;
	
	@Override
	public BasicDBObject getInboxMails(JSONObject employee) {		
		return employeemailservicedao.getInboxMails(employee);
	}

	@Override
	public JSONObject sendInmail(JSONObject sendmail) {		
		return employeemailservicedao.sendInmail(sendmail);
	}

	@Override
	public BasicDBObject getsendMails(JSONObject employee) {		
		return employeemailservicedao.getsendMails(employee);
	}

	@Override
	public JSONObject sendReply(JSONObject employee) {		
		return employeemailservicedao.sendReply(employee);
	}

}
