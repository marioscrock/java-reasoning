package app.client;

import java.util.Scanner;

/**
 * Main class to manage observed application.
 * To attach debugger run the class with following options and then launch a proper debugger to be attached
		-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y
 * @author Mario
 */
public class ReasonedArtMarketMain {
	
	/**
	 * Main method to run ReasonedArtMarketMain
	 * @param args No args required
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
		ArtMarket artMarket = new ArtMarket();
		
		//Import Catalogue1
		artMarket.startApp(1);
		//Clear object saved
		artMarket.startApp(3);
		//Import Catalogue1 and Catalogue2
		artMarket.startApp(1);
		artMarket.startApp(2);
		
		System.out.println("\nStopping app!");
		
	}

}
