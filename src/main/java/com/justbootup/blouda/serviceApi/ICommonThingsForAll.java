package com.justbootup.blouda.serviceApi;

import com.mongodb.BasicDBObject;

public interface ICommonThingsForAll {
	
		public void getAllCompaniesName();
		public void getAllLocations();
		public void getAllSkills();
		public void getAllIndustryTypes();
		public BasicDBObject friendSearchViewData();
}
