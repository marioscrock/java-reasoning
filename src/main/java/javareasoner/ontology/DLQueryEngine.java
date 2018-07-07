/*
 * This class has been coded thanks to code from:
 * - https://github.com/phillord/owl-api/blob/master/contract/src/test/java/org/coode/owlapi/examples/DLQueryExample.java
 * 	 (Merge in a class + OWL API update to avoid deprecated methods)
*/
package javareasoner.ontology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OntologyConfigurator;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

public class DLQueryEngine {

	private ShortFormProvider shortFormProvider;
	private final BidirectionalShortFormProvider bidiShortFormProvider;
	private final OntologyHandler oh;

    /** Constructs a DLQueryEngine. This will answer "DL queries" using the
     * specified reasoner. A short form provider specifies how entities are
     * rendered.
     * @param reasoner The reasoner to be used for answering the queries. */
    public DLQueryEngine(OntologyHandler oh) {
    	shortFormProvider = new SimpleShortFormProvider();
    	this.oh = oh;
        Stream<OWLOntology> importsClosure = oh.appOntology.importsClosure();
        // Create a bidirectional short form provider to do the actual mapping.
        // It will generate names using the input
        // short form provider.
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(oh.manager,
        			importsClosure.collect(Collectors.toSet()), shortFormProvider);
    }

    public void doQueryLoop() throws IOException {
        while (true) {
            // Prompt the user to enter a class expression
            System.out
                    .println("Please type a class expression in Manchester Syntax and press Enter (or press x to exit):");
            System.out.println("");
            String classExpression = readInput();
            // Check for exit condition
            if (classExpression.equalsIgnoreCase("x")) {
                break;
            }
            askQuery(classExpression.trim());
            System.out.println();
        }
    }

    private static String readInput() throws IOException {
        InputStream is = System.in;
        InputStreamReader reader;
        reader = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(reader);
        return br.readLine();
    }
   
    /** Gets the superclasses of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *            The string from which the class expression will be parsed.
     * @param direct
     *            Specifies whether direct superclasses should be returned or
     *            not.
     * @return The superclasses of the specified class expression
     * @throws ParserException
     *             If there was a problem parsing the class expression. */
    public Stream<OWLClass> getSuperClasses(String classExpressionString, boolean direct) throws ParserException {
    	
        if (classExpressionString.trim().length() == 0) {
            return Stream.empty();
        }
        OWLClassExpression classExpression = parseClassExpression(classExpressionString);
        NodeSet<OWLClass> superClasses = oh.r.getSuperClasses(classExpression, direct);
        
        return superClasses.entities();
    }

    /** Gets the equivalent classes of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *            The string from which the class expression will be parsed.
     * @return The equivalent classes of the specified class expression
     * @throws ParserException
     *             If there was a problem parsing the class expression. */
    public Stream<OWLClass> getEquivalentClasses(String classExpressionString) throws ParserException {
        if (classExpressionString.trim().length() == 0) {
            return Stream.empty();
        }
        OWLClassExpression classExpression = parseClassExpression(classExpressionString);
        Node<OWLClass> equivalentClasses = oh.r.getEquivalentClasses(classExpression);
        Stream<OWLClass> result;
        if (classExpression.isAnonymous()) {
            result = equivalentClasses.entities();
        } else {
            result = equivalentClasses.getEntitiesMinus(classExpression.asOWLClass()).stream();
        }
        return result;
    }

    /** Gets the subclasses of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *            The string from which the class expression will be parsed.
     * @param direct
     *            Specifies whether direct subclasses should be returned or not.
     * @return The subclasses of the specified class expression
     * @throws ParserException
     *             If there was a problem parsing the class expression. */
    public Stream<OWLClass> getSubClasses(String classExpressionString, boolean direct) throws ParserException {
        if (classExpressionString.trim().length() == 0) {
            return Stream.empty();
        }
        OWLClassExpression classExpression = parseClassExpression(classExpressionString);
        NodeSet<OWLClass> subClasses = oh.r.getSubClasses(classExpression, direct);
        return subClasses.entities();
    }

    /** Gets the instances of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *            The string from which the class expression will be parsed.
     * @param direct
     *            Specifies whether direct instances should be returned or not.
     * @return The instances of the specified class expression
     * @throws ParserException
     *             If there was a problem parsing the class expression. */
    public Stream<OWLNamedIndividual> getInstances(String classExpressionString, boolean direct) throws ParserException {
        if (classExpressionString.trim().length() == 0) {
            return Stream.empty();
        }
        OWLClassExpression classExpression = parseClassExpression(classExpressionString);
        NodeSet<OWLNamedIndividual> individuals = oh.r.getInstances(classExpression,direct);
        return individuals.entities();
    }

    /** Parses a class expression string to obtain a class expression.
     * 
     * @param classExpressionString
     *            The class expression string
     * @return The corresponding class expression
     * @throws ParserException
     *             if the class expression string is malformed or contains
     *             unknown entity names. */
    public OWLClassExpression parseClassExpression(String classExpressionString) {
        // Set up the real parser
        ManchesterOWLSyntaxParser parser = new ManchesterOWLSyntaxParserImpl(new OntologyConfigurator(), oh.df);
        parser.setDefaultOntology(oh.appOntology);
        parser.setStringToParse(classExpressionString);
        // Specify an entity checker that will be used to check a class
        // expression contains the correct names.
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
        parser.setOWLEntityChecker(entityChecker);
        // Do the actual parsing
        return parser.parseClassExpression();
    }

    /** @param classExpression
     *            the class expression to use for interrogation */
    public void askQuery(String classExpression) {
        if (classExpression.length() == 0) {
            System.out.println("No class expression specified");
        } else {
        	try {
	            StringBuilder sb = new StringBuilder();
	            sb.append("\n--------------------------------------------------------------------------------\n");
	            sb.append("QUERY:   ");
	            sb.append(classExpression);
	            sb.append("\n");
	            sb.append("--------------------------------------------------------------------------------\n\n");
	            // Ask for the subclasses, superclasses etc. of the specified
	            // class expression. Print out the results.
	            Stream<OWLClass> superClasses = getSuperClasses(classExpression, false);
	            printEntities("SuperClasses", superClasses, sb);
	            Stream<OWLClass> equivalentClasses = getEquivalentClasses(classExpression);
	            printEntities("EquivalentClasses", equivalentClasses, sb);
	            Stream<OWLClass> subClasses = getSubClasses(classExpression, false);
	            printEntities("SubClasses", subClasses, sb);
	            Stream<OWLNamedIndividual> individuals = getInstances(classExpression, false);
	            printEntities("Instances", individuals, sb);
	            
	            System.out.println(sb.toString());
	            
        	} catch (ParserException e){
        		System.out.println("Wrong query!");
        	}     
        }

    }

    private void printEntities(String name, Stream<? extends OWLEntity> entities, StringBuilder sb) {
    	
        sb.append(name);
        int length = 50 - name.length();
        for (int i = 0; i < length; i++) {
            sb.append(".");
        }
        sb.append("\n\n");
        
        Set<? extends OWLEntity> entitiesSet = entities.collect(Collectors.toSet());
        if (!entitiesSet.isEmpty()) {
            for (OWLEntity entity : entitiesSet) {
                sb.append("\t");
                sb.append(shortFormProvider.getShortForm(entity));
                sb.append("\n");
            }
        } else {
            sb.append("\t[NONE]\n");
        }
	    
        sb.append("\n");
    }

}
