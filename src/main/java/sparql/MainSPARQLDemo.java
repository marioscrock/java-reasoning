package sparql;

import java.io.FileNotFoundException;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class MainSPARQLDemo {
	
	/**
	 * Main DEMO showing queries in SPARQL against ES ontology plus random data dumped from running application.
	 * The difference between different inferred models is shown.
	 * @param args
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws OWLOntologyCreationException,
    OWLOntologyStorageException, FileNotFoundException {
			
		SPARQLEngine.shouldCreateInferredAxioms("owl/appOntologyES.owl", "owl/inferredAppOntologyES.owl");
		
		Model modelPre = FileManager.get().loadModel("owl/appOntologyES.owl", "RDFXML");
		Model modelPost = FileManager.get().loadModel("owl/inferredAppOntologyES.owl", "RDFXML");
		
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		reasoner = reasoner.bindSchema(modelPre);
		InfModel infmodelPreOwl = ModelFactory.createInfModel(reasoner, modelPre);
		InfModel infmodelPreRDFS = ModelFactory.createRDFSModel(modelPre);
		
		String queryString = "PREFIX ex: <http://projects.ke.appOntology#> \n" + 
				"SELECT ?y ?x \n" + 
				"WHERE {\n" + 
				" 	?y ex:interestedIn ?x .\n" +
				" 	?y a ex:VIPCustomer .\n" +
				" 	?y ex:perc20Offer ?x .\n" +
				"}" ;
		
		System.out.println("For each VIPCustomer, products he's interestedIn and he can buy with a vip discount");
		System.out.println(queryString + "\n");
		System.out.println("Pre-reasoning model");
		SPARQLEngine.askQuery(queryString, modelPre);
		System.out.println("*********************************************************\n\n");
		
		queryString = "PREFIX ex: <http://projects.ke.appOntology#> \n" + 
				"SELECT ?y (SUM(?z) AS ?totPriceInterestedIn) \n" + 
				"WHERE {\n" + 
				"	?y a ex:Guest .\n" +
				" 	?y ex:interestedIn ?x .\n" +
				" 	?x ex:price ?z .\n" +
				"}\n"
				+ "GROUP BY ?y" ;
		
		System.out.println("For each Guest, prices sum for products he's interestedIn");
		System.out.println(queryString + "\n");
		System.out.println("Pre-reasoning model");
		SPARQLEngine.askQuery(queryString, modelPre);
		System.out.println("*********************************************************\n\n");
		
		queryString = "PREFIX ex: <http://projects.ke.appOntology#> \n" + 
				"SELECT ?x ?y\n" + 
				"WHERE {\n" + 
				"	?x a ex:Customer .\n" +
				"	?y a ex:Product .\n" +
				"	?x ex:interestedIn ?y .\n" +
				"	?x ex:productOnOffer ?y .\n" +
				"}" ;
		
		System.out.println("For each Customer, products he's interestedIn and he can buy with a discount");
		System.out.println(queryString + "\n");
		System.out.println("Pre-reasoning model");
		SPARQLEngine.askQuery(queryString, modelPre);
		System.out.println("Pre-reasoning model with RDFS inference");
		SPARQLEngine.askQuery(queryString, infmodelPreRDFS);
		System.out.println("*********************************************************\n\n");
		
		queryString = "PREFIX ex: <http://projects.ke.appOntology#> \n" + 
				"SELECT ?y (SUM(?z) AS ?totPriceInterestedIn) \n" + 
				"WHERE {\n" + 
				"   ?x a ex:PopularProduct .\n" +
				"	?y a ex:Customer .\n" +
				" 	?y ex:interestedIn ?x .\n" +
				" 	?x ex:price ?z .\n" +
				"}\n"
				+ "GROUP BY ?y" ;
		
		System.out.println("For each Customer, prices sum for PopularProducts he's interestedIn");
		System.out.println(queryString + "\n");
		System.out.println("Pre-reasoning model");
		SPARQLEngine.askQuery(queryString, modelPre);
		System.out.println("Pre-reasoning model with RDFS inference");
		SPARQLEngine.askQuery(queryString, infmodelPreRDFS);
		System.out.println("Pre-reasoning model with Jena engine OWL inference");
		System.out.println("[Cardinality restrictions supported only 0 and 1]");
		SPARQLEngine.askQuery(queryString, infmodelPreOwl);
		System.out.println("Post-reasoning model");
		SPARQLEngine.askQuery(queryString, modelPost);
		System.out.println("*********************************************************\n\n");
		
		queryString = "PREFIX ex: <http://projects.ke.appOntology#> \n" + 
				"SELECT ?x \n" + 
				"WHERE {\n" + 
				" 	?y a ex:VIPCustomer .\n" +
				" 	?y ex:perc10Offer ?x .\n" +
				" 	?y ex:perc20Offer ?x .\n" +
				"}" ;
		
		System.out.println("Check no VIPCustomer can have a perc10Offer and a perc20Offer on same product");
		System.out.println(queryString + "\n");
		System.out.println("Post-reasoning model");
		SPARQLEngine.askQuery(queryString, modelPost);
		System.out.println("*********************************************************\n\n");
		
		
		
	}

}
