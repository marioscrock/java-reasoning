package javareasoner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;


public class Main {

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
		
		OntologyHandler.serializationType = SerializationType.FUNCTIONAL;
		
		switch (args.length) {
			case 2:
				switch (args[2].toLowerCase()) {
					case "functional":
						OntologyHandler.serializationType = SerializationType.FUNCTIONAL;
					case "manchester":
						OntologyHandler.serializationType = SerializationType.MANCHESTER;
					case "rdfxml":
						OntologyHandler.serializationType = SerializationType.RDFXML;
					case "turtle":
						OntologyHandler.serializationType = SerializationType.TURTLE;
				}
	        case 1:
	            OntologyHandler.loadOntologyFromFile(args[0]);
	        case 0:
	        	OntologyHandler.initOntology();
		}
		
		System.out.println("Do you want to attach to application (y/n)");
		Scanner scan = new Scanner(System.in);
		String s = scan.next();
		
		if (s.toLowerCase().equals("y")) {
			
			Thread debugger = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						DebugAttach.startDebug(8000, "app.client.ArtMarket", "startApp");
					} catch (IOException | IllegalConnectorArgumentsException | InterruptedException
							| IncompatibleThreadStateException | AbsentInformationException e) {
						e.printStackTrace();
					} 		
				}
		
			});
			
			debugger.start();
			
		}
		
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
		
		while (true) {
			
			System.out.println("\nDo you want me to read <input.txt> to parse additional axioms?\n"
					+ "If NO type \"exit\"");
			s = scan.next();
			
			if (s.toLowerCase() == "exit")
				break;
		
			try(BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();
	
			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }
			    
			    String content = sb.toString();
			    OntologyHandler.addStringAxiom(content, SerializationType.FUNCTIONAL);
			    
			    System.out.println("Is ontology still consistent? " + OntologyHandler.isConsistent());
			    
			}
		}
		
		OntologyHandler.saveOntology();
		
		scan.close();
		
	}


}
