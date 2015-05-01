/**
 * 
 */

package com.justbootup.blouda.util;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;

/**
 * @author Prakash.A
 * Mar 25, 2015
 * MongodbConnection.java
 */
public class MongodbConnection {
	
	/**
	 * 
	 * @return Mongodb Connection Object 
	 * @throws UnknownHostException 
	 */
	public MongoClient connect() throws UnknownHostException{
		
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		return mongoClient;
	}	
	
}
