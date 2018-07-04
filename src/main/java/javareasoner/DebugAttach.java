/*
 * This class has been coded thanks to snippets from:
 * - Wayne Adams, http://wayne-adams.blogspot.com/2011/10/generating-minable-event-stream-with.html
 * 	 			  http://wayne-adams.blogspot.com/2011/12/examining-variables-in-jdi.html
 * - souparno majumder, https://stackoverflow.com/questions/47939691/build-a-simple-debugger-with-jdi-to-set-breakpoints-and-retrieve-the-value-of-a
 */

package javareasoner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;


public class DebugAttach {
	
	/**
	 * Every time the method of the class specified is executed a breakpointEvent
	   is triggered on the last instruction and active instances are gathered to populate the ontology.
	 * @param debugPort Port of the virtual machine to debug
	 * @param classPattern The name of the class that you wish to debug
	 * @param methodName The name of the method that you want to debug
	 * @throws IOException
	 * @throws IllegalConnectorArgumentsException
	 * @throws InterruptedException
	 * @throws IncompatibleThreadStateException
	 * @throws AbsentInformationException
	 */
	public static void startDebug(int debugPort, String classPattern, String methodName) 
			throws IOException, IllegalConnectorArgumentsException, InterruptedException, IncompatibleThreadStateException, AbsentInformationException {
		
		
		VirtualMachineManager vmMgr = Bootstrap.virtualMachineManager();
		AttachingConnector socketConnector = null;
		List<AttachingConnector> attachingConnectors = vmMgr.attachingConnectors();
		for (AttachingConnector ac: attachingConnectors) {
			if (ac.transport().name().equals("dt_socket")) {
		      socketConnector = ac;
		      break;
		    }
		}

		if (socketConnector != null) {
			
		    Map<String, Argument> paramsMap = socketConnector.defaultArguments();
		    Connector.IntegerArgument portArg = (Connector.IntegerArgument)paramsMap.get("port");
		    portArg.setValue(debugPort);
		    VirtualMachine vm = socketConnector.attach(paramsMap);
		    System.out.println("Attached to process '" + vm.name() + "'");


	        // Create a class prepare request
		    // We want to be sure class already loaded before registering BreakpointEvent Request
	        EventRequestManager erm = vm.eventRequestManager();
	        ClassPrepareRequest r = erm.createClassPrepareRequest();
	        r.addClassFilter(classPattern);
	        r.enable();
	
	        EventQueue queue = vm.eventQueue();
	        while (true) {
	        	
	            EventSet eventSet = queue.remove();
	            EventIterator it = eventSet.eventIterator();
	            while (it.hasNext()) {
	            	
	                Event event = it.nextEvent();
	                
	                if (event instanceof ClassPrepareEvent) {
	                    ClassPrepareEvent evt = (ClassPrepareEvent) event;
	                    ClassType classType = (ClassType) evt.referenceType();
	
	                    classType.methodsByName(methodName).forEach(new Consumer<Method>() {
	                        @Override
	                        public void accept(Method m) {
	                            List<Location> locations = null;
	                            try {
	                                locations = m.allLineLocations();
	                            } catch (AbsentInformationException ex) {
	                                Logger.getLogger(DebugAttach.class.getName()).log(Level.SEVERE, null, ex);
	                            }
	                            // get the last line location of the function and enable the 
	                            // break point
	                            Location location = locations.get(locations.size() - 1);
	                            BreakpointRequest bpReq = erm.createBreakpointRequest(location);
	                            bpReq.enable();
	                        }
	                    });
	
	                }
	                
	                if (event instanceof BreakpointEvent) {
	                	
	                    // Disable the breakpoint event
	                    // event.request().disable();
	
	                    //ThreadReference thread = ((BreakpointEvent) event).thread();
	                    //StackFrame stackFrame = thread.frame(0);
	
	                    // Inspecting Stack Variables
	                    //Map<LocalVariable, Value> visibleVariables = (Map<LocalVariable, Value>) stackFrame.getValues(stackFrame.visibleVariables());
	                    //for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
	                    //    System.out.println(entry.getKey() + ":" + entry.getValue());
	                    //}
	                	
	                    List<String> classes = new ArrayList<>();
	                    //classes.add((Person.class).toString());
	                    classes.add("app.person.Painter");
	                    //classes.add((Artist.class).toString());
	                    //classes.add((Artisan.class).toString());
	                    //classes.add((Sculptor.class).toString());
	                    //classes.add((Thing.class).toString());
	                    //classes.add((ArtWork.class).toString());
	                    //classes.add((Paint.class).toString());
	                    //classes.add((Product.class).toString());
	                    //classes.add((Sculpt.class).toString());
	                    
	                    for (String c : classes) {
	                    	ReferenceType refType = vm.classesByName(c).get(0);
	                    	List<ObjectReference> objRefs = refType.instances(0);
	                    	
	                    	for (ObjectReference objRef : objRefs) {
	                    		
	                    		Value val = objRef.getValue(refType.fieldByName("name"));
	                    		System.out.println(( (StringReference) val).value());
	                    		
	                    		ObjectReference objRefList = (ObjectReference) objRef.getValue(refType.fieldByName("paints"));
	                    		ReferenceType refTypeList = objRefList.referenceType();
	                    		int size = ((IntegerValue) objRefList.getValue(refTypeList.fieldByName("size"))).value();
	                    		ArrayReference arrayRef = (ArrayReference) objRefList.getValue(refTypeList.fieldByName("elementData"));
	                    		for(int i = 0; i < size; i ++) {
	                    			
	                    			ObjectReference objRefEl = (ObjectReference) arrayRef.getValue(i);
	                    			ReferenceType refTypeEl = objRefEl.referenceType();
	                    			Value valEl = objRefEl.getValue(refTypeEl.fieldByName("identifier"));
		                    		System.out.println(( (StringReference) valEl).value());
	                    		}
	                    
	                    		
	                    	}
	                    	
	                    }
	                    // Inspecting Static and Instance Variables
	                    //Get the <this> object
	                    //ObjectReference objRef = stackFrame.thisObject();
	                    //Reference Type refers to the class of the object, Object Reference to the instance
	                    //ReferenceType ref = objRef.referenceType();
	                    //List<Field> fields = ref.allFields();
	                    //for(Field f : fields) {
	                    //	Value val = objRef.getValue(f);
	                    //}
	                    
	                    
	                }
	                
	                //All threads are resumed
	                vm.resume();
	            }
	            
	        }
	     }
	}

}
