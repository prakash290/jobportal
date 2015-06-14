package com.justbootup.blouda.dao;


 
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.impl.conn.Wire;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.justbootup.blouda.util.SocialNetworkProperties;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;


@Repository
public class EmployeeSocialRegisterDao {	
	
	@Autowired
	SocialNetworkProperties socialProperties;	
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	MongoClient client;
	
	@SuppressWarnings("unchecked")
	public JSONObject getLinkedInLoginUrl()
	{	
			// This is for state field of API
		socialProperties.setLinkedin_state(RandomStringUtils.randomAlphanumeric(20));
		
		String authorizationUrl ="https://www.linkedin.com/uas/oauth2/authorization?client_id="+socialProperties.linkedin_clientId+"&redirect_uri="+socialProperties.linkedin_redirectUrl+"&state="+socialProperties.getLinkedin_state()+"&response_type=code";
		
		JSONObject loginUrl = new JSONObject();
		loginUrl.put("loginurl", authorizationUrl);
		System.out.println(authorizationUrl);
		return loginUrl;
	}
	

	@SuppressWarnings("unchecked")
	public JSONObject createNewLinkedinEmployee(HashMap<String, String> linkedInParams)	 
	{	
		JSONObject employee = new JSONObject();
		
		try
		{
			// verfiy state Params
			if(linkedInParams.get("state").equals(socialProperties.getLinkedin_state()))
			{
				System.out.println("Request is came from our app only");
				
				System.out.println(linkedInParams);
				
				String accessTokenUrl = "https://www.linkedin.com/uas/oauth2/accessToken";
				
				MultiValueMap<String,String> rquestParams=new LinkedMultiValueMap<String,String>();			 
				rquestParams.add("grant_type", "authorization_code");
				rquestParams.add("code", linkedInParams.get("code"));
				rquestParams.add("redirect_uri", socialProperties.linkedin_redirectUrl);
				rquestParams.add("client_id", socialProperties.linkedin_clientId);
				rquestParams.add("client_secret", socialProperties.linkedin_clientSecret);
				
				HttpHeaders requestHeaders=new HttpHeaders();
			    requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			    requestHeaders.set("Accept","application/json");
			    
			    HttpEntity<MultiValueMap<String,String>> requestEntity=new HttpEntity<MultiValueMap<String,String>>(rquestParams,requestHeaders);
				
				ResponseEntity<JSONObject> response = restTemplate.exchange(accessTokenUrl,HttpMethod.POST,requestEntity,JSONObject.class);
								
				if(response.getStatusCode().equals(HttpStatus.OK))
				{
					//Get employee Profile and start session
					System.out.println("True Condition");
					JSONObject result = response.getBody();
					System.out.println("access_token is : "+result.get("access_token"));
					result.put("appauthorizationcode", linkedInParams.get("code"));					
					employee.put("Credential", result);
					employee.put("socialnetworkname", "linkedIn");
					employee.put("Profile",getLinkedInEmployeeProfile(result.get("access_token").toString()));
				}
				else
				{
					
				}
				
				System.out.println(response.getBody() +" , "+response.getHeaders());
			}
			else
			{
				System.out.println("Something went wrong");
			}			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("The Above Exception Rasied in EmployeeSocialRegister Class");
		}
		return employee;
	}
	
	
	public JSONObject getLinkedInEmployeeProfile(String oauthAccessToken){		
		
		JSONObject linkedInemployee = new JSONObject();
		
		try {
			
			// https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,location,industry,summary,positions,email-address,specialties,picture-url,public-profile-url)?format=json
			String getProfileUrl = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,location,industry,summary,positions,email-address,specialties,picture-url,public-profile-url)";
			
			HttpHeaders authenticationHeader = new HttpHeaders();			
			authenticationHeader.set("Authorization", "Bearer "+oauthAccessToken);
			authenticationHeader.set("x-li-format", "json");
			authenticationHeader.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<?> requestEntity = new HttpEntity<>(authenticationHeader);
			
			ResponseEntity<JSONObject> response = restTemplate.exchange(getProfileUrl,HttpMethod.GET,requestEntity,JSONObject.class);
			linkedInemployee = response.getBody();			
			
			System.out.println(linkedInemployee);
			
		} catch (Exception e) {			
			e.printStackTrace();			
		}
		
		return linkedInemployee;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getGoogleLoginUrl(){
		
		JSONObject loginUrl = new JSONObject();
		
		try{
			
			socialProperties.setGoogle_state(RandomStringUtils.randomAlphanumeric(20));			
			String googleauthorizationUrl ="https://accounts.google.com/o/oauth2/auth?scope="+socialProperties.google_scopes+"&state="+socialProperties.getGoogle_state()+"&redirect_uri="+socialProperties.google_redirectUrl+"&response_type=code&client_id="+socialProperties.google_clientId+"&access_type=offline&approval_prompt=force";			
			loginUrl.put("loginurl", googleauthorizationUrl);
			System.out.println(googleauthorizationUrl);
			
		}
		catch(Exception e)
		{
			System.out.println("Exception Raised in getGoogleLoginUrl");
		}
		return loginUrl;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject createNewEmployeeUsingGoogle(HashMap<String, String> googleauthorizationParams)	 
	{	
		JSONObject googleEmployee = new JSONObject();
		
		try
		{
			System.out.println("google state is : "+googleauthorizationParams.get("state"));
			System.out.println("class state is : "+socialProperties.getGoogle_state());
			// verfiy state Params
			if(googleauthorizationParams.get("state").equals(socialProperties.getGoogle_state()))
			{
				System.out.println("Request is came from our app only");
				
				System.out.println(googleauthorizationParams);
				
				MultiValueMap<String,String> paramsForRefreshToken=new LinkedMultiValueMap<String,String>();
				
				paramsForRefreshToken.add("code", googleauthorizationParams.get("code").toString());
				paramsForRefreshToken.add("client_id", socialProperties.google_clientId);
				paramsForRefreshToken.add("client_secret", socialProperties.google_clientSecret);
				paramsForRefreshToken.add("redirect_uri", socialProperties.google_redirectUrl);
				paramsForRefreshToken.add("grant_type", "authorization_code");
				
				
				HttpHeaders requestHeaders=new HttpHeaders();
				requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);		
				HttpEntity<MultiValueMap<String,String>> requestEntity=new HttpEntity<MultiValueMap<String,String>>(paramsForRefreshToken,requestHeaders);
				
				String url = "https://accounts.google.com/o/oauth2/token";
				
				
				
				ResponseEntity<JSONObject> response = restTemplate.exchange(url,HttpMethod.POST,requestEntity,JSONObject.class);
				JSONObject googleCredentials = response.getBody();
				
				if(response.getStatusCode().equals(HttpStatus.OK))
				{
					//Get employee Profile and start session
					System.out.println("True Condition");
					
					System.out.println("refresh token is "+googleCredentials.get("refresh_token").toString());	
					
					googleEmployee.put("Credential", googleCredentials);
					googleEmployee.put("Profile",getEmployeeProfilefromGoogle(googleCredentials.get("access_token").toString()));
					
					
					// This call is for getting access token using refresh token.
					//getGoogleAccessTokenUsingRefreshToken(googleCredentials.get("refresh_token").toString(),googleCredentials.get("access_token").toString());
				}
				else
				{
					
				}
				
				System.out.println(response.getBody() +" , "+response.getHeaders());
			}
			else
			{
				System.out.println("Something went wrong");
			}			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("The Above Exception Rasied in EmployeeSocialRegister Class");
		}
		return googleEmployee;
	}
	
	public JSONObject getEmployeeProfilefromGoogle(String googleoauthAccessToken){		
		
		JSONObject googleEmployeeProfile = new JSONObject();
		try {
			String getProfileUrl = "https://www.googleapis.com/oauth2/v1/userinfo?access_token="+googleoauthAccessToken;
			
			HttpHeaders authenticationHeader = new HttpHeaders();		
			
			authenticationHeader.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<?> requestEntity = new HttpEntity<>(authenticationHeader);
			
			ResponseEntity<JSONObject> response = restTemplate.exchange(getProfileUrl,HttpMethod.GET,requestEntity,JSONObject.class);
			
			googleEmployeeProfile = response.getBody();
			System.out.println(response.getBody());
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return googleEmployeeProfile;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject generateFacebooklongliveToken(JSONObject facebook) throws Exception
	{
		JSONObject facebookEmployee = new JSONObject();
		try {
			
			facebookEmployee.put("Credential", getLongLivedAccessToken(facebook));
			facebookEmployee.put("Profile",getEmployeeProfileFromFacebook(getLongLivedAccessToken(facebook)));
			facebookEmployee.put("socialnetworkname", "facebook");
			
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("Exception Raised In employee social register dao class of facebook method ");
			
		}
		return facebookEmployee;
	}
	
	public JSONObject getEmployeeProfileFromFacebook(String accessToken) throws Exception
	{
		System.out.println("Access Token is : "+accessToken);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);		
		String url = "https://graph.facebook.com/v2.3/me?access_token="+accessToken;
		URI FacebookProfileUrl = new URI(url);	
		
		ResponseEntity<JSONObject> response = restTemplate.getForEntity(FacebookProfileUrl, JSONObject.class);
		
		System.out.println("From Facebook is "+response);
		return response.getBody();
	}
	
	
	@SuppressWarnings("unchecked")
	public JSONObject createNewEmployeeUsingSocialNetworks(JSONObject newEmployee,JSONObject socialNetworkDetails)
	{
		
		JSONObject newEmployeeDetails = new JSONObject();
		
		System.out.println("socialNetworkDetails is : "+socialNetworkDetails);
		try {
			
				
			MongoClient mongoclient = client; 
			
			DB db = mongoclient.getDB("jobportal");			
			DBCollection collection = db.getCollection("employee");			
			newEmployee.put("isRegistrationfromApp", false);
			newEmployee.put("isRegistrationfromSocial", true);
			newEmployee.put("LoggedInsocialVendarName", socialNetworkDetails.get("socialnetworkname"));
			
			 Date currentDate = new Date( );
		      SimpleDateFormat currenDateFormat = 
		      new SimpleDateFormat ("dd-MM-yyyy hh:mm:ss");
		      
			BasicDBObject timing = new BasicDBObject();				
			timing.append("createdAt",currenDateFormat.format(currentDate));				
			newEmployee.put("employeeSession", timing);
			timing.append("lastLoggedIn",currenDateFormat.format(currentDate));		
			
			BasicDBObject socialCredentials = new BasicDBObject();
			socialCredentials.append(socialNetworkDetails.get("socialnetworkname").toString(), socialNetworkDetails);
			
			newEmployee.put("socialnetworkCredentials",socialCredentials);					
			
			BasicDBObject appInsertion = (BasicDBObject) JSON.parse(newEmployee.toJSONString());
			
			WriteResult result = collection.insert(appInsertion);
			
			System.out.println("write result object is : "+result);
			
			insertEmployeeConnection(newEmployee);
			
			JSONObject profiles = (JSONObject) socialNetworkDetails.get("Profile");
			
			String socialNetworkName = socialNetworkDetails.get("socialnetworkname").toString();
			newEmployeeDetails = getSocialNetworkEmployeeProfile(socialNetworkName,profiles);
			newEmployeeDetails.put("email",newEmployee.get("email"));
			System.out.println("newEmployee Details is : "+newEmployeeDetails);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Raised in EmployeeSocialRegisterclass");			
		}
		return newEmployeeDetails;
	}	
	
	private void insertEmployeeConnection(JSONObject employee){

		try {
			
			MongoClient mongoclient = client; 
			
			DB db = mongoclient.getDB("jobportal");			
			DBCollection collection = db.getCollection("employeeconnections");		
			
			BasicDBObject restriction = new BasicDBObject();			
			
			 Date currentDate = new Date( );
		      SimpleDateFormat currenDateFormat = 
		      new SimpleDateFormat ("dd-MM-yyyy");
		      
		      restriction.append("startdate", currenDateFormat.format(currentDate));
		      restriction.append("totalfriendrequest", 10);	
		      restriction.append("usedfriendrequest",0);
		      restriction.append("remainingfriendrequest",10);
		      restriction.append("totalinmails", 10);
		      restriction.append("usedinmails",0);
		      restriction.append("remaininginmails",10);		    
		    
			BasicDBObject insertQuery = new BasicDBObject();
			insertQuery.append("email", employee.get("email"));
			insertQuery.append("restriction", restriction);
			
			
			WriteResult result = collection.insert(insertQuery);
			System.out.println("Result is : "+result);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("The Following Exeception is raised in insertEmployeeConnection Method");
		}
		
	}
	
	private String getLongLivedAccessToken(JSONObject facebook) throws Exception{
		
		URI uri1 = new URI("https://graph.facebook.com/oauth/access_token?grant_type=fb_exchange_token&client_id="+socialProperties.facebook_clientId+"&client_secret="+socialProperties.facebook_clientSecret+"&fb_exchange_token="+facebook.get("accessToken").toString());
		RestTemplate restTemplated = new RestTemplate();
		
		ResponseEntity<String> response = restTemplated.getForEntity(uri1, String.class);
		System.out.println(response.getBody());
		System.out.println("Status code is : "+response.getStatusCode());
		
		String longLivedAccessToken = response.getBody();
		longLivedAccessToken = longLivedAccessToken.substring(longLivedAccessToken.indexOf("=")+1,longLivedAccessToken.indexOf("&") );			
		return longLivedAccessToken;
	}
	private JSONObject getGoogleAccessTokenUsingRefreshToken(String refreshToken,String oldAccessToken){		
		
		try {
			
			MultiValueMap<String,String> paramsForAccesTokenUsingRefreshToken=new LinkedMultiValueMap<String,String>();		
			
			paramsForAccesTokenUsingRefreshToken.add("client_id", socialProperties.google_clientId);
			paramsForAccesTokenUsingRefreshToken.add("client_secret", socialProperties.google_clientSecret);
			paramsForAccesTokenUsingRefreshToken.add("refresh_token", refreshToken);
			paramsForAccesTokenUsingRefreshToken.add("grant_type", "refresh_token");
			
			
			HttpHeaders requestHeadersForNewAccessToken=new HttpHeaders();
			requestHeadersForNewAccessToken.setContentType(MediaType.APPLICATION_FORM_URLENCODED);		
			HttpEntity<MultiValueMap<String,String>> requestEntity=new HttpEntity<MultiValueMap<String,String>>(paramsForAccesTokenUsingRefreshToken,requestHeadersForNewAccessToken);
			
			String url = "https://accounts.google.com/o/oauth2/token";		
			
			ResponseEntity<JSONObject> newresponse = restTemplate.exchange(url,HttpMethod.POST,requestEntity,JSONObject.class);
			JSONObject newgoogleCredentials = newresponse.getBody();
			if(oldAccessToken.equals(newgoogleCredentials.get("access_token").toString()))
			{
				System.out.println("Two token are same");
			}
			else
			{
				System.out.println("Two tokens are different");
			}
			System.out.println("New Access Token is : "+newgoogleCredentials);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject employeeEmail(JSONObject socialEmployeeObject)
	{
		JSONObject employeeEmail = new JSONObject();
		try {
			
			JSONObject employeeProfiles = (JSONObject) socialEmployeeObject.get("Profile");			
			HashMap<String, String> emailFields = new HashMap<String, String>();
			emailFields.put("google", "email");
			emailFields.put("linkedIn","emailAddress");
			emailFields.put("facebook", "email");
			System.out.println("social network name : "+socialEmployeeObject.get("socialnetworkname"));
			employeeEmail.put("email",employeeProfiles.get(emailFields.get(socialEmployeeObject.get("socialnetworkname"))));			
			System.out.println("employeeEmail object is : "+employeeEmail);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Excepion Raised in employeesocial register dao class");
		}
		
		
		return employeeEmail;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getSocialNetworkEmployeeProfile(String networkName,JSONObject profile)
	{
		JSONObject empProfile = new JSONObject();
		try{
			
			switch (networkName) {
			case "google":				
				empProfile.put("fullname",profile.get("name").toString() );
				break;
			case "linkedIn":
				
				String firstName = profile.get("firstName").toString();
				String lastName = profile.get("lastName").toString();			
				
				empProfile.put("fullname", firstName+lastName);
			
				ArrayList<String> profileFields = new ArrayList<String>();
				profileFields.add("headline");
				profileFields.add("summary");
				
				for(String field:profileFields)
				{
					if(profile.containsKey(field))
					{
						empProfile.put(field,profile.get(field));
					}
				}
				
				break;
				
			case "facebook":
				empProfile.put("fullname",profile.get("name").toString() );
				break;		

			default:
				System.out.println("something went wrong in getting social Profiles");
				break;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("The following exception is raised in employee social register dao");
		}
		return empProfile;
	}
	@SuppressWarnings("unchecked")
	public JSONObject checkEmployeeExists(JSONObject employeeEmail,JSONObject updateSocialCredentials)
	{
		JSONObject isEmployeeExists = new JSONObject();
		
		try {			
				MongoClient mongoclient = client; 			
				DB db = mongoclient.getDB("jobportal");					
				DBCollection collection = db.getCollection("employee");
				
				BasicDBObject query = new BasicDBObject();
				query.append("email", employeeEmail.get("email"));
				
				BasicDBObject projection = new BasicDBObject();
				projection.put("_id",0);
				projection.put("email",1);				
				
				
				DBCursor result = collection.find(query,projection);
				System.out.println("result count : "+result.count()+" result size : "+result.size());
				if(result.count() > 0)
				{
					System.out.println("User already Existes go and update theire social credentials field");
					System.out.println("Social Network credentials is : "+updateSocialCredentials);
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
					String nowAsString = df.format(new Date());
					Date updateDate = df.parse(nowAsString);
					System.out.println("Update date is : "+updateDate);
					updateSocialCredentials.put("lastUpdated", updateDate);
					System.out.println("vendor details is : "+updateSocialCredentials.get("socialnetworkname"));
					BasicDBObject socialObject = new BasicDBObject();
					socialObject.append("socialnetworkCredentials."+updateSocialCredentials.get("socialnetworkname").toString(),updateSocialCredentials);
					System.out.println("Update query is : "+socialObject);
					
					BasicDBObject updateQuery = new BasicDBObject();
					updateQuery.append("$set",socialObject);
					
					WriteResult updateResult = collection.update(query, updateQuery);
					
					if(updateResult.isUpdateOfExisting())

					{	System.out.println("Successfully Updated");
						isEmployeeExists.put("isEmployeeExists", true);						
					}
				}
				else
				{
					System.out.println("new User");
					isEmployeeExists.put("isEmployeeExists", false);	
				}
				
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Excepion Raised in employeesocial register dao class of checkEmployeeExists Method");
		}
		
		
		return isEmployeeExists;
	}
	
	
		
}
