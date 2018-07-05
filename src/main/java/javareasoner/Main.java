package javareasoner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

import javareasoner.inspect.InspectToAxiom;
import javareasoner.inspect.ReasonedArtMarketInspector;
import javareasoner.ontology.AMOntologyHandler;


public class Main {
	
	private static AMOntologyHandler oh = new AMOntologyHandler();
	private static InspectToAxiom app = new ReasonedArtMarketInspector(oh);
	private static String inputFilePath = "input.owl";

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
		
		//Init ontology and determine serialization type from args
		initOntologyHandler(args);
		
		//Ask for connection to debuggable app
		Scanner scan = new Scanner(System.in);
		initDebugger(scan);
		
		System.out.println("\nPaint(x)");
		oh.getInstances("Paint").forEach(System.out::println);
		
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
		
		oh.addStringAxiom(contentRDFXML, SerializationType.RDFXML);
		System.out.println("\nAxioms ADDED");
		System.out.println("\nPaint(x)");
		oh.getInstances("Paint").forEach(System.out::println);
		
		oh.addStringAxiom(contentFUNCTIONAL, SerializationType.FUNCTIONAL);
		System.out.println("\nAxioms ADDED");
		System.out.println("\nPaint(x)");
		oh.getInstances("Paint").forEach(System.out::println);
		
		System.out.println("\nq(x) := Artist(x) and creates(x,y) and ArtWork(y)");
		oh.getInstancesArtistsCreatingArtworks().forEach(System.out::println);
		
		while (true) {
			
			System.out.println("\nDo you want me to read <input.txt> to parse additional axioms?\n"
					+ "If NO type \"exit\"");
			String s;
			s = scan.next();
			
			if (s.toLowerCase().equals("exit"))
				break;
		
			try(BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();
	
			    while (line != null) {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = br.readLine();
			    }
			    
			    String content = sb.toString();
			    oh.addStringAxiom(content, SerializationType.FUNCTIONAL);
			    
			    System.out.println("Is ontology still consistent? " + oh.isConsistent());
			    
			    //TO be DELETED
			    oh.getInstancesArtistsCreatingArtworks().forEach(System.out::println);
			    oh.getInstances("Paint").forEach(System.out::println);
			    
			}
		}
		
		oh.saveOntology();
		
		scan.close();
		
	}

	private static void initDebugger(Scanner scan) {
		
		System.out.println("Do you want to attach to application (y/n)");
		String s = scan.next();
		
		if (s.toLowerCase().equals("y")) {
			
			Thread debugger = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						
						DebugAttach.startDebug(app, oh);
						
					} catch (IOException | IllegalConnectorArgumentsException | InterruptedException
							| IncompatibleThreadStateException | AbsentInformationException e) {
						e.printStackTrace();
					} 		
				}
		
			});
			
			debugger.start();
		}
		
		return;
		
	}

	private static void initOntologyHandler(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {

		switch (args.length) {
			case 2:
				switch (args[2].toLowerCase()) {
					case "functional":
						oh.serializationType = SerializationType.FUNCTIONAL;
						break;
					case "manchester":
						oh.serializationType = SerializationType.MANCHESTER;
						break;
					case "rdfxml":
						oh.serializationType = SerializationType.RDFXML;
						break;
					case "turtle":
						oh.serializationType = SerializationType.TURTLE;
						break;
				}
	        case 1:
	            oh.loadOntologyFromFile(args[0]);
	        case 0:
	        	oh.initOntology();
		}
		
	}
	

}
