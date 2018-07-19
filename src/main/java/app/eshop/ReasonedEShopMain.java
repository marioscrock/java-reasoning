package app.eshop;

import java.util.Scanner;

/**
 * Main class to manage observed application.
 * To attach debugger run the class with following options and then launch a proper debugger to be attached
		-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y
 * @author Mario
 */
public class ReasonedEShopMain {
	
	/**
	 * Main method to run ReasonedArtMarketMain
	 * @param args	No args required
	 */
	public static void main(String[] args) {
		
		//Before running actual application we must be sure debugger is ready (e.g. breakpoint enabled)
		System.out.println("Is debugger ready to be attached? (y/n)");
		Scanner scan = new Scanner(System.in);
		String s = scan.next();
		scan.close();
		
		if (!s.toLowerCase().equals("y")) 
			System.out.println("No active instances check enabled!");
		
		//Actual application execution
		EShop eshop = new EShop();
		
		//Emulate an execution session
		eshop.startApp(1);
		//Clear references active users
		eshop.startApp(2);
		//Emulate other 2 execution sessions
		eshop.startApp(1);
		eshop.startApp(1);

		
		System.out.println("\nStopping app!");
		
	}

}
