package app.eshop.user;

import java.util.ArrayList;
import java.util.List;

import app.eshop.product.Product;

public class SimpleCustomer extends Customer {
	
	private List<Product> perc10Offer = new ArrayList<>();
	
	public SimpleCustomer(String username) {
		super(username);
	}
	
	@Override
	public void productOnOffer(Product p, boolean vip) {
		super.productOnOffer(p, vip);
		if (!perc10Offer.contains(p)) 
			perc10Offer.add(p);
	}

}
