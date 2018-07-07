package app.person;

public abstract class Person {
	
	private String name;
	
	public Person (String name) {
		this.name = name;
		System.out.println("Adding " + name + " of class " + this.getClass().getSimpleName());
	}

	public String getName() {
		return name;
	}	

}
