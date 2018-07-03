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
		
		OntologyHandler.getInstances("crafts", null).forEach(System.out::println);
		
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
