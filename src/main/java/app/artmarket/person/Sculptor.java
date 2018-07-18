package app.artmarket.person;

import java.util.ArrayList;
import java.util.List;

import app.artmarket.thing.Sculpt;

public class Sculptor extends Artist {
	
	private List<Sculpt> sculpts;
	
	public Sculptor(String name) {
		super(name);
		sculpts = new ArrayList<>();
	}
	
	public void sculpts(Sculpt sculpt) {
		sculpts.add(sculpt);
		crafts(sculpt);
	}

	public List<Sculpt> getSculpts() {
		return sculpts;
	}
	
}
