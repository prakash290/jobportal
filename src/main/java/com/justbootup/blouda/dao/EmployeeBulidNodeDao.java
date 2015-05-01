package com.justbootup.blouda.dao;



import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.sf.json.JSONArray;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;



@Repository
public class EmployeeBulidNodeDao {	
	
	 @Autowired
	 RestTemplate restTemplate;
	 
	@SuppressWarnings("unchecked")
	public void createEmployeeNode(net.sf.json.JSONObject employee){
		
		try{				
				StringBuffer finalRestQueryForEmployee = new StringBuffer();
				
				String employeeLableName = prepareLabelNameforEmployee(employee);
				
				String employeeNodeQuery = prepareEmployeeNodeQuery(employeeLableName,employee);				
				finalRestQueryForEmployee.append(employeeNodeQuery);
				
				String employeeCommonQuery = prepareEmployeeCommonQuery(employee);				
				finalRestQueryForEmployee.append(employeeCommonQuery);
				
				String employeeRelationShipQuery = createEmployeeRelationShipQuery(employee);
				finalRestQueryForEmployee.append(employeeRelationShipQuery);
				
				executeQuery(finalRestQueryForEmployee.toString());
				
		    }
		    catch(Exception e)
		     {
		        e.printStackTrace();
		
		     }
		
		 	
	}
	
	public void newEmployeeNode(net.sf.json.JSONObject employee){
		System.out.println("Inside employee buildnode dao : "+employee);
		
	}
	
	public void newEmployeeProfileNode(JSONObject employeeProfile)
	{
		StringBuffer finalQueryofEmployeeProfile = new StringBuffer();
		
		// This append for creating node of common employee email + current company name + previous company names
		finalQueryofEmployeeProfile.append(createEmployeeProfileNodeQuery(employeeProfile));
		
		// This append for creating relation of employee to company
		finalQueryofEmployeeProfile.append(createEmployeetoCompanyRelationShipQuery(employeeProfile));
		
		System.out.println("Final Profile Query is : "+finalQueryofEmployeeProfile);
		
		executeQuery(finalQueryofEmployeeProfile.toString());
		
	}
	// This method generates label for employee node
	private String prepareLabelNameforEmployee(net.sf.json.JSONObject employee)
	{
		
		StringBuffer employeeLabelName = new StringBuffer(":`employee`");
		
		try{
			// This is for finding the labelNames
			final ArrayList<String> labelNames = new ArrayList<String>();
			labelNames.add("industrytype");
			labelNames.add("currentlocation");
			labelNames.add("experience");
			
							
			for(String label:labelNames)
			{
				if(employee.containsKey(label))
				{
					/*String value = (String) employee.get(label);
					
					// This is for converting integer experience to string. Because We can't create label name of starting with integer. So convert it 
					value = (label.equals("experience"))? "experience"+value:value;
					*/
					if(label.equals("experience"))
					{
						employeeLabelName.append(":`experience"+employee.get(label)+"`");
					}
					else
					{	
						employeeLabelName.append(":`"+employee.get(label).toString().toLowerCase()+"`");
					}
				}
			}	
			
			System.out.println("Final label name is: "+employeeLabelName);
		}
		
		catch(Exception e)
		{
			System.out.println("Exception Raised in prepareLabelNameforEmployee method ");
			e.printStackTrace();
		}		
		
		return employeeLabelName.toString();
	}
	
	
	private String prepareEmployeeNodeQuery(String nodelablename, net.sf.json.JSONObject employee)
	{
		
		// merge (sample@sample.com:employee:it {name:"sample",email : 'sample@sample.com'})	
		
		StringBuffer employeeMergeNode = new StringBuffer();
		employeeMergeNode.append("merge ( `"+employee.get("email")+"` "+nodelablename+" {");
		
		final ArrayList<String> propertiesofNodes = new ArrayList<String>();
		propertiesofNodes.add("email");
		propertiesofNodes.add("fullname");
		
		int beginValue = 0;
		int propertylength = propertiesofNodes.size();
		
		for(String property:propertiesofNodes)
		{
			if(employee.containsKey(property))
			{
				beginValue++;
				if(beginValue == propertylength)
				{
					employeeMergeNode.append(" `"+property+"` : \""+employee.get(property)+"\" } ) ");
				}
				else
				{
					employeeMergeNode.append(" `"+property+"` : \""+employee.get(property)+"\" ,");
				}
							
			}
		}		
		
		
		return employeeMergeNode.toString();				
	}
	
	
	// This method of common nodes
	private String prepareEmployeeCommonQuery(net.sf.json.JSONObject employee)
	{
		StringBuffer employeeCommonNodes = new StringBuffer();
			
		final ArrayList<String> commonNodeFields = new ArrayList<String>();
		commonNodeFields.add("industrytype");
		commonNodeFields.add("currentlocation");		
		commonNodeFields.add("skillset");
		commonNodeFields.add("basiceducation");
		commonNodeFields.add("mastereduction");
		commonNodeFields.add("doctorateeducation");
		
		for(String field:commonNodeFields)
		{
			if(employee.containsKey(field) && !employee.get(field).equals(""))
			{
				StringBuffer commonNodes = new StringBuffer();
				// merge (IT:commonNode {name:"IT"})			
				
				if(field.equals("skillset"))
				{
					JSONArray lowecaseSkillSets = new JSONArray();
					for(int i=0;i<employee.getJSONArray(field).size();i++)
					{
						/*String skill = (String) employee.getJSONArray(field).get(i);
						lowecaseSkillSets.add(skill.toLowerCase());*/
						commonNodes.append("merge ( `"+employee.getJSONArray(field).get(i).toString().toLowerCase()+"`:commonNode:"+field+" { `name` : \""+ employee.getJSONArray(field).get(i).toString().toLowerCase()+"\" } ) ");
					}
					// FOREACH(i in RANGE(0, 20) | merge (a:employee {name : i}))					
					//commonNodes.append("FOREACH (skill in "+lowecaseSkillSets+" | merge ( skills:commonNode:"+field+" {name : skill } ) ) ");
				}
				else
				{
					commonNodes.append("merge ( `"+employee.get(field).toString().toLowerCase()+"`:commonNode:"+field+" { `name` : \""+ employee.get(field).toString().toLowerCase()+"\" } ) ");
				}
				
				
				employeeCommonNodes.append(commonNodes);				
			}
		}		
		
		System.out.println(employeeCommonNodes);
		return employeeCommonNodes.toString();
	}
	
	private String createEmployeeRelationShipQuery(net.sf.json.JSONObject employee)
	{
		StringBuffer employeeNodeRelationShips = new StringBuffer();
		
		final ArrayList<String> relationShipNodeFields = new ArrayList<String>();
		relationShipNodeFields.add("industrytype");
		relationShipNodeFields.add("currentlocation");
		relationShipNodeFields.add("experience");
		relationShipNodeFields.add("skillset");
		relationShipNodeFields.add("basiceducation");
		relationShipNodeFields.add("mastereduction");
		relationShipNodeFields.add("doctorateeducation");
		
		for(String field:relationShipNodeFields)
		{
			if(employee.containsKey(field) && !employee.get(field).equals(""))
			{
				StringBuffer commonNodes = new StringBuffer();
				// merge (IT:commonNode {name:"IT"})			
				
				if(field.equals("skillset"))
				{
					
					for(int i=0;i<employee.getJSONArray(field).size();i++)
					{											
						commonNodes.append("merge ( `"+employee.get("email")+"` ) - [:having_"+field+"] -> (`"+employee.getJSONArray(field).get(i).toString().toLowerCase()+"`) ");
					}										
					
				}
				else if(field.equals("experience"))
				{
					double experience = employee.getDouble(field);
					for(int i=0;i<10;i=i+0)
					{
						//merge ( `sample@sample.com` ) - [:having_experience {year:0}] -> ( `experience_0to2`) 
						
						if(experience>=i && experience<i+2)
						{
							commonNodes.append("merge ( `experience_"+i+"to"+(i+2)+"`:commonNode:"+field+" { `name` : \"experience_"+i+"to"+(i+2)+"\" } ) ");
							commonNodes.append("merge ( `"+employee.get("email")+"` ) - [:having_"+field+" { `year` :"+experience+" } ] -> (`experience_"+i+"to"+(i+2)+"`) ");
							break;
						}
						else if(experience >= 10)
						{
							commonNodes.append("merge ( `experience_above10`:commonNode:"+field+" { `name` : \"experience_above10\" } ) ");
							commonNodes.append("merge ( `"+employee.get("email")+"` ) - [:having_"+field+" { `year` :"+experience+" } ] -> (`experience_above10`) ");
							break;
						}
						i=i+2;						
					}
				}
				else
				{
					//merge (sample@sample.com) - [:having_industryType] -> (IT)
					commonNodes.append("merge ( `"+employee.get("email")+"` ) - [:having_"+field+"] -> ( `"+employee.get(field).toString().toLowerCase()+"`) ");
				}
				
				
				employeeNodeRelationShips.append(commonNodes);				
			}
		}		
		
		return employeeNodeRelationShips.toString();
	}
	

	@SuppressWarnings("unchecked")
	private String createEmployeeProfileNodeQuery(JSONObject employeeProfile)
	{
		StringBuffer employeeProfileNode= new StringBuffer();
		
		try{			
			
			// Append common employee merge email query
			employeeProfileNode.append(createEmployeeCommonQuery(employeeProfile));		
	
			LinkedHashMap<String, Object> currentCompanyDetails = new LinkedHashMap<String, Object>();
			currentCompanyDetails = (LinkedHashMap<String, Object>) employeeProfile.get("currentcompanydetails");
			
			employeeProfileNode.append("merge ( `"+currentCompanyDetails.get("current_company_name").toString().toLowerCase()+"`:employer { `name` : \""+currentCompanyDetails.get("current_company_name").toString().toLowerCase()+"\" } ) ");
			
			ArrayList<Object> previousCompanyDetails = new ArrayList<Object>();
			
			previousCompanyDetails = (ArrayList<Object>) employeeProfile.get("previouscompanydetails");
			
			for(int i=0;i<previousCompanyDetails.size();i++)
			{
				LinkedHashMap<String, Object> previousCompany = (LinkedHashMap<String, Object>) previousCompanyDetails.get(i);
				employeeProfileNode.append("merge ( `"+previousCompany.get("companyname").toString().toLowerCase()+"`:employer { `name` : \""+previousCompany.get("companyname").toString().toLowerCase()+"\" } ) ");			
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception Raised in createEmployeeProfileNodeQuery of employeeBulidNodeDao layer");
		}
		
		return employeeProfileNode.toString();
	}
	
	private String createEmployeeCommonQuery(JSONObject employee)
	{
		StringBuffer commonQuery = new StringBuffer();
		//employeeMergeNode.append("merge ( `"+employee.get("email")+"` "+nodelablename+" {");
		commonQuery.append("merge ( `"+employee.get("useremail")+"`:employee { `email` : \""+employee.get("useremail")+"\" } ) ");
		return commonQuery.toString();
	}
	
	@SuppressWarnings("unchecked")
	private String createEmployeetoCompanyRelationShipQuery(JSONObject employeeProfile)
	{
		
		StringBuffer employeetoCompanyRelationShipQuery= new StringBuffer();		
		try{
			
			//merge (sample@sample.com) - [:having_industryType] -> (IT)
			String commonforAll = "merge ( `"+employeeProfile.get("useremail")+"`)";	
			
			LinkedHashMap<String, Object> currentCompanyDetails = new LinkedHashMap<String, Object>();
			currentCompanyDetails = (LinkedHashMap<String, Object>) employeeProfile.get("currentcompanydetails");
			
			employeetoCompanyRelationShipQuery.append(commonforAll+" - [:working_in { name : \""+currentCompanyDetails.get("current_company_name").toString().toLowerCase() +"\" , role : \""+currentCompanyDetails.get("role")+"\" } ] " +" -> ( `"+currentCompanyDetails.get("current_company_name").toString().toLowerCase() +"` )");
			
			ArrayList<Object> previousCompanyDetails = new ArrayList<Object>();
			
			previousCompanyDetails = (ArrayList<Object>) employeeProfile.get("previouscompanydetails");
			
			for(int i=0;i<previousCompanyDetails.size();i++)
			{
				LinkedHashMap<String, Object> previousCompany = (LinkedHashMap<String, Object>) previousCompanyDetails.get(i);
				employeetoCompanyRelationShipQuery.append(commonforAll+" - [:worked_as { name : \""+previousCompany.get("companyname").toString().toLowerCase() +"\" , role : \""+previousCompany.get("companydesignation")+"\" } ]" +" ->  ( `"+previousCompany.get("companyname").toString().toLowerCase() +"` )");
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception Raised in createEmployeetoCompanyRelationShipQuery of employeeBulidNodeDao layer");
		}
		return employeetoCompanyRelationShipQuery.toString();
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject executeQuery(String queryParams){
		
		JSONObject nodeQueryResult = new JSONObject();
		
		try{
			
			RestTemplate restTemplate = new RestTemplate();			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			
			
			JSONObject sss = new JSONObject();					
			sss.put("query",queryParams);
			HttpEntity<JSONObject> request = new HttpEntity<JSONObject>(sss,headers);
			
			URI uri1 = new URI("http://localhost:7474/db/data/cypher");			
	       
			ResponseEntity<JSONObject> response = restTemplate.exchange(uri1, HttpMethod.POST, request, JSONObject.class);
			
	        
	        if(HttpStatus.OK == response.getStatusCode())
	        {
	        	// Request Successfully completed	
	        	nodeQueryResult.put("result",response.getBody());
	        }
	        else
	        {
	        	// Some Error is made	
	        	
	        }
			
		}
		catch(Exception e)
		{
			nodeQueryResult.put("error", "some error is made");
			System.out.println("Exception thrown in executeQuery method of employeebulidnodedao layer");
			e.printStackTrace();
		}
		
		return nodeQueryResult;
	}
}
