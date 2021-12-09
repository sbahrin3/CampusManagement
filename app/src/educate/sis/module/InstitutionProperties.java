package educate.sis.module;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.velocity.VelocityContext;

public class InstitutionProperties {
	
	private static InstitutionProperties instance = null;
	private static ResourceBundle rb;
	private static String type = "";
	private static Hashtable<String, String> titles = new Hashtable<String, String>();
	
	public static InstitutionProperties getInstance() {
		if ( instance == null ) instance = new InstitutionProperties();
		return instance;
	}
	
	private InstitutionProperties() {
		prepareLabels();
	}

	private static String getType() {
		return type;
	}
	
	private void prepareLabels() {
		try {
			rb = ResourceBundle.getBundle("InstitutionLabels");
			for ( Enumeration keys = rb.getKeys(); keys.hasMoreElements(); ) {
				String key = (String) keys.nextElement();
				String value = rb.getString(key);
				titles.put(key, value);
			}
		} catch ( Exception  e ) {
			e.printStackTrace();
		}
	}
	
	public String getTitle(String name) {
		return titles.get(name) != null ? titles.get(name) : "";
		
	}
	
	public Hashtable<String, String> getTitles() {
		return titles;
	}
	
	public static void setInstitutionType(VelocityContext context) {
		if ( instance == null ) getInstance();
		if ( context != null ) {
			for ( Enumeration keys = titles.keys(); keys.hasMoreElements(); ) {
				String key = (String) keys.nextElement();
				context.put(key, titles.get(key));
			}
		}
		else {
			for ( Enumeration keys = titles.keys(); keys.hasMoreElements(); ) {
				String key = (String) keys.nextElement();
				System.out.println(key + " = " + titles.get(key));
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		InstitutionProperties.getInstance();
		InstitutionProperties.setInstitutionType(null);
	}

}
