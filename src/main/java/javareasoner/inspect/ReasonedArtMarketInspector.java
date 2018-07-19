package javareasoner.inspect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import app.artmarket.ArtMarket;
import app.artmarket.person.*;
import app.artmarket.thing.*;
import javareasoner.ontology.OntologyHandler;

/**
 * Extension of InspectToAxiom class for ArtMarket application and ontology.
 * @author Mario
 *
 */
public class ReasonedArtMarketInspector extends InspectToAxiom {
	
	private int debugPort = 8000;
	private String classPattern = ArtMarket.class.getName();
	private String methodName = "startApp";
	
	/**
	 * Constructor of the class ReasonedArtMarketInspector. It implements super() constructor.
	 * @param oh
	 */
	public ReasonedArtMarketInspector(OntologyHandler oh) {
		super(oh);
	}
	
	@Override
	public int getDebugPort() {
		return debugPort;
	}
	
	@Override
	public String getClassPattern() {
		return classPattern;
	}
	
	@Override
	public String getMethodName() {
		return methodName;
	}
	
	@Override
	protected Map<String, String> getMapClassToFieldId() {
		
		Map<String, String> classToFieldId = new HashMap<>();
		classToFieldId.put("Person", "name");
		classToFieldId.put("Painter", "name");
		classToFieldId.put("Artist", "name");
		classToFieldId.put("Artisan", "name");
		classToFieldId.put("Sculptor", "name");
		classToFieldId.put("Thing", "id");
		classToFieldId.put("ArtWork", "id");
		classToFieldId.put("Paint", "id");
		classToFieldId.put("Product", "id");
		classToFieldId.put("Sculpt", "id");
		
		return classToFieldId;
		
	}
	
	@Override
	protected Map<String, String> getMapOntToAppClasses() {
		
		Map<String, String> ontToAppClasses = new HashMap<>();
		ontToAppClasses.put("Person", Person.class.getName());
		ontToAppClasses.put("Painter", Painter.class.getName());
		ontToAppClasses.put("Artist", Artist.class.getName());
		ontToAppClasses.put("Artisan", Artisan.class.getName());
		ontToAppClasses.put("Sculptor", Sculptor.class.getName());
		ontToAppClasses.put("Thing", Thing.class.getName());
		ontToAppClasses.put("ArtWork", ArtWork.class.getName());
		ontToAppClasses.put("Paint", Paint.class.getName());
		ontToAppClasses.put("Product", Product.class.getName());
		ontToAppClasses.put("Sculpt", Sculpt.class.getName());
		
		return ontToAppClasses;
		
	}
	
	@Override
	protected Map<String, HashSet<Pair<String,String>>> getMapClassToObjProp() {
		
		Map<String, HashSet<Pair<String,String>>> classToProp = new HashMap<>();
		classToProp.put("Person", new HashSet<>());
		
		HashSet<Pair<String,String>> setPainter = new HashSet<>();
		setPainter.add(new Pair<String, String>("paints", "paints"));
		classToProp.put("Painter", setPainter);
		
		HashSet<Pair<String,String>> setArtist = new HashSet<>();
		setArtist.add(new Pair<String, String>("crafts", "crafts"));
		classToProp.put("Artist", setArtist);
		
		HashSet<Pair<String,String>> setArtisan = new HashSet<>();
		setArtisan.add(new Pair<String, String>("produces", "produces"));
		classToProp.put("Artisan", setArtisan);
		
		HashSet<Pair<String,String>> setSculptor = new HashSet<>();
		setSculptor.add(new Pair<String, String>("sculpts", "sculpts"));
		classToProp.put("Sculptor", setSculptor);
		
		return classToProp;
		
	}
	
	@Override
	protected Map<String, HashSet<Pair<String,String>>> getMapClassToDataProp() {
		
		Map<String, HashSet<Pair<String,String>>> classToProp = new HashMap<>();
		
		HashSet<Pair<String,String>> setName = new HashSet<>();
		setName.add(new Pair<String, String>("name", "name"));
		classToProp.put("Person", setName);
		classToProp.put("Painter", setName);
		classToProp.put("Artist", setName);
		classToProp.put("Artisan", setName);
		classToProp.put("Sculptor", setName);
		
		HashSet<Pair<String,String>> setId = new HashSet<>();
		setId.add(new Pair<String, String>("id", "id"));
		classToProp.put("Thing", setId);
		classToProp.put("ArtWork", setId);
		classToProp.put("Paint", setId);
		classToProp.put("Product", setId);
		classToProp.put("Sculpt", setId);
		
		return classToProp;
		
	}

}
