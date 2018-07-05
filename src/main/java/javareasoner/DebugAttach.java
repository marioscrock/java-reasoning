/*
 * This class has been coded thanks to snippets from:
 * - Wayne Adams, http://wayne-adams.blogspot.com/2011/10/generating-minable-event-stream-with.html
 * 	 			  http://wayne-adams.blogspot.com/2011/12/examining-variables-in-jdi.html
 * - souparno majumder, https://stackoverflow.com/questions/47939691/build-a-simple-debugger-with-jdi-to-set-breakpoints-and-retrieve-the-value-of-a
 */

package javareasoner;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
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

import javareasoner.inspect.InspectToAxiom;
import javareasoner.server.ReasoningServer;


public class DebugAttach {
	
	/**
	 * Every time the method of the class specified by inspector is executed, a breakpointEvent
	   is triggered on the last instruction and virtual machine is stopped and passed to the inspector.
	 * @param inspector The inspector for the application to debug
	 * @throws IOException
	 * @throws IllegalConnectorArgumentsException
	 * @throws InterruptedException
	 * @throws IncompatibleThreadStateException
	 * @throws AbsentInformationException
	 */
	public static void startDebug(InspectToAxiom inspector, ReasoningServer rs) 
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
		    portArg.setValue(inspector.getDebugPort());
		    VirtualMachine vm = socketConnector.attach(paramsMap);
		    System.out.println("Attached to process '" + vm.name() + "'");


	        // Create a class prepare request
		    // We want to be sure class already loaded before registering BreakpointEvent Request
	        EventRequestManager erm = vm.eventRequestManager();
	        ClassPrepareRequest r = erm.createClassPrepareRequest();
	        r.addClassFilter(inspector.getClassPattern());
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
	
	                    classType.methodsByName(inspector.getMethodName()).forEach(new Consumer<Method>() {
	                    	
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
	                	
	                	inspector.inspectClasses(vm);               
	                    
	                }
	                
	                //All threads are resumed
	                vm.resume();
	            }
	            
	        }
	     }
	}

}
