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
            OntologyHandler.loadOntology(args[0]);
        case 0:
        	OntologyHandler.initOntology();
		}
		
		startApp();
		
	}
	
	private static void startApp() {
		
		Painter leonardo = new Painter();
		leonardo.identifier = "Leonardo";
		
		Paint monnaLisa = new Paint();
		monnaLisa.identifier = "MonnaLisa";
		
		leonardo.paints(monnaLisa);
		
		Sculptor bernini = new Sculptor();
		bernini.identifier = "Bernini";
		
		Sculpt david = new Sculpt();
		david.identifier = "David";
		
		bernini.sculpts(david);
		
		Artisan belfiore = new Artisan();
		belfiore.identifier = "Belfiore";
		
		Product product05714 = new Product();
		product05714.identifier = "Product05714";
		
		belfiore.produces(product05714);
			
	}


}
