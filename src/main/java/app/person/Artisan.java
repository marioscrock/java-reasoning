package app.person;

import java.util.ArrayList;
import java.util.List;

import app.thing.Product;
import client.OntologyHandler;

public class Artisan extends Person {

	private List<Product> produces = new ArrayList<>();
	
	public Artisan(String name) {
		super(name);
	}
	
	public void produces(Product product) {
		produces.add(product);
		OntologyHandler.addObjectProperty(this.getName(), product.getIdentifier(), "produces");
	}

	public List<Product> getProduces() {
		return produces;
	}


	
}
