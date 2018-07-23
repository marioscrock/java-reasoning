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
import org.apache.jena.rdf.model.Model;
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

public class SPARQLEngine {
	
	/**
	 * Execute a SPARQL queries on the given model printing on System.Out
	 * the result set formatted.
	 * @param queryString The query to be performed
	 * @param model The model to execute the query on
	 */
	public static void askQuery(String queryString, Model model) {
		
		Query query = QueryFactory.create(queryString) ;
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect() ;
			ResultSetFormatter.out(System.out, results, query) ;
		}
		
	}
	
	/**
	 * Load the ontology in file at {@code ontPath}, through a DL reasoner
	 * exports to file at {@code infOntPath} the ontology and all inferrable axioms.
	 * @param ontPath Starting ontology
	 * @param infOntPath Post-reasoning ontology
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 */
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
