package app.person;

import java.util.ArrayList;
import java.util.List;

import app.thing.ArtWork;

public class Artist extends Person {

	private List<ArtWork> crafts = new ArrayList<>();
	
	public Artist(String name) {
		super(name);
	}
	
	public void crafts(ArtWork artwork) {
		crafts.add(artwork);
		//OntologyHandler.addObjectProperty(this.getName(), artwork.getIdentifier(), "crafts");
	}

	public List<ArtWork> getCrafts() {
		return crafts;
	}

}
