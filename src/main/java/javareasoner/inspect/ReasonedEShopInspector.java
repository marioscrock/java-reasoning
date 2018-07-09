package javareasoner.inspect;

import java.util.HashMap;
import java.util.Map;

import app.eshop.EShop;
import app.eshop.user.*;
import app.eshop.product.*;
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
		classToFieldId.put("User", "username");
		classToFieldId.put("Customer", "username");
		classToFieldId.put("Guest", "username");
		classToFieldId.put("SimpleCustomer", "username");
		classToFieldId.put("VIPCustomer", "username");
		classToFieldId.put("Product", "id");
		classToFieldId.put("ProductA", "id");
		classToFieldId.put("ProductB", "id");
		classToFieldId.put("ProductC", "id");
	
		return classToFieldId;
		
	}
	
	@Override
	protected Map<String, String> getMapOntToAppClasses() {
		
		Map<String, String> ontToAppClasses = new HashMap<>();
		ontToAppClasses.put("User", User.class.getName());
		ontToAppClasses.put("Customer", Customer.class.getName());
		ontToAppClasses.put("Guest", Guest.class.getName());
		ontToAppClasses.put("VIPCustomer", VIPCustomer.class.getName());
		ontToAppClasses.put("SimpleCustomer", SimpleCustomer.class.getName());
		ontToAppClasses.put("Product", Product.class.getName());
		ontToAppClasses.put("ProductA", ProductA.class.getName());
		ontToAppClasses.put("ProductB", ProductB.class.getName());
		ontToAppClasses.put("ProductC", ProductC.class.getName());
		
		return ontToAppClasses;
		
	}
	
	@Override
	protected Map<String, String[]> getMapClassToObjProp() {
		
		Map<String, String[]> classToProp = new HashMap<>();
		classToProp.put("User", new String[]{"interestedIn"});
		classToProp.put("Guest", new String[]{"interestedIn"});
		classToProp.put("Customer", new String[]{"productOnOffer", "interestedIn"});
		classToProp.put("SimpleCustomer", new String[]{"productOnOffer", "perc10Offer", "interestedIn"});
		classToProp.put("VIPCustomer", new String[]{"productOnOffer", "perc10Offer", "interestedIn"});
		
		return classToProp;
		
	}
	
	@Override
	protected Map<String, String[]> getMapClassToDataProp() {
		
		Map<String, String[]> classToProp = new HashMap<>();
		classToProp.put("User", new String[]{"username"});
		classToProp.put("Customer", new String[]{"username"});
		classToProp.put("Guest", new String[]{"username"});
		classToProp.put("SimpleCustomer", new String[]{"username"});
		classToProp.put("VIPCustomer", new String[]{"username"});
		classToProp.put("Product", new String[]{"id", "price"});
		classToProp.put("ProductA", new String[]{"id", "price"});
		classToProp.put("ProductB", new String[]{"id", "price"});
		classToProp.put("ProductC", new String[]{"id", "price"});
		
		return classToProp;
		
	}

}
