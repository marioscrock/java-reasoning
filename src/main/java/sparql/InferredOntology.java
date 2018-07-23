package sparql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;

public class InferredOntology {
	
	public static void main(String[] args) throws OWLOntologyCreationException,
    OWLOntologyStorageException, FileNotFoundException {
			
		//shouldCreateInferredAxioms("appOntologyES.owl", "inferredAppOntologyES.owl");
		
		Model modelPre = FileManager.get().loadModel("appOntologyES.owl", "RDFXML");
		Model modelPost = FileManager.get().loadModel("inferredAppOntologyES.owl", "RDFXML");
		
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
		
		System.out.println("For each VIPCustomer Products he's interestedIn and he can buy with a vip discount");
		System.out.println(queryString + "\n");
		System.out.println("Pre-reasoning model");
		askQuery(queryString, modelPre);
		System.out.println("*********************************************************\n\n");
		
		queryString = "PREFIX ex: <http://projects.ke.appOntology#> \n" + 
				"SELECT ?y (SUM(?z) AS ?totPriceInterestedIn) \n" + 
				"WHERE {\n" + 
				"	?y a ex:Guest .\n" +
				" 	?y ex:interestedIn ?x .\n" +
				" 	?x ex:price ?z .\n" +
				"}\n"
				+ "GROUP BY ?y" ;
		
		System.out.println("For each Guest prices sum for products he's interestedIn");
		System.out.println(queryString + "\n");
		System.out.println("Pre-reasoning model");
		askQuery(queryString, modelPre);
		System.out.println("*********************************************************\n\n");
		
		queryString = "PREFIX ex: <http://projects.ke.appOntology#> \n" + 
				"SELECT ?x ?y\n" + 
				"WHERE {\n" + 
				"	?x a ex:Customer .\n" +
				"	?y a ex:Product .\n" +
				"	?x ex:interestedIn ?y .\n" +
				"	?x ex:productOnOffer ?y .\n" +
				"}" ;
		
		System.out.println("For each Customer products he's interestedIn and he can buy with a discount");
		System.out.println(queryString + "\n");
		System.out.println("Pre-reasoning model");
		askQuery(queryString, modelPre);
		System.out.println("Pre-reasoning model with RDFS inference");
		askQuery(queryString, infmodelPreRDFS);
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
		
		System.out.println("For each Customer prices sum for PopularProducts he's interestedIn");
		System.out.println(queryString + "\n");
		System.out.println("Pre-reasoning model");
		askQuery(queryString, modelPre);
		System.out.println("Pre-reasoning model with RDFS inference");
		askQuery(queryString, infmodelPreRDFS);
		System.out.println("Pre-reasoning model with Jena engine OWL inference");
		System.out.println("[Cardinality restrictions supported only 0 and 1]");
		askQuery(queryString, infmodelPreOwl);
		System.out.println("Post-reasoning model");
		askQuery(queryString, modelPost);
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
		askQuery(queryString, modelPost);
		System.out.println("*********************************************************\n\n");
		
		
		
	}
	
	public static void askQuery(String queryString, Model model) {
		
		Query query = QueryFactory.create(queryString) ;
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect() ;
			ResultSetFormatter.out(System.out, results, query) ;
		}
		
	}
	
	public static void shouldCreateInferredAxioms(String ontPath, String infOntPath) throws OWLOntologyCreationException,
    OWLOntologyStorageException, FileNotFoundException {
		
		OWLReasonerFactory reasonerFactory = new ReasonerFactory();
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = man.getOWLDataFactory();
		OWLOntology ont = man.loadOntologyFromOntologyDocument(new File(ontPath));

		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ont);
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredSubClassAxiomGenerator());
        gens.add(new InferredClassAssertionAxiomGenerator());
        gens.add( new InferredDisjointClassesAxiomGenerator());
        gens.add( new InferredEquivalentClassAxiomGenerator());
        gens.add( new InferredEquivalentDataPropertiesAxiomGenerator());
        gens.add( new InferredEquivalentObjectPropertyAxiomGenerator());
        gens.add( new InferredInverseObjectPropertiesAxiomGenerator());
        gens.add( new InferredObjectPropertyCharacteristicAxiomGenerator());
        gens.add( new InferredPropertyAssertionGenerator());
        gens.add( new InferredSubDataPropertyAxiomGenerator());
        gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
        gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
        gens.add( new InferredSubObjectPropertyAxiomGenerator());
        
		OWLOntology infOnt = man.createOntology();

		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
		iog.fillOntology(df, infOnt);
		
		man.saveOntology(infOnt, new RDFXMLDocumentFormat(),
				new FileOutputStream(new File(infOntPath)));
		
	}

}
