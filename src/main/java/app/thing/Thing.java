package app.thing;

public abstract class Thing {
	
	private String id;
	
	public Thing (String id) {
		this.id = id;
		System.out.println("Adding " + id + " of class " + this.getClass().getSimpleName());
	}

	public String getIdentifier() {
		return id;
	}	

}
