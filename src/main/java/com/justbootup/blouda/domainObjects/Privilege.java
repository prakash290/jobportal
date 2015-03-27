package com.justbootup.blouda.domainObjects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Privilege {
	
	@Id
	private long id;
	private String name;
	long getId() {
		return id;
	}
	void setId(long id) {
	
		this.id = id;
	}
	String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	
}
