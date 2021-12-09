/**
 * 
 */
package bsh;

import java.io.File;
import java.util.ResourceBundle;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class Source {
	
	
	public static String dir() {
		String dir = "/Users/Admin/Documents/workspace1/educate4/WebContent/WEB-INF/classes/";
		ResourceBundle rb;
		try {
    		rb = ResourceBundle.getBundle("files");
    		dir = rb.getString("bshDir");
    	} catch (java.util.MissingResourceException ex) {
    		ex.printStackTrace();
    	}
    	if ( !dir.endsWith("/")) dir += "/";
    	File filedir = new File(dir);
		if ( !filedir.exists() ) filedir.mkdir();
		return dir;
	}

}
