package javareasoner.ontology;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.functional.parser.OWLFunctionalSyntaxOWLParserFactory;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFXMLParserFactory;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import javareasoner.SerializationType;

public class OntologyHandler {
	
	protected OWLOntologyManager manager;
	protected OWLOntology appOntology;
	protected OWLDataFactory df;
	protected OWLReasoner r;
	
	public SerializationType serializationType = SerializationType.FUNCTIONAL;
	
	protected final IRI IOR = IRI.create("http://projects.ke.appOntology");
	protected final File fileout = new File("appOntology.owl");
   
	public void loadRemoteOntology(String stringIRI) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		
		manager = OWLManager.createOWLOntologyManager();
		IRI importOntology = IRI.create(stringIRI);
		appOntology = manager.loadOntology(importOntology);
		
		saveOntology();
		
		getReasoner();
		
	}
	
	public void loadOntologyFromFile(String filePath) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		
		manager = OWLManager.createOWLOntologyManager();
		appOntology =  manager.loadOntologyFromOntologyDocument(new File(filePath));
		
		saveOntology();
		
		getReasoner();
		
	}
	
	public void initOntology() throws OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException {
		
		//EMPTY ONTOLOGY
		manager = OWLManager.createOWLOntologyManager();
		appOntology = manager.createOntology(IOR);
		
		df = appOntology.getOWLOntologyManager().getOWLDataFactory();
		
	    saveOntology();
	    
	    getReasoner();
		
	}
	
	public void saveOntology() throws OWLOntologyStorageException, FileNotFoundException {
		
		switch (serializationType) {
			case RDFXML:
				manager.saveOntology(appOntology, new RDFXMLDocumentFormat(),
						new FileOutputStream(fileout));
				break;
			case MANCHESTER:
				manager.saveOntology(appOntology, new ManchesterSyntaxDocumentFormat(),
						new FileOutputStream(fileout));
				break;
			case FUNCTIONAL:
				manager.saveOntology(appOntology, new FunctionalSyntaxDocumentFormat(),
						new FileOutputStream(fileout));
				break;
			case TURTLE:
				manager.saveOntology(appOntology, new TurtleDocumentFormat(),
						new FileOutputStream(fileout));
				break;
			default:
				break;		
		}
		
	}
	
	protected OWLReasoner getReasoner() {
		
		if (r == null) {
			OWLReasonerFactory rf = new ReasonerFactory();
			r = rf.createReasoner(appOntology);
			r.precomputeInferences(InferenceType.CLASS_HIERARCHY);	
		} 
	
		return r;
	}
	
	public NodeSet<OWLNamedIndividual> getInstances(String classId){
		
		return r.getInstances(df.getOWLClass(IOR + "#" + classId));
	
	}
	
	public NodeSet<OWLNamedIndividual> getInstances(String objectPropertyId, String classId){
		
		if (classId == null) {
			return r.getInstances(df.getOWLObjectSomeValuesFrom(df.getOWLObjectProperty(IOR + "#" + objectPropertyId),
					df.getOWLThing()));
		} else {
			return r.getInstances(df.getOWLObjectSomeValuesFrom(df.getOWLObjectProperty(IOR + "#" + objectPropertyId),
					df.getOWLClass(IOR + "#" + classId)));
		}
	
	}
	
	
	public boolean isConsistent() {

		return r.isConsistent();
		
	}
	
	public void addObjectProperty(String idFrom, String idTo, String propertyName) {
		
		OWLNamedIndividual idFromInd = df.getOWLNamedIndividual(IOR + "#" + idFrom.replaceAll("\\s","-"));
		OWLNamedIndividual idToInd = df.getOWLNamedIndividual(IOR + "#" + idTo.replaceAll("\\s","-"));
		OWLObjectProperty property = df.getOWLObjectProperty(IOR + "#" + propertyName.replaceAll("\\s","-"));
		
		OWLObjectPropertyAssertionAxiom p_ass = df.getOWLObjectPropertyAssertionAxiom(property, idFromInd, idToInd);
		appOntology.add(p_ass);
		
		r.flush();
		
	}
	
	public void addStringDataProperty(String idFrom, String toValue, String propertyName) {
		
		OWLNamedIndividual idFromInd = df.getOWLNamedIndividual(IOR + "#" + idFrom.replaceAll("\\s","-"));
		OWLDataProperty property = df.getOWLDataProperty(IOR + "#" + propertyName.replaceAll("\\s","-"));
		
		OWLDataPropertyAssertionAxiom p_ass = df.getOWLDataPropertyAssertionAxiom(property, idFromInd, toValue);
		appOntology.add(p_ass);
		
		r.flush();
		
	}
	
	public void createIndividual(String id, String classId) {
		
		OWLNamedIndividual ind = df.getOWLNamedIndividual(IOR + "#" + id.replaceAll("\\s","-"));
		OWLClass c = df.getOWLClass(IOR + "#" + classId.replaceAll("\\s","-"));
		OWLClassAssertionAxiom ax = df.getOWLClassAssertionAxiom(c, ind);
		appOntology.add(ax);
		
		r.flush();
		
	}
	
	public Set<OWLClass> allClassesInOntology() {
		
		return appOntology.classesInSignature().collect(Collectors.toSet());
		
	}
	
	public void addStringAxiom(String axiom, SerializationType type) {
		
		OWLParser parser = null;
		switch (type) {
		//	case MANCHESTER : 
		//		parser = new ManchesterOWLSyntaxOntologyParserFactory().createParser();
		//		break;
			case FUNCTIONAL : 
				axiom = "Prefix(:=<http://projects.ke.appOntology#>)\n" + 
						"Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n" + 
						"Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n" + 
						"Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n" + 
						"Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + 
						"Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" +
						"Ontology(<http://projects.ke.appOntology>\n" +
						axiom + "\n)";
				parser = new OWLFunctionalSyntaxOWLParserFactory().createParser();
				break;
		//	case TURTLE : 
		//		parser = new TurtleOntologyParserFactory().createParser();
		//		break;
			case RDFXML :
				axiom = "<rdf:RDF xmlns=\"http://projects.ke.appOntology#\"\n" + 
		    	   		"     xml:base=\"http://projects.ke.appOntology\"\n" + 
		    	   		"     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" + 
		    	   		"     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" + 
		    	   		"     xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n" + 
		    	   		"     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" + 
		    	   		"     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n" +
		    	   		axiom + "\n</rdf:RDF>";   		
				parser = new RDFXMLParserFactory().createParser();
				break;
			default:
				break;
		}
        
       if (parser != null) {
    	   
    	   InputStream in = new ByteArrayInputStream(axiom.getBytes());
    	   parser.parse( new StreamDocumentSource( in ), appOntology, new OWLOntologyLoaderConfiguration());
    	   
    	   r.flush();
    	   
       }
       
	}
	
	public void printOntology() {
		appOntology.logicalAxioms().forEach(System.out::println);
	}

}
