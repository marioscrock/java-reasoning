package app.eshop.user;

import java.util.ArrayList;
import java.util.List;

import app.eshop.product.Product;

public abstract class Customer extends User {
	
	private List<Product> productOnOffer;
	
	public Customer(String username) {
		super(username);
		productOnOffer = new ArrayList<>();
	}
	
	public void productOnOffer(Product p, boolean vip) {
		if (!productOnOffer.contains(p)) 
			productOnOffer.add(p);
	}

	public List<Product> getProductOnOffer() {
		return productOnOffer;
	}

}
