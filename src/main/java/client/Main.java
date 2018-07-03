package client;

import java.io.FileNotFoundException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import app.person.*;
import app.thing.*;

public class Main {

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		
		switch (args.length) {
        case 1:
            OntologyHandler.loadOntologyFromFile(args[0]);
        case 0:
        	OntologyHandler.initOntology();
		}
		
		startApp();
		
		OntologyHandler.getInstances("Paint").forEach(System.out::println);
		
		String content = ""
    	   		+ "<rdf:RDF xmlns=\"http://projects.ke.appOntology#\"\n" + 
    	   		"     xml:base=\"http://projects.ke.appOntology\"\n" + 
    	   		"     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" + 
    	   		"     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" + 
    	   		"     xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n" + 
    	   		"     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" + 
    	   		"     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">" +
    	   		"<owl:NamedIndividual rdf:about=\"http://projects.ke.appOntology#Cenacolo\">\n" + 
           		"     <rdf:type rdf:resource=\"http://projects.ke.appOntology#Paint\"/>\n" + 
           		"     <id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Cenacolo</id>\n" + 
           		"</owl:NamedIndividual>" +
           		"<owl:NamedIndividual rdf:about=\"http://projects.ke.appOntology#Leonardo\">\n" + 
           		"     <paints rdf:resource=\"http://projects.ke.appOntology#Cenacolo\"/>\n" + 
           		"</owl:NamedIndividual>"
           		+ "</rdf:RDF>";
		
		OntologyHandler.addStringAxiom(content, ParserType.RDFXML);
		System.out.println("\nAxioms ADDED");
		OntologyHandler.getInstances("Paint").forEach(System.out::println);
		
	}
	
	private static void startApp() {
		
		Painter leonardo = new Painter("Leonardo");
		
		Paint monnaLisa = new Paint("MonnaLisa");
		
		leonardo.paints(monnaLisa);
		
		Sculptor bernini = new Sculptor("Bernini");
		
		Sculpt david = new Sculpt("David");
		
		bernini.sculpts(david);
		
		Artisan belfiore = new Artisan("Belfiore");
		
		Product product05714 = new Product("Product05714");
		
		belfiore.produces(product05714);
			
	}


}
