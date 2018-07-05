package app.person;

import java.util.ArrayList;
import java.util.List;

import app.thing.Sculpt;

public class Sculptor extends Artist {
	
	private List<Sculpt> sculpts = new ArrayList<>();
	
	public Sculptor(String name) {
		super(name);
	}
	
	public void sculpts(Sculpt sculpt) {
		sculpts.add(sculpt);
	}

	public List<Sculpt> getSculpts() {
		return sculpts;
	}
	
}
