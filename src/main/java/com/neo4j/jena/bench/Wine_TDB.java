/*
 * Copyright 2014 NUST. All rights reserved.
 * Use is subject to license terms.
 * 
 * Modified 2017 Libertas. All rights reserved.
 */
package com.neo4j.jena.bench;

import java.io.InputStream;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
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
 * * Test Class of TDB for Wine dataset.
 * 
 * @author Khalid Latif, Mahek Hanfi, Libertas
 */
public class Wine_TDB {
	private static final String TDB_STORE = "YOUR_TDBSTORE_PATH";
	
	private static final String inputFileName = "wine.owl" ;

	public static void main(String[] args) {
		Dataset dataset = TDBFactory.createDataset(TDB_STORE);
//		Dataset dataset = DatasetFactory.createMem();
		
		Wine_TDB.write(dataset);
		Wine_TDB.search(dataset);
		dataset.close();
	}
	
	public static void search(Dataset dataset) {
		Model tdbmodel = dataset.getDefaultModel();
		      
		String s2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
					"PREFIX food: <http://www.w3.org/TR/2003/PR-owl-guide-20031209/food#>"+
					"PREFIX wine: <http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#>" +
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>"+
					"SELECT ?X WHERE {"+
					"?X food:SweetFruit ?Z . }";

        Query query = QueryFactory.create(s2);
        QueryExecution qExe = QueryExecutionFactory.create(query, tdbmodel);
        StopWatch watch = new StopWatch();
        ResultSet results = qExe.execSelect();
        System.out.println("Query took (ms): "+ watch.stop());
        //ResultSetFormatter.out(System.out, results);
        
        int count=0;
        while(results.hasNext()){
        	//System.out.println("in while"+count);
        	QuerySolution sol = results.next();
        	System.out.print(sol.get("X"));
        	count++;
        }
       System.out.println("Record fetched:"+ count);
       
	}
	
	public static void write(Dataset dataset) {
		InputStream in = FileManager.get().open( inputFileName );
		if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }
        
		Model model = ModelFactory.createDefaultModel();
        model.read(in,"","RDF");
        double triples = model.size();
        System.out.println("Model loaded with " +  triples + " triples");
		
		Model tdbmodel = dataset.getDefaultModel();
		System.out.println("TDBGraph Model initiated");
		StopWatch watch = new StopWatch();
		tdbmodel.add(model);
		System.out.println("Storing completed (ms): " + watch.stop());
	}
	
}
