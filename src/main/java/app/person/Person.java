package app.person;

import client.OntologyHandler;

public abstract class Person {
	
	private String name;
	
	public Person (String name) {
		this.name = name;
		OntologyHandler.createIndividual(name, this.getClass().getSimpleName());
		OntologyHandler.addStringDataProperty(name, name, "name");
	}

	public String getName() {
		return name;
	}	

}
