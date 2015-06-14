/**
 * 
 */
package com.justbootup.blouda.serviceApi;

import org.json.simple.JSONObject;

/**
 * @author admin
 *
 */
public interface IEmployeeBuildGraph {	
	public void createEmployeeNode(net.sf.json.JSONObject employee);
	public void createEmployeeProfileNode(JSONObject employeeProfile);
	public void updateEmployeeProfileNode(net.sf.json.JSONObject employee);
}
