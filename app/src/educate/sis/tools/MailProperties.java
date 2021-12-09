package educate.sis.tools;
import java.util.ResourceBundle;

public class MailProperties {
	
	private static ResourceBundle rb = ResourceBundle.getBundle("mail");
	
	public static void setRb(ResourceBundle rb){
		MailProperties.rb = rb;
	}
	
	public static ResourceBundle getRb(){
		return rb;
	}
	
	public static String getHostName(){
		return rb.getString("hostName");
	}
	
	public static String getPort(){
		return rb.getString("portNum");
	}
	
	public static String getUserName(){
		return rb.getString("userName");
	}
	
	public static String getPassWord(){
		return rb.getString("password");
	}

}
