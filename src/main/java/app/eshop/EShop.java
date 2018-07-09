package app.eshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import app.eshop.product.*;
import app.eshop.user.*;
/**
 * Example Application related to the EShop ontology.
 * @author Mario
 *
 */
public class EShop {
	
	//To avoid garbage collection
	//It mimics references of the application
	private List<Guest> guests = new ArrayList<>();
	private List<Customer> customers = new ArrayList<>();
	private List<Product> products = new ArrayList<>();
	
	/**
	 * Main method of the example application
	 * @param session 	Integer parameter to switch modalities of execution
	 * 		1: Execute first session
	 * 		2: Execute second session
	 * 		3: Clear data active users (delete references)
	 */
	public void startApp(int session) {
		
		switch (session) {
			case 1:
				System.out.println("\n****  Execution session 1  ****");
				execSession1();
				break;
			case 2:
				System.out.println("\n****  Execution session 2  ****");
				execSession2();
				break;
			case 3:
				System.out.println("\n****  Deleting Data Active Users  ****");
				customers.clear();
				guests.clear();		
				break;
		}
		
		//To enable breakpoint before returning
		return;
			
	}
	
	/**
	 * Method to emulate execution session 1.
	 */
	private void execSession1() {
		
		Product pA0567 = new ProductA("pA0567", 20);
		products.add(pA0567);
		Product pA0568 = new ProductA("pA0568", 20);
		products.add(pA0568);
		Product pA0569 = new ProductA("pA0569", 20);
		products.add(pA0569);
		
		Product pB0343 = new ProductB("pB0343", 40);
		products.add(pB0343);
		Product pB0344 = new ProductB("pB0344", 40);
		products.add(pB0344);
		Product pB0345 = new ProductB("pB0345", 40);
		products.add(pB0345);
		
		Product pC0117 = new ProductA("pC0117", 60);
		products.add(pC0117);
		Product pC0118 = new ProductA("pC0118", 60);
		products.add(pC0118);
		Product pC0119 = new ProductA("pC0119", 60);
		products.add(pC0119);
		
		Guest guest = new Guest();
		guests.add(guest);
		
		SimpleCustomer username1 = new SimpleCustomer("username1");
		customers.add(username1);
		SimpleCustomer username2 = new SimpleCustomer("username2");
		customers.add(username2);
		SimpleCustomer username3 = new SimpleCustomer("username3");
		customers.add(username3);
		
		VIPCustomer vip_username1 = new VIPCustomer("vip_username1");
		customers.add(vip_username1);
		VIPCustomer vip_username2 = new VIPCustomer("vip_username2");
		customers.add(vip_username2);
		
		for(int i=0; i < customers.size() * 2; i++) {
			Random r = new Random();
			Customer c = customers.get(r.nextInt(customers.size()));
			Product p = products.get(r.nextInt(products.size()));
			c.productOnOffer(p, r.nextBoolean());
		}
		
			
	}
	
	/**
	 * Method to emulate execution session 2.
	 */
	private void execSession2() {
		

			
	}
	
	


}
