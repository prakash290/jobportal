package com.justbootup.blouda.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SocialNetworkProperties {
	
	@Value("${linkedin_clientid}")
	public String linkedin_clientId;
	
	@Value("${linkedin_client_secret}")
	public String linkedin_clientSecret;
	
	@Value("${linkedin_redirecturl}")
	public String linkedin_redirectUrl;
	
	public String linkedin_state;
	
	@Value("${google_clientid}")
	public String google_clientId;
	
	@Value("${google_client_secret}")
	public String google_clientSecret;
	
	@Value("${google_scopes}")
	public String google_scopes;
	
	@Value("${google_redirecturl}")
	public String google_redirectUrl;
	
	public String google_state;	
	
	@Value("${facebook_clientid}")
	public String facebook_clientId;
	
	@Value("${facebook_client_secret}")
	public String facebook_clientSecret;	
	

	public String getGoogle_state() {
		return google_state;
	}
	public void setGoogle_state(String google_state) {
		this.google_state = google_state;
	}
	
	
	public String getLinkedin_state() {
		return linkedin_state;
	}
	public void setLinkedin_state(String linkedin_state) {
		this.linkedin_state = linkedin_state;
	}
	


}
