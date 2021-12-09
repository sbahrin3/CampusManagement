package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.finance.entity.Payment;
import educate.sis.struct.entity.Program;
import lebah.portal.action.AjaxModule;

public class PaymentReportModule  extends AjaxModule {
	String path = "apps/util/finance/payment/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	
	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));			
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "list_payments".equals(command)) listPayments();
		else if ( "payment_info".equals(command)) paymentInfo();
		return vm;
	}

	private void paymentInfo() {
		vm = path + "payment_info.vm";
		String dateFrom = request.getParameter("date_from");
		String dateTo = request.getParameter("date_to");
		context.put("date_from", dateFrom);
		context.put("date_to", dateTo);
		String paymentId = request.getParameter("payment_id");
		Payment payment = db.find(Payment.class, paymentId);
		context.put("payment", payment);
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		if ( !"".equals(programId)) {
			Program program = db.find(Program.class, programId);
			context.put("program", program);
		}
		else
			context.remove("program");
		
	}

	private void listPayments() {
		vm = path + "list_payments.vm";
		String dateFrom = request.getParameter("date_from");
		String dateTo = request.getParameter("date_to");
		Date date1 = getDate(dateFrom);
		Date date2 = getDate(dateTo);
		context.put("date_from", dateFrom);
		context.put("date_to", dateTo);
		context.put("date1", date1);
		context.put("date2", date2);
		Hashtable param = new Hashtable();
		param.put("date1", date1);
		param.put("date2", date2);
		
		String cbReceipt = request.getParameter("cb_receipt");
		String cbCreditNote = request.getParameter("cb_creditnote");
		boolean isReceipt = cbReceipt == null ? false : true;
		boolean isCreditNote = cbCreditNote == null ? false : true;
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		if ( !"".equals(programId)) {
			Program program = db.find(Program.class, programId);
			context.put("program", program);
		}
		else
			context.remove("program");
		
		String sql = "select i from Payment i Join i.student s where i.createDate between :date1 and :date2 and i.amount > 0 ";
		if ( !"".equals(programId)) sql += " and s.program.id = '" + programId + "' ";
		if ( !isReceipt || !isCreditNote ) {
			if ( isReceipt ) sql += " and i.isCreditNote = 0 ";
			if ( isCreditNote ) sql += " and i.isCreditNote = 1 ";
		}
		sql += "order by i.createDate, i.createTime";
		List<Payment> payments = db.list(sql, param);
		context.put("payments", payments);
		
		sql = "select SUM(i.amount) from Payment i Join i.student s where i.createDate between :date1 and :date2 and i.amount > 0 ";
		if ( !"".equals(programId)) sql += " and s.program.id = '" + programId + "' ";
		if ( !isReceipt || !isCreditNote ) {
			if ( isReceipt ) sql += " and i.isCreditNote = 0 ";
			if ( isCreditNote ) sql += " and i.isCreditNote = 1 ";
		}
		List<Double> list = db.list(sql, param);
		double totalAmount = 0.0d;
		if ( list.size() > 0 ) {
			totalAmount = list.get(0) != null ? list.get(0) : 0.0d;
			context.put("total_amount", totalAmount);
		}
		
		//excel
		session.setAttribute("payment_report", payments);
		session.setAttribute("total_amount", totalAmount);
		//
		
	}

	private void start() {
		vm = path + "start.vm";
		
		String dateFrom = request.getParameter("date_from") != null ? request.getParameter("date_from") : "";
		String dateTo = request.getParameter("date_to") != null ? request.getParameter("date_to") : "";
		context.put("date_from", dateFrom);
		context.put("date_to", dateTo);
		
		//List of Program
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		
	}
	
	private Date getDate(String date) {
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		return new GregorianCalendar(year, month, day).getTime();
	}

}
