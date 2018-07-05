package app.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

import app.person.*;
import app.thing.*;
import javareasoner.InspectToAxiom;
import javareasoner.OntologyHandler;

public class ReasonedArtMarket implements InspectToAxiom {
	
	public int debugPort = 8000;
	public String classPattern = ArtMarket.class.getName();
	public String methodName = "startApp";
	
	private static ArtMarket artMarket;
	
	public static void main(String[] args) {
		
		//To attach debugger run the class with following options and then launch a proper debugger to be attached
		//-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y
		System.out.println("Is listening for debugger to be attached? (y/n)");
		Scanner scan = new Scanner(System.in);
		String s = scan.next();
		
		if (!s.toLowerCase().equals("y")) 
			System.out.println("No active instances check enabled!");
		
		artMarket = new ArtMarket();
		artMarket.startApp();
		
		while(!s.toLowerCase().equals("exit")) {
			System.out.println("\nType \"exit\" to stop");
			s = scan.next(); 			
		}
		
		scan.close();
		
	}

	@Override
	public void inspectClass(VirtualMachine vm, String ontClassId, String className,
			String fieldId, String[] objProperties, String[] dataProperties) {
		
		List<ReferenceType> classesByName = vm.classesByName(className);
		
		if (!classesByName.isEmpty()) {
			
	        ReferenceType refType = classesByName.get(0);
	        List<ObjectReference> objRefs = refType.instances(0);
	        
	        if(!objRefs.isEmpty()) {
	        	
		        //For each active instance of the given class
		        for (ObjectReference objRef : objRefs) {
		        	
		        	//Create individual determining IRI through field named fieldId
		        	Value val = objRef.getValue(refType.fieldByName(fieldId));
		        	String instanceId = ( (StringReference) val).value();
		        	OntologyHandler.createIndividual(instanceId, ontClassId);
		        	
		        	//Create data properties, up to know only to xsd:string 
		        	if (dataProperties != null && dataProperties.length > 0)
		        		for (String dataProperty : dataProperties) {
		        			
		        			Value valData = objRef.getValue(refType.fieldByName(dataProperty));
		        			
		        			if (valData != null) {
			        			if (valData instanceof StringReference)
			        			   OntologyHandler.addStringDataProperty(instanceId,
			        					   ( (StringReference) valData).value(), dataProperty);
			
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
		        				
		        				//TODO Exploit signature to understand right field
		        				Value valEl = objRefEl.getValue(refTypeEl.fieldByName("id"));
		        				System.out.println(arrayRef.getValue(i).type().signature());
		        				OntologyHandler.addObjectProperty(instanceId, ( (StringReference) valEl).value(), objProperty);
		        				
		        			}
		        		}
		        	
		        	}
	        }
		}
	}

	@Override
	public void inspectClasses(VirtualMachine vm) {
		
		Set<OWLClass> set = OntologyHandler.allClassesInOntology();
		Map<String, String> ontToAppClasses = getMapOntToAppClasses();
		Map<String, String> classToFieldId = getMapClassToFieldId();
		Map<String, String[]> classToObjProp = getMapClassToObjProp();
		Map<String, String[]> classToDataProp = getMapClassToDataProp();
	
		for (OWLClass c : set) {
			
			String classId = c.toStringID().split("#")[1];
			
			System.out.println(classId);
			System.out.println(ontToAppClasses.get(classId));
			System.out.println(classToFieldId.get(classId));
			System.out.println(classToObjProp.get(classId));
			System.out.println(classToDataProp.get(classId));
			
			if(ontToAppClasses.get(classId) != null) {
				inspectClass(vm, classId, ontToAppClasses.get(classId),
					classToFieldId.get(classId), classToObjProp.get(classId), classToDataProp.get(classId));
			}
			
		}

	}
	
	@Override
	public int getDebugPort() {
		return debugPort;
	}
	
	@Override
	public String getClassPattern() {
		return classPattern;
	}
	
	@Override
	public String getMethodName() {
		return methodName;
	}
	
	public Map<String, String> getMapOntToAppClasses() {
		
		Map<String, String> ontToAppClasses = new HashMap<>();
		ontToAppClasses.put("Person", Person.class.getName());
		ontToAppClasses.put("Painter", Painter.class.getName());
		ontToAppClasses.put("Artist", Artist.class.getName());
		ontToAppClasses.put("Artisan", Artisan.class.getName());
		ontToAppClasses.put("Sculptor", Sculptor.class.getName());
		ontToAppClasses.put("Thing", Thing.class.getName());
		ontToAppClasses.put("ArtWork", ArtWork.class.getName());
		ontToAppClasses.put("Paint", Paint.class.getName());
		ontToAppClasses.put("Product", Product.class.getName());
		ontToAppClasses.put("Sculpt", Sculpt.class.getName());
		
		return ontToAppClasses;
		
	}
	
	public Map<String, String[]> getMapClassToObjProp() {
		
		Map<String, String[]> classToProp = new HashMap<>();
		classToProp.put("Person", new String[0]);
		classToProp.put("Painter", new String[]{"paints"});
		classToProp.put("Artist", new String[]{"crafts"});
		classToProp.put("Artisan", new String[]{"produces"});
		classToProp.put("Sculptor", new String[]{"sculpts"});
		
		return classToProp;
		
	}
	
	public Map<String, String[]> getMapClassToDataProp() {
		
		Map<String, String[]> classToProp = new HashMap<>();
		classToProp.put("Person", new String[]{"name"});
		classToProp.put("Painter", new String[]{"name"});
		classToProp.put("Artist", new String[]{"name"});
		classToProp.put("Artisan", new String[]{"name"});
		classToProp.put("Sculptor", new String[]{"name"});
		classToProp.put("Thing", new String[]{"id"});
		classToProp.put("ArtWork", new String[]{"id"});
		classToProp.put("Paint", new String[]{"id"});
		classToProp.put("Product", new String[]{"id"});
		classToProp.put("Sculpt", new String[]{"id"});
		
		return classToProp;
		
	}
	
	//For each class determines name of field to be used as identifier in the IRI
	public Map<String, String> getMapClassToFieldId() {
		
		Map<String, String> classToFieldId = new HashMap<>();
		classToFieldId.put("Person", "name");
		classToFieldId.put("Painter", "name");
		classToFieldId.put("Artist", "name");
		classToFieldId.put("Artisan", "name");
		classToFieldId.put("Sculptor", "name");
		classToFieldId.put("Thing", "id");
		classToFieldId.put("ArtWork", "id");
		classToFieldId.put("Paint", "id");
		classToFieldId.put("Product", "id");
		classToFieldId.put("Sculpt", "id");
		
		return classToFieldId;
		
	}

}
