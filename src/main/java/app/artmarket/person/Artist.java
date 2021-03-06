package app.artmarket.person;

import java.util.ArrayList;
import java.util.List;

import app.artmarket.thing.ArtWork;

public abstract class Artist extends Person {

	private List<ArtWork> crafts;
	
	public Artist(String name) {
		super(name);
		crafts = new ArrayList<>();
	}
	
	public void crafts(ArtWork artwork) {
		crafts.add(artwork);
	}

	public List<ArtWork> getCrafts() {
		return crafts;
	}

}
