package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
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
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class OntologyHandler {
	
	private static OWLOntologyManager manager;
	private static OWLOntology appOntology;
	private static OWLDataFactory df;
	private static OWLReasoner r;
	
	private final static IRI IOR = IRI.create("http://projects.ke.appOntology");
   
	public static void loadOntology(String stringIRI) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		
		manager = OWLManager.createOWLOntologyManager();
		
		File fileout = new File("appOntology.owl");
		
		IRI importOntology = IRI.create(stringIRI);
		OWLOntology importedOntology = manager.loadOntology(importOntology);
		
		manager.saveOntology(importedOntology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));
		
	}
	
	public static void initOntology() throws OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException {
		
		manager = OWLManager.createOWLOntologyManager();
		
		File fileout = new File("appOntology.owl");
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
		
		OWLObjectProperty crafts = df.getOWLObjectProperty(IOR + "#crafts");
		//OWLObjectPropertyRangeAxiom c_range = df.getOWLObjectPropertyRangeAxiom(crafts, artwork);
		OWLObjectProperty produces = df.getOWLObjectProperty(IOR + "#produces");
		OWLObjectProperty paints = df.getOWLObjectProperty(IOR + "#paints");
		OWLObjectProperty sculpts = df.getOWLObjectProperty(IOR + "#sculpts");
		
		OWLSubObjectPropertyOfAxiom p_sub_c = df.getOWLSubObjectPropertyOfAxiom(paints, crafts);
		appOntology.add(p_sub_c);
		OWLSubObjectPropertyOfAxiom s_sub_c = df.getOWLSubObjectPropertyOfAxiom(sculpts, crafts);
		appOntology.add(s_sub_c);
		
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
		
		manager.saveOntology(appOntology, new FunctionalSyntaxDocumentFormat(),
				new FileOutputStream(fileout));
		
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
		
		r = getReasoner();
		return r.getInstances(df.getOWLClass(IOR + "#" + classId));
	
	}
	
	public static NodeSet<OWLNamedIndividual> getInstances(String objectPropertyId, String classId){
		
		r = getReasoner();
		
		if (classId == null) {
			return r.getInstances(df.getOWLObjectSomeValuesFrom(df.getOWLObjectProperty(IOR + "#" + objectPropertyId),
					df.getOWLThing()));
		} else {
			return r.getInstances(df.getOWLObjectSomeValuesFrom(df.getOWLObjectProperty(IOR + "#" + objectPropertyId),
					df.getOWLClass(IOR + "#" + classId)));
		}
	
	}
	
	public static boolean isConsistent() {
		r = getReasoner();
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
		
	}
	
	public static void addStringDataProperty(String idFrom, String toValue, String propertyName) {
		
		OWLNamedIndividual idFromInd = df.getOWLNamedIndividual(IOR + "#" + idFrom);
		OWLDataProperty property = df.getOWLDataProperty(IOR + "#" + propertyName);
		
		OWLDataPropertyAssertionAxiom p_ass = df.getOWLDataPropertyAssertionAxiom(property, idFromInd, toValue);
		appOntology.add(p_ass);
		
	}
	
	public static void createIndividual(String id, String classId) {
		
		OWLNamedIndividual ind = df.getOWLNamedIndividual(IOR + "#" + id);
		OWLClass c = df.getOWLClass(IOR + "#" + classId);
		OWLClassAssertionAxiom ax = df.getOWLClassAssertionAxiom(c, ind);
		appOntology.add(ax);
		
	}

}
