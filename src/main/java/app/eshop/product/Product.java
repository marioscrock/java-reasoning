package app.eshop.product;

public abstract class Product {
	
	private String id;
	private float price;
	
	public Product (String id, float price) {
		this.id = id;
		this.price = price;
		System.out.println("Adding " + id + " of class " + this.getClass().getSimpleName());
	}

	public String getIdentifier() {
		return id;
	}

	public float getPrice() {
		return price;
	}
	
}
