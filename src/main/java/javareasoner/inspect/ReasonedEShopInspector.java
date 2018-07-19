package javareasoner.inspect;

import java.util.HashMap;
import java.util.HashSet;
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
	protected Map<String, HashSet<Pair<String,String>>> getMapClassToObjProp() {
		
		Map<String, HashSet<Pair<String,String>>> classToProp = new HashMap<>();
		
		HashSet<Pair<String,String>> setUser = new HashSet<>();
		setUser.add(new Pair<String, String>("interestedIn", "interestedIn"));
		classToProp.put("User", setUser);
		classToProp.put("Guest", setUser);
		
		HashSet<Pair<String,String>> setCustomer = new HashSet<>();
		setCustomer.add(new Pair<String, String>("interestedIn", "interestedIn"));
		setCustomer.add(new Pair<String, String>("productOnOffer", "productOnOffer"));
		classToProp.put("Customer", setCustomer);
		
		HashSet<Pair<String,String>> setSimpleCustomer = new HashSet<>();
		setSimpleCustomer.addAll(setCustomer);
		setSimpleCustomer.add(new Pair<String, String>("perc10Offer", "perc10Offer"));	
		classToProp.put("SimpleCustomer", setSimpleCustomer);
		
		HashSet<Pair<String,String>> setVIPCustomer = new HashSet<>();
		setVIPCustomer.addAll(setSimpleCustomer);
		setVIPCustomer.add(new Pair<String, String>("perc20Offer", "perc20Offer"));	
		classToProp.put("SimpleCustomer", setVIPCustomer);
		classToProp.put("VIPCustomer", setVIPCustomer);
		
		return classToProp;
		
	}
	
	@Override
	protected Map<String, HashSet<Pair<String,String>>> getMapClassToDataProp() {
		
		Map<String, HashSet<Pair<String,String>>> classToProp = new HashMap<>();
		
		HashSet<Pair<String,String>> setUser = new HashSet<>();
		setUser.add(new Pair<String, String>("username", "username"));

		classToProp.put("User", setUser);
		classToProp.put("Customer", setUser);
		classToProp.put("Guest", setUser);
		classToProp.put("SimpleCustomer", setUser);
		classToProp.put("VIPCustomer", setUser);
		
		HashSet<Pair<String,String>> setProduct = new HashSet<>();
		setProduct.add(new Pair<String, String>("id", "id"));
		setProduct.add(new Pair<String, String>("price", "price"));
		
		classToProp.put("Product", setProduct);
		classToProp.put("ProductA", setProduct);
		classToProp.put("ProductB", setProduct);
		classToProp.put("ProductC", setProduct);
		
		return classToProp;
		
	}

}
