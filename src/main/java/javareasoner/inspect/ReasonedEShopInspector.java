package javareasoner.inspect;

import java.util.HashMap;
import java.util.Map;

import app.artmarket.ArtMarket;

import app.eshop.EShop;
import javareasoner.ontology.OntologyHandler;

/**
 * Extension of InspectToAxiom class for ArtMarket application and ontology.
 * @author Mario
 *
 */
public class ReasonedEShopInspector extends InspectToAxiom {
	
	private int debugPort = 8000;
	private String classPattern = EShop.class.getName();
	private String methodName = "startApp";
	
	/**
	 * Constructor of the class ReasonedArtMarketInspector. It implements super() constructor.
	 * @param oh
	 */
	public ReasonedEShopInspector(OntologyHandler oh) {
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
//		classToFieldId.put("Person", "name");
//		classToFieldId.put("Painter", "name");
//		classToFieldId.put("Artist", "name");
//		classToFieldId.put("Artisan", "name");
//		classToFieldId.put("Sculptor", "name");
//		classToFieldId.put("Thing", "id");
//		classToFieldId.put("ArtWork", "id");
//		classToFieldId.put("Paint", "id");
//		classToFieldId.put("Product", "id");
//		classToFieldId.put("Sculpt", "id");
//		
		return classToFieldId;
		
	}
	
	@Override
	protected Map<String, String> getMapOntToAppClasses() {
		
		Map<String, String> ontToAppClasses = new HashMap<>();
//		ontToAppClasses.put("Person", Person.class.getName());
//		ontToAppClasses.put("Painter", Painter.class.getName());
//		ontToAppClasses.put("Artist", Artist.class.getName());
//		ontToAppClasses.put("Artisan", Artisan.class.getName());
//		ontToAppClasses.put("Sculptor", Sculptor.class.getName());
//		ontToAppClasses.put("Thing", Thing.class.getName());
//		ontToAppClasses.put("ArtWork", ArtWork.class.getName());
//		ontToAppClasses.put("Paint", Paint.class.getName());
//		ontToAppClasses.put("Product", Product.class.getName());
//		ontToAppClasses.put("Sculpt", Sculpt.class.getName());
//		
		return ontToAppClasses;
		
	}
	
	@Override
	protected Map<String, String[]> getMapClassToObjProp() {
		
		Map<String, String[]> classToProp = new HashMap<>();
//		classToProp.put("Person", new String[0]);
//		classToProp.put("Painter", new String[]{"paints"});
//		classToProp.put("Artist", new String[]{"crafts"});
//		classToProp.put("Artisan", new String[]{"produces"});
//		classToProp.put("Sculptor", new String[]{"sculpts"});
//		
		return classToProp;
		
	}
	
	@Override
	protected Map<String, String[]> getMapClassToDataProp() {
		
		Map<String, String[]> classToProp = new HashMap<>();
//		classToProp.put("Person", new String[]{"name"});
//		classToProp.put("Painter", new String[]{"name"});
//		classToProp.put("Artist", new String[]{"name"});
//		classToProp.put("Artisan", new String[]{"name"});
//		classToProp.put("Sculptor", new String[]{"name"});
//		classToProp.put("Thing", new String[]{"id"});
//		classToProp.put("ArtWork", new String[]{"id"});
//		classToProp.put("Paint", new String[]{"id"});
//		classToProp.put("Product", new String[]{"id"});
//		classToProp.put("Sculpt", new String[]{"id"});
		
		return classToProp;
		
	}

}
