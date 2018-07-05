package app.client;

import java.util.Scanner;


public class ReasonedArtMarketMain {
	
	private static ArtMarket artMarket;
	
	public static void main(String[] args) {
		
		//To attach debugger run the class with following options and then launch a proper debugger to be attached
		//-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y
		System.out.println("Is listening for debugger to be attached? (y/n)");
		Scanner scan = new Scanner(System.in);
		String s = scan.next();
		scan.close();
		
		if (!s.toLowerCase().equals("y")) 
			System.out.println("No active instances check enabled!");
		
		artMarket = new ArtMarket();
		
		//Catalogue1
		artMarket.startApp(1);
		//Catalogue2
		artMarket.startApp(2);
		
		System.out.println("\nStopping app!");
		
	}

}
