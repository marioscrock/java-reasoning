package javareasoner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.UnloadableImportException;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

import javareasoner.inspect.InspectToAxiom;
import javareasoner.ontology.OntologyHandler;
import javareasoner.server.ReasoningServer;

/**
 * Common method for Main classes to run {@code javareasoner} package.
 * @author Mario
 *
 */
public class Main {
	
	/**
	 * Enable parsing loop asking if user want to parse a file to add axioms to the ontology.	
	 * @param oh OntologyHandler related to the ontology I want to add axioms to
	 * @param rs ReasoningServer providing reasoning routine
	 * @param scan Scanner to receive inputs (usually System.in)
	 * @param filepath File path of the file to parse
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void parsingLoop(OntologyHandler oh, ReasoningServer rs, Scanner scan, String filepath) throws FileNotFoundException, IOException {
		
		while (true) {
			
			System.out.println("\nDo you want me to read <" + filepath + "> to parse additional axioms?\n"
					+ "Type serialization format (if not recognised FUNCTIONAL parsing)\nType x to exit");
			
			String s;
			s = scan.next();
			boolean done = false;
			
			if (s.equalsIgnoreCase("x")) {
				break;
			}
			
			switch (s.toLowerCase()) {
				case "rdfxml":
					done = parseAxioms(oh, filepath, SerializationType.RDFXML);
					break;
				case "manchester":
					done = parseAxioms(oh, filepath, SerializationType.MANCHESTER);
					break;
				case "turtle":
					done = parseAxioms(oh, filepath, SerializationType.TURTLE);
					break;
				default:
					done = parseAxioms(oh, filepath, SerializationType.FUNCTIONAL);
					break;
			}
			
			if (done) {
				System.out.println("\nAxioms ADDED");
				rs.reasoningRoutine();
			}
			
		}
		    
	}
	
	/**
	 * Manage connection to observed java application
	 * @param inspector	Inspector to manage application
	 * @param rs	ReasoningServer to call routines at breakpoints
	 * @param scan	Scanner to receive inputs (usually System.in)
	 * @throws InterruptedException
	 */
	public static void initDebugger(InspectToAxiom inspector, ReasoningServer rs, Scanner scan) throws InterruptedException {
		
		System.out.println("Do you want to attach to application (y/n)");
		String s = scan.next();
		
		if (s.toLowerCase().equals("y")) {
			
			Thread debugger = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						
						DebugAttach.startDebug(inspector, rs);
						
					} catch (IOException | IllegalConnectorArgumentsException | InterruptedException
							| IncompatibleThreadStateException | AbsentInformationException e) {
						e.printStackTrace();
					} 		
				}
		
			});
			
			debugger.start();
			debugger.join();
			
		}
		
	}
	
	/**
	 * Parse args of main.
	 * @param oh	OntologyHandler to initialize
	 * @param args	Args of main method
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 */
	public static void initOntologyHandler(OntologyHandler oh, String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {

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
	 * @param filePath	OntologyHandler related to ontology you want to add axioms to
	 * @param filePath	File path of the file to be parsed
	 * @param serType	Serialization type to identify correct parser
	 * @return {@code true} if file correctly parsed, {@code false} otherwise 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean parseAxioms(OntologyHandler oh, String filePath, SerializationType serType) throws FileNotFoundException, IOException {
		
		try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    
		    String content = sb.toString();
		    
		    try {
		    	oh.addStringAxiom(content, serType);
		    	return true;    	
		    } catch (OWLParserException | OWLOntologyChangeException | UnloadableImportException e) {
		    	System.out.println("Cannot parse file!");
		    	System.out.println(e.getMessage() + "\n" + e.getClass());
		    	return false;
		    }
		    
		}
		
	}
	

}
