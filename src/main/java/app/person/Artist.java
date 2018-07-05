package app.person;

import java.util.ArrayList;
import java.util.List;

import app.thing.ArtWork;

public abstract class Artist extends Person {

	private List<ArtWork> crafts = new ArrayList<>();
	
	public Artist(String name) {
		super(name);
	}
	
	public void crafts(ArtWork artwork) {
		crafts.add(artwork);
	}

	public List<ArtWork> getCrafts() {
		return crafts;
	}

}
