# java-reasoning
A java project exploiting [Java Debug Interface](https://docs.oracle.com/javase/7/docs/jdk/api/jpda/jdi/) and 
[OWL API](https://github.com/owlcs/owlapi) to reason on active instances of a running Java application.

Complex Java applications often contain a data model representing the domain they are related to.

<p align="center"><img src="/img/architecture.png" alt="Architecture" width="600"></p>

Our idea is to give to the programmer the possibility to check the java application model and the runtime evolution of its instances against an OWL2 ontology and a related knowledge base.

## Project Goals ##
Given a Java application we would like to connect it to a __javareasoner__ component able to:
* **Reason about active instances of application classes** instanciated at runtime making use of an ontology describing same domain:
   * Map java classes' instances to individuals in the ontology
   * Integrate ABox axioms generated with given knowledge base
   * Check no inconsistencies are generated at runtime
   * Obtain and model concepts (e.g. high-level ones) not expressed by the application structure through a more comprehensive ontology hiding implementation details on behalf of domain logic coherence
* **Dynamic Analysis** 
   * Execute periodic reasoning routines checking runtime consistency of the application
* **Static Analysis** on the last snapshot of the application
   * Let the user add other axioms (parsing from file) to generated knowledge base
   * Let the user interactively ask DL queries through the reasoner synched with the knowledge base
* **Inter-software consistency**
  * Mapping a Java application model to an ontology enables the possibility to describe inter-software consistency constraints between data models of different applications. 
  * Exploiting the reasoner backend to manage semantics from different applications mapped to the same ontology makes possible to check also at runtime consistency of their integration.

## How to run the demo ##

### The application ###
The project contains two example application in packages ```app.artmarket``` and ```app.eshop```. For each application an ontology is described through the OWLAPI in the respective extension of ```OntologyHandler``` ( ```AMOntologyHandler``` and ```ESOntologyHandler```).

The two applications are two toy example and their execution is managed respectively by ```ReasonedArtMarketMain``` and ```ReasonedEshopMain``` that must be run with following options to enable remote debug mode:

```-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y```

### Java Reasoner ###

The ```javareasoner``` package contains the main components of the debugger connecting to the application that can be executed respectively through the classes ```MainAM``` and ```MainES```.

 * Both examples show how the ontology changes with respect to the runtime evolution of the application.
 * ```MainAM``` shows in particular examples of integration between ABox axioms parsed from file and ABox axioms generated from active instances.
 * ```MainEs``` shows in particular how a higher level ontology can be exploited to extract information from the application (e.g. classes not represented in the application).

**Note** The user is expected to provide inputs from the console in both running executables to enable connection and ensure debugger is ready when the application actually starts its execution.


## Main components ##

#### ```InspectToAxiom``` ####

Abstract class representing an inspector component able to build up ABox axioms with respect to active instances.
  * The methods ```getDebugPort()```, ```getClassPattern()```, ```getMethodName()``` returns information about the port where the application in debugging mode is listening and where to put the breakpoint in the observed application (last location available of the class method specified).
  * The methods ```inspectClass()``` and ```inspectClasses()``` actually analyze the VM to populate the ontology managed by the OnologyHandler given when building the instance. A default implementation of those two methods is provided under the ASSUMPTION that the following methods are described as specified:
    * ```getMapOntToAppClasses()``` returns a map binding short identifier of ontology's classes
        to the fully qualified name of application Java classes. Not all classes of the ontology need to be bound and have a value in the map.
    * ```getMapClassToFieldId()``` returns a map binding short identifier of ontology's classes to
        the class field name that must be used as IRI fragment (identifier)
        of the named individual in the ontology.
    * ```getMapClassToDataProp()``` returns a map binding short identifier of ontology's classes to the class fields names representing a data property of the ontology. We assume the name of each field corresponds to the IRI fragment (identifier) of the data property in the ontology, otherwise another map must be exploited to correlate them. We consider as data properties all fields of type integer, double, float, boolean and String.
    * ```getMapClassToObjProp()``` returns a map binding short identifier of ontology's classes to the class fields names representing an object property of the ontology. We assume the name of each field corresponds to the IRI fragment (identifier) of the object property in the ontology, otherwise another map must be exploited to correlate them. We consider as object properties all fields relating to a fully qualified class bound to an ontology class in the map returned from getMapOntToAppClasses() method.
     
     We also assume multiple property binary relations from the same instance are stored in ArrayList
     fields named as the property:
     
     (e.g. friendOf property in class Person -> ```private List<Object> friendOf = new ArrayList<>();```
     
     **Note** that the type of objects in the list can be Object or specific types given that dynamic type at
     runtime is exploited to determine the class of the individual to be the target of the relation.

#### ```OntologyHandler``` ####
Ontology handler provides a high-level API to deal with OWL API and may contain the coded axioms of the ontology.

**Note** The ```OntologyHandler``` keeps a buffer that is exploited by the inspector to ensure previously added ABox axioms (generated by last inspection of application instances) are deleted and only currently active instances are represented in the ontology.

#### ```ReasoningServer``` ####
Interface that can be implemented by ```OntologyHandler``` component that provides a reasoning routine executed each time the virtual machine is suspended.

#### ```DebugAttach``` ####
Manage the JDI API to place the breakpoint. It notifies the inspector and runs the reasoning routine each time receive the breakpoint event.

**Note** The application executed in the Virtual Machine is suspended until the inspection and the routine are completed.

#### ```DLQueryEngine``` ####
Allow enabling a query loop in the console asking for and replying to submitted Manchester syntax DL queries.

#### ```Main``` ####
Contains useful method to build Main classes for a ```javareasoner``` application.
  * ```parsingLoop``` Method to enable a parsing loop asking if user want to parse a file to add axioms to the ontology (functional syntax, rdfxml, manchester syntax and turtle syntax are supported).
  * ```parsingAxioms``` Method to directly parse a file containing axioms.
  * ```initDebugger``` Method to manage connection through a ```DebugAttach``` instance.
  * ```initOntologyHandler``` Method to initialize the ontology of a ```OntologyHandler``` given an array of ```args``` options: 
    * If empty array initialize default ontology through ```initOntology``` method
    * If ```args.length``` equal to 1: 1st arg specifies the serialization type to save the ontology (can be ```functional```, ```manchester```, ```rdfxml```, ```turtle```)
    * If ```args.length``` equal to 2: 2nd arg is used as file path to load the ontology instead of the default one

## How to connect your own application ##

* Extend ```InspectToAxiom``` providing an implementation of methods as specified above.
* Extend ```OntologyHandler``` providing a default ontology through OWLAPI overriding the ```initOntology``` method (follow the same structure of examples provided) or load your ontology from file.
* Build a ```Main``` class for reasoning backend managing execution of ```javareasoner``` components.
* Run your Java application in debug mode (listening for remote connection) and the *main* method of the backend.

