package client;

import java.io.FileNotFoundException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import app.person.*;
import app.thing.*;

public class Main {

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		
		OntologyHandler.serializationType = SerializationType.FUNCTIONAL;
		
		switch (args.length) {
        case 1:
            OntologyHandler.loadOntologyFromFile(args[0]);
        case 0:
        	OntologyHandler.initOntology();
		}
		
		startApp();
		
		System.out.println("\nPaint(x)");
		OntologyHandler.getInstances("Paint").forEach(System.out::println);
		
		String contentRDFXML =
    	   		"<owl:NamedIndividual rdf:about=\"#Cenacolo\">\n" + 
           		"     <rdf:type rdf:resource=\"#Paint\"/>\n" + 
           		"     <id rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Cenacolo</id>\n" + 
           		"</owl:NamedIndividual>" +
           		"<owl:NamedIndividual rdf:about=\"#Leonardo\">\n" + 
           		"     <paints rdf:resource=\"#Cenacolo\"/>\n" + 
           		"</owl:NamedIndividual>";
		
		String contentFUNCTIONAL = "Declaration(NamedIndividual(:VanGogh))\n" + 
				"Declaration(NamedIndividual(:StanzaAdArles))\n" + 
				"ClassAssertion(:Painter :VanGogh)\n" + 
				"ClassAssertion(:Paint :StanzaAdArles)\n" +
				"DataPropertyAssertion(:name :VanGogh \"Van Gogh\"^^xsd:string)\n" +
				"DataPropertyAssertion(:name :StanzaAdArles \"Stanza Ad Arles\"^^xsd:string)\n" +
				"ObjectPropertyAssertion(:paints :VanGogh :StanzaAdArles)";
		
		OntologyHandler.addStringAxiom(contentRDFXML, SerializationType.RDFXML);
		System.out.println("\nAxioms ADDED");
		System.out.println("\nPaint(x)");
		OntologyHandler.getInstances("Paint").forEach(System.out::println);
		
		OntologyHandler.addStringAxiom(contentFUNCTIONAL, SerializationType.FUNCTIONAL);
		System.out.println("\nAxioms ADDED");
		System.out.println("\nPaint(x)");
		OntologyHandler.getInstances("Paint").forEach(System.out::println);
		
		System.out.println("\nq(x) := Artist(x) and creates(x,y) and ArtWork(y)");
		OntologyHandler.getInstancesArtistsCreatingArtworks().forEach(System.out::println);
		
		OntologyHandler.saveOntology();
		
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
