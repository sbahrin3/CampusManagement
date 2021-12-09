package educate.fusionchart;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ChartServlet  extends HttpServlet {
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		Map<String, String> properties = getProperties(request, out);		
		String classname = properties.get("ClassName");
		Chart x = new Chart(); 
		ChartData data = null;
		try {
			Class klazz = Class.forName(classname);
			data = (ChartData) klazz.newInstance();
			data.useProperties(properties);
			data.createValues(x);
			out.println(x.getDataXML());	
		} catch (ClassNotFoundException e) {
			e.printStackTrace(out);
		} catch (InstantiationException e) {
			e.printStackTrace(out);
		} catch (IllegalAccessException e) {
			e.printStackTrace(out);
		}
	}

	private Map<String, String> getProperties(HttpServletRequest request, PrintWriter out) {
		ResourceBundle bundle = ResourceBundle.getBundle("xmlchart");
		String path = bundle.getString("path");
		String pathInfo = request.getPathInfo();
        pathInfo = pathInfo != null ? pathInfo.substring(1) : ""; //get rid of the first '/'
		String filename = pathInfo != null ? pathInfo : "";
		filename = path + "/" + filename;
		
		Map<String, String> properties = new HashMap<String, String>();
	    try {
	        BufferedReader in = new BufferedReader(new FileReader(filename));
	        String str;
	        while ((str = in.readLine()) != null) {
	        	String key = str.substring(0, str.indexOf("="));
	        	String value = str.substring(str.indexOf("=")+1);
	        	properties.put(key, value);
	        }
	        in.close();
	    } catch (IOException e) {
	    	e.printStackTrace(out);
	    }
		return properties;
	}



}
