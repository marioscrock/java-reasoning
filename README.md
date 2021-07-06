# Java Knowledge Interface (JKI)

A java project exploiting [Java Debug Interface](https://docs.oracle.com/javase/7/docs/jdk/api/jpda/jdi/) and 
[OWL API](https://github.com/owlcs/owlapi) to reason on active instances of a running Java application.

## Project Goals ##
Complex Java applications often contain a data model representing the domain they are related to. Our idea is to give to the programmer the possibility to check the java application model and the runtime evolution of its instances against an OWL2 ontology and a related knowledge base.

<p align="center"><img src="/docs/architecture.png" alt="Architecture" width="600"></p>

Given a Java application we would like to connect it to a _JKI_ component able to:
* **Reason about active instances of application classes** instantiated at runtime making use of an ontology describing same domain
   * Map java classes' instances to individuals in the ontology
   * Integrate ABox axioms generated with given knowledge base
   * Check no inconsistencies are generated at runtime
   * Obtain and model concepts (e.g. high-level ones) not expressed by the application structure through a more comprehensive ontology hiding implementation details on behalf of domain logic coherence
* **Dynamic Analysis** 
   * Execute periodic reasoning routines while the application is running checking runtime consistency of the application
* **Static Analysis** on the last snapshot of the application
   * Let the user add other axioms (parsing from file) to generated knowledge base
   * Let the user interactively ask DL queries through the reasoner synched with the knowledge base
   * Enable SPARQL queries (e.g path-based, aggregations) on RDF graph generated
* **Inter-software consistency**
  * Mapping a Java application model to an ontology enables the possibility to describe inter-software consistency constraints between data models of different applications. 
  * Exploiting the reasoner backend to manage semantics from different applications mapped to the same ontology makes possible to check also at runtime consistency of their integration.

## Implementation ##

### The application ###
Given a generic Java application we would like to connect it to a _JKI_ component able to monitor it providing runtime snapshots of active instances running in the JVM. We exploit the Java Debug Interface (JDI) running the application in debug mode and connecting to it remotely through socket and ```AttachingConnector```.

To run the java application in debug virtual machine it must be run with options:
```-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y```

Through the JDI API we are able remotely to:
* **Attach** to the Java Application listening on a given port
* **Dialogue with the class loader** to ensure classes of interest are loaded
* **Place in-code breakpoints** and listen for breakpoints event
* **Suspend VM execution** and **inspect stack variables and class/instance variables** (this tree structure)
* Resume VM execution
* Detect application termination and detach from it

### Java Reasoner ###

The _java-reasoner_ is a proof-of-concept (POC) of the described _JKI_.

<p align="center"><img src="/docs/java-reasoner.png" alt="Architecture" width="600"></p>

_Notes_
* Ontologies can be loaded from file, from remote IRI or from coded function defining axioms through OWL API
* Additional axioms can be added to the ontology through file parsing before/after the application monitoring
* Syntaxes supported are RDFXML, Manchester, Functional, Turtle (also to save ontology)

Let's now see in details java classes defining the _java-reasoner_ component.

#### ```InspectToAxiom``` ####

Abstract class representing an inspector component able to build up ABox axioms with respect to active instances.
  * The methods ```getDebugPort()```, ```getClassPattern()```, ```getMethodName()``` returns information about the port where the application in debugging mode is listening and where to put the breakpoint in the observed application (last location available of the class method specified).
  * The methods ```inspectClass()``` and ```inspectClasses()``` actually analyze the VM to populate the ontology managed by the OnologyHandler given when building the instance. A default implementation of those two methods is provided under the ASSUMPTION that the following methods are described as specified:
    * ```getMapOntToAppClasses()``` returns a map binding short identifier of ontology's classes
        to the fully qualified name of application Java classes. Not all classes of the ontology need to be bound and have a value in the map.
    * ```getMapClassToFieldId()``` returns a map binding short identifier of ontology's classes to
        the class field name that must be used as IRI fragment (identifier)
        of the named individual in the ontology.
    * ```getMapClassToDataProp()``` returns a map binding short identifier of ontology's classes to the class fields names representing a data property of the ontology. For each class, provide a set of pairs binding the name of each field to the IRI fragment (identifier) of the data property in the ontology. We consider as data properties all fields of type integer, double, float, boolean and String.
    * ```getMapClassToObjProp()``` returns a map binding short identifier of ontology's classes to the class fields names representing an object property of the ontology. For each class, provide a set of pairs binding the name of each field to the IRI fragment (identifier) of the data property in the ontology. We consider as object properties all fields relating to a fully qualified class bound to an ontology class in the map returned from getMapOntToAppClasses() method.
     
     We also assume multiple property binary relations from the same instance are stored in ArrayList
     fields named as the property:
     
     (e.g. friendOf property in class Person -> ```private List<Object> friendOf = new ArrayList<>();```
     
     **Note** that the type of objects in the list can be Object or specific types given that at runtime dynamic type is checked to determine the class of the individual to be the target of the relation.

#### ```OntologyHandler``` ####
Ontology handler provides a high-level API to deal with OWL API and may contain the coded axioms of the ontology.

**Note** The ```OntologyHandler``` keeps a buffer that is exploited by the inspector to ensure previously added ABox axioms (generated by last inspection of application instances) are deleted and only currently active instances are represented in the ontology.

#### ```ReasoningServer``` ####
Interface that can be implemented by ```OntologyHandler``` component that provides a reasoning routine executed each time the virtual machine is suspended.

#### ```DebugAttach``` ####
Manage the JDI API to place the breakpoint. It notifies the inspector and runs the reasoning routine each time it receives a breakpoint event.

**Note** The application executed in the Virtual Machine is suspended until the inspection and the routine are completed.

#### ```DLQueryEngine``` ####
Allow enabling a query loop in the console asking for and replying to submitted Manchester syntax DL queries.

#### ```Main``` ####
Contains useful methods to build Main classes for a ```javareasoner``` application.
  * ```parsingLoop``` Method to enable a parsing loop asking if user want to parse a file to add axioms to the ontology (functional syntax, rdfxml, manchester syntax and turtle syntax are supported).
  * ```parsingAxioms``` Method to directly parse a file containing axioms.
  * ```initDebugger``` Method to manage connection through a ```DebugAttach``` instance.
  * ```initOntologyHandler``` Method to initialize the ontology of a ```OntologyHandler``` given an array of ```args``` options: 
    * If empty array initialize default ontology through ```initOntology``` method
    * If ```args.length``` equal to 1: 1st arg specifies the serialization type to save the ontology (can be ```functional```, ```manchester```, ```rdfxml```, ```turtle```)
    * If ```args.length``` equal to 2: 2nd arg is used as file path to load the ontology instead of the default one
    
### SPARQL Engine ###

[Jena ARQ](https://jena.apache.org/documentation/query/) is a query engine that supports the SPARQL RDF Query language. An interface to make _select_ queries is provided with class ```SPARQLEngine``` through method ```askQuery```.

More or less expressive SPARQL queries can be done against Jena Models (RDF graphs) more or less efficiently with respect to the reasoner enabled if the pre-reasoning saved knowledge base is used as model (_Jena Reasoners_ : RDFS / OWL rule-based reasoner)
  
We can save the KB axioms (TBox, RBox, ABox) managed by the ```javareasoner``` as a RDF document through the  ```OntologyHandler``` component and use it as Jena model. 
```SPARQLEngine``` class also offers the method ```shouldCreateInferredAxioms``` to save a dump of the knowledge base and all inferable axioms. Using post-reasoning saved knowledge base as model enables full expressivity also without enabling Jena Reasoners but it's unfeasible for large KBs.
  
## How to run the demo ##

### The application ###
The project contains two example application in packages ```app.artmarket``` and ```app.eshop```. For each application an ontology is described through the OWLAPI in the respective extension of ```OntologyHandler``` ( ```AMOntologyHandler``` and ```ESOntologyHandler```).

The two applications are two toy example and their execution is managed respectively by ```ReasonedArtMarketMain``` and ```ReasonedEshopMain``` that must be run with above specified options to enable remote debug mode.

A [document](https://github.com/marioscrock/java-reasoning/blob/master/docs/demo-docs.pdf) describing the two Java applications, the defined ontologies and the peculiarities of each demo is available in the ```docs``` folder.

### Java Reasoner ###

The ```javareasoner``` package contains the main classes of the _java-reasoner_ component. Two implementations, one for each example application can be executed respectively through the classes ```MainAM``` and ```MainES```.

 * Both examples show how the ontology changes with respect to the runtime evolution of the application.
 * ```MainAM``` shows in particular examples of integration between ABox axioms parsed from file and ABox axioms generated from active instances.
 * ```MainES``` shows in particular how a higher level ontology can be exploited to extract information from the application (e.g. classes not represented in the application).

Example reasoning routines are defined for the [```app.artmarket```](https://github.com/marioscrock/java-reasoning/blob/448d258b943db2b51b8bf3e8701e71b8cac31a86/src/main/java/javareasoner/ontology/AMOntologyHandler.java#L152) and the [```app.eshop```](https://github.com/marioscrock/java-reasoning/blob/448d258b943db2b51b8bf3e8701e71b8cac31a86/src/main/java/javareasoner/ontology/ESOntologyHandler.java#L214). An example [snapshot](https://github.com/marioscrock/java-reasoning/blob/master/owl/appOntologyES.owl) of the knowledge base for the ```app.eshop``` (including the semantically lifted runtime state) is also available.

**Note** The user is expected to provide inputs from the console in both running executables to enable connection and ensure debugger is ready when the application actually starts its execution.

### SPARQL Engine ###

The ```sparql``` package contains the main class ```MainSPARQLDemo``` that shows (through an example KB obtained by running the EShop demo and saved in ```appOntologyES.owl``` file) the tradeoff between expressivity of queries and model/reasoning enabled.

## How to connect your own application ##

* Extend ```InspectToAxiom``` providing an implementation of methods as specified above.
* Extend ```OntologyHandler``` providing a default ontology through OWLAPI overriding the ```initOntology``` method (follow the same structure of examples provided) or load your ontology from file.
* Build a ```Main``` class for reasoning backend managing execution of ```javareasoner``` components.
* Run your Java application in debug mode (listening for remote connection) and the *main* method of the backend.
