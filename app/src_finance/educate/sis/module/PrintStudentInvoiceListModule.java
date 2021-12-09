package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.velocity.Template;

import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.InvoiceItem;
import educate.sis.finance.module.MoneyInWord;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.velocity.VTemplate;
import lebah.template2.DbPersistence;

public class PrintStudentInvoiceListModule extends VTemplate {
	
	String path = "apps/util/batch_create_invoice/";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		Template template = engine.getTemplate(getInvoiceList());	
		return template;		
	}

	private String getInvoiceList() throws Exception {
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
        
        String programId = getParam("programId");
        String intakeId = getParam("intakeId");
        String sessionId = getParam("sessionId");
        
        System.out.println("programId = " + programId);;
        System.out.println("intakeId = " + intakeId);
        System.out.println("sessionId = " + sessionId);
        
        Program program = db.find(Program.class, programId);
        Session session = db.find(Session.class, sessionId);
        
		Hashtable h = new Hashtable();
		h.put("program", program);
		h.put("session", session);
        
		List<StudentStatus> list = null;
		if ( !"".equals(intakeId) ) {
			Session intake = db.find(Session.class, intakeId);
			h.put("intake", intake);
			list = db.list("select s from StudentStatus s where s.student.program = :program and s.student.intake = :intake and s.session = :session and s.type.inActive = 0 order by s.student.biodata.name", h);
			context.put("students", list);
		}
		else {
			list = db.list("select s from StudentStatus s where s.student.program = :program and s.session = :session and s.type.inActive = 0 order by s.student.biodata.name", h);
			context.put("students", list);
		}

        List<BulkInvoiceItem> invoices = new ArrayList<BulkInvoiceItem>();
        context.put("invoices", invoices);
        if ( list != null ) {
	        for ( StudentStatus studentStatus : list ) {
	        	if ( studentStatus != null ) {
	        		Invoice invoice = (Invoice) db.get("select i from Invoice i where i.student.id = '" + studentStatus.getStudent().getId() + "' and i.session.id = '" + studentStatus.getSession().getId() + "' and i.invoiceType = 1");
	        		if ( invoice != null ) {
	        			double totalPaid = 0.0d;
	        			for ( InvoiceItem p : invoice.getInvoiceItems()) {
	        				totalPaid += p.getAmount();
	        			}
	        			MoneyInWord w = new MoneyInWord();
	        			String totalPaidInWord = w.toWord(totalPaid);		
	                	BulkInvoiceItem b = new BulkInvoiceItem();
	                	b.setStudentStatus(studentStatus);
	                	b.setInvoice(invoice);
	                	b.setTotalPaid(totalPaid);
	                	b.setTotalPaidInWord(totalPaidInWord);
	                	
	                	invoices.add(b);
	        		}
	        	}
	        }
        
        }
        
		return path + "print_template.vm";
	}



}
