package app.person;

import java.util.ArrayList;
import java.util.List;

import app.thing.Paint;
import client.OntologyHandler;

public class Painter extends Artist {

	private List<Paint> paints = new ArrayList<>();
	
	public Painter(String name) {
		super(name);
	}

	public void paints(Paint paint) {
		paints.add(paint);
		OntologyHandler.addObjectProperty(this.getName(), paint.getIdentifier(), "paints");
	}
	
	public List<Paint> getPaints() {
		return paints;
	}
	
}
