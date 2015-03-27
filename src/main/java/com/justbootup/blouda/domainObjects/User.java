package com.justbootup.blouda.domainObjects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {
	
	@Id
	private long id;
	private String name;
	private String password;
	private boolean enabled;
	private long privilegeId;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	boolean isEnabled() {
		return enabled;
	}
	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	long getPrivilegeId() {
		return privilegeId;
	}
	void setPrivilegeId(long privilegeId) {
		this.privilegeId = privilegeId;
	}
	
	

}
