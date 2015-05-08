/**
 * 
 */
package com.justbootup.blouda.util;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;






import com.mongodb.DBCursor;

/**
 * @author admin
 *
 */
public class RServe {
	public void rServe() throws REXPMismatchException, Exception{
		try {
            RConnection c = new RConnection();// make a new local connection on default port (6311)
            
            double d[] = c.eval("rnorm(10)").asDoubles();   
            
            
            org.rosuda.REngine.REXP x0 = c.eval("R.version.string");           
            
            int[] datax = new int[] {10,20,10,20,10,30,50,10,30,60,80,90};
            
            c.assign("x",datax);
            
            int[] freq = c.eval("table(x)").asIntegers();
            
           for(int i:freq)
           {
        	   System.out.println(i);
           }
        	   
            
            System.out.println(x0.asString());
         
           c.close(); 
            
		} catch (REngineException e) {
            //manipulation
        }    
	}
	
	public void getData(DBCursor cursor) throws Exception
	{
		System.out.println("DB Object is : "+cursor);
		RConnection c = new RConnection();
		
		c.eval("library(rmongodb)");
		
		REXP pwd = c.parseAndEval("getwd()");
        System.out.println(pwd.asString());
        
        
		REXP connection = c.parseAndEval("mongo <- mongo.create()");
		if (connection.inherits("try-error")) System.err.println("Error: "+connection.asString());
        else System.out.println("Success eval 1");
		REXP r = c.parseAndEval("db <- \"jobportal\"");

        if (r.inherits("try-error")) System.err.println("Error: "+r.asString());
        else System.out.println("Success eval 1");
        c.parseAndEval("collection <- \"jobportal.employeeuser\"");
        c.parseAndEval("query <- mongo.bson.from.list(list())");
        c.parseAndEval("sort <- mongo.bson.from.list(list())");
        c.parseAndEval("projection <-  mongo.bson.from.list(c(list(\"_id\" = 0L), list(\"industry_type\" = 1L)))");
        
        // Query Result
        c.parseAndEval("queryresult <- mongo.find.all(mongo,collection,query,sort,projection)");
        
        // Destroy or Close MongoDB Connection
        c.parseAndEval("mongo.destroy(mongo)");
        
        // Convert list to dataframe
        c.parseAndEval("dataframeObject <- do.call(rbind, lapply(queryresult, data.frame, stringsAsFactors=FALSE))");
        
        // Get only industrytype filed 
        c.parseAndEval("industrytype <- dataframeObject$industry_type");
        
        // Find the frequency of industrytype
        c.parseAndEval("industrytype.freq <- table(industrytype)");
        
        // Save empty file
        c.parseAndEval("jpeg('myfirst.jpg')");
        
        // Draw the bar chart
        c.parseAndEval("barplot(industrytype.freq)");
        
        // close the graphics pkg and save it to the file
        c.parseAndEval("dev.off()");
        
       	c.close();
	}
	
	public byte[] readImagefile() throws Exception{
		
		File fi = new File("C:\\Users\\admin\\Documents\\myfirst.jpg");
		byte[] fileContent = Files.readAllBytes(fi.toPath());		
		System.out.println("Byte Array is : "+fileContent);
		return fileContent;
	}

	

}
