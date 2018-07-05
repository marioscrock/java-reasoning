package app.thing;

public abstract class Thing {
	
	private String id;
	
	public Thing (String id) {
		this.id = id;
	}

	public String getIdentifier() {
		return id;
	}	

}
