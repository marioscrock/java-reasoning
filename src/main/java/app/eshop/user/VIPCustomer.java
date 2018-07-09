package app.eshop.user;

import java.util.ArrayList;
import java.util.List;

import app.eshop.product.Product;

public class VIPCustomer extends Customer {
	
	private List<Product> perc10Offer = new ArrayList<>();
	private List<Product> perc20Offer = new ArrayList<>();
	
	public VIPCustomer(String username) {
		super(username);
	}

	@Override
	public void productOnOffer(Product p, boolean vip) {
		
		super.productOnOffer(p, vip);	
		
		if (vip) {
			if (perc10Offer.contains(p))
				perc10Offer.remove(p);
			if (!perc20Offer.contains(p)) 
				perc20Offer.add(p);
		} else {
			if (!perc10Offer.contains(p)) 
				perc10Offer.add(p);
		}
		
	}

}
