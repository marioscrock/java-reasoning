package javareasoner.inspect;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LongValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ShortValue;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

import javareasoner.ontology.OntologyHandler;

public abstract class InspectToAxiom {
	
	protected OntologyHandler oh;
	private final int ALL_INSTANCES = 0;
	
	public void inspectClass(VirtualMachine vm, String ontClassId, String className,
			String fieldId, String[] objProperties, String[] dataProperties) {
		
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
		        	if (dataProperties != null && dataProperties.length > 0)
		        		for (String dataProperty : dataProperties) {
		        			
		        			Value valData = objRef.getValue(refType.fieldByName(dataProperty));
		        			
		        			if (valData != null) {
				        		String data = dataToString(valData);
				        		if (data != null) 
				        			oh.addDataProperty(instanceId, data, dataProperty);
				        	
		        			}
		        			
		        		}
		        	
		        	//Create object properties: assuming each property refers to an object or an ArrayList of 
		        	//objects related through the property (same name of the field) with the instance
		        	if (objProperties != null && objProperties.length > 0)
		        		for (String objProperty : objProperties) {
		        			
		        			Value valObj = objRef.getValue(refType.fieldByName(objProperty));
		        			
		        			if (valObj instanceof ObjectReference) { 	
		        				
			        			ObjectReference objProp = (ObjectReference) valObj;
			        			ReferenceType refTypeProp = objProp.referenceType();
			        			String classTargetName = classValueName(valObj);
			        			
			        			if (classTargetName.equals("ArrayList")) {
			        				
				        			int size = ((IntegerValue) objProp.getValue(refTypeProp.fieldByName("size"))).value();
				        			ArrayReference arrayRef = (ArrayReference) objProp.getValue(refTypeProp.fieldByName("elementData"));
				        			for(int i = 0; i < size; i ++) {   			
				        				ObjectReference objRefEl = (ObjectReference) arrayRef.getValue(i);
				        				ReferenceType refTypeEl = objRefEl.referenceType();
				        				Value valEl = arrayRef.getValue(i);
				        				String classTargetElName = classValueName(valEl);
				        				String fieldIdTargetEl = getMapClassToFieldId().get(classTargetElName);
				        				Value valElId = objRefEl.getValue(refTypeEl.fieldByName(fieldIdTargetEl));
					        			oh.addObjectProperty(instanceId, ( (StringReference) valElId).value(), objProperty);		
				        			}
				        			
			        			} else {
			        						
			        				String fieldIdTarget = getMapClassToFieldId().get(classTargetName);
			        				Value valElId = objProp.getValue(refTypeProp.fieldByName(fieldIdTarget));
				        			oh.addObjectProperty(instanceId, ( (StringReference) valElId).value(), objProperty);
			        				
			        			}
			        			        			
		        			}
		        		}			        	
		        	}
	        }
		}
				
	}
	
	private String dataToString(Value valData) {

		if (valData instanceof StringReference)
		   return ( (StringReference) valData).value();
		else if (valData instanceof IntegerValue)
			return Integer.toString(((IntegerValue) valData).value());
		else if (valData instanceof BooleanValue)
			return Boolean.toString(((BooleanValue) valData).value());
		else if (valData instanceof FloatValue)
			return Float.toString(((FloatValue) valData).value());
		else if (valData instanceof CharValue)
			return String.valueOf(((CharValue) valData).value());			        			
		else if (valData instanceof DoubleValue)
			return Double.toString(((DoubleValue) valData).value());
		else if (valData instanceof ByteValue)
			return Byte.toString(((ByteValue) valData).value());
		else if (valData instanceof LongValue)
			return Long.toString(((LongValue) valData).value());
		else if (valData instanceof ShortValue)
			return Short.toString(((ShortValue) valData).value());
		else {
			return null;
		}
		
	}

	private String classValueName(Value val) {
		
		//Retrieve class of target and related fieldId
		//Assuming all fully-qualified-classes signature since object properties
		//L fully-qualified-class ;	 (e.g. for a string) Ljava/lang/String;
		String[] classSignatureName = val.type().signature().replaceFirst("L", "").split("/");
		String classTargetName = classSignatureName[classSignatureName.length - 1].replace(";", "");
		
		return classTargetName;
		
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
