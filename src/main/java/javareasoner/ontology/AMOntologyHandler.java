package javareasoner.ontology;

import java.io.FileNotFoundException;
import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import javareasoner.server.ReasoningServer;

public class AMOntologyHandler extends OntologyHandler implements ReasoningServer {
	
	public AMOntologyHandler() {
		super();
	}
	
	/**
	 * Override the method specifying the ArtMarket ontology's axioms through
	 * the OWLAPI.
	 */
	@Override
	public void initOntology() throws OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException {
		
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
		OWLObjectPropertyRangeAxiom c_range = df.getOWLObjectPropertyRangeAxiom(crafts, artwork);
		appOntology.add(c_range);
		OWLObjectPropertyDomainAxiom c_domain = df.getOWLObjectPropertyDomainAxiom(crafts, artist);
		appOntology.add(c_domain);
		OWLObjectProperty produces = df.getOWLObjectProperty(IOR + "#produces");
		OWLObjectPropertyRangeAxiom p_range = df.getOWLObjectPropertyRangeAxiom(produces, product);
		appOntology.add(p_range);
		OWLObjectPropertyDomainAxiom p_domain = df.getOWLObjectPropertyDomainAxiom(produces, artisan);
		appOntology.add(p_domain);
		OWLObjectProperty paints = df.getOWLObjectProperty(IOR + "#paints");
		OWLObjectPropertyRangeAxiom pa_range = df.getOWLObjectPropertyRangeAxiom(paints, paint);
		appOntology.add(pa_range);
		OWLObjectPropertyDomainAxiom pa_domain = df.getOWLObjectPropertyDomainAxiom(paints, painter);
		appOntology.add(pa_domain);
		OWLObjectProperty sculpts = df.getOWLObjectProperty(IOR + "#sculpts");
		OWLObjectPropertyRangeAxiom s_range = df.getOWLObjectPropertyRangeAxiom(sculpts, sculpt);
		appOntology.add(s_range);
		OWLObjectPropertyDomainAxiom s_domain = df.getOWLObjectPropertyDomainAxiom(sculpts, sculptor);
		appOntology.add(s_domain);
		
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
		
		HashSet<OWLClass> disclasses = new HashSet<>();
		disclasses.add(person);
		disclasses.add(thing);
		OWLDisjointClassesAxiom discla = df.getOWLDisjointClassesAxiom(disclasses);
	    appOntology.add(discla);
	    
		HashSet<OWLClass> disclassesThings = new HashSet<>();
		disclassesThings.add(paint);
		disclassesThings.add(sculpt);
		disclassesThings.add(product);
		OWLDisjointClassesAxiom discla2 = df.getOWLDisjointClassesAxiom(disclassesThings);
	    appOntology.add(discla2);
		
	    saveOntology();
	    
	    getReasoner();
		
	}
	
	/**
	 * Utility method to declare a class with the IRI fragment given as string.
	 * @param id	String representing IRI fragment
	 * @return The OWLClass declared
	 */
	private OWLClass declareClass(String id) { 
		
		OWLClass owlClass = df.getOWLClass(IOR + "#" + id);		
		OWLDeclarationAxiom da = df.getOWLDeclarationAxiom(owlClass);
		appOntology.add(da);
		
		return owlClass;
		
	}
	
	@Override
	public void reasoningRoutine() {
		
		boolean consistent = isConsistent();
		System.out.println("\nIs ontology still consistent? " + consistent);
		
		if (consistent) {
			System.out.println("\nReasoning Routine");
			
			System.out.println("\nPainter(*)");
			getInstances("Painter").forEach(System.out::println);
			
			System.out.println("\nPaint(*)");
			getInstances("Paint").forEach(System.out::println);
			
			System.out.println("\nThing(*)");
			getInstances("Thing").forEach(System.out::println);
			
			System.out.println("\nproduces(*)");
			getInstances("produces", null).forEach(System.out::println);
			
			System.out.println("\npaints some Paint(*)");
			getInstances("paints", null).forEach(System.out::println);
			
			System.out.println("\ncrafts some Sculpt(*)");
			getInstances("crafts", "Sculpt").forEach(System.out::println);
			
			System.out.println("\nArtist and (crafts some ArtWork) (*)");
			getInstances("crafts", "ArtWork").forEach(System.out::println);
		}
		
		System.out.println("*******************************************************");
		
	}

}
