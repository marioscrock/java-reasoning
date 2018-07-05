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

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, InterruptedException {
		
		//Init ontology and determine serialization type from args
		initOntologyHandler(args);
		
		oh.printOntology();
		System.out.println("\nConceptual Model loaded\n");
		
		oh.breakpointRoutine();
		
		System.out.println("Parsing file input-rdfxml.owl");
		parseAxioms("input-rdfxml.owl", SerializationType.RDFXML);
		System.out.println("Axioms ADDED");
		oh.breakpointRoutine();
		
		System.out.println("Parsing file input-functional.owl");
		parseAxioms("input-functional.owl", SerializationType.FUNCTIONAL);
		System.out.println("\nAxioms ADDED");
		oh.breakpointRoutine();
		oh.saveOntology();
		
		//Ask for connection to debuggable app
		Scanner scan = new Scanner(System.in);
		initDebugger(scan);	
		
		while (true) {
			
			System.out.println("\nDo you want me to read <input.owl> to parse additional axioms?\n"
					+ "If NO type \"exit\"\nFor RDFXML write \"RDFXML\" (any other string FUNCTIONAL parsing)");
			String s;
			s = scan.next();
			
			if (s.toLowerCase().equals("exit")) {
				break;
			}
			else {
				if (s.toLowerCase().equals("rdfxml")) {
					parseAxioms("input.owl", SerializationType.RDFXML);
				}
				else {
					parseAxioms("input.owl", SerializationType.FUNCTIONAL);
				}
			}
			
			System.out.println("\nAxioms ADDED");
			oh.breakpointRoutine();
			    
		}
		
		oh.saveOntology();
		scan.close();
		
	}

	private static void initDebugger(Scanner scan) throws InterruptedException {
		
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
			debugger.join();
			
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
	
	private static void parseAxioms(String filePath, SerializationType serType) throws FileNotFoundException, IOException {
		
		try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    
		    String content = sb.toString();
		    oh.addStringAxiom(content, serType);
		    
		}
		
	}
	

}
