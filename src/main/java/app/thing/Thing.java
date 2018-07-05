package app.thing;

public abstract class Thing {
	
	private String id;
	
	public Thing (String id) {
		this.id = id;
		//OntologyHandler.createIndividual(id.replaceAll("\\s","-"), this.getClass().getSimpleName());
		//OntologyHandler.addStringDataProperty(identifier, identifier, "id");
	}

	public String getIdentifier() {
		return id;
	}	

}
