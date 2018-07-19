package javareasoner.inspect;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

import javareasoner.DataPropertyType;
import javareasoner.ontology.OntologyHandler;
/**
 * Abstract class representing an inspector component able to build up ABox
 * axioms with respect to active instances.
 * 
 * The methods getDebugPort(), getClassPattern(), getMethodName() returns information
 * about where to put breakpoint in the observed application.
 * 
 * The methods inspectClass() and inspectClasses() actually analyze the vm to
 * populate the ontology managed by the OnologyHandler given when building the instance.
 * A default implementation of those two methods is provided under the ASSUMPTION that the following 
 * methods are described as specified:
 * <ul>
   <li> {@link #getMapOntToAppClasses()}: returns a map binding short identifier of ontology's classes
   	 to fully qualified name of application java classes. Not all classes of the ontology 
   	 need to be bound and have a value in the map.
   <li> {@link #getMapClassToFieldId()}: returns a map binding short identifier of ontology's classes to
   	 the class field name that must be used as IRI fragment (identifier)
   	 of the named individual in the ontology.
   <li> {@link #getMapClassToDataProp()}: returns a map binding short identifier of ontology's classes
     to the class fields names representing a data property of the ontology. For each class, provide a set
	 of {@link javareasoner.inspect.Pair Pair}s binding the name of each field to the IRI fragment (identifier) 
	 of the data property in the ontology. We consider as data 
     properties all fields of type integer, double, float, boolean and String.
   <li> {@link #getMapClassToObjProp()}: returns a map binding short identifier of ontology's classes
     to the class fields names representing an object property of the ontology. For each class, provide a set
	 of {@link javareasoner.inspect.Pair Pair}s  binding the name of each field to the IRI fragment (identifier) 
	 of the data property in the ontology. We consider as object 
     properties all fields relating to a fully qualified class bound to an ontology class in the map
     returned from getMapOntToAppClasses() method.<br>
     We also assume multiple property binary relations from the same instance are stored in ArrayList
     fields named as the property: <br>
     (e.g. friendOf property in class Person -> {@code private List<Object> friendOf = new ArrayList<>();}) <br>
     Note that the type of objects in the list can be Object or specific types given that dynamic type at
     runtime is exploited to determine the class of the individual to be target of the relation.
   </ul>
 * @author Mario
 *
 */
public abstract class InspectToAxiom {
	
	protected OntologyHandler oh;
	private final int ALL_INSTANCES = 0;
	
	/**
	 * Abstract constructor of the InspectToAxiom class.
	 * @param oh	OntologyHandler of the ontology related to the application to inspect
	 */
	public InspectToAxiom(OntologyHandler oh) {
		this.oh = oh;
	}
	
	/**
	 * Given a VirtualMachine, through the OntologyHandler and information provided
	 * generate ABox axioms related to active classes in the application.
	 * It exploits {@link javareasoner.ontology.OntologyHandler#createIndividual(String, String) createIndividual},
	 * {@link javareasoner.ontology.OntologyHandler#addDataProperty(String, String, String) addDataProperty},
	 * and {@link javareasoner.ontology.OntologyHandler#addObjectProperty(String, String, String) addObjectProperty}
	 * @param vm	Suspended virtual machine executing java app
	 * @param ontClassId	IRI fragment of the ontology's class 
	 * @param className	For each class Class, the name returned by Class.class.getName()
	 * 					Refers to the java Class related to the ontology's class
	 * @param fieldId	Field name of the java class to be used as IRI fragment for each individual
	 * 				  of the given class created.
	 * @param objProperties	Set of pairs binding field names of the java class to object properties of the ontology
	 * @param dataProperties	Set of pairs binding field names of the java class to data properties of the ontology
	 * @requires Implementation of {@link #getMapClassToFieldId()} as described.
	 */
	public void inspectClass(VirtualMachine vm, String ontClassId, String className,
			String fieldId, HashSet<Pair<String,String>> objProperties, HashSet<Pair<String,String>> dataProperties) {
		
		//Get all classes matching a given string
		List<ReferenceType> classesByName = vm.classesByName(className);
		
		if (!classesByName.isEmpty()) {
			
			//Get the first class returned assuming only one class related to a given string
	        ReferenceType refType = classesByName.get(0);
	        //Get all active instances of the given refType
	        List<ObjectReference> objRefs = refType.instances(ALL_INSTANCES);
	        
	        if(!objRefs.isEmpty()) {
	        	
		        //For each active instance of the given class
		        for (ObjectReference objRef : objRefs) {
		        	
		        	//Create individual determining IRI through field named fieldId
		        	Value val = objRef.getValue(refType.fieldByName(fieldId));
		        	String instanceId = ( (StringReference) val).value();
		        	oh.createIndividual(instanceId, ontClassId);
		        	
		        	//Create data properties
		        	if (dataProperties != null && dataProperties.size() > 0)
		        		for (Pair<String,String> dataProperty : dataProperties) {
		        			
		        			Value valData = objRef.getValue(refType.fieldByName(dataProperty.getLeft()));
		        			
		        			if (valData != null)
				        		valToDataProperty(valData, dataProperty.getRight(), instanceId);		
		        			
		        		}
		        	
		        	//Create object properties: assuming each property refers to an object or an ArrayList of 
		        	//objects related through the property (same name of the field) with the instance
		        	if (objProperties != null && objProperties.size() > 0)
		        		for (Pair<String,String> objProperty : objProperties) {
		        			
		        			Value valObj = objRef.getValue(refType.fieldByName(objProperty.getLeft()));
		        			
		        			//Check if it is an object property
		        			if (valObj instanceof ObjectReference) { 	
		        				
			        			ObjectReference objProp = (ObjectReference) valObj;
			        			ReferenceType refTypeProp = objProp.referenceType();
			        			String classTargetName = classValueName(valObj);
			        			
			        			//If it is a list
			        			if (classTargetName.equals("ArrayList")) {
			        				
				        			int size = ((IntegerValue) objProp.getValue(refTypeProp.fieldByName("size"))).value();
				        			ArrayReference arrayRef = (ArrayReference) objProp.getValue(refTypeProp.fieldByName("elementData"));
				        			for(int i = 0; i < size; i ++) {   			
				        				ObjectReference objRefEl = (ObjectReference) arrayRef.getValue(i);
				        				ReferenceType refTypeEl = objRefEl.referenceType();
				        				Value valEl = arrayRef.getValue(i);
				        				String classTargetElName = classValueName(valEl);
				        				String fieldIdTargetEl = getMapClassToFieldId().get(classTargetElName);
				        				//Check if target type refers to an object of a java class mapped to an ontology class
				        				if (fieldIdTargetEl != null) {
				        					Value valElId = objRefEl.getValue(refTypeEl.fieldByName(fieldIdTargetEl));
				        					oh.addObjectProperty(instanceId, ( (StringReference) valElId).value(), objProperty.getRight());
				        				}
				        			}
				        			
			        			} else {
			        				//Else: If it is a single field		
			        				String fieldIdTarget = getMapClassToFieldId().get(classTargetName);
			        				//Check if target type refers to an object of a java class mapped to an ontology class
			        				if (fieldIdTarget != null) {
			        					Value valElId = objProp.getValue(refTypeProp.fieldByName(fieldIdTarget));
			        					oh.addObjectProperty(instanceId, ( (StringReference) valElId).value(), objProperty.getRight());
			        				}
			        				
			        			}
			        			        			
		        			}
		        		}			        	
		        	}
	        }
		}
				
	}
	
	/**
	 * Check all possible types of data for a given value adding the right
	 * type of data properties for data in {@code Value valData} for the specified individual.
	 * @param valData	The value to be transformed
	 * @param dataProperty	IRI Fragment identifying the data property
	 * @param instanceId	IRI Fragment identifying the individual
	 */
	private void valToDataProperty(Value valData, String dataProperty, String instanceId) {

		if (valData instanceof StringReference) {
			String toValue = ( (StringReference) valData).value();
			oh.addDataProperty(instanceId, toValue, dataProperty, DataPropertyType.STRING);
		} else if (valData instanceof IntegerValue) {
			int toValue = ((IntegerValue) valData).value();
			oh.addDataProperty(instanceId, toValue, dataProperty, DataPropertyType.INTEGER);
		} else if (valData instanceof BooleanValue) {
			boolean toValue = ((BooleanValue) valData).value();
			oh.addDataProperty(instanceId, toValue, dataProperty, DataPropertyType.BOOLEAN);
		} else if (valData instanceof FloatValue) {
			float toValue = ((FloatValue) valData).value();
			oh.addDataProperty(instanceId, toValue, dataProperty, DataPropertyType.FLOAT);
		} else if (valData instanceof DoubleValue) {
			double toValue = ((DoubleValue) valData).value();
			oh.addDataProperty(instanceId, toValue, dataProperty, DataPropertyType.DOUBLE);
		}
		
	}
	
	/**
	 * Retrieve class of value's type assuming {@code Value val}
	 * refers to a fully-qualified-class signature<br>	 
	   (e.g. for a string -> Ljava/lang/String; -> {@code It returns "String"})
	 * @param val
	 * @return Simple name of {@code val}'s type class
	 */
	private String classValueName(Value val) {
		
		//
		//
		String[] classSignatureName = val.type().signature().replaceFirst("L", "").split("/");
		String classTargetName = classSignatureName[classSignatureName.length - 1].replace(";", "");
		
		return classTargetName;
		
	}
	
	/**
	 * For all classes in the ontology mapped to a java class lookup to active
	 * instances in the {@code vm}, generating ABox axioms related. It empty the buffer
	 * of axioms in the OntologyHandler to ensure previously added instances are deleted
	 * and only currently active ones are represented in the ontology.<br>
	 * It exploits {@link #getMapOntToAppClasses()}, {@link #getMapClassToFieldId()},
	 * {@link #getMapClassToObjProp()}, {@link #getMapClassToDataProp()} to call
	 * {@link #inspectClass(VirtualMachine, String, String,
			String, String[], String[]) inspectClass} for each class.
	 * @param vm Suspended virtual machine executing java app
	 */
	public void inspectClasses(VirtualMachine vm) {
		
		oh.deleteFromOntAxiomsInBuffer();
		
		Set<OWLClass> set = oh.allClassesInOntology();
		Map<String, String> ontToAppClasses = getMapOntToAppClasses();
		Map<String, String> classToFieldId = getMapClassToFieldId();
		Map<String, HashSet<Pair<String,String>>> classToObjProp = getMapClassToObjProp();
		Map<String, HashSet<Pair<String,String>>> classToDataProp = getMapClassToDataProp();
	
		for (OWLClass c : set) {
			
			//Get only #Fragment
			String classId = c.toStringID().split("#")[1];
			
			if(ontToAppClasses.get(classId) != null) {
				inspectClass(vm, classId, ontToAppClasses.get(classId),
					classToFieldId.get(classId), classToObjProp.get(classId), classToDataProp.get(classId));
			}
			
		}
		
	}
	
	/**
	 * Get the port of the virtual machine to debug
	 * @return int Port of the virtual machine to debug
	 */
	public abstract int getDebugPort();
	
	/**
	 * Get the name of the class to debug
	 * @return String The name of the class that you wish to debug
	 */
	public abstract String getClassPattern();
	
	/**
	 * Get the name of the method to debug
	 * @return String The name of the method to debug
	 */
	public abstract String getMethodName();
	
	/**
	 * Get a map binding short identifier of ontology's classes
   	 to fully qualified name of application java classes. Not all
   	 classes of the ontology need to be bound and have a value in the map.
	 * @return map binding short identifier of ontology's classes
   	 to fully qualified name of application java classes
	 */
	protected abstract Map<String, String> getMapOntToAppClasses();
	
	/**
	 * Get a map binding short identifier of ontology's classes to
   	 the class field name that must be used as IRI fragment (identifier)
   	 of the named individual in the ontology
	 * @return map binding short identifier of ontology's classes to
   	 the class field name that must be used as IRI fragment (identifier)
   	 of the named individual in the ontology.
	 */
	protected abstract Map<String, String> getMapClassToFieldId();
	
	/**
	 * Get a map binding short identifier of ontology's classes to the class fields
	 names representing a data property of the ontology. For each class, provide a set
	 of {@link javareasoner.inspect.Pair Pair}s  binding the name of each field to the IRI 
	 fragment (identifier) of the data property in the ontology. We consider as data 
     properties all fields of type integer, double, float, boolean and String.
	 * @return map binding short identifier of ontology's classes
     to the class fields names representing a data property of the ontology
	 */
	protected abstract Map<String, HashSet<Pair<String,String>>> getMapClassToDataProp();
	
	/**
	 * Get a map binding short identifier of ontology's classes
     to the class fields names representing an object property of the ontology. For each class, provide a set
	 of {@link javareasoner.inspect.Pair Pair}s  binding the name of each field to the IRI fragment 
	 (identifier) of the data property in the ontology. We consider as object 
     properties all fields relating to a fully qualified class bound to an ontology class in the map
     returned from getMapOntToAppClasses() method.
     We also assume multiple property binary relations from the same instance are stored in ArrayList
     fields named as the property:
     (e.g. friendOf property in class Person -> private List<Object> friendOf = new ArrayList<>();)
     Note that the type of objects in the list can be Object or specific types given that dynamic type at
     runtime is exploited to determine the class of the individual to be target of the relation.
	 * @return map binding short identifier of ontology's classes
     to the class fields names representing an object property of the ontology
	 */
	protected abstract Map<String, HashSet<Pair<String,String>>> getMapClassToObjProp();
	
}
