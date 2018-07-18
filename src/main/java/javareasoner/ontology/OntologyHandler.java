package javareasoner.ontology;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxOntologyParserFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
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
import org.semanticweb.owlapi.rdf.turtle.parser.TurtleOntologyParserFactory;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import javareasoner.DataPropertyType;
import javareasoner.SerializationType;

/**
 * Ontology handler provides an high level API to deal with OWL API.
 * @author Mario
 *
 */
public class OntologyHandler {
	
	protected OWLOntologyManager manager;
	protected OWLOntology appOntology;
	protected OWLDataFactory df;
	protected OWLReasoner r;
	
	protected final IRI IOR = IRI.create("http://projects.ke.appOntology");
	protected File fileout = new File("appOntology.owl");
	protected SerializationType serializationType = SerializationType.RDFXML;

	private List<OWLAxiom> bufferAxioms = new ArrayList<>();
	
	public OntologyHandler() {
		manager = OWLManager.createOWLOntologyManager();
	}
	
	/**
	 * Load a remote ontology given the string of the IRI.
	 * @param stringIRI
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 */
	public void loadRemoteOntology(String stringIRI) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		
		IRI importOntology = IRI.create(stringIRI);
		appOntology = manager.loadOntology(importOntology);
		
		saveOntology();
		
		getReasoner();
		
	}
	
	/**
	 * Load an ontology from an .owl file.
	 * @param filePath
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 */
	public void loadOntologyFromFile(String filePath) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		
		appOntology =  manager.loadOntologyFromOntologyDocument(new File(filePath));
		
		saveOntology();
		
		getReasoner();
		
	}
	
	/**
	 * Init an empty ontology.
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 * @throws OWLOntologyCreationException
	 */
	public void initOntology() throws OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException {
		
		//EMPTY ONTOLOGY
		appOntology = manager.createOntology(IOR);
		
		df = appOntology.getOWLOntologyManager().getOWLDataFactory();
		
	    saveOntology();
	    
	    getReasoner();
		
	}
	
	/**
	 * Save ontology to file. <br>
	 * Output file determined by class field {@code fileout}, serialization format
	 * determined by class field {@code serializationType}.
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 */
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
	
	/**
	 * Return the OWLReasoner instance inferring on the related ontology.
	 * @return OWLReasoner instance inferring on the related ontology
	 */
	protected OWLReasoner getReasoner() {
		
		if (r == null) {
			OWLReasonerFactory rf = new ReasonerFactory();
			r = rf.createReasoner(appOntology);
			r.precomputeInferences(InferenceType.CLASS_HIERARCHY);	
		} 
	
		return r;
	}
	
	/**
	 * Return all individuals of class with IRI fragment as input string
	 * in the ontology. Reasoning enabled.<br>
	 * Manchester syntax DL query: {@code classId}
	 * @param classId	IRI fragment of the class
	 * @return individuals of the atomic class
	 */
	public NodeSet<OWLNamedIndividual> getInstances(String classId){
		
		return r.getInstances(df.getOWLClass(IOR + "#" + classId));
	
	}
	
	/**
	 * Return all individuals having a property with IRI fragment as input objectPropertyId string
	 * and targeting a class with IRI fragment classId in the ontology. Reasoning enabled.<br>
	 * Manchester syntax DL query: {@code objectPropertyId} some {@code classId}
	 * @param objectPropertyId	IRI fragment of the property
	 * @param classId	If null OWLThing is considered
	 * @return individuals of the class defined by the query
	 */
	public NodeSet<OWLNamedIndividual> getInstances(String objectPropertyId, String classId){
		
		if (classId == null) {
			return r.getInstances(df.getOWLObjectSomeValuesFrom(df.getOWLObjectProperty(IOR + "#" + objectPropertyId),
					df.getOWLThing()));
		} else {
			return r.getInstances(df.getOWLObjectSomeValuesFrom(df.getOWLObjectProperty(IOR + "#" + objectPropertyId),
					df.getOWLClass(IOR + "#" + classId)));
		}
	
	}
	
	/**
	 * Ask the reasoner to check whether the ontology is consistent
	 * @return {@code true}	If the ontology is consistent, {@code false} otherwise.
	 */
	public boolean isConsistent() {

		return r.isConsistent();
		
	}
	
	/**
	 * Add an ABox axiom stating object property {@code propertyName} holds between individual
	 * {@code idFrom} and {@code idTo}, sync the reasoner and keeps track of the axiom 
	 * inserted in the OntologyHandler's buffer. 
	 * @param idFrom	IRI fragment of the source individual
	 * @param idTo	IRI fragment of the target individual
	 * @param propertyName IRI fragment of the property
	 */
	public void addObjectProperty(String idFrom, String idTo, String propertyName) {
		
		OWLNamedIndividual idFromInd = df.getOWLNamedIndividual(IOR + "#" + idFrom.replaceAll("\\s","-"));
		OWLNamedIndividual idToInd = df.getOWLNamedIndividual(IOR + "#" + idTo.replaceAll("\\s","-"));
		OWLObjectProperty property = df.getOWLObjectProperty(IOR + "#" + propertyName.replaceAll("\\s","-"));
		
		OWLObjectPropertyAssertionAxiom p_ass = df.getOWLObjectPropertyAssertionAxiom(property, idFromInd, idToInd);
		appOntology.add(p_ass);
		bufferAxioms.add(p_ass);
		
		r.flush();
		
		//DEBUG
		//System.out.println("Is consistent? " + isConsistent() + " " + idFrom + " " + idTo + " " + propertyName);
		
	}
	
	/**
	 * Add an ABox axiom stating data property {@code propertyName} holds between individual
	 * {@code idFrom} and value {@code toValue}, sync the reasoner and keeps track of the axiom 
	 * inserted in the OntologyHandler's buffer.
	 * @param idFrom	IRI fragment of the source individual
	 * @param toValue	String representing data value
	 * @param propertyName	IRI fragment of the property
	 * @type Type of the data property
	 */
	public void addDataProperty(String idFrom, Object toValue, String propertyName, DataPropertyType type) {
		
		OWLNamedIndividual idFromInd = df.getOWLNamedIndividual(IOR + "#" + idFrom.replaceAll("\\s","-"));
		OWLDataProperty property = df.getOWLDataProperty(IOR + "#" + propertyName.replaceAll("\\s","-"));
		
		OWLDataPropertyAssertionAxiom p_ass;
		switch (type) {
			case STRING:
				p_ass = df.getOWLDataPropertyAssertionAxiom(property, idFromInd, (String) toValue);
				break;
			case INTEGER:
				p_ass = df.getOWLDataPropertyAssertionAxiom(property, idFromInd, (int) toValue);
				break;
			case DOUBLE:
				p_ass = df.getOWLDataPropertyAssertionAxiom(property, idFromInd, (double) toValue);
				break;
			case FLOAT:
				p_ass = df.getOWLDataPropertyAssertionAxiom(property, idFromInd, (float) toValue);
				break;
			case BOOLEAN:
				p_ass = df.getOWLDataPropertyAssertionAxiom(property, idFromInd, (boolean) toValue);
				break;
			default:
				return;
		}
		
		appOntology.add(p_ass);
		bufferAxioms.add(p_ass);
		
		r.flush();
		
		//DEBUG
		//System.out.println("Is consistent? " + isConsistent() + " " + idFrom + " " + toValue + " " + propertyName);
		
	}
	
	/**
	 * Add an ABox axiom stating individual {@code id} belongs to class
	 * {@code classId}, sync the reasoner and keeps track of the axiom 
	 * inserted in the OntologyHandler's buffer.
	 * @param id	IRI fragment of the individual
	 * @param classId	IRI fragment of the class
	 */
	public void createIndividual(String id, String classId) {
		
		OWLNamedIndividual ind = df.getOWLNamedIndividual(IOR + "#" + id.replaceAll("\\s","-"));
		OWLClass c = df.getOWLClass(IOR + "#" + classId.replaceAll("\\s","-"));
		OWLClassAssertionAxiom ax = df.getOWLClassAssertionAxiom(c, ind);
		appOntology.add(ax);
		bufferAxioms.add(ax);
		
		r.flush();
		
		//DEBUG
		//System.out.println("Is consistent? " + isConsistent() + " " + id + " " + classId);
		
	}
	
	/**
	 * Returns a set of all OWLClass in the ontology.
	 * @return	set of all OWLClass in the ontology
	 */
	public Set<OWLClass> allClassesInOntology() {
		
		return appOntology.classesInSignature().collect(Collectors.toSet());
		
	}
	
	/**
	 * Parse the string {@code axiom} given the SerializationType {@code type}.
	 * The method add to the string the prefixes 
	 * @param axiom The string to parse
	 * @param type The serialization type 
	 */
	public void addStringAxiom(String axiom, SerializationType type) {
		
		OWLParser parser = null;
		switch (type) {
			case MANCHESTER : 
				axiom = "Prefix: owl: <http://www.w3.org/2002/07/owl#>\n" + 
						"Prefix: rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
						"Prefix: rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
						"Prefix: xml: <http://www.w3.org/XML/1998/namespace>\n" + 
						"Prefix: xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
						"Ontology: <" + IOR + ">\n" + 
						axiom;
				parser = new ManchesterOWLSyntaxOntologyParserFactory().createParser();
				break;
			case FUNCTIONAL : 
				axiom = "Prefix(:=<" + IOR + "#>)\n" + 
						"Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n" + 
						"Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n" + 
						"Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n" + 
						"Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" + 
						"Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" +
						"Ontology(<" + IOR + ">\n" +
						axiom + "\n)";
				parser = new OWLFunctionalSyntaxOWLParserFactory().createParser();
				break;
			case TURTLE : 
				axiom = "@prefix : <" + IOR + "#> .\n" + 
						"@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" + 
						"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" + 
						"@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n" + 
						"@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" + 
						"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" + 
						"@base <" + IOR + "> .\n" + 
						"<" + IOR + "> rdf:type owl:Ontology .\n" +
						axiom;
				parser = new TurtleOntologyParserFactory().createParser();
				break;
			case RDFXML :
				axiom = "<rdf:RDF xmlns=\"" + IOR + "#\"\n" + 
		    	   		"     xml:base=\"" + IOR + "\"\n" + 
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
	
	/**
	 * Print the ontology axioms on System.out
	 */
	public void printOntology() {
		appOntology.logicalAxioms().forEach(System.out::println);
	}
	
	/**
	 * Empty the buffer associated to the OntologyHandler
	 */
	public void emptyBuffer() {
		bufferAxioms.clear();
	}
	
	/**
	 * Delete from the ontology the axioms in buffer and empty the buffer.
	 */
	public void deleteFromOntAxiomsInBuffer() {		
		appOntology.remove(bufferAxioms);
		r.flush();
		emptyBuffer();	
	}
	
	/**
	 * Get the IRI of the ontology.
	 * @return	IRI of the ontology
	 */
	public IRI getIOR() {
		return IOR;
	}
	
	/**
	 * Get the string representation of output file where ontology is saved
	 * by {@link #saveOntology()}.
	 * @return string representation of output file
	 */
	public String getFileout() {
		return fileout.getPath();
	}
	
	/**
	 * Allow to set output file where ontology is saved by {@link #saveOntology()}
	 * given the string {@code fileout} representing the file path.
	 * @param fileout	string output file path
	 */
	public void setFileout(String fileout) {
		this.fileout = new File(fileout);
	}
	
	/**
	 * Return the serialization type used by {@link #saveOntology()}.
	 * @return serialization type used to save the ontology
	 */
	public SerializationType getSerializationType() {
		return serializationType;
	}
	
	/**
	 * Allow to set the serialization type used by {@link #saveOntology()}.
	 * @param serializationType serialization type to save the ontology
	 */
	public void setSerializationType(SerializationType serializationType) {
		this.serializationType = serializationType;
	}

}
