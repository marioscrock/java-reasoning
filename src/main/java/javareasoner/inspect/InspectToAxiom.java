package javareasoner.inspect;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

import javareasoner.ontology.OntologyHandler;

public abstract class InspectToAxiom {
	
	protected OntologyHandler oh;
	
	public void inspectClass(VirtualMachine vm, String ontClassId, String className,
			String fieldId, String[] objProperties, String[] dataProperties) {
		
		//Get all classes matching a given string
				List<ReferenceType> classesByName = vm.classesByName(className);
				
				if (!classesByName.isEmpty()) {
					
					//Get the first class returned assuming only one class related to a given string
			        ReferenceType refType = classesByName.get(0);
			        //Get all active instances of the given refType
			        List<ObjectReference> objRefs = refType.instances(0);
			        
			        if(!objRefs.isEmpty()) {
			        	
				        //For each active instance of the given class
				        for (ObjectReference objRef : objRefs) {
				        	
				        	//Create individual determining IRI through field named fieldId
				        	Value val = objRef.getValue(refType.fieldByName(fieldId));
				        	String instanceId = ( (StringReference) val).value();
				        	oh.createIndividual(instanceId, ontClassId);
				        	
				        	//Create data properties, up to know only to xsd:string 
				        	if (dataProperties != null && dataProperties.length > 0)
				        		for (String dataProperty : dataProperties) {
				        			
				        			Value valData = objRef.getValue(refType.fieldByName(dataProperty));
				        			
				        			if (valData != null) {
					        			if (valData instanceof StringReference)
					        			   oh.addStringDataProperty(instanceId,
					        					   ( (StringReference) valData).value(), dataProperty);
					        			
					        			  //TODO
					        			  //else if (value instanceof BooleanValue)
					        			  //{...} ...
				        			}
				        	
				        		}
				        	
				        	//Create object properties: assuming each property refers to a list of 
				        	//object related through the property (same name of the list) with the instance
				        	if (objProperties != null && objProperties.length > 0)
				        		for (String objProperty : objProperties) {
				        			
				        			ObjectReference objRefList = (ObjectReference) objRef.getValue(refType.fieldByName(objProperty));
				        			ReferenceType refTypeList = objRefList.referenceType();
				        			int size = ((IntegerValue) objRefList.getValue(refTypeList.fieldByName("size"))).value();
				        			ArrayReference arrayRef = (ArrayReference) objRefList.getValue(refTypeList.fieldByName("elementData"));
				        			for(int i = 0; i < size; i ++) {
				    			
				        				ObjectReference objRefEl = (ObjectReference) arrayRef.getValue(i);
				        				ReferenceType refTypeEl = objRefEl.referenceType();
				        				
				        				//Retrieve class of target and related fieldId
				        				//Assuming all fully-qualified-classes signature since object properties
				        				//L fully-qualified-class ;	 (e.g. for a string) Ljava/lang/String;
				        				String[] classSignatureName = arrayRef.getValue(i).type().signature().replaceFirst("L", "").split("/");
				        				String classTargetName = classSignatureName[classSignatureName.length - 1].replace(";", "");
				        				String fieldIdTarget = getMapClassToFieldId().get(classTargetName);
				  
				        				Value valEl = objRefEl.getValue(refTypeEl.fieldByName(fieldIdTarget));
				        				
				        				oh.addObjectProperty(instanceId, ( (StringReference) valEl).value(), objProperty);
				        				
				        			}
				        		}
				        	
				        	}
			        }
				}
				
	}
	
	public void inspectClasses(VirtualMachine vm) {
		
		oh.deleteFromOntAxiomsInBuffer();
		
		Set<OWLClass> set = oh.allClassesInOntology();
		Map<String, String> ontToAppClasses = getMapOntToAppClasses();
		Map<String, String> classToFieldId = getMapClassToFieldId();
		Map<String, String[]> classToObjProp = getMapClassToObjProp();
		Map<String, String[]> classToDataProp = getMapClassToDataProp();
	
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
	
	public abstract Map<String, String> getMapOntToAppClasses();
	public abstract Map<String, String[]> getMapClassToObjProp();
	public abstract Map<String, String[]> getMapClassToDataProp();
	public abstract Map<String, String> getMapClassToFieldId();
	
}
