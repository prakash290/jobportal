package com.justbootup.blouda.dao;



import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
				
				System.out.println("createEmployeeNode final string is : "+finalRestQueryForEmployee);
				
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
		
		// This append for creating node of companies.
		finalQueryofEmployeeProfile.append(createEmployeeProfileNodeQuery(employeeProfile));
		
		// This append for creating relation of employee to company
		finalQueryofEmployeeProfile.append(createEmployeetoCompanyRelationShipQuery(employeeProfile));
		
		System.out.println("Final Profile Query is : "+finalQueryofEmployeeProfile);
		
		executeQuery(finalQueryofEmployeeProfile.toString());
		
	}
	
public void updateEmployeeProfileNode(net.sf.json.JSONObject employee){
		
		try{				
				StringBuffer finalRestQueryForEmployee = new StringBuffer();
				
				String employeeNodeQuery = createEmployeeCommonEmailQuery(employee);				
				finalRestQueryForEmployee.append(employeeNodeQuery);
				
				String LabelUpdationQuery = labelUpdationQuery(employee);
				finalRestQueryForEmployee.append(LabelUpdationQuery);
				
				String employeeCommonQuery = prepareEmployeeCommonQuery(employee);				
				finalRestQueryForEmployee.append(employeeCommonQuery);
				
				// This append for creating node of companies.
				finalRestQueryForEmployee.append(updateProfessionalDetails(employee));
				
				// This append for creating relation of employee to company
				finalRestQueryForEmployee.append(updateProfessionalDetailsRelationShipQuery(employee));
				
				
				String employeeRelationShipQuery = createEmployeeRelationShipQuery(employee);
				finalRestQueryForEmployee.append(employeeRelationShipQuery);
				
				System.out.println("Before excution neo4j final query is : "+finalRestQueryForEmployee);
				executeQuery(finalRestQueryForEmployee.toString());
				
		    }
		    catch(Exception e)
		     {
		        e.printStackTrace();
		
		     }
		
		 	
	}

	// This method generates label for employee node
	private String prepareLabelNameforEmployee(net.sf.json.JSONObject employee)
	{
		
		StringBuffer employeeLabelName = new StringBuffer(":`employee`");
		
		try{
			// This is for finding the labelNames
			final ArrayList<String> labelNames = new ArrayList<String>();
			labelNames.add("currentindustrytype");
			
			// This is for finding the current location field. This field is embedded in personaldetails object.
			labelNames.add("personaldetails");
			
			labelNames.add("employeementtype");
			
							
			for(String label:labelNames)
			{
				if(employee.containsKey(label))
				{
					/*String value = (String) employee.get(label);
					
					// This is for converting integer experience to string. Because We can't create label name of starting with integer. So convert it 
					value = (label.equals("experience"))? "experience"+value:value;
					*/
					if(label.equals("employeementtype"))
					{
						// Generate Label Based on employementtype fresher or experiencer
						if(employee.get(label).toString().equals("fresher"))
						{
							employeeLabelName.append(":`"+employee.get(label).toString().toLowerCase()+"`");
						}
						else
						{
							// if experiencer means get experiencer details object for getting experience in years.
							
							net.sf.json.JSONObject getExperiencerDetails = new net.sf.json.JSONObject();
							getExperiencerDetails = (net.sf.json.JSONObject) employee.get("experiencedetails");	
							
							// Get experience from getexperiencer details object							
							employeeLabelName.append(":`experiencer`");
							
						}
						
					}
					else if(label.equals("personaldetails"))
					{
						net.sf.json.JSONObject getPersonalDetails = new net.sf.json.JSONObject();
						getPersonalDetails =  (net.sf.json.JSONObject) employee.get(label);						
						// Get current location from personal details object
						employeeLabelName.append(":`"+getPersonalDetails.get("currentlocation").toString().toLowerCase()+"`");
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
		// This for add fullname
		propertiesofNodes.add("personaldetails");
		
		int beginValue = 0;
		int propertylength = propertiesofNodes.size();
		
		for(String property:propertiesofNodes)
		{
			if(employee.containsKey(property))
			{
				beginValue++;
				if(beginValue == propertylength)
				{
					 if(property.equals("personaldetails"))
						{
							net.sf.json.JSONObject getPersonalDetails = new net.sf.json.JSONObject();
							getPersonalDetails =  (net.sf.json.JSONObject) employee.get(property);		
							
							// Get fullname from personal details object							
							employeeMergeNode.append(" `fullname` : \""+getPersonalDetails.get("fullname")+"\" } ) ");
						}
					 else
					 {
						 employeeMergeNode.append(" `"+property+"` : \""+employee.get(property)+"\" } ) ");
					 }
				}
				else
				{
					 if(property.equals("personaldetails"))
						{
							net.sf.json.JSONObject getPersonalDetails = new net.sf.json.JSONObject();
							getPersonalDetails =  (net.sf.json.JSONObject) employee.get(property);						
							// Get fullname from personal details object							
							 employeeMergeNode.append(" `fullname` : \""+getPersonalDetails.get("fullname")+"\" ,");
						}
					 else
					 {
						 employeeMergeNode.append(" `"+property+"` : \""+employee.get(property)+"\" ,");
					 }
					 
					
				}
							
			}
		}		
		
		
		return employeeMergeNode.toString();				
	}
	
	
	// This method is used to generateCommonNode Query Based on employement Type.
	@SuppressWarnings("unchecked")
	private String prepareEmployeeCommonQuery(net.sf.json.JSONObject employee)
	{
		System.out.println("Employee from common query is : "+employee);
		StringBuffer employeeCommonNodes = new StringBuffer();
			
		final ArrayList<String> commonNodeFields = new ArrayList<String>();
		commonNodeFields.add("personaldetails");
		commonNodeFields.add("currentindustrytype");		
		commonNodeFields.add("keyskills");
		commonNodeFields.add("educationdetails");
		commonNodeFields.add("employeementtype");
		//
		final ArrayList<String> educationdetails = new ArrayList<String>();
		educationdetails.add("basic");
		educationdetails.add("master");
		educationdetails.add("doctorate");
		
		final ArrayList<String> personaldetails = new ArrayList<String>();		
		personaldetails.add("currentlocation");	
		
		final ArrayList<String> commonNodeFieldsForExperiencer = new ArrayList<String>();
		commonNodeFieldsForExperiencer.add("personaldetails");
		commonNodeFieldsForExperiencer.add("currentindustrytype");		
		commonNodeFieldsForExperiencer.add("keyskills");
		commonNodeFieldsForExperiencer.add("educationdetails");
		commonNodeFieldsForExperiencer.add("professionaldetails");
		
		for(String field:commonNodeFields)
		{
			if(employee.containsKey(field) && !employee.get(field).equals(""))
			{
				StringBuffer commonNodes = new StringBuffer();
				// merge (IT:commonNode {name:"IT"})			
				
				if(field.equals("keyskills"))
				{
					
					for(int i=0;i<employee.getJSONArray(field).size();i++)
					{
						/*String skill = (String) employee.getJSONArray(field).get(i);
						lowecaseSkillSets.add(skill.toLowerCase());*/
						commonNodes.append("merge ( `"+employee.getJSONArray(field).get(i).toString().toLowerCase()+"`:commonNode:"+field+" { `name` : \""+ employee.getJSONArray(field).get(i).toString().toLowerCase()+"\" } ) ");
					}
					// FOREACH(i in RANGE(0, 20) | merge (a:employee {name : i}))					
					//commonNodes.append("FOREACH (skill in "+lowecaseSkillSets+" | merge ( skills:commonNode:"+field+" {name : skill } ) ) ");
				}
				else if(field.equals("educationdetails"))
				{
					if(employee.containsKey("olddata"))
					{
						net.sf.json.JSONObject olddata = new net.sf.json.JSONObject();
						olddata = (net.sf.json.JSONObject) employee.get("olddata");
						
						net.sf.json.JSONObject education = new net.sf.json.JSONObject();
						education = (net.sf.json.JSONObject) employee.get(field);
						
						//merge ( `aprakash290@gmail.com`) - [r:worked_as ] ->  ( `kambaa` ) delete r
						Iterator<String> keys = olddata.keys();
						while (keys.hasNext()) {
							
							String key = (String) keys.next();							
							net.sf.json.JSONObject innerObject = new net.sf.json.JSONObject();
							innerObject = (net.sf.json.JSONObject) olddata.get(key);
							
							net.sf.json.JSONObject updatingObject = new net.sf.json.JSONObject();
							updatingObject = (net.sf.json.JSONObject) education.get(key);
							commonNodes.append("merge ( `"+innerObject.get("course").toString().toLowerCase()+"`:commonNode:"+key+" { `name` : \""+innerObject.get("course").toString().toLowerCase()+"\" } ) ");						
							commonNodes.append("merge ( `"+employee.get("email")+"` ) - [r:having_"+key+"education] -> ( `"+innerObject.get("course").toString().toLowerCase()+"`) delete r ");
							commonNodes.append("merge ( `"+updatingObject.get("course").toString().toLowerCase()+"`:commonNode:"+key+" { `name` : \""+updatingObject.get("course").toString().toLowerCase()+"\" } ) ");					
							
						}
							
					}
					else
					{
						net.sf.json.JSONObject education = new net.sf.json.JSONObject();
						education = (net.sf.json.JSONObject) employee.get(field);
						for(String edField:educationdetails)
						{
							if(education.containsKey(edField))
							{
								net.sf.json.JSONObject innerObject = new net.sf.json.JSONObject();
								innerObject = (net.sf.json.JSONObject) education.get(edField);
								commonNodes.append("merge ( `"+innerObject.get("course").toString().toLowerCase()+"`:commonNode:"+edField+" { `name` : \""+innerObject.get("course").toString().toLowerCase()+"\" } ) ");
								
							}
						}
					}
				}
				else if(field.equals("personaldetails"))
				{
					net.sf.json.JSONObject personal = new net.sf.json.JSONObject();
					personal = (net.sf.json.JSONObject) employee.get(field);
					for(String PField:personaldetails)
					{
						if(personal.containsKey(PField))
						{
								commonNodes.append("merge ( `"+personal.get(PField).toString().toLowerCase()+"`:commonNode:"+PField+" { `name` : \""+personal.get(PField).toString().toLowerCase()+"\" } ) ");
							
						}
					}
				}
				else
				{
					commonNodes.append("merge ( `"+employee.get(field).toString().toLowerCase()+"`:commonNode:"+employee.get(field).toString().toLowerCase()+" { `name` : \""+ employee.get(field).toString().toLowerCase()+"\" } ) ");
				}
				
				
				employeeCommonNodes.append(commonNodes);				
			}
		}
		
		
		System.out.println("common node query : "+employeeCommonNodes);
		return employeeCommonNodes.toString();
	}
	
	
	@SuppressWarnings("unused")
	private Boolean toCheckFieldExists(net.sf.json.JSONObject employee,String keyname)
	{
		Boolean result = false;
		try {
			
			if(employee.containsKey(keyname))
			{
				result = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("The following exception is raised in toCheckFieldExists Method");
		}
		return result;
	}	
	
	private String createEmployeeRelationShipQuery(net.sf.json.JSONObject employee)
	{
		StringBuffer employeeNodeRelationShips = new StringBuffer();		
		
		final ArrayList<String> educationdetails = new ArrayList<String>();
		educationdetails.add("basic");
		educationdetails.add("master");
		educationdetails.add("doctorate");
		
		final ArrayList<String> personaldetails = new ArrayList<String>();		
		personaldetails.add("currentlocation");
		

		final ArrayList<String> commonNodeFields = new ArrayList<String>();
		commonNodeFields.add("personaldetails");
		commonNodeFields.add("currentindustrytype");		
		commonNodeFields.add("keyskills");
		commonNodeFields.add("educationdetails");		
		
		
		final ArrayList<String> relationShipNodeFields = new ArrayList<String>();
		relationShipNodeFields.add("personaldetails");
		relationShipNodeFields.add("currentindustrytype");		
		relationShipNodeFields.add("keyskills");
		relationShipNodeFields.add("educationdetails");
		relationShipNodeFields.add("employeementtype");
		
		if(employee.containsKey("employeementtype"))
		{	
			if(employee.getString("employeementtype").equals("experiencer"))
			{
				relationShipNodeFields.add("experiencedetails");
			}
		}
		
		for(String field:relationShipNodeFields)
		{			
			if(employee.containsKey(field) && !employee.get(field).equals(""))
			{
				StringBuffer commonNodes = new StringBuffer();
				// merge (IT:commonNode {name:"IT"})			
				
				if(field.equals("keyskills"))
				{
					
					for(int i=0;i<employee.getJSONArray(field).size();i++)
					{											
						commonNodes.append("merge ( `"+employee.get("email")+"` ) - [:having_"+field+"] -> (`"+employee.getJSONArray(field).get(i).toString().toLowerCase()+"`) ");
					}										
					
				}
				else if(field.equals("experiencedetails"))
				{
					net.sf.json.JSONObject experiencedetails = new net.sf.json.JSONObject();
					experiencedetails = employee.getJSONObject("experiencedetails");					
					
					double experience = experiencedetails.getDouble("experience");
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
				
				else if(field.equals("educationdetails"))
				{
					net.sf.json.JSONObject education = new net.sf.json.JSONObject();
					education = (net.sf.json.JSONObject) employee.get(field);
					for(String edField:educationdetails)
					{
						if(education.containsKey(edField))
						{
							net.sf.json.JSONObject innerObject = new net.sf.json.JSONObject();
							innerObject = (net.sf.json.JSONObject) education.get(edField);
							commonNodes.append("merge ( `"+employee.get("email")+"` ) - [:having_"+edField+"education] -> ( `"+innerObject.get("course").toString().toLowerCase()+"`) ");
						}
					}				
					
				}
				else if(field.equals("personaldetails"))
				{
					net.sf.json.JSONObject personal = new net.sf.json.JSONObject();
					personal = (net.sf.json.JSONObject) employee.get(field);
					for(String PField:personaldetails)
					{
						if(personal.containsKey(PField))
						{
							commonNodes.append("merge ( `"+employee.get("email")+"` ) - [:having_"+PField+"] -> ( `"+personal.get(PField).toString().toLowerCase()+"`) ");
							
						}
					}
				}
				else if(field.equals("employeementtype"))
				{
					commonNodes.append("merge ( `"+employee.get("email")+"` ) - [:is"+employee.get(field).toString().toLowerCase()+"] -> ( `"+employee.get(field).toString().toLowerCase()+"`) ");
					
				}
				else
				{
					//merge (sample@sample.com) - [:having_industryType] -> (IT)
					commonNodes.append("merge ( `"+employee.get("email")+"` ) - [:having_"+field+"] -> ( `"+employee.get(field).toString().toLowerCase()+"`) ");
				}
				
				
				employeeNodeRelationShips.append(commonNodes);				
			}
		}		
		
		System.out.println("employeeNodeRelationShipQuery is : "+employeeNodeRelationShips);
		return employeeNodeRelationShips.toString();
	}

	@SuppressWarnings("unchecked")
	private String createEmployeeProfileNodeQuery(JSONObject employeeProfile)
	{
		StringBuffer employeeProfileNode= new StringBuffer();
		
		try{			
			// Append common employee merge email query
			employeeProfileNode.append(createEmployeeCommonQuery(employeeProfile));		
			
			HashMap<String, Object> professionaldetails = new HashMap<String, Object>();
			
			professionaldetails = (HashMap<String, Object>) employeeProfile.get("professionaldetails");
			
			
			LinkedHashMap<String, Object> currentCompanyDetails = new LinkedHashMap<String, Object>();
			currentCompanyDetails = (LinkedHashMap<String, Object>) professionaldetails.get("currentcompany");
			
			employeeProfileNode.append("merge ( `"+currentCompanyDetails.get("name").toString().toLowerCase()+"`:employer:company { `name` : \""+currentCompanyDetails.get("name").toString().toLowerCase()+"\" } ) ");
			
			if(professionaldetails.containsKey("previouscompanydetails"))
			{
				LinkedHashMap<String, Object> previousCompanyDetails = new LinkedHashMap<String, Object>();
				previousCompanyDetails = (LinkedHashMap<String, Object>) professionaldetails.get("previouscompanydetails");
				
				for(int i=0;i<previousCompanyDetails.size();i++)
				{
					LinkedHashMap<String, Object> previousCompany = (LinkedHashMap<String, Object>) previousCompanyDetails.get("previouscompany"+i);
					employeeProfileNode.append("merge ( `"+previousCompany.get("name").toString().toLowerCase()+"`:employer:company { `name` : \""+previousCompany.get("name").toString().toLowerCase()+"\" } ) ");			
				}
				
				
			}			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception Raised in createEmployeeProfileNodeQuery of employeeBulidNodeDao layer");
		}
		System.out.println("EmployeeProfileNode Query is : "+employeeProfileNode);
		
		return employeeProfileNode.toString();
	}
	
	private String createEmployeeCommonQuery(JSONObject employee)
	{
		StringBuffer commonQuery = new StringBuffer();
		//employeeMergeNode.append("merge ( `"+employee.get("email")+"` "+nodelablename+" {");
		commonQuery.append("merge ( `"+employee.get("email")+"`:employee { `email` : \""+employee.get("email")+"\" } ) ");
		return commonQuery.toString();
	}
	
	private String createEmployeeCommonEmailQuery(net.sf.json.JSONObject employee)
	{
		StringBuffer commonQuery = new StringBuffer();
		//employeeMergeNode.append("merge ( `"+employee.get("email")+"` "+nodelablename+" {");
		commonQuery.append("merge ( `"+employee.get("email")+"`:employee { `email` : \""+employee.get("email")+"\" } ) ");
		return commonQuery.toString();
	}
	
	
	private String labelUpdationQuery(net.sf.json.JSONObject employee){
		
		/**
		 * 	Update node property query: merge(n {email:"fresher@gmail.com"}) set n.fullname="fresh"
		 * 						
		 * 	Update node label query : merge(n {email:"fresher@gmail.com"}) remove n:fresher set n:experiencer
		 *  	
		 */
		
		StringBuffer labelUpdationQuery = new StringBuffer();
		try 
		{
			String MailId =employee.getString("email"); 
			
			final ArrayList<String> labelNames = new ArrayList<String>();
			labelNames.add("currentindustrytype");
			
			// This is for finding the current location field. This field is embedded in personaldetails object.
			labelNames.add("personaldetails");
			
			labelNames.add("employeementtype");
			
							
			for(String label:labelNames)
			{
				if(employee.containsKey(label))
				{
					/*String value = (String) employee.get(label);
					
					// This is for converting integer experience to string. Because We can't create label name of starting with integer. So convert it 
					value = (label.equals("experience"))? "experience"+value:value;
					*/
					if(label.equals("employeementtype"))
					{
						// Generate Label Based on employementtype fresher or experiencer
						if(employee.get(label).toString().equals("fresher"))
						{
							// Update Label Query							
							labelUpdationQuery.append(" set `"+MailId+"`:`"+employee.get(label).toString().toLowerCase()+"` ");							
						}
						else
						{
							// if experiencer means get experiencer details object for getting experience in years.							
							labelUpdationQuery.append(" remove `"+MailId +"`:`fresher` set `"+MailId+"`:`"+employee.get(label).toString().toLowerCase()+"` ");							
						}
						
					}
					else if(label.equals("personaldetails"))
					{
						net.sf.json.JSONObject getPersonalDetails = new net.sf.json.JSONObject();
						getPersonalDetails =  (net.sf.json.JSONObject) employee.get(label);						
						// Get current location from personal details object						
						labelUpdationQuery.append(" set `"+MailId+"`:`"+getPersonalDetails.get("currentlocation").toString().toLowerCase()+"` ");
					}
					else
					{	
						labelUpdationQuery.append(" set `"+MailId+"`:`"+employee.get(label).toString().toLowerCase()+"` ");						
				    }
				}
			}	
				
				System.out.println("Final label updatin Query is : "+labelUpdationQuery);
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("The Above exception is raised in labelUdpationQUery method");
		}
		return labelUpdationQuery.toString();
	}
	
	
	private String updateProfessionalDetails(net.sf.json.JSONObject employeeProfile)
	{
		StringBuffer employeeProfileNode= new StringBuffer();
		
		try{			
			
			if(employeeProfile.containsKey("olddata"))
			{
				net.sf.json.JSONObject olddata = new net.sf.json.JSONObject();
				olddata = employeeProfile.getJSONObject("olddata");
				
				if(olddata.containsKey("currentcompany"))
				{	
					
					net.sf.json.JSONObject professionaldetails = new net.sf.json.JSONObject();				
					professionaldetails = (net.sf.json.JSONObject) employeeProfile.get("professionaldetails");	
					
					net.sf.json.JSONObject currentCompanyDetails = new net.sf.json.JSONObject();
					currentCompanyDetails = (net.sf.json.JSONObject) professionaldetails.get("currentcompany");
					
					net.sf.json.JSONObject olddatacurrentcompanydetails = new net.sf.json.JSONObject();
					olddatacurrentcompanydetails = olddata.getJSONObject("currentcompany");
					
					employeeProfileNode.append("merge ( `"+olddatacurrentcompanydetails.get("name").toString().toLowerCase()+"`:employer:company { `name` : \""+olddatacurrentcompanydetails.get("name").toString().toLowerCase()+"\" } ) ");					
					employeeProfileNode.append("merge ( `"+employeeProfile.get("email").toString().toLowerCase()+"` ) - [r:working_in { name : \""+olddatacurrentcompanydetails.get("name").toString().toLowerCase() +"\"  } ] " +" -> ( `"+olddatacurrentcompanydetails.get("name").toString().toLowerCase() +"` ) delete r ");
					employeeProfileNode.append("merge ( `"+currentCompanyDetails.get("name").toString().toLowerCase()+"`:employer:company { `name` : \""+currentCompanyDetails.get("name").toString().toLowerCase()+"\" } ) ");								
				}
				else
				{
					
					ArrayList<String> previouscompanies = new ArrayList<String>();
					previouscompanies.add("professionaldetails.previouscompanydetails.previouscompany0");
					previouscompanies.add("professionaldetails.previouscompanydetails.previouscompany1");
					previouscompanies.add("professionaldetails.previouscompanydetails.previouscompany2");
					
					
					for(String companyField : previouscompanies)
					{	
						if(olddata.containsKey(companyField))
						{	
							net.sf.json.JSONObject olddatapreviousCompany = (net.sf.json.JSONObject) olddata.get(companyField);
							net.sf.json.JSONObject previousCompany = (net.sf.json.JSONObject) employeeProfile.get(companyField);							
							employeeProfileNode.append("merge ( `"+olddatapreviousCompany.get("name").toString().toLowerCase()+"`:employer:company { `name` : \""+olddatapreviousCompany.get("name").toString().toLowerCase()+"\" } ) ");
							employeeProfileNode.append("merge ( `"+employeeProfile.get("email").toString().toLowerCase()+"`) - [r:worked_as { name : \""+olddatapreviousCompany.get("name").toString().toLowerCase() +"\" } ]" +" ->  ( `"+olddatapreviousCompany.get("name").toString().toLowerCase() +"` ) delete r ");							
							employeeProfileNode.append("merge ( `"+previousCompany.get("name").toString().toLowerCase()+"`:employer:company { `name` : \""+previousCompany.get("name").toString().toLowerCase()+"\" } ) ");
							
						}	
						
					}
				}
				
			}
			else
			{
				if(employeeProfile.containsKey("professionaldetails"))
				{	
					net.sf.json.JSONObject professionaldetails = new net.sf.json.JSONObject();				
					professionaldetails = (net.sf.json.JSONObject) employeeProfile.get("professionaldetails");				
					net.sf.json.JSONObject currentCompanyDetails = new net.sf.json.JSONObject();
					currentCompanyDetails = (net.sf.json.JSONObject) professionaldetails.get("currentcompany");				
					employeeProfileNode.append("merge ( `"+currentCompanyDetails.get("name").toString().toLowerCase()+"`:employer:company { `name` : \""+currentCompanyDetails.get("name").toString().toLowerCase()+"\" } ) ");
				}
				
				ArrayList<String> previouscompanies = new ArrayList<String>();
				previouscompanies.add("professionaldetails.previouscompanydetails.previouscompany0");
				previouscompanies.add("professionaldetails.previouscompanydetails.previouscompany1");
				previouscompanies.add("professionaldetails.previouscompanydetails.previouscompany2");
				
				
				for(String companyField : previouscompanies)
				{	
					if(employeeProfile.containsKey(companyField))
					{				
						net.sf.json.JSONObject previousCompany = (net.sf.json.JSONObject) employeeProfile.get(companyField);										
						employeeProfileNode.append("merge ( `"+previousCompany.get("name").toString().toLowerCase()+"`:employer:company { `name` : \""+previousCompany.get("name").toString().toLowerCase()+"\" } ) ");
					}	
					
				}
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception Raised in createEmployeeProfileNodeQuery of employeeBulidNodeDao layer");
		}
		System.out.println("EmployeeProfileNode Query is : "+employeeProfileNode);
		
		return employeeProfileNode.toString();
	}
	
	

	private String updateProfessionalDetailsRelationShipQuery(net.sf.json.JSONObject employeeProfile)
	{
		
		StringBuffer employeetoCompanyRelationShipQuery= new StringBuffer();		
		try{
			
			//merge (sample@sample.com) - [:having_industryType] -> (IT)
			String commonforAll = "merge ( `"+employeeProfile.get("email")+"`)";		
			
			if(employeeProfile.containsKey("professionaldetails"))
			{	
				net.sf.json.JSONObject professionaldetails = new net.sf.json.JSONObject();				
				professionaldetails = (net.sf.json.JSONObject) employeeProfile.get("professionaldetails");				
				net.sf.json.JSONObject currentCompanyDetails = new net.sf.json.JSONObject();
				currentCompanyDetails = (net.sf.json.JSONObject) professionaldetails.get("currentcompany");
				employeetoCompanyRelationShipQuery.append(commonforAll+" - [:working_in { name : \""+currentCompanyDetails.get("name").toString().toLowerCase() +"\" , role : \""+currentCompanyDetails.get("role")+"\" } ] " +" -> ( `"+currentCompanyDetails.get("name").toString().toLowerCase() +"` )");				
			}
			
			ArrayList<String> previouscompanies = new ArrayList<String>();
			previouscompanies.add("professionaldetails.previouscompanydetails.previouscompany0");
			previouscompanies.add("professionaldetails.previouscompanydetails.previouscompany1");
			previouscompanies.add("professionaldetails.previouscompanydetails.previouscompany2");
			
			for(String companyField : previouscompanies)
			{	
				if(employeeProfile.containsKey(companyField))
				{					
					net.sf.json.JSONObject previousCompany = (net.sf.json.JSONObject) employeeProfile.get(companyField);
					employeetoCompanyRelationShipQuery.append(commonforAll+" - [:worked_as { name : \""+previousCompany.get("name").toString().toLowerCase() +"\" , role : \""+previousCompany.get("role")+"\" } ]" +" ->  ( `"+previousCompany.get("name").toString().toLowerCase() +"` )");
				}	
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
	private String createEmployeetoCompanyRelationShipQuery(JSONObject employeeProfile)
	{
		
		StringBuffer employeetoCompanyRelationShipQuery= new StringBuffer();		
		try{
			
			//merge (sample@sample.com) - [:having_industryType] -> (IT)
			String commonforAll = "merge ( `"+employeeProfile.get("email")+"`)";	
			
			HashMap<String, Object> professionaldetails = new HashMap<String, Object>();
			
			professionaldetails = (HashMap<String, Object>) employeeProfile.get("professionaldetails");
			
			
			LinkedHashMap<String, Object> currentCompanyDetails = new LinkedHashMap<String, Object>();
			currentCompanyDetails = (LinkedHashMap<String, Object>) professionaldetails.get("currentcompany");			
			
			employeetoCompanyRelationShipQuery.append(commonforAll+" - [:working_in { name : \""+currentCompanyDetails.get("name").toString().toLowerCase() +"\" , role : \""+currentCompanyDetails.get("role")+"\" } ] " +" -> ( `"+currentCompanyDetails.get("name").toString().toLowerCase() +"` )");
						
			if(professionaldetails.containsKey("previouscompanydetails"))
			{
				LinkedHashMap<String, Object> previousCompanyDetails = new LinkedHashMap<String, Object>();
				previousCompanyDetails = (LinkedHashMap<String, Object>) professionaldetails.get("previouscompanydetails");
				
				for(int i=0;i<previousCompanyDetails.size();i++)
				{
					LinkedHashMap<String, Object> previousCompany = (LinkedHashMap<String, Object>) previousCompanyDetails.get("previouscompany"+i);
					employeetoCompanyRelationShipQuery.append(commonforAll+" - [:worked_as { name : \""+previousCompany.get("name").toString().toLowerCase() +"\" , role : \""+previousCompany.get("role")+"\" } ]" +" ->  ( `"+previousCompany.get("name").toString().toLowerCase() +"` )");
				}	
				
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
