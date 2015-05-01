package com.justbootup.blouda.serviceApi;

import java.util.*;

import org.json.simple.JSONObject;

public interface IEmployerDocumentSearchService {
	public void getDocuments();
	public LinkedHashMap<String, Object> getWholeSearchDocuments(JSONObject keywords);
	public LinkedHashMap<String, Object> getMultikeywordSearchDocument(JSONObject multiSearchKeyword);
	public HashMap<String,Object> getEmployeeResume(JSONObject selectedEmployee) throws Exception;
}
