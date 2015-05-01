package com.justbootup.blouda.dao;




import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import com.justbootup.blouda.util.MongodbConnection;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.Hash;




@Repository
public class EmployerDocumentSearchDao {
	
	@SuppressWarnings("unchecked")
	public void getDocuments()
	
	{
		try
		{
						
			Settings settings = ImmutableSettings.settingsBuilder()
			        //.put("cluster.name", "justbootup-elasticsearch")
					.put("cluster.name","elasticsearch-siren-example")
			        .put("client.transport.sniff", true).build();
			
			// Making a TransportClient Connection
			@SuppressWarnings("resource")
			Client client = new TransportClient(settings)
	        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));		
			
			
			HashMap<String, String> queryFiled = new HashMap<String, String>();
			queryFiled.put("attribute", "content");
			queryFiled.put("query", "(+Java)~100");
			
			HashMap<String, Object> node = new HashMap<String, Object>();
			node.put("node",queryFiled);
			
			HashMap<String, Object> tree = new HashMap<String, Object>();
			tree.put("tree", node);
			
			// HightLight Query		
			BoolQueryBuilder highlightQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("content","Java Developer"));
			
			SearchResponse response = client.prepareSearch("it").setTypes("doc").setQuery(tree).setHighlighterQuery(highlightQuery).addHighlightedField("content").setHighlighterFragmentSize(102).setHighlighterNumOfFragments(3).execute().actionGet();	
			
			System.out.println(response);
			
			HashMap<String, Object> searchHit = new HashMap<String, Object>();
			
			
			
			Iterator<SearchHit> its = response.getHits().iterator();
			

			while(its.hasNext())
			{
			 SearchHit result = its.next();
			 searchHit = (HashMap<String, Object>) result.getSource();
			 
			 // To get filename from file object
			 HashMap<String, Object> fileObject = new HashMap<String, Object>();
			 fileObject = (HashMap<String, Object>) searchHit.get("file");
			 System.out.println(fileObject.get("filename"));
			 
			 // To get fileorginalpath from path object
			 HashMap<String, Object> pathObject = new HashMap<String, Object>();
			 pathObject = (HashMap<String, Object>) searchHit.get("path");
			 System.out.println(pathObject.get("real"));
			 
			// To get fileorginalpath from path object
			HashMap<String, Object> metaObject = new HashMap<String, Object>();
			metaObject = (HashMap<String, Object>) searchHit.get("meta");
			System.out.println(metaObject.get("title"));	
			
			// For Highligh Field		
			for (    HighlightField field : result.getHighlightFields().values()) {	
					String fields = Arrays.toString(field.fragments());
					String fds=fields.replaceAll("[\\t\\n\\r]"," ");
					String removeSpecialchar = fds.replaceAll("[\\[\\]]", "");
					String hh = removeSpecialchar.trim().replaceAll("\\s+"," ");
					
				 System.out.println(hh.replaceFirst("\\W","").trim());				
				
			    }		
			}
			
			client.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Object> getWholeSearchDocuments(JSONObject keywords)
	
	{
		LinkedHashMap<String, Object> searchResultofEmployees = new LinkedHashMap<String, Object>();
		
		try
		{		
			
			Settings settings = ImmutableSettings.settingsBuilder()
			        //.put("cluster.name", "justbootup-elasticsearch")
					.put("cluster.name","elasticsearch-siren-example")
			        .put("client.transport.sniff", true).build();
			
			// Making a TransportClient Connection
			@SuppressWarnings("resource")
			Client client = new TransportClient(settings)
	        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));		
			
			
			String queryContent = (String) keywords.get("keywords");
			
			LinkedHashMap<String, Object> searchResultofWholeDocs = new LinkedHashMap<String, Object>();
			
			HashMap<String, String> queryFiled = new HashMap<String, String>();
			queryFiled.put("attribute", "content");
			queryFiled.put("query", "(+"+queryContent+")~100");
			
			System.out.println("Query Filed is : "+queryFiled);
			
			HashMap<String, Object> node = new HashMap<String, Object>();
			node.put("node",queryFiled);
			
			HashMap<String, Object> tree = new HashMap<String, Object>();
			tree.put("tree", node);
			
			// HightLight Query		
			BoolQueryBuilder highlightQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("content",queryContent));
			
			SearchResponse response = client.prepareSearch(keywords.get("industrytype").toString().toLowerCase()).setTypes("doc").setQuery(tree).setHighlighterQuery(highlightQuery).addHighlightedField("content").setHighlighterFragmentSize(102).setHighlighterNumOfFragments(3).setHighlighterPreTags("<b class='highlight'>").setHighlighterPostTags("</b>").execute().actionGet();	
			
			System.out.println(response);
			
			HashMap<String, Object> searchHit = new HashMap<String, Object>();
			
			
			Iterator<SearchHit> its = response.getHits().iterator();
			

			while(its.hasNext())
			{
			 SearchHit result = its.next();
			 searchHit = (HashMap<String, Object>) result.getSource();
			 
			 // To get filename from file object
			 HashMap<String, Object> fileObject = new HashMap<String, Object>();
			 fileObject = (HashMap<String, Object>) searchHit.get("file");
			 
			
			 String highlightedText = null;
			// For Highligh Field		
			for (    HighlightField field : result.getHighlightFields().values()) {	
					String fields = Arrays.toString(field.fragments());
					String fds=fields.replaceAll("[\\t\\n\\r]"," ");
					String removeSpecialchar = fds.replaceAll("[\\[\\]]", "");
					String hh = removeSpecialchar.trim().replaceAll("\\s+"," ");
					highlightedText = hh.replaceFirst("\\W","").trim();
					
			    }	
			
			searchResultofWholeDocs.put((String) fileObject.get("filename"), highlightedText);
			
			}			
			
			if(searchResultofWholeDocs.size() > 0)
			{
				searchResultofEmployees = getEmployeesInfo(searchResultofWholeDocs);
				System.out.println("Final Result is : "+searchResultofEmployees);
			}
			else
			{
				// No Results found
				searchResultofEmployees.put("noresult", "Not Found");
			}
			
			
			
			client.close();
		}
		catch(Exception e)
		{
			searchResultofEmployees.put("error", "server is too busy");
			System.out.println(e);
		}
		
		return searchResultofEmployees;
	}

	
	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Object> getMultikeywordSearchDocument(JSONObject multiSearchKeyword)
	
	{
		LinkedHashMap<String, Object> searchResultofMultikeywordEmployees = new LinkedHashMap<String, Object>();
		try
		{		
			
			Settings settings = ImmutableSettings.settingsBuilder()
			        //.put("cluster.name", "justbootup-elasticsearch")
					.put("cluster.name","elasticsearch-siren-example")
			        .put("client.transport.sniff", true).build();
			
			// Making a TransportClient Connection
			@SuppressWarnings("resource")
			Client client = new TransportClient(settings)
	        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));		
			
			String index = (String) multiSearchKeyword.get("industrytype");
			
			String queryContent = generateSirenQuery(multiSearchKeyword);
			
			LinkedHashMap<String, Object> searchResultofMultiKeword = new LinkedHashMap<String, Object>();
			
			HashMap<String, String> queryFiled = new HashMap<String, String>();
			queryFiled.put("attribute", "content");
			//queryFiled.put("query", "(+"+queryContent+")~100");
			queryFiled.put("query",queryContent);
			
			HashMap<String, Object> node = new HashMap<String, Object>();
			node.put("node",queryFiled);
			
			HashMap<String, Object> tree = new HashMap<String, Object>();
			tree.put("tree", node);
			HashMap<String, Object> queryKeyword = multiSearchKeyword;
			
			StringBuffer highlighQueryContent = new StringBuffer(); 
			
			for (Map.Entry<String, Object> entry : queryKeyword.entrySet())
			{
				highlighQueryContent.append(entry.getValue() + " ");
			  
			}
			System.out.println(highlighQueryContent);
			
			// HightLight Query		
			BoolQueryBuilder highlightQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("content",highlighQueryContent));
			
			
			SearchResponse response = client.prepareSearch(index.toLowerCase()).setTypes("doc").setQuery(tree).setHighlighterQuery(highlightQuery).addHighlightedField("content").setHighlighterFragmentSize(102).setHighlighterNumOfFragments(3).setHighlighterPreTags("<b class='highlight'>").setHighlighterPostTags("</b>").execute().actionGet();	
			
			System.out.println(response);
			
			HashMap<String, Object> searchHit = new HashMap<String, Object>();
			
			
			Iterator<SearchHit> its = response.getHits().iterator();
			

			while(its.hasNext())
			{
			 SearchHit result = its.next();
			 searchHit = (HashMap<String, Object>) result.getSource();
			
			 
			 // To get filename from file object
			 HashMap<String, Object> fileObject = new HashMap<String, Object>();
			 fileObject = (HashMap<String, Object>) searchHit.get("file");				 
			
			 String multihighlightedText = null;			 
			// For Highligh Field		
			for (    HighlightField field : result.getHighlightFields().values()) {	
					String fields = Arrays.toString(field.fragments());
					String fds=fields.replaceAll("[\\t\\n\\r]"," ");
					String removeSpecialchar = fds.replaceAll("[\\[\\]]", "");
					String hh = removeSpecialchar.trim().replaceAll("\\s+"," ");
					multihighlightedText = hh.replaceFirst("\\W","").trim();
					
			    }	
			
			searchResultofMultiKeword.put((String) fileObject.get("filename"), multihighlightedText);
			
			}			
			
			if(searchResultofMultiKeword.size() > 0)
			{
				searchResultofMultikeywordEmployees = getEmployeesInfo(searchResultofMultiKeword);
				System.out.println("Final Result is : "+searchResultofMultikeywordEmployees);
			}
			else
			{
				// No Results found				
				searchResultofMultikeywordEmployees.put("noresult", "Not Found");
			}
			
			
			client.close();			
			
		}
		catch(Exception e)
		{
			// Error Block			
			searchResultofMultikeywordEmployees.put("error", "server is too busy");		
			e.printStackTrace();
		}
		
		return searchResultofMultikeywordEmployees;
	}

	
	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Object> getEmployeesInfo(LinkedHashMap<String, Object> documentSearchResult){
		
		MongoClient mongoclient = null;
		MongodbConnection mongodb = new MongodbConnection();
		LinkedHashMap<String, Object> employeeInfos = new LinkedHashMap<String, Object>();
		ArrayList< LinkedHashMap<String, Object> > allEmployees = new ArrayList< LinkedHashMap<String, Object> >();
		
		try{
			
			System.out.println("Inside a getEmployeeInfo method : "+documentSearchResult.size());
			mongoclient = mongodb.connect();
			
			DB db = mongoclient.getDB("jobportal");
			
			DBCollection collection = db.getCollection("employee");			
			
			for (Map.Entry<String, Object> entry : documentSearchResult.entrySet())
			{
			   BasicDBObject getEmployee = new BasicDBObject();
			   getEmployee.put("resumeDetails.FileName", entry.getKey());
			   
			   System.out.println("Get Emplyee Query is :"+getEmployee);
			  			   
			   DBObject result = collection.findOne(getEmployee);
			   
			   HashMap<String, Object> resultInMap = (HashMap<String, Object>) result.toMap();
			   
			   LinkedHashMap<String, Object> singleEmployeeInfos = new LinkedHashMap<String, Object>();
			   // This part is used to view the search Result. Here we pickup fields.
				   if(!resultInMap.isEmpty())
				   {
					   singleEmployeeInfos.put("fullname",result.get("fullname"));
					   singleEmployeeInfos.put("email", result.get("email"));
					   singleEmployeeInfos.put("experience",result.get("experience"));
					   singleEmployeeInfos.put("currentlocation", result.get("currentlocation"));
					   singleEmployeeInfos.put("docsmatching",entry.getValue());
					   allEmployees.add(singleEmployeeInfos);					   
				   }	   
			  
			}
			employeeInfos.put("result",allEmployees);
			
		}
		catch(Exception e)
		{			
			e.printStackTrace();
		}
		finally
		{
			if(mongoclient != null)
			{
				mongoclient.close();
			}
			
		}		
		return employeeInfos;
	}
	
	@SuppressWarnings("unchecked")
	private String generateSirenQuery(JSONObject queries)
	{
		
		Map<String, Object> hash = queries;
		
		if(hash.containsKey("industrytype"))
			hash.remove("industrytype");		
		int querySize = hash.size();
		//( (project^0.6 description^0.6 +(spring jsp)#4)#4 OR (working^0.6 experience^0.6 +(2)#4)#4 )
		
		int beginvalue = 0;
		
		StringBuffer sirenQuery = new StringBuffer("( "); 
		
		for (Map.Entry<String, Object> entry : hash.entrySet()) {
			
				
			beginvalue++;
			
			sirenQuery.append("( "+entry.getKey());
			sirenQuery.append(" +("+entry.getValue()+")"+")");
			
		    if(beginvalue == querySize)
		    {
		    	sirenQuery.append(" ) ");
		    }
		    else
		    {
		    	sirenQuery.append(" OR ");
		    }			    
		    
		}
		
		System.out.println(sirenQuery);
		
		return sirenQuery.toString();

	}
	

	public HashMap<String,Object> getEmployeeResume(JSONObject selectedEmployee) throws Exception{
		
		JSONObject details = getEmployeeFilePath(selectedEmployee);
		File fi = new File(details.get("filepath").toString());
		byte[] fileContent = Files.readAllBytes(fi.toPath());		
		System.out.println("Byte Array is : "+fileContent);
		HashMap<String, Object> downloadedDetails = new HashMap<String,Object>();
		downloadedDetails.put("filename", details.get("filename").toString());
		downloadedDetails.put("filecontent",fileContent);
		return downloadedDetails;
	}
	
	
	@SuppressWarnings("unchecked")
	private JSONObject getEmployeeFilePath(JSONObject selectedEmployee){
		
		JSONObject details = new JSONObject();
		MongoClient mongoclient = null;
		MongodbConnection mongodb = new MongodbConnection();		
		try {
			
			mongoclient = mongodb.connect();
			DB db = mongoclient.getDB("jobportal");			
			DBCollection collection = db.getCollection("employee");
					
			
			BasicDBObject getFilename = new BasicDBObject();
			getFilename.put("email",selectedEmployee.get("email"));
			DBObject result = collection.findOne(getFilename);
			
			HashMap<String, Object> allDetails = (HashMap<String, Object>) result.toMap();
			
			HashMap<String, Object> resumeObject = new HashMap<String, Object>();
			resumeObject = (HashMap<String, Object>) allDetails.get("resumeDetails");			
			details.put("filepath",resumeObject.get("FilePath"));
			details.put("filename", resumeObject.get("FileName"));
		
		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		finally
		{
			if(mongoclient != null)
			{
				mongoclient.close();
			}
			
		}		
		return details;
	}

	

}


