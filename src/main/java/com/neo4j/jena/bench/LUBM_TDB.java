/*
 * Copyright 2014 NUST. All rights reserved.
 * Use is subject to license terms.
 * 
 * Modified 2017 Libertas. All rights reserved.
 */
package com.neo4j.jena.bench;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

/**
 * Test Class for LUBM dataset.
 * 
 * @author Khalid Latif, Mahek Hanfi (2014-03-10), Libertas (2017)
 */

public class LUBM_TDB {
	private static final String TDB_STORE = "YOUR_TDBSTORE_PATH";
	
	private static final String inputFileName = "University.rdf" ;
	
	 static Logger log= Logger.getLogger(LUBM.class);

	public static void main(String[] args) {
		Dataset dataset = TDBFactory.createDataset(TDB_STORE);
//		Dataset dataset = DatasetFactory.createMem();
		log.info("Connection created");
		LUBM_TDB.write(dataset);
		LUBM_TDB.search(dataset);
		dataset.close();
		log.info("Connection closed");
	}
	
	public static void search(Dataset dataset) {
		Model tdbmodel = dataset.getDefaultModel();
		
		//long start = System.currentTimeMillis();
		String s2 = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                	"PREFIX ub: <http://swat.cse.lehigh.edu/onto/univ-bench.owl#>" +
                "SELECT ?X ?name "+
                "WHERE" +
                "{ ?X ub:name ?name ." +
                 "FILTER regex(?name,\"^Publication\") ."+
                "}"; 
       	          
        Query query = QueryFactory.create(s2);
        QueryExecution qExe = QueryExecutionFactory.create(query, tdbmodel);
        StopWatch watch = new StopWatch();
        ResultSet results = qExe.execSelect();
        log.info("Query took (ms): "+ watch.stop());
        System.out.println("Query took (ms): "+ watch.stop());
        //ResultSetFormatter.out(System.out, results);
        
        int count=0;
        while(results.hasNext()){
        	//System.out.println("in while"+count);
        	QuerySolution sol = results.next();
        	System.out.println(sol.get("name"));
        	count++;
        }
       
       log.info("Record fetched:"+ count);
       System.out.println("Record fetched:"+ count);
       
	}

	public static void write(Dataset dataset) {
		Logger log= Logger.getLogger(Wine.class);
		InputStream in = FileManager.get().open( inputFileName );
		if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }
        
		Model model = ModelFactory.createDefaultModel();
        model.read(in,"","RDF");
        double triples = model.size();
        log.info("Model loaded with " +  triples + " triples");
        System.out.println("Model loaded with " +  triples + " triples");
        
		log.info("Connection created");
		Model tdbmodel = dataset.getDefaultModel();
		log.info("NeoGraph Model initiated");
		System.out.println("NeoGraph Model initiated");
		StopWatch watch = new StopWatch();
		tdbmodel.add(model);
		log.info("Storing completed (ms): " + watch.stop());
		System.out.println("Storing completed (ms): " + watch.stop());
	}
}
