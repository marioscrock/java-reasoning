package javareasoner;

import java.io.IOException;
import java.util.Scanner;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import javareasoner.inspect.InspectToAxiom;
import javareasoner.inspect.ReasonedArtMarketInspector;
import javareasoner.ontology.AMOntologyHandler;
import javareasoner.ontology.DLQueryEngine;


public class MainAM {
	
	private static AMOntologyHandler oh;
	private static InspectToAxiom inspector;
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
		Main.initOntologyHandler(oh, args);
		
		oh.printOntology();
		System.out.println("\nConceptual Model loaded\n");
		
		oh.reasoningRoutine();
		
		System.out.println("Parsing file input-rdfxml.owl");
		Main.parseAxioms(oh, "input-rdfxml.owl", SerializationType.RDFXML);
		System.out.println("Axioms ADDED");
		oh.reasoningRoutine();
		
		System.out.println("Parsing file input-functional.owl");
		Main.parseAxioms(oh, "input-functional.owl", SerializationType.FUNCTIONAL);
		System.out.println("\nAxioms ADDED");
		oh.reasoningRoutine();
		oh.saveOntology();
		
		//Ask for connection to debuggable app
		Scanner scan = new Scanner(System.in);
		inspector = new ReasonedArtMarketInspector(oh);
		Main.initDebugger(inspector, oh, scan);
		
		Main.parsingLoop(oh, oh, scan, "inputAM.owl");
		
		query = new DLQueryEngine(oh);
		query.doQueryLoop();
		
		oh.saveOntology();
		scan.close();
		
	}

}
