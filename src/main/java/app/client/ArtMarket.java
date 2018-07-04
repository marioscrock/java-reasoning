package app.client;

import java.util.ArrayList;
import java.util.List;

import app.person.*;
import app.thing.*;

public class ArtMarket {
	
	private List<Person> persons = new ArrayList<>();
	private List<Thing> things = new ArrayList<>();
	
	public void startApp() {
		
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
		
		//To enable breakpoint before returning
		return;
			
	}


}
