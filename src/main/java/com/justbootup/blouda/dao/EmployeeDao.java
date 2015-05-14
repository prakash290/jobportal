package com.justbootup.blouda.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Scanner;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.ws.soap.AddressingFeature.Responses;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.GitHubTokenResponse;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.justbootup.blouda.util.MongodbConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

@Repository
public class EmployeeDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public BasicDBObject emloyeeLinkedInLogin(JSONObject employeeLinkedInCredentials) throws UnknownHostException
	{
		
		MongodbConnection mongo = new MongodbConnection();
		MongoClient mongoclient = null;
		mongoclient = mongo.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("thirdpartyemployee");
		
		BasicDBObject credentials = new BasicDBObject(employeeLinkedInCredentials);
		
		DBObject object = collection.findOne(credentials);
		
		if(object != null)
		{
			credentials.append("email",credentials.get("linkedinemail"));
			credentials.remove("linkedinemail");
			credentials.append("isUserExists", true);
		}
		else
		{
			credentials.put("isUserExists", false);
		}
		System.out.println(credentials);
		
		return credentials;
	}
	
	public BasicDBObject employeeLogin(JSONObject employeeCredentials) throws UnknownHostException
	{
		
		MongodbConnection mongo = new MongodbConnection();
		MongoClient mongoclient = null;
		mongoclient = mongo.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("employee");
		
		BasicDBObject credentials = new BasicDBObject(employeeCredentials);
		
		DBObject object = collection.findOne(credentials);
		
		if(object != null)
		{
			credentials.remove("password");
			credentials.append("login", true);
		}
		else
		{
			credentials.put("login", false);
		}
		System.out.println(credentials);
		
		return credentials;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject employeeUpdateProfile(JSONObject jsonEmployeeProfile) throws UnknownHostException{
		
		MongodbConnection mongo = new MongodbConnection();
		MongoClient mongoclient = null;
		mongoclient = mongo.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("employee");
		
		String email = (String) jsonEmployeeProfile.get("useremail");
		jsonEmployeeProfile.remove("useremail");
		
		// Query condition for email
		BasicDBObject condition = new BasicDBObject();
		condition.append("email", email);	
		
		// update condition
		BasicDBObject updateQuery = new BasicDBObject();
		updateQuery.append("$set",jsonEmployeeProfile);
		System.out.println(updateQuery);		
		
		WriteResult result =collection.update(condition, updateQuery, true, false);
		
		JSONObject returnStatus = new JSONObject();
		if(result.isUpdateOfExisting())
		{
			returnStatus.put("email", email);
		}
		else
		{
			returnStatus.put("update", false);
		}		
		
		return returnStatus;
	}
	
	public JSONObject generateLinkedinAccessToken(JSONObject cookiecredentials)
	{
		System.out.println(cookiecredentials);
		System.out.println(validateSignatures(cookiecredentials));		
		return null;
	}
	
	public List<JSONObject> getAllUsers()
	{
		System.out.println("user Dao Called");
		return mongoTemplate.findAll(JSONObject.class,"User");
	}
	
	@SuppressWarnings("unchecked")
	public BasicDBObject getEmployeeProfile(JSONObject employeeSession) throws UnknownHostException	
	{		
		MongodbConnection mongo = new MongodbConnection();
		MongoClient mongoclient = null;
		mongoclient = mongo.connect();
		
		DB db = mongoclient.getDB("jobportal");
		
		DBCollection collection = db.getCollection("employee");
		
		BasicDBObject condition = new BasicDBObject(employeeSession);
		
		DBCursor cursor = collection.find(condition);		
		
		 BasicDBObject userProfile = new BasicDBObject();
		 
		try {
		   while(cursor.hasNext()) {
			   userProfile = (BasicDBObject) cursor.next();		      
		   }
		} finally {
		   cursor.close();
		}
		
		
		return userProfile;
	}
	
	
	@SuppressWarnings("unchecked")
	private JSONObject saveResume(MultipartFile file,net.sf.json.JSONObject userDetails) throws Exception 
	{	
		
		JSONObject employeeFileDetails = new JSONObject();
		
		String emailID = userDetails.getString("email");
		String industryType = userDetails.getString("industrytype");
		final String rootDirectory = "D:\\resumes\\"+industryType;
		
		File createDirectory = new File(rootDirectory);		
		
		
		if (!createDirectory.exists()) 		        
		    createDirectory.mkdir();
		
		String fileName = file.getOriginalFilename();
		String extension=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		
		String modifiedFileName = fileName.substring(0,fileName.lastIndexOf(".")).concat("-"+emailID).concat(extension);
		
		employeeFileDetails.put("FileName",modifiedFileName);
		
		String modifiedFileNamewithDirectory = rootDirectory.concat("\\"+fileName.substring(0,fileName.lastIndexOf("."))).concat("-"+emailID).concat(extension);
		System.out.println("Modified file Name is : "+modifiedFileNamewithDirectory);
		
		employeeFileDetails.put("FilePath", modifiedFileNamewithDirectory);
		// Create File
		File saveResume = new File(modifiedFileNamewithDirectory);
		file.transferTo(saveResume);
		
		File checking = new File(modifiedFileNamewithDirectory);
		if(checking.isFile())
		{
			System.out.println("Successfully created");
		}
		else
		{
			System.out.println("Failed to create");
		}
		return employeeFileDetails;
	}
	
	@SuppressWarnings("unchecked")
	public net.sf.json.JSONObject saveUser(MultipartFile resume,net.sf.json.JSONObject employeeDetails)
	{
		try {
			if(employeeDetails.get("linkedinemail") == null) 
			{
			System.out.println("Before Inserting Data is : "+employeeDetails);
			ObjectId id = new ObjectId();
			System.out.println("Inserted Id value is : "+id);
			employeeDetails.put("_id", id);	
			System.out.println(resume.getContentType());
			JSONObject employeeFileDetails = saveResume(resume, employeeDetails);			
			
			employeeDetails.put("resumeDetails", employeeFileDetails);
			
			mongoTemplate.insert(employeeDetails,"employee");
			}
			else
			{
				System.out.println("Before Inserting Data is with linked in email : "+employeeDetails);
				MongodbConnection mongo = new MongodbConnection();
				MongoClient mongoclient = null;
				mongoclient = mongo.connect();
				
				DB db = mongoclient.getDB("jobportal");
				
				DBCollection collection = db.getCollection("employee");
				
				DBCollection thirdpartycollection = db.getCollection("thirdpartyemployee");
				
				String linkedinemail = (String) employeeDetails.get("linkedinemail");
				
				employeeDetails.remove("linkedinemail");
				
				ObjectId id = new ObjectId();
				System.out.println("Inserted Id value is : "+id);
				employeeDetails.put("_id", id);	
			
				
				BasicDBObject employee = new BasicDBObject(employeeDetails);
								
				collection.insert(employee);
				
				
				BasicDBObject thirdparty = new BasicDBObject();
				thirdparty.append("linkedinemail", linkedinemail);
				thirdpartycollection.insert(thirdparty);			
				
			}
			
		} catch (Exception e) {
			
			employeeDetails.put("error", "failure");
            return employeeDetails;
		}
		
		
		return employeeDetails;
	}
	
	
	@SuppressWarnings("unchecked")
	public void generateFacebooklongliveToken(JSONObject facebook) throws Exception{
		
		getLongLivedAccessToken(facebook);
		
			    
		RestTemplate restTemplated = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		
		
	
		 //restTemplated.exchange(uri1, HttpMethod.GET, request12, JSONObject.class);
       
		
		//ResponseEntity<JSONObject> response = restTemplated.exchange(uri1, HttpMethod.GET, request12, JSONObject.class);
		
		
		
		
		
		String accessToken = "CAAWyBDrRE3IBACj1Xp79xKJ3L50ug90RAnrRqMrGPdCUFAZCZCbd23V6NySFxJgAaywkoMZAfjkkOhPtY21lqIi6fTRp4avm7HWIpWBg9y6C2ZC2zlZB3cgg8DOF1XMXDQvYqViORYZCcu3r53TqaT7YkOXdePaUcUtQ55Go4AU8fj7mRLWKHVvObt8l0WlUoZD";
		String  newaccessToken = "CAAWyBDrRE3IBAPZASp5oul5w2VKPuhYeyh1eOjI1PW6Woj4lNWpmRiZAEuCyCTOrgwrXz2H0uhJDth5dlQTcRM6KWzM0hWDa9U0XEdFIv0AZCnW3QoJzswhwhZCcBak9qnjyP83hnwTQXByksgpS2fBbnD7dgNei1QRJhhZBjeY5y3oDmeYGJVIY9G0LmWgwZD";
	
		if(accessToken.equals(newaccessToken))
		{
			System.out.println("Sam");
		}
		else
		{
			System.out.println("not sam");
		}
		//String jsonResponse = restTemplated.getForObject("https://graph.facebook.com/me?fields=id,name &access_token=" + accessToken, String.class);
		
		String us = "https://graph.facebook.com/me/friends&access_token="+accessToken+"&debug=all";
		URI user = new URI(us);
		
		//net.sf.json.JSONObject o = net.sf.json.JSONObject.fromObject(jsonResponse);
		//System.out.println(o);
		
		ResponseEntity<JSONObject> res = restTemplated.getForEntity(user, JSONObject.class);
		
		System.out.println(res.getHeaders() +" , "+res.getBody()  );
		
		
		/* HttpHeaders requestHeaders1 = new HttpHeaders();
		requestHeaders1.setContentType(MediaType.APPLICATION_JSON);		
		
		
		HttpEntity<?> requestEntity1 = new HttpEntity<Object>( requestHeaders1);
		
		ResponseEntity<JSONObject> respon = restTemplated.exchange(user,HttpMethod.GET,requestEntity1,JSONObject.class);
		System.out.println(respon.getBody() +" , "+respon.getHeaders());
		*/
		
		
        
      /*if(HttpStatus.OK == response.getStatusCode())
        {
        	// Request Successfully completed	
        	System.out.println(response.getBody());
        }
        else
        {
        	// Some Error is made
        	
        	
        }*/
		
	}
	
	
	
	private String getLongLivedAccessToken(JSONObject facebook) throws Exception{
		
		URI uri1 = new URI("https://graph.facebook.com/oauth/access_token?grant_type=fb_exchange_token&client_id=1603106119947122&client_secret=32d1a1d78ca29a59b2fa2379730c6a60&fb_exchange_token="+facebook.get("accessToken").toString());
		RestTemplate restTemplated = new RestTemplate();
		
		ResponseEntity<String> response = restTemplated.getForEntity(uri1, String.class);
		System.out.println(response.getBody());
		System.out.println("Status code is : "+response.getStatusCode());
		
		//access_token=CAAWyBDrRE3IBAHAHb26Qyc0mR7S7eHy6QR0JVCD7chZAn2qZB6S0BYKJMuYvwj3x4ZBlXYTfqgSVEy0EtNZBUzlZAanlZCKXMcbbFAcNBK9gdEzHb1PJ8mVQAyhyzkZC9PfhKaBl4M4Ar3mzMy83d9xi3CUhffI2TCth1RZB2vOMRJtPbh1dy9ADbaECL5yvpuQZD&expires=5121481
		
		String longLivedAccessToken = response.getBody();
		System.out.println(longLivedAccessToken.substring(longLivedAccessToken.indexOf("=")+1,longLivedAccessToken.indexOf("&") ));
		int seconds = 5121481;
		System.out.println(seconds/86);		
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public void generateoauth2Token(String state,String code)
	{
		
		RestTemplate rest = new RestTemplate();
		String client_id="1603106119947122";
		String client_secret = "32d1a1d78ca29a59b2fa2379730c6a60";
		
		String redirect_uri = "https://localhost:8443/blouda/callback";
		String url = "grant_type=authorization_code&code="+code+"&redirect_uri="+redirect_uri+"&client_id="+client_id+"&client_secret="+client_secret;
		
		String accessTokenUrl = "https://www.linkedin.com/uas/oauth2/accessToken";
		
		String jobportalcid="75ge19xj0x6olv";
		String jobportalcsid="KdqbojdVqjzicbJd";
		
		
		/*ResponseEntity<JSONObject> codeResponse = rest.getForEntity(accessCodeURL, JSONObject.class);
		
		JSONObject code1= codeResponse.getBody();
		//https://graph.facebook.com/oauth/access_token?code=...&client_id=...&redirect_uri=...&machine_id= ...
		String refreshTokenurl = "https://graph.facebook.com/oauth/access_token?code="+code1.get("code").toString()+"&client_id="+client_id+"&redirect_uri="+redirect_uri;
		URI refreshcodeURL = new URI(refreshTokenurl);
		ResponseEntity<JSONObject> refreshedLongLivedAccessToken = rest.getForEntity(refreshcodeURL, JSONObject.class);
		System.out.println(refreshedLongLivedAccessToken.getBody());*/
		
		
		 MultiValueMap<String,String> params=new LinkedMultiValueMap<String,String>();
		  params.set("Accept","application/json");
		  params.set("Content-Type","application/x-www-form-urlencoded");
		  MultiValueMap<String,String> params1=new LinkedMultiValueMap<String,String>();
		 
		  params1.add("grant_type", "authorization_code");
			params1.add("code", code);
			params1.add("redirect_uri", redirect_uri);
			params1.add("client_id", jobportalcid);
			params1.add("client_secret", jobportalcsid);
		
		HttpHeaders requestHeaders=new HttpHeaders();
	  requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	  requestHeaders.set("Accept","application/json");
	  HttpEntity<MultiValueMap<String,String>> requestEntity=new HttpEntity<MultiValueMap<String,String>>(params1,requestHeaders);
		/*HttpHeaders requestHeaders1 = new HttpHeaders();
		requestHeaders1.setContentType(MediaType.APPLICATION_FORM_URLENCODED);		
		*/
		
		
		
		
		
		/*HttpEntity<?> requestEntity1 = new HttpEntity<Object>(params,requestHeaders1);*/
		
		ResponseEntity<JSONObject> respon = rest.exchange(accessTokenUrl,HttpMethod.POST,requestEntity,JSONObject.class);
		System.out.println(respon.getBody() +" , "+respon.getHeaders());
		
	}
	
	public void googleOauth2Token(Map queryParams) throws Exception
	{
		try{
			
			RestTemplate rest = new RestTemplate();
			String client_id="639006534942-2flg7bu4be6du9ti5418p50ntif2nimd.apps.googleusercontent.com";
			String client_secret = "I7v4GrqA6Rb2nZX2oErDyfVi";
			
			String redirect_uri = "https://localhost:8443/blouda/newEmployeeGoogleLogin";		
			String accessTokenUrl = "https://accounts.google.com/o/oauth2/token";
			
			String grant_type = "authorization_code";
			String access_type = "offline";
			
			String urlFormation = "https://accounts.google.com/o/oauth2/token?code="+URLEncoder.encode(queryParams.get("code").toString())+"&client_id="+URLEncoder.encode(client_id)+"&client_secret="+URLEncoder.encode(client_secret)+"&redirect_uri="+URLEncoder.encode(redirect_uri)+"&grant_type="+URLEncoder.encode(grant_type)+"&access_type="+URLEncoder.encode(access_type);
			
			System.out.println(urlFormation);
			
			MultiValueMap<String,String> paramsForRefreshToken=new LinkedMultiValueMap<String,String>();
		
			paramsForRefreshToken.add("code", queryParams.get("code").toString());
			paramsForRefreshToken.add("client_id", client_id);
			paramsForRefreshToken.add("client_secret", client_secret);
			paramsForRefreshToken.add("redirect_uri", redirect_uri);
			paramsForRefreshToken.add("grant_type", grant_type);
			
			
			HttpHeaders requestHeaders=new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);		
			HttpEntity<MultiValueMap<String,String>> requestEntity=new HttpEntity<MultiValueMap<String,String>>(paramsForRefreshToken,requestHeaders);
			
			String url = "https://accounts.google.com/o/oauth2/token";
			
			
			
			ResponseEntity<JSONObject> response = rest.exchange(url,HttpMethod.POST,requestEntity,JSONObject.class);
			JSONObject googleCredentials = response.getBody();
			
			System.out.println("with Refresh Token is : "+googleCredentials);			
			
			String refreshToken = googleCredentials.get("refresh_token").toString();
			MultiValueMap<String,String> paramsForAccesTokenUsingRefreshToken=new LinkedMultiValueMap<String,String>();		
		
			paramsForAccesTokenUsingRefreshToken.add("client_id", client_id);
			paramsForAccesTokenUsingRefreshToken.add("client_secret", client_secret);
			paramsForAccesTokenUsingRefreshToken.add("refresh_token", refreshToken);
			paramsForAccesTokenUsingRefreshToken.add("grant_type", "refresh_token");
			
			
			HttpHeaders requestHeadersForNewAccessToken=new HttpHeaders();
			requestHeadersForNewAccessToken.setContentType(MediaType.APPLICATION_FORM_URLENCODED);		
			HttpEntity<MultiValueMap<String,String>> requestEntity12=new HttpEntity<MultiValueMap<String,String>>(paramsForAccesTokenUsingRefreshToken,requestHeadersForNewAccessToken);
			
			
			ResponseEntity<JSONObject> newresponse = rest.exchange(url,HttpMethod.POST,requestEntity12,JSONObject.class);
			JSONObject newgoogleCredentials = newresponse.getBody();
			if(refreshToken.equals(newgoogleCredentials.get("access_token").toString()))
			{
				System.out.println("Two token are same");
			}
			else
			{
				System.out.println("Two tokens are different");
			}
			System.out.println("New Access Token is : "+newgoogleCredentials);
			
			
			
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
	}
	
	
	
	private String validateSignatures(JSONObject cookiecredentials)
	{
		System.out.println("access token is "+cookiecredentials.get("access_token"));
		String HMAC_SHA1_ALGORITHM = "HmacSHA1";
		System.out.println("Inside method");
		
		try {
			
			
			
			String APIKEY = "75xcotynliiowz";
			String SECRETKEY = "1RAhIQMHOcjuNMWe";
			
			/*String oauth_token = "aa56eaa3-f24a-4163-a0b6-99ebf4e09a34";
			String oauth_token_secret = "0fb6a0d1-93a7-498e-bcb7-76c7424daf57";*/
			
			
			
			 OAuthService service = new ServiceBuilder()
	         .apiKey(APIKEY)
	         .apiSecret(SECRETKEY)
	         .provider(LinkedInApi.class)	         
	         .build();
			 
			 
			 
			 String ACCESS_TOKEN_ENDPOINT = "https://api.linkedin.com/uas/oauth/accessToken";
			 String ACCESS_TOKEN_ENDPOINT2 = "https://api.linkedin.com/uas/oauth2/authorization";
			 
			 String PROTECTED_RESOURCE_URL = "http://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline)";
			 
			 String OAUTH2_ACCESS_TOKEN = "xoauth_oauth2_access_token";
			 
			 // Exchange 2.0 token for 1.0a (long lived)
			 OAuthRequest request = new OAuthRequest(Verb.POST, ACCESS_TOKEN_ENDPOINT);
			 
			 // Add the 2.0 token as a parameter
			  request.addBodyParameter(OAUTH2_ACCESS_TOKEN, cookiecredentials.get("access_token").toString());			  
			  
			// Use an empty 1.0a access_token
			    Token token = new Token("","");
			    
			    // Sign and then send the request
			    service.signRequest(token, request);
			    Response oauthresponse = request.send();
			    OAuthCookie cookie = new OAuthCookie();
			    cookie.oauth_one_token = oauthresponse.getBody();
			    Gson parser = new Gson();
			    System.out.println(parser.toJson(cookie));
			    
			    System.out.println(oauthresponse.getBody());
			    
			    String previousOAuth = "78--9a6652a1-5b9a-4994-a509-64231b806fb6";
			    String prOauthSecrte = "b83359ed-6642-4c6d-9bcd-48868874de12";
			    
			    
			   
			    
			    OAuthRequest getProfile = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
			    
			    getProfile.addHeader("x-li-format", "json");
			    
			    Token token1 = new Token("78--9a6652a1-5b9a-4994-a509-64231b806fb6","b83359ed-6642-4c6d-9bcd-48868874de12");
			    
			    
			    service.signRequest(token1, getProfile);
			    
			    Response oauthresponse2 = getProfile.send();
			    System.out.println("Profile : "+oauthresponse2.getBody());
			    
			    OAuthService service1 = new ServiceBuilder()
		         .apiKey(APIKEY)
		         .apiSecret(SECRETKEY)
		         .provider(LinkedInApi.class)	
		         .callback("https://localhost:8443/blouda/callback")
		         .build();
			    
			    OAuthRequest appVerfication = new OAuthRequest(Verb.GET, ACCESS_TOKEN_ENDPOINT2);
			    Token token4 = new Token("aa56eaa3-f24a-4163-a0b6-99ebf4e09a34", "0fb6a0d1-93a7-498e-bcb7-76c7424daf57");
			    service1.signRequest(token4, appVerfication);
			    Response ssss = appVerfication.send();
			    System.out.println(ssss.getBody()+" , "+ssss.getHeaders());
			   
			    
			    /*OAuthRequest requestforrefress = new OAuthRequest(Verb.POST, ACCESS_TOKEN_ENDPOINT2);
			    
			    service.signRequest(token1, requestforrefress);
			    
			    Response refresh = requestforrefress.send();
			    
			    System.out.println(refresh.getBody());*/
			    
			    

	/*OAuthRequest oAuthRequestData = new OAuthRequest(Verb.POST,ACCESS_TOKEN_ENDPOINT2);
	
	Token accessToken = new Token(previousOAuth, prOauthSecrte);
	System.out.println(accessToken);
	service.signRequest(accessToken, oAuthRequestData);
	Response oAuthResponse = oAuthRequestData.send();

	System.out.println("Refreshed Token : "+oAuthResponse.getBody());
			    
			    System.out.println("Now we're going to access a protected resource...");
			    OAuthRequest prequest = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
			    service.signRequest(previousOAuth, request);
			    Response response = request.send();
			    System.out.println("Got it! Lets see what we found...");
			    System.out.println();
			    System.out.println(response.getBody());*/
			    
	
			/*RestTemplate restTemplate = new RestTemplate();			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Authorization", "Bearer " + previousOAuth);
			headers.add("x-li-format", "json");
			
			
			
			HttpEntity<JSONObject> request12 = new HttpEntity<JSONObject>(headers);
			
			URI uri1 = new URI("https://api.linkedin.com/v1/people/~?format=json");			
	       
			ResponseEntity<JSONObject> response = restTemplate.exchange(uri1, HttpMethod.GET, request12, JSONObject.class);
			
	        
	        if(HttpStatus.OK == response.getStatusCode())
	        {
	        	// Request Successfully completed	
	        	System.out.println(response.getBody());
	        }
	        else
	        {
	        	// Some Error is made
	        	
	        	
	        }*/
	        
			/*RestTemplate restTemplate = new RestTemplate();
			System.out.println(restTemplate);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
			MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
			
			
			//JSONObject queryParams = new JSONObject();
			queryParams.add("grant_type", "authorization_code");
			queryParams.add("code", cookiecredentials.get("access_token").toString());
			queryParams.add("redirect_uri", "https://localhost:8443/blouda/auth/linkedin/callback");
			queryParams.add("client_id", "75xcotynliiowze");
			queryParams.add("client_secret", "1RAhIQMHOcjuNMWe");
			
			JSONObject sss = new JSONObject();					
			sss.put("query",queryParams);
			HttpEntity<?> request = new HttpEntity<Object>(sss,headers);
			
			System.out.println(request);
			URI uri1 = new URI("https://www.linkedin.com/uas/oauth2/accessToken");	
			
			
			System.out.println(uri1);
			
			ResponseEntity<JSONObject> response2 = restTemplate.exchange(uri1, HttpMethod.POST, request, JSONObject.class);
			
			
			JSONObject response = restTemplate.postForObject(uri1,queryParams,JSONObject.class);
			System.out.println("Response object is : "+response);
			HMAC_SHA1_ALGORITHM ="outer";*/
	        /*if(HttpStatus.OK == response.getStatusCode())
	        {
	        	HMAC_SHA1_ALGORITHM="dfsadf";
	        	// Request Successfully completed	
	        	System.out.println(response.getBody());
	        }
	        else
	        {
	        	// Some Error is made	
	        	System.out.println(response.getStatusCode());
	        	System.out.println(response.getHeaders());
	        	System.out.println(response.getBody());
	        }*/
			
	
			
			/*HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("https://api.linkedin.com/uas/oauth/accessToken");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);			
			nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
			nameValuePairs.add(new BasicNameValuePair("code", cookiecredentials.get("access_token").toString()));
			nameValuePairs.add(new BasicNameValuePair("redirect_uri", "https://localhost:8443/blouda/auth/linkedin/callback"));
			nameValuePairs.add(new BasicNameValuePair("client_id", "75xcotynliiowze"));
			nameValuePairs.add(new BasicNameValuePair("client_secret", "1RAhIQMHOcjuNMWe"));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			
			System.out.println(response.getAllHeaders());
			
			String result = EntityUtils.toString(response.getEntity());*/
			
			/*OAuthClientRequest request = OAuthClientRequest
					.tokenProvider(OAuthProviderType.LINKEDIN)
					.setClientId("75xcotynliiowze")
					.setGrantType(GrantType.AUTHORIZATION_CODE)
					.setRedirectURI("https://localhost:8443/blouda/auth/linkedin/callback")
					.setCode(cookiecredentials.get("access_token").toString())
					.setClientSecret("1RAhIQMHOcjuNMWe")
					.buildQueryMessage();
			 OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			 
			 GitHubTokenResponse oAuthResponse = oAuthClient.accessToken(request, GitHubTokenResponse.class);
			 System.out.println(oAuthResponse.getAccessToken());*/
			
			
			
		} catch (Exception e) {
			e.printStackTrace();	
			
			
		}		
		
		return HMAC_SHA1_ALGORITHM;
	}

	
}
