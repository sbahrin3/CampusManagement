/**
 * 
 */
package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import lebah.portal.velocity.VTemplate;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class CollectionReportPDFModule  extends VTemplate {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/collectionReport";
	//private static String[] paymentModes = {"Cash", "Check", "Online", "Credit Card", "Bank-In", "Direct Debit", "Telegraph Transfer", "Bank Draft"};
	private static String[] paymentModes = {"Cash", "Check", "Credit Card", "Bank-In"};

	public Template doTemplate() throws Exception {
		setShowVM(false);
		
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
        String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
        String serverUrl = http + server;
        context.put("serverUrl", serverUrl);
        String uri = request.getRequestURI();
        String appname = uri.substring(1);
        appname = appname.substring(0, appname.indexOf("/"));
        context.put("appUrl", serverUrl.concat("/").concat(appname));     
        
        String portal_username = request.getParameter("portal_username");
        context.put("portal_username", portal_username);
        
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		context.put("print", true);
		Template template = engine.getTemplate(getCollection());	
		return template;		
	}

	/**
	 * @return
	 */
	private String getCollection() throws Exception {
		context.put("paymentModes", paymentModes);
		
		
		String _date = getParam("date");
		Date date =  new Date();
		try {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(_date);
		} catch ( Exception e ) {
			
		}
		context.put("date", date);
		
		int dayDiff = 0;
		try {
			dayDiff = Integer.parseInt(getParam("dayDiff"));
		} catch ( Exception e ) {
			
		}
		
		System.out.println("report dayDiff = " + dayDiff);
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		//c.add(Calendar.DATE, dayDiff);
		
		CollectionReportModule.createCollectionReport(db, context, c.getTime());	
		
		return path + "/print_template.vm";
	}

}
