package app.artmarket.person;

import java.util.ArrayList;
import java.util.List;

import app.artmarket.thing.Paint;

public class Painter extends Artist {

	private List<Paint> paints;
	
	public Painter(String name) {
		super(name);
		paints = new ArrayList<>();
	}

	public void paints(Paint paint) {
		paints.add(paint);
		crafts(paint);
	}
	
	public List<Paint> getPaints() {
		return paints;
	}
	
}
