package com.justbootup.blouda.dao;


 
import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.justbootup.blouda.util.SocialNetworkProperties;

@Repository
public class EmployeeSocialRegisterDao {	
	
	@Autowired
	SocialNetworkProperties socialProperties;
	
	@SuppressWarnings("unchecked")
	public JSONObject getLinkedInLoginUrl()
	{	
			// This is for state field of API
		socialProperties.setLinkedin_state(RandomStringUtils.randomAlphanumeric(20));
		
		String authorizationUrl ="https://www.linkedin.com/uas/oauth2/authorization?client_id="+socialProperties.getLinkedin_clientId()+"&redirect_uri="+socialProperties.getLinkedin_redirectUrl()+"&state="+socialProperties.getLinkedin_state()+"&response_type=code";
		
		JSONObject loginUrl = new JSONObject();
		loginUrl.put("loginurl", authorizationUrl);
		System.out.println(authorizationUrl);
		return loginUrl;
	}
		
}
