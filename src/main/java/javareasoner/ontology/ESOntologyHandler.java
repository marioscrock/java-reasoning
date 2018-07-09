package javareasoner.ontology;

import java.io.FileNotFoundException;
import java.util.HashSet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.NodeSet;

import javareasoner.server.ReasoningServer;

public class ESOntologyHandler extends OntologyHandler implements ReasoningServer {
	
	/**
	 * Override the method specifying the EShop ontology's axioms through
	 * the OWLAPI.
	 */
	@Override
	public void initOntology() throws OWLOntologyStorageException, FileNotFoundException, OWLOntologyCreationException {
		
		manager = OWLManager.createOWLOntologyManager();
		appOntology = manager.createOntology(IOR);
		
		df = appOntology.getOWLOntologyManager().getOWLDataFactory();
		
		//CLASSES
		//Note: Declare Axiom can be omitted because triggered by subclass axioms
		OWLClass user = declareClass("User");
		OWLClass guest = declareClass("Guest");
		OWLClass customer = declareClass("Customer");
		OWLClass simpleCustomer = declareClass("SimpleCustomer");
		OWLClass vipCustomer = declareClass("VIPCustomer");
		OWLClass product = declareClass("Product");
		OWLClass productA = declareClass("ProductA");
		OWLClass productB = declareClass("ProductB");
		OWLClass productC = declareClass("ProductC");
		
		OWLSubClassOfAxiom g_sub_u = df.getOWLSubClassOfAxiom(guest, user);
		appOntology.add(g_sub_u);
		OWLSubClassOfAxiom c_sub_u = df.getOWLSubClassOfAxiom(customer, user);
		appOntology.add(c_sub_u);
		OWLSubClassOfAxiom sc_sub_c = df.getOWLSubClassOfAxiom(simpleCustomer, customer);
		appOntology.add(sc_sub_c);
		OWLSubClassOfAxiom vc_sub_c = df.getOWLSubClassOfAxiom(vipCustomer, customer);
		appOntology.add(vc_sub_c);
		
		OWLSubClassOfAxiom pA_sub_P = df.getOWLSubClassOfAxiom(productA, product);
		appOntology.add(pA_sub_P);
		OWLSubClassOfAxiom pB_sub_P = df.getOWLSubClassOfAxiom(productB, product);
		appOntology.add(pB_sub_P);
		OWLSubClassOfAxiom pC_sub_P = df.getOWLSubClassOfAxiom(productC, product);
		appOntology.add(pC_sub_P);
		
		//OBJECT PROPERTIES
		OWLObjectProperty interestedIn = df.getOWLObjectProperty(IOR + "#interestedIn");
		OWLObjectPropertyDomainAxiom i_domain = df.getOWLObjectPropertyDomainAxiom(interestedIn, user);
		appOntology.add(i_domain);
		OWLObjectPropertyRangeAxiom i_range = df.getOWLObjectPropertyRangeAxiom(interestedIn, product);
		appOntology.add(i_range);
		OWLObjectProperty productOnOffer = df.getOWLObjectProperty(IOR + "#productOnOffer");
		OWLObjectPropertyDomainAxiom p_domain = df.getOWLObjectPropertyDomainAxiom(productOnOffer, customer);
		appOntology.add(p_domain);
		OWLObjectPropertyRangeAxiom p_range = df.getOWLObjectPropertyRangeAxiom(productOnOffer, product);
		appOntology.add(p_range);
		OWLObjectProperty perc10Offer = df.getOWLObjectProperty(IOR + "#perc10Offer");
		OWLObjectProperty perc20Offer = df.getOWLObjectProperty(IOR + "#perc20Offer");
		OWLObjectPropertyDomainAxiom p20_domain = df.getOWLObjectPropertyDomainAxiom(perc20Offer, vipCustomer);
		appOntology.add(p20_domain);
		
		OWLSubObjectPropertyOfAxiom p10_sub_p = df.getOWLSubObjectPropertyOfAxiom(perc10Offer, productOnOffer);
		appOntology.add(p10_sub_p);
		OWLSubObjectPropertyOfAxiom p20_sub_p = df.getOWLSubObjectPropertyOfAxiom(perc20Offer, productOnOffer);
		appOntology.add(p20_sub_p);
		
		//DATA PROPERTIES
		OWLDatatype stringDatatype = df.getStringOWLDatatype();
		OWLDataProperty username = df.getOWLDataProperty(IOR + "#username");
		OWLDataPropertyDomainAxiom username_domain = df.getOWLDataPropertyDomainAxiom(username, user);
		appOntology.add(username_domain);
		OWLDataPropertyRangeAxiom username_range = df.getOWLDataPropertyRangeAxiom(username, stringDatatype);
		appOntology.add(username_range);
		OWLFunctionalDataPropertyAxiom fun_username = df.getOWLFunctionalDataPropertyAxiom(username);
		appOntology.add(fun_username);
		
		OWLDataProperty id = df.getOWLDataProperty(IOR + "#id");
		OWLDataPropertyDomainAxiom id_domain = df.getOWLDataPropertyDomainAxiom(id, product);
		appOntology.add(id_domain);
		OWLDataPropertyRangeAxiom id_range = df.getOWLDataPropertyRangeAxiom(id, stringDatatype);
		appOntology.add(id_range);
		OWLFunctionalDataPropertyAxiom fun_id = df.getOWLFunctionalDataPropertyAxiom(id);
		appOntology.add(fun_id);
		
		OWLDataProperty price = df.getOWLDataProperty(IOR + "#price");
		OWLDataPropertyDomainAxiom price_domain = df.getOWLDataPropertyDomainAxiom(price, product);
		appOntology.add(price_domain);
		OWLDataPropertyRangeAxiom price_range = df.getOWLDataPropertyRangeAxiom(price, stringDatatype);
		appOntology.add(price_range);
		OWLFunctionalDataPropertyAxiom price_id = df.getOWLFunctionalDataPropertyAxiom(price);
		appOntology.add(price_id);
		
		HashSet<OWLClass> disclasses = new HashSet<>();
		disclasses.add(user);
		disclasses.add(product);
		OWLDisjointClassesAxiom discla = df.getOWLDisjointClassesAxiom(disclasses);
	    appOntology.add(discla);
	    
		HashSet<OWLClass> disclassesProducts = new HashSet<>();
		disclassesProducts.add(productA);
		disclassesProducts.add(productB);
		disclassesProducts.add(productC);
		OWLDisjointClassesAxiom discla2 = df.getOWLDisjointClassesAxiom(disclassesProducts);
	    appOntology.add(discla2);
	    
	    HashSet<OWLClass> disclassesUsers = new HashSet<>();
	    disclassesUsers.add(guest);
	    disclassesUsers.add(simpleCustomer);
	    disclassesUsers.add(vipCustomer);
		OWLDisjointClassesAxiom disclaUsers = df.getOWLDisjointClassesAxiom(disclassesUsers);
	    appOntology.add(disclaUsers);
	    
	    OWLClass popularProduct = declareClass("PopularProduct");
	    OWLSubClassOfAxiom pp_sub_p = df.getOWLSubClassOfAxiom(popularProduct, product);
		appOntology.add(pp_sub_p);
		OWLClass popularProductSC = declareClass("PopularProductSC");
	    OWLSubClassOfAxiom ppsc_sub_p = df.getOWLSubClassOfAxiom(popularProductSC, popularProduct);
		appOntology.add(ppsc_sub_p);
		OWLClass popularProductVC = declareClass("PopularProductVC");
	    OWLSubClassOfAxiom ppvc_sub_p = df.getOWLSubClassOfAxiom(popularProductVC, popularProduct);
		appOntology.add(ppvc_sub_p);
		
		OWLEquivalentClassesAxiom e_ppSC = df.getOWLEquivalentClassesAxiom(popularProductSC, df.getOWLObjectMinCardinality(2,
				df.getOWLObjectInverseOf(df.getOWLObjectProperty(IOR + "#interestedIn")),
				df.getOWLClass(IOR + "#SimpleCustomer")));
		appOntology.add(e_ppSC);
		OWLEquivalentClassesAxiom e_ppVC = df.getOWLEquivalentClassesAxiom(popularProductVC, df.getOWLObjectMinCardinality(2,
				df.getOWLObjectInverseOf(df.getOWLObjectProperty(IOR + "#interestedIn")),
				df.getOWLClass(IOR + "#VIPCustomer")));
		appOntology.add(e_ppVC);
		
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
	
	/**
	 * Return all individuals targeted by more than {@code card} relations of type {@code objectPropertyId}.
	 * Reasoning enabled.<br>
	 * Manchester syntax DL query: inverse {@code objectPropertyId} min {@code <card>} {@code classId}
	 * @param objectPropertyId	IRI fragment of the property
	 * @param int	min cardinality value
	 * @param classId	If null OWLThing is considered
	 * @return individuals of the class defined by the query
	 */
	public NodeSet<OWLNamedIndividual> getInstancesGreaterCardInvObjectProperty(String objectPropertyId, int card,  String classId){
		
		r.flush();
		if (classId != null) {
			return r.getInstances(df.getOWLObjectMinCardinality(card,
				df.getOWLObjectInverseOf(df.getOWLObjectProperty(IOR + "#" + objectPropertyId)),
				df.getOWLClass(IOR + "#" + classId)));
		} else {
			return r.getInstances(df.getOWLObjectMinCardinality(card,
					df.getOWLObjectInverseOf(df.getOWLObjectProperty(IOR + "#" + objectPropertyId))));
		}
	
	}
	
	@Override
	public void reasoningRoutine() {
		
		boolean consistent = isConsistent();
		System.out.println("\nIs ontology still consistent? " + consistent);
		
		if (consistent) {
			System.out.println("\nReasoning Routine");
			
			System.out.println("\nGuest(*)");
			getInstances("Guest").forEach(System.out::println);
			
			System.out.println("\nCustomer(*)");
			getInstances("Customer").forEach(System.out::println);
			
			System.out.println("\nproductOnOffer(*)");
			getInstances("productOnOffer", null).forEach(System.out::println);
			
			System.out.println("\nproductOnOffer(*) some ProductB");
			getInstances("productOnOffer", "ProductB").forEach(System.out::println);
			
			System.out.println("\nPopularProduct (equivalent to: inverse interestedIn min 2)");
			getInstances("PopularProduct").forEach(System.out::println);
			
			System.out.println("\nPopularProductSC (equivalent to: inverse interestedIn min 2 SimpleCustomer)");
			getInstances("PopularProductSC").forEach(System.out::println);
			
			System.out.println("\nPopularProductVC (equivalent to: inverse interestedIn min 2 VIPCustomer)");
			getInstances("PopularProductVC").forEach(System.out::println);
			
			System.out.println("\ninverse interestedIn some Guest");
			getInstancesGreaterCardInvObjectProperty("interestedIn", 1, "Guest").forEach(System.out::println);
			
		}
		
		System.out.println("*******************************************************");
		
	}

}
