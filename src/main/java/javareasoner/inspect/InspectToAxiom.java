package javareasoner.inspect;

import com.sun.jdi.VirtualMachine;

public interface InspectToAxiom {
	
	public void inspectClass(VirtualMachine vm, String ontClassId, String className,
			String fieldId, String[] objProperties, String[] dataProperties);
	
	public void inspectClasses(VirtualMachine vm);
	
	/**
	 * Get the port of the virtual machine to debug
	 * @return int Port of the virtual machine to debug
	 */
	public int getDebugPort();
	
	/**
	 * Get the name of the class to debug
	 * @return String The name of the class that you wish to debug
	 */
	public String getClassPattern();
	
	/**
	 * Get the name of the method to debug
	 * @return String The name of the method to debug
	 */
	public String getMethodName();
	
}
