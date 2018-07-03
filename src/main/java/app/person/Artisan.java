package app.person;

import java.util.ArrayList;
import java.util.List;

import app.thing.Product;

public class Artisan extends Person {
	
	private List<Product> produces = new ArrayList<>();
	
	public void produces(Product product) {
		produces.add(product);
	}

	public List<Product> getProduces() {
		return produces;
	}


	
}
