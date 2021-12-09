package educate.sis.module;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.finance.entity.XInvoice;
import lebah.portal.action.AjaxModule;

public class DeletedInvoiceModule extends AjaxModule {
	
	String path = "apps/util/student_invoice_deleted/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( command == null || "".equals(command)) start();
		else if ( "by_student".equals(command)) byStudent();
		else if ( "by_date".equals(command)) byDate();
		else if ( "view_invoice".equals(command)) viewInvoice();
		
		return vm;
	}

	private void viewInvoice() {
		String invoiceId = request.getParameter("invoice_id");
		XInvoice invoice = db.find(XInvoice.class, invoiceId);
		context.put("invoice", invoice);
		
		vm = path + "view.vm";
	}

	private void byDate() {
		String dateFrom = request.getParameter("date_from");
		String dateTo = request.getParameter("date_to");
		
		try {
			Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(dateFrom);
			Date date2 = new SimpleDateFormat("dd-MM-yyyy").parse(dateTo);
			
			Hashtable h = new Hashtable();
			h.put("date_from", date1);
			h.put("date_to", date2);
			
			List<XInvoice> invoices = db.list("select i from XInvoice i where i.deleteDate between :date_from and :date_to", h);
			context.put("invoices", invoices);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		vm = path + "result.vm";
	}

	private void byStudent() {
		String matricNo = request.getParameter("matric_no");
		List<XInvoice> invoices = db.list("select i from XInvoice i where i.student.matricNo = '" + matricNo + "' order by i.createDate");
		context.put("invoices", invoices);
		
		vm = path + "result.vm";
	}

	private void start() {
		vm = path + "start.vm";
		
	}

}
