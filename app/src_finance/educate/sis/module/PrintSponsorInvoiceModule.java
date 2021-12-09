package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.velocity.Template;

import educate.sis.finance.entity.SponsorInvoice;
import lebah.portal.velocity.VTemplate;
import lebah.template2.DbPersistence;

public class PrintSponsorInvoiceModule extends VTemplate {
	
	String path = "apps/sponsor_invoice/";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		Template template = engine.getTemplate(getSponsorInvoice());	
		return template;		
	}

	private String getSponsorInvoice() {
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
        context.put("currentDate", new Date());
        
		String invoiceId = request.getParameter("invoice_id");
		SponsorInvoice invoice = (SponsorInvoice) db.find(SponsorInvoice.class, invoiceId);
		context.put("invoice", invoice);
		
		return path + "print_template.vm";
	}


}
