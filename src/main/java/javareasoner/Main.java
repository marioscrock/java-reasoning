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
import javareasoner.ontology.DLQueryEngine;


public class Main {
	
	private static AMOntologyHandler oh;
	private static InspectToAxiom app;
	private static DLQueryEngine query;
	
	/**
	 * Example Main to deal with ArtMarket ontology and related java application.
	 * @param args	If no args: init default ontology, if {@code args.length} equal to 1:
	 * 1st arg specifies the serialization type to save the ontology (functional, manchester,
	 * rdfxml, turtle), if {@code args.length} equal to 2: 2nd arg is used as file path to load the ontology 
	 * instead of default one
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, InterruptedException {
		
		oh = new AMOntologyHandler();
		
		//Init ontology and determine serialization type from args
		initOntologyHandler(args);
		
		oh.printOntology();
		System.out.println("\nConceptual Model loaded\n");
		
		oh.reasoningRoutine();
		
		System.out.println("Parsing file input-rdfxml.owl");
		parseAxioms("input-rdfxml.owl", SerializationType.RDFXML);
		System.out.println("Axioms ADDED");
		oh.reasoningRoutine();
		
		System.out.println("Parsing file input-functional.owl");
		parseAxioms("input-functional.owl", SerializationType.FUNCTIONAL);
		System.out.println("\nAxioms ADDED");
		oh.reasoningRoutine();
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
			oh.reasoningRoutine();
			    
		}
		
		query = new DLQueryEngine(oh);
		query.doQueryLoop();
		
		oh.saveOntology();
		scan.close();
		
	}
	
	/**
	 * Manage connection to observed java application
	 * @param scan Scanner to receive inputs (usually System.in)
	 * @throws InterruptedException
	 */
	private static void initDebugger(Scanner scan) throws InterruptedException {
		
		System.out.println("Do you want to attach to application (y/n)");
		String s = scan.next();
		
		if (s.toLowerCase().equals("y")) {
			
			app = new ReasonedArtMarketInspector(oh);
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
	
	/**
	 * Parse args of main.
	 * @param args Args of main method
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 */
	private static void initOntologyHandler(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {

		switch (args.length) {
			case 1:
				switch (args[2].toLowerCase()) {
					case "functional":
						oh.setSerializationType(SerializationType.FUNCTIONAL);
						break;
					case "manchester":
						oh.setSerializationType(SerializationType.MANCHESTER);
						break;
					case "rdfxml":
						oh.setSerializationType(SerializationType.RDFXML);
						break;
					case "turtle":
						oh.setSerializationType(SerializationType.TURTLE);
						break;
				}
	        case 2:
	            oh.loadOntologyFromFile(args[0]);
	        case 0:
	        	oh.initOntology();
		}
		
	}
	
	/**
	 * Parse axioms from file.
	 * @param filePath	File path of the file to be parsed
	 * @param serType	Serialization type to identify correct parser
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
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
