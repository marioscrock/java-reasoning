package app.eshop.user;

import java.util.ArrayList;
import java.util.List;

import app.eshop.product.Product;

public abstract class User {
	
	private String username;
	private List<Product> interestedIn;
	
	public User (String username) {
		this.username = username;
		interestedIn = new ArrayList<>();
		System.out.println("Adding " + username + " of class " + this.getClass().getSimpleName());
	}

	public String getUsername() {
		return username;
	}

	public List<Product> getInterestedIn() {
		return interestedIn;
	}
	
	public void interestedIn(Product p) {
		if (!interestedIn.contains(p))
			interestedIn.add(p);
	}
	
}
