package javareasoner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFXMLParserFactory;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class OntologyHandler {
	
	private static OWLOntologyManager manager;
	private static OWLOntology appOntology;
	private static OWLDataFactory df;
	private static OWLReasoner r;
	
	public static SerializationType serializationType = SerializationType.RDFXML;
	
	private final static IRI IOR = IRI.create("http://projects.ke.appOntology");
	private final static File fileout = new File("appOntology.owl");
   
	public static void loadRemoteOntology(String stringIRI) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		
		manager = OWLManager.createOWLOntologyManager();
		IRI importOntology = IRI.create(stringIRI);
		appOntology = manager.loadOntology(importOntology);
		
		saveOntology();
		
		getReasoner();
		
	}
	
	public static void loadOntologyFromFile(String filePath) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		
		manager = OWLManager.createOWLOntologyManager();
		appOntology =  manager.loadOntologyFromOntologyDocument(new File(filePath));
		
		saveOntology();
		
		getReasoner();
		
	}
	
	public static void initOntology() throws OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException {
		
		manager = OWLManager.createOWLOntologyManager();
		appOntology = manager.createOntology(IOR);
		
		df = appOntology.getOWLOntologyManager().getOWLDataFactory();
		
		//CLASSES
		//Note: Declare Axiom can be omitted because triggered by subclass axioms
		OWLClass person = declareClass("Person");
		OWLClass artist = declareClass("Artist");
		OWLClass painter = declareClass("Painter");
		OWLClass sculptor = declareClass("Sculptor");
		OWLClass artisan = declareClass("Artisan");
		OWLClass thing = declareClass("Thing");
		OWLClass artwork = declareClass("ArtWork");
		OWLClass product = declareClass("Product");
		OWLClass paint = declareClass("Paint");
		OWLClass sculpt = declareClass("Sculpt");
		
		OWLSubClassOfAxiom artist_sub_p = df.getOWLSubClassOfAxiom(artist, person);
		appOntology.add(artist_sub_p);
		OWLSubClassOfAxiom artisan_sub_p = df.getOWLSubClassOfAxiom(artisan, person);
		appOntology.add(artisan_sub_p);
		OWLSubClassOfAxiom p_sub_a = df.getOWLSubClassOfAxiom(painter, artist);
		appOntology.add(p_sub_a);
		OWLSubClassOfAxiom s_sub_a = df.getOWLSubClassOfAxiom(sculptor, artist);
		appOntology.add(s_sub_a);
		
		OWLSubClassOfAxiom a_sub_t = df.getOWLSubClassOfAxiom(artwork, thing);
		appOntology.add(a_sub_t);
		OWLSubClassOfAxiom p_sub_t = df.getOWLSubClassOfAxiom(product, thing);
		appOntology.add(p_sub_t);
		OWLSubClassOfAxiom paint_sub_a = df.getOWLSubClassOfAxiom(paint, artwork);
		appOntology.add(paint_sub_a);
		OWLSubClassOfAxiom sculpt_sub_a = df.getOWLSubClassOfAxiom(sculpt, artwork);
		appOntology.add(sculpt_sub_a);
		
		//OBJECT PROPERTIES
		OWLObjectProperty crafts = df.getOWLObjectProperty(IOR + "#crafts");
		//OWLObjectPropertyRangeAxiom c_range = df.getOWLObjectPropertyRangeAxiom(crafts, artwork);
		OWLObjectProperty produces = df.getOWLObjectProperty(IOR + "#produces");
		OWLObjectProperty paints = df.getOWLObjectProperty(IOR + "#paints");
		OWLObjectProperty sculpts = df.getOWLObjectProperty(IOR + "#sculpts");
		
		OWLSubObjectPropertyOfAxiom p_sub_c = df.getOWLSubObjectPropertyOfAxiom(paints, crafts);
		appOntology.add(p_sub_c);
		OWLSubObjectPropertyOfAxiom s_sub_c = df.getOWLSubObjectPropertyOfAxiom(sculpts, crafts);
		appOntology.add(s_sub_c);
		
		//DATA PROPERTIES
		OWLDatatype stringDatatype = df.getStringOWLDatatype();
		OWLDataProperty name = df.getOWLDataProperty(IOR + "#name");
		OWLDataPropertyDomainAxiom name_domain = df.getOWLDataPropertyDomainAxiom(name, person);
		appOntology.add(name_domain);
		OWLDataPropertyRangeAxiom name_range = df.getOWLDataPropertyRangeAxiom(name, stringDatatype);
		appOntology.add(name_range);
		OWLFunctionalDataPropertyAxiom fun_name = df.getOWLFunctionalDataPropertyAxiom(name);
		appOntology.add(fun_name);
		
		OWLDataProperty identifier = df.getOWLDataProperty(IOR + "#id");
		OWLDataPropertyDomainAxiom id_domain = df.getOWLDataPropertyDomainAxiom(identifier, thing);
		appOntology.add(id_domain);
		OWLDataPropertyRangeAxiom id_range = df.getOWLDataPropertyRangeAxiom(identifier, stringDatatype);
		appOntology.add(id_range);
		OWLFunctionalDataPropertyAxiom fun_id = df.getOWLFunctionalDataPropertyAxiom(identifier);
		appOntology.add(fun_id);
		
		OWLSubClassOfAxiom a_some_ca = df.getOWLSubClassOfAxiom(df.getOWLObjectSomeValuesFrom(crafts, artwork), artist);
		appOntology.add(a_some_ca);
		OWLSubClassOfAxiom a_some_pp = df.getOWLSubClassOfAxiom(df.getOWLObjectSomeValuesFrom(produces, product), artisan);
		appOntology.add(a_some_pp);
		OWLSubClassOfAxiom p_some_pp = df.getOWLSubClassOfAxiom(df.getOWLObjectSomeValuesFrom(paints, paint), painter);
		appOntology.add(p_some_pp);
		OWLSubClassOfAxiom s_some_ss = df.getOWLSubClassOfAxiom(df.getOWLObjectSomeValuesFrom(sculpts, sculpt), sculptor);
		appOntology.add(s_some_ss);
		
		HashSet<OWLClass> disclasses = new HashSet<>();
		disclasses.add(paint);
		disclasses.add(sculpt);
		disclasses.add(product);
		OWLDisjointClassesAxiom discla = df.getOWLDisjointClassesAxiom(disclasses);
	    appOntology.add(discla);
		
	    saveOntology();
	    
	    getReasoner();
		
	}
	
	public static void saveOntology() throws OWLOntologyStorageException, FileNotFoundException {
		
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
	
	private static OWLReasoner getReasoner() {
		
		if (r == null) {
			OWLReasonerFactory rf = new ReasonerFactory();
			r = rf.createReasoner(appOntology);
			r.precomputeInferences(InferenceType.CLASS_HIERARCHY);	
		} 
	
		return r;
	}
	
	public static NodeSet<OWLNamedIndividual> getInstances(String classId){
		
		return r.getInstances(df.getOWLClass(IOR + "#" + classId));
	
	}
	
	public static NodeSet<OWLNamedIndividual> getInstances(String objectPropertyId, String classId){
		
		if (classId == null) {
			return r.getInstances(df.getOWLObjectSomeValuesFrom(df.getOWLObjectProperty(IOR + "#" + objectPropertyId),
					df.getOWLThing()));
		} else {
			return r.getInstances(df.getOWLObjectSomeValuesFrom(df.getOWLObjectProperty(IOR + "#" + objectPropertyId),
					df.getOWLClass(IOR + "#" + classId)));
		}
	
	}
	
	public static NodeSet<OWLNamedIndividual> getInstancesArtistsCreatingArtworks(){
		
		OWLClass artist = df.getOWLClass(IOR + "#Artist");
		OWLObjectProperty isArtist = df.getOWLObjectProperty(IOR + "#isArtist");
		OWLEquivalentClassesAxiom is_a_self = df.getOWLEquivalentClassesAxiom(artist,
				df.getOWLObjectHasSelf(isArtist));
		appOntology.add(is_a_self);
		
		OWLClass artwork = df.getOWLClass(IOR + "#ArtWork");
		OWLObjectProperty isArtwork = df.getOWLObjectProperty(IOR + "#isArtWork");
		OWLEquivalentClassesAxiom is_aw_self = df.getOWLEquivalentClassesAxiom(artwork,
				df.getOWLObjectHasSelf(isArtwork));
		appOntology.add(is_aw_self);
		
		OWLObjectProperty a_c = df.getOWLObjectProperty(IOR + "a_c");
		OWLObjectProperty crafts = df.getOWLObjectProperty(IOR + "#crafts");
		List<OWLObjectProperty> chain = new ArrayList<>();
		chain.add(isArtist);
		chain.add(crafts);
		chain.add(isArtwork);
		OWLSubPropertyChainOfAxiom s_a_c = df.getOWLSubPropertyChainOfAxiom(chain, a_c);
		appOntology.add(s_a_c);
		
		r.flush();
		return r.getInstances(df.getOWLObjectSomeValuesFrom(a_c, df.getOWLThing()));
	
	}
	
	public static boolean isConsistent() {

		return r.isConsistent();
		
	}
	
	private static OWLClass declareClass(String id) { 
		
		OWLClass owlClass = df.getOWLClass(IOR + "#" + id);		
		OWLDeclarationAxiom da = df.getOWLDeclarationAxiom(owlClass);
		appOntology.add(da);
		
		return owlClass;
		
	}
	
	public static void addObjectProperty(String idFrom, String idTo, String propertyName) {
		
		OWLNamedIndividual idFromInd = df.getOWLNamedIndividual(IOR + "#" + idFrom);
		OWLNamedIndividual idToInd = df.getOWLNamedIndividual(IOR + "#" + idTo);
		OWLObjectProperty property = df.getOWLObjectProperty(IOR + "#" + propertyName);
		
		OWLObjectPropertyAssertionAxiom p_ass = df.getOWLObjectPropertyAssertionAxiom(property, idFromInd, idToInd);
		appOntology.add(p_ass);
		
		r.flush();
		
	}
	
	public static void addStringDataProperty(String idFrom, String toValue, String propertyName) {
		
		OWLNamedIndividual idFromInd = df.getOWLNamedIndividual(IOR + "#" + idFrom);
		OWLDataProperty property = df.getOWLDataProperty(IOR + "#" + propertyName);
		
		OWLDataPropertyAssertionAxiom p_ass = df.getOWLDataPropertyAssertionAxiom(property, idFromInd, toValue);
		appOntology.add(p_ass);
		
		r.flush();
		
	}
	
	public static void createIndividual(String id, String classId) {
		
		OWLNamedIndividual ind = df.getOWLNamedIndividual(IOR + "#" + id);
		OWLClass c = df.getOWLClass(IOR + "#" + classId);
		OWLClassAssertionAxiom ax = df.getOWLClassAssertionAxiom(c, ind);
		appOntology.add(ax);
		
		r.flush();
		
	}
	
	public static void addStringAxiom(String axiom, SerializationType type) {
		
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

}
