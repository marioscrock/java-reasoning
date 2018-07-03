package app.thing;

import client.OntologyHandler;

public abstract class Thing {
	
	private String identifier;
	
	public Thing (String id) {
		this.identifier = id;
		OntologyHandler.createIndividual(id, this.getClass().getSimpleName());
		OntologyHandler.addStringDataProperty(identifier, identifier, "id");
	}

	public String getIdentifier() {
		return identifier;
	}	

}
