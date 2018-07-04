package app.client;

import java.util.Scanner;

public class ReasonedArtMarket {
	
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

}
