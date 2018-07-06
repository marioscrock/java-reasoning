package app.client;

import java.util.ArrayList;
import java.util.List;

import app.person.*;
import app.thing.*;

public class ArtMarket {
	
	//To avoid garbage collection
	//It mimics references of the application
	private List<Person> persons = new ArrayList<>();
	private List<Thing> things = new ArrayList<>();
	
	public void startApp(int catalogue) {
		
		switch (catalogue) {
			case 1:
				importCatalogue1();
				break;
			case 2:
				persons.clear();
				things.clear();
				importCatalogue2();
				break;
		}
		
		//To enable breakpoint before returning
		return;
			
	}
	
	public void importCatalogue1() {
		
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

	public void importCatalogue2() {
		
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
