package app.artmarket.person;

import java.util.ArrayList;
import java.util.List;

import app.artmarket.thing.Product;

public class Artisan extends Person {

	private List<Product> produces;
	
	public Artisan(String name) {
		super(name);
		produces = new ArrayList<>();
	}
	
	public void produces(Product product) {
		produces.add(product);
	}

	public List<Product> getProduces() {
		return produces;
	}


	
}
