package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.finance.entity.Payment;
import educate.sis.finance.entity.XPayment;
import educate.sis.struct.entity.Program;
import lebah.portal.action.AjaxModule;

public class PaymentAdjustmentReportModule  extends AjaxModule {
	
	String path = "apps/util/finance/payment_adjustment_report/";
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
		else if ( "get_report".equals(command)) getReport();
		return vm;
	}
	
	private void getReport() {
		vm = path + "report.vm";
		String dateFrom = request.getParameter("date_from") != null ? request.getParameter("date_from") : "";
		String dateTo = request.getParameter("date_to") != null ? request.getParameter("date_to") : "";
		context.put("date_from", dateFrom);
		context.put("date_to", dateTo);
		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		Hashtable param = new Hashtable();
		param.put("date1", getDate(dateFrom));
		param.put("date2", getDate(dateTo));
		String sql = "select p from Payment p where p.adjusted = 1 " +
				" and p.createDate between :date1 and :date2 ";
		if ( !"".equals(programId)) sql += " and p.student.program.id = '" + programId + "' ";
		sql += " order by p.createDate, p.createTime";
		List<Payment> payments = db.list(sql, param);
		List<Adjust> adjustments = new ArrayList<Adjust>();
		double total = 0.0d;
		for (Payment p : payments ) {
			double lastAmount = p.getAmount();
			sql = "select x from XPayment x where x.payment.id = '" + p.getId() + "' order by x.adjustDate, x.adjustTime";
			List<XPayment> xpayments = db.list(sql);
			double amt = 0.0d, d = 0.0d, subtotal = 0.0d;
			int i = 0;
			for ( XPayment x : xpayments ) {
				if ( i > 0 ) {
					d = x.getAmount() - amt;
					subtotal += d;
					System.out.println("adjusted by = " + x.getAdjustUserId());
					
					adjustments.add(new Adjust(x.getPaymentNo(), x.getAdjustDate(), x.getAdjustTime(), x.getAdjustUserId(), x.getStudent(), d, 0.0d));
				}
				amt = x.getAmount();
				i++;
			}
			d = lastAmount - amt;
			subtotal += d;
			adjustments.add(new Adjust(p.getPaymentNo(), p.getAdjustDate(), p.getAdjustTime(), p.getAdjustUserId(), p.getStudent(), d, subtotal));
			total += subtotal;
		}
		context.put("adjustments", adjustments);
		context.put("total_adjustment", total);

	}

	private void start() {
		vm = path + "select_date.vm";
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
