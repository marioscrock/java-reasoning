package debugger;

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
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;


public class MainDebugAttach {

	public static void main(String[] args) throws IOException, IllegalConnectorArgumentsException, AbsentInformationException, InterruptedException, VMStartException, IncompatibleThreadStateException {
		
		int debugPort = 8000;
		//Every time the method of the class specified is executed a breakpointEvent
		//is triggered on the last instruction and active instances are gathered to populate the ontology
		String classPattern = "client.Main"; // <the name of the class that you wish to debug>
		String methodName = "startApp"; // <the name of the method that you want to debug>
		
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


	        // create a class prepare request
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
	                                Logger.getLogger(MainDebugAttach.class.getName()).log(Level.SEVERE, null, ex);
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
	                    // disable the breakpoint event
	                    //event.request().disable();
	
	                    ThreadReference thread = ((BreakpointEvent) event).thread();
	                    StackFrame stackFrame = thread.frame(0);
	
	                    // print all the visible variables with the respective values
	                    Map<LocalVariable, Value> visibleVariables = (Map<LocalVariable, Value>) stackFrame.getValues(stackFrame.visibleVariables());
	                    for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
	                        System.out.println(entry.getKey() + ":" + entry.getValue());
	                    }
	                }
	                vm.resume();
	            }
	            
	        }
	        }
	  }

}
