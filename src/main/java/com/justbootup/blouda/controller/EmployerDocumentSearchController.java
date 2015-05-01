package com.justbootup.blouda.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justbootup.blouda.serviceApi.IEmployerDocumentSearchService;
import com.justbootup.blouda.util.RServe;

@Controller
public class EmployerDocumentSearchController {
	
	@Autowired
	IEmployerDocumentSearchService employerDocumentSearchService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/employerDocSearch",method=RequestMethod.GET)
	public @ResponseBody JSONObject employerSearch() throws SQLException
	{
		JSONObject sample = new JSONObject();
		sample.put("name", "Prakash");
		employerDocumentSearchService.getDocuments();	
		return sample;
	}	
	
	
	@RequestMapping(value="/searchWholeDocument",method=RequestMethod.POST)
	public @ResponseBody LinkedHashMap<String, Object> employerWholeDocumentSearch(@RequestBody JSONObject keywords) throws SQLException
	{
		LinkedHashMap<String, Object> wholeDocResult = new LinkedHashMap<String, Object>();		
		wholeDocResult = employerDocumentSearchService.getWholeSearchDocuments(keywords);	
		return wholeDocResult;
	}	
	
	@RequestMapping(value="/multikeywordDocSearch",method=RequestMethod.POST)
	public @ResponseBody LinkedHashMap<String, Object> employerMultikeywordDocumentSearch(@RequestBody JSONObject multiKeywords) throws SQLException
	{
		LinkedHashMap<String, Object> multiDocResult = new LinkedHashMap<String, Object>();			
		multiDocResult = employerDocumentSearchService.getMultikeywordSearchDocument(multiKeywords);		
		return multiDocResult;
	}
	
	@RequestMapping(value="/downloadResume",method=RequestMethod.POST)
	public void employerDownloadSingleResume(@RequestBody JSONObject selectedEmployee,HttpServletResponse response) throws Exception
	{		
		
		response.setContentType("Content-Disposition:attachment; filename=Naveen-naveen@gmail.com.pdf");
		response.setContentType("application/pdf");
		response.flushBuffer();
			
	}
	
	@RequestMapping(value="/downloadResume1", method = RequestMethod.POST)
	public void doDownload(@RequestBody JSONObject selectedEmployee, HttpServletRequest request, HttpServletResponse response) throws Exception {
	           
        HashMap<String,Object> resumeDetails = new HashMap<String,Object>();        
        resumeDetails = employerDocumentSearchService.getEmployeeResume(selectedEmployee);
        byte[] filecontent = (byte[]) resumeDetails.get("filecontent");
        response.setContentType("application/octet-stream"); 
        
        response.setHeader("Content-Disposition", "attachment; filename="+resumeDetails.get("filename"));
        response.setHeader("filename",resumeDetails.get("filename").toString());
        response.getOutputStream().write(filecontent);
 
       response.flushBuffer();
	}

}
