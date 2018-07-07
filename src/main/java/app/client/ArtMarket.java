package app.client;

import java.util.ArrayList;
import java.util.List;

import app.person.*;
import app.thing.*;
/**
 * Example Application related to the ArtMarket ontology.
 * @author Mario
 *
 */
public class ArtMarket {
	
	//To avoid garbage collection
	//It mimics references of the application
	private List<Person> persons = new ArrayList<>();
	private List<Thing> things = new ArrayList<>();
	
	/**
	 * Main method of the example application
	 * @param catalogue 	Integer parameter to switch modalities of execution
	 * 		1: Import first catalogue
	 * 		2: Import second catalogue
	 * 		3: Clear catalogue (delete references)
	 */
	public void startApp(int catalogue) {
		
		switch (catalogue) {
			case 1:
				System.out.println("\n****  Import Catalogue 1  ****");
				importCatalogue1();
				break;
			case 2:
				System.out.println("\n****  Import Catalogue 2  ****");
				importCatalogue2();
				break;
			case 3:
				System.out.println("\n****  Clear Catalogue  ****");
				persons.clear();
				things.clear();			
				break;
		}
		
		//To enable breakpoint before returning
		return;
			
	}
	
	/**
	 * Method to import catalogue1 instances in the app.
	 */
	private void importCatalogue1() {
		
		Painter leonardo = new Painter("Leonardo");
		persons.add(leonardo);
		Paint monnaLisa = new Paint("MonnaLisa");
		things.add(monnaLisa);
		
		leonardo.paints(monnaLisa);
		
		Sculptor bernini = new Sculptor("Bernini");
		persons.add(bernini);
		Sculpt david = new Sculpt("David");
		things.add(david);
		
		bernini.sculpts(david);
		
		Artisan belfiore = new Artisan("Belfiore");
		persons.add(belfiore);
		Product product05714 = new Product("Product05714");
		things.add(product05714);
		
		belfiore.produces(product05714);
			
	}
	
	/**
	 * Method to import catalogue2 instances in the app.
	 */
	private void importCatalogue2() {
		
		Painter caravaggio = new Painter("Caravaggio");
		persons.add(caravaggio);
		Paint sanMatteo = new Paint("Vocazione di san Matteo");
		things.add(sanMatteo);
		
		caravaggio.paints(sanMatteo);
		
		Sculpt santaTeresa = new Sculpt("Estasi di Santa Teresa");
		things.add(santaTeresa);
		
		for(Person p : persons) {
			if (p.getName().equals("Bernini")) {
				((Sculptor) p).sculpts(santaTeresa);
			}
		}
		
		Artisan travisanutto = new Artisan("Travisanutto");
		persons.add(travisanutto);
		Product mosaic134 = new Product("Mosaic134");
		things.add(mosaic134);
		
		travisanutto.produces(mosaic134);
			
	}
	
	


}
