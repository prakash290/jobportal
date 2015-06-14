package com.justbootup.blouda.serviceApi;

import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;

public interface IEmployeeEmail {
	public JSONObject sendInmail(JSONObject sendmail);
	public BasicDBObject getInboxMails(JSONObject employee);	
	public BasicDBObject getsendMails(JSONObject employee);
	public JSONObject sendReply(JSONObject employee);
}
