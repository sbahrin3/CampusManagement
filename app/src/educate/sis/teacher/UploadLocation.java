package educate.sis.teacher;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class UploadLocation {
	
	public static String getDir(String locationType) {
		String dir = "/";
		try {
			dir = ResourceBundle.getBundle("portal-services").getString(locationType);
		} catch ( MissingResourceException e ) {
			System.out.println("portal-services.properties NOT FOUND!");
		} catch ( NullPointerException e ) {
			System.out.println("NullPointerException: " + locationType);
		}
		return dir;
	}	

}
