package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.sis.finance.entity.FeeItem;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.Payment;
import educate.sis.struct.entity.Program;
import lebah.portal.action.AjaxModule;

public class FinanceReportModule  extends AjaxModule{
	
	String path = "apps/util/finance/report/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("df", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("dateFormat", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));			
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "list_records".equals(command)) listRecords();
		else if ( "list_transactions".equals(command)) viewTransactions();
		else if ( "print_transactions".equals(command)) printTransactions();
		return vm;
	}
	
	private void printTransactions() {
		context.put("is_print", true);
		listTransactions();
	}
	
	private void viewTransactions() {
		context.remove("is_print");
		listTransactions();
	}

	private void listTransactions() {
		vm = path + "list_transactions.vm";
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
		
		String feeItemId = request.getParameter("fee_item_id") != null ? request.getParameter("fee_item_id") : "";
		if ( !"".equals(feeItemId )) {
			FeeItem feeItem = (FeeItem) db.find(FeeItem.class, feeItemId);
			context.put("feeItem", feeItem);
		}
		else
			context.remove("feeItem");
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		if ( !"".equals(programId)) {
			Program program = (Program) db.find(Program.class, programId);
			context.put("program", program);
		}
		else
			context.remove("program");
		listRecordsByTransaction(param, feeItemId, programId);
		
	}

	private void listRecords() {
		vm = path + "list_records.vm";
		
		Hashtable param = new Hashtable();
		String dateTransaction = request.getParameter("date_transaction");
		if ( dateTransaction != null || !"".equals(dateTransaction)) {
			Date date1 = getDate(dateTransaction);
			context.put("date1", date1);
			param.put("date1", date1);
			param.put("date2", date1);
		}
		else {
			String dateFrom = request.getParameter("date_from");
			String dateTo = request.getParameter("date_to");
			Date date1 = getDate(dateFrom);
			Date date2 = getDate(dateTo);
			context.put("date_from", dateFrom);
			context.put("date_to", dateTo);
			context.put("date1", date1);
			context.put("date2", date2);
			param.put("date1", date1);
			param.put("date2", date2);
		}
		
		

		
		String feeItemId = request.getParameter("fee_item_id") != null ? request.getParameter("fee_item_id") : "";
		if ( !"".equals(feeItemId )) {
			FeeItem feeItem = (FeeItem) db.find(FeeItem.class, feeItemId);
			context.put("feeItem", feeItem);
		}
		else
			context.remove("feeItem");
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		if ( !"".equals(programId)) {
			Program program = (Program) db.find(Program.class, programId);
			context.put("program", program);
		}
		else
			context.remove("program");
		
		String statusId = request.getParameter("status_id") != null ? request.getParameter("status_id") : "";
		if ( !"".equals(statusId)) {
			StatusType status = (StatusType) db.find(StatusType.class, statusId);
			context.put("status_type", status);
		}
		else
			context.remove("status_type");
		
		
		listRecordsByStudent(param, feeItemId, programId, statusId);

	}
	

	private void listRecordsByStudent(Hashtable param, String feeItemId, String programId, String statusId) {
		
		String cbNormal = getParam("cb_normal");
		String cbCreditNote = getParam("cb_creditnote");
		String cbDebitNote = getParam("cb_debitnote");
		String cbRefund = getParam("cb_refund");
		
		String paymentMode = getParam("paymentMode");
		
		boolean isNormal = cbNormal == null ? false : true;
		boolean isCreditNote = cbCreditNote == null ? false : true;
		boolean isDebitNote = cbDebitNote == null ? false : true;
		boolean isRefund = cbRefund == null ? false : true;
		
		Set<String> ids = new HashSet<String>();
		Map<String, R> pmap = new HashMap<String, R>();
		Map<String, R> imap = new HashMap<String, R>();
		String sql1 = "";
		if ( "".equals(feeItemId)) {
			sql1 = "select new educate.sis.finance.module.R(i.student.id, i.student.matricNo, i.student.biodata.icno, i.student.biodata.name,  SUM(i.amount))" +
					" from Payment i where i.createDate between :date1 and :date2 ";
			
		}
		else {
			sql1 = "select new educate.sis.finance.module.R(i.student.id, i.student.matricNo, i.student.biodata.icno, i.student.biodata.name,  SUM(pi.amount))" +
			" from Payment i " +
			" join i.paymentItems pi " +
			" join pi.invoiceItem it" +
			" where i.createDate between :date1 and :date2 " +
			" and it.feeItem.id = '" + feeItemId + "' ";

		}
		
		//paymentMode
		if ( !"".equals(paymentMode)) sql1 += " and i.paymentMode = '" + paymentMode + "'";
		
		if ( !"".equals(programId)) sql1 += " and i.student.program.id = '" + programId + "' ";
		if ( isCreditNote && isNormal ) {
			sql1 += " and (i.isCreditNote = 1 or i.isCreditNote = 0) ";
		}
		else if ( !isCreditNote && isNormal ) {
			sql1 += " and i.isCreditNote = 0 ";
		}
		else if ( isCreditNote && !isNormal ) {
			sql1 += " and i.isCreditNote = 1 ";
		}
		else if ( !isCreditNote && !isNormal ) {
			sql1 += " and i.isCreditNote = 99 ";
		}
		sql1 += " group by i.student";
		
		
		List<R> rs = db.list(sql1, param);
	
		for ( R r : rs ) {
			ids.add(r.getId());
			pmap.put(r.getId(), r);
		}
		
		String sql2 = "";
		if ( "".equals(feeItemId)) {
			sql2 = "select new educate.sis.finance.module.R(i.student.id, i.student.matricNo, i.student.biodata.icno, i.student.biodata.name, SUM(i.amount))" +
			" from Invoice i where i.createDate between :date1 and :date2 ";
			
		}
		else {
			sql2 = "select new educate.sis.finance.module.R(i.student.id, i.student.matricNo, i.student.biodata.icno, i.student.biodata.name, SUM(it.amount))" +
			" from Invoice i join i.invoiceItems it" +
			" where i.createDate between :date1 and :date2 " +
			" and it.feeItem.id = '" + feeItemId + "' ";
		}

		if ( !"".equals(programId)) sql2 += " and i.student.program.id = '" + programId + "' ";
		
		if ( isDebitNote && isNormal ) {
			if ( isRefund ) sql2 += " and ( i.isRefund = 1 or i.isRefund = 0 )";
			else if ( !isRefund ) sql2 += "  and i.isRefund = 0 ";				
		}
		else if ( !isDebitNote && isNormal ) {
			if ( isRefund ) sql2 += " and (i.isDebitNote = 0 or i.isRefund = 1) ";
			else if ( !isRefund ) sql2 += " and i.isDebitNote = 0 and i.isRefund = 0 ";
			
		}
		else if ( isDebitNote && !isNormal ) {
			if ( isRefund ) sql2 += " and (i.isDebitNote = 1 or i.isRefund = 1) ";
			else if ( !isRefund ) sql2 += " and i.isDebitNote = 1 and i.isRefund = 0 ";
		}
		else if ( !isDebitNote && !isNormal ) {
			if ( !isRefund ) sql2 += " and i.isDebitNote = 99 ";
			else sql2 += " and i.isDebitNote = 0 and i.isRefund = 1 ";
		}
		sql2 += " group by i.student";
		
		List<R> rs2 = db.list(sql2, param);
		for ( R r : rs2 ) {
			ids.add(r.getId());
			imap.put(r.getId(), r);
		}
		//the report
		List<Record> records = new ArrayList<Record>();
		context.put("records", records);
		double totalPaid = 0.0d;
		double totalInvoiced = 0.0d;
		double totalBalance = 0.0d;
		for ( String id : ids ) {
			double paid = pmap.get(id) != null ? pmap.get(id).getValue() : 0.0d;
			double invd = imap.get(id) != null ? imap.get(id).getValue() : 0.0d;
			R r = imap.get(id) != null ? imap.get(id) : pmap.get(id);
			Record record = new Record(id, r.getMatricNo(), r.getIcno(), r.getName(), invd, paid);
			records.add(record);
			totalInvoiced += invd;
			totalPaid += paid;
			totalBalance += record.getBalance();
		}
		context.put("total_paid", totalPaid);
		context.put("total_invoiced", totalInvoiced);
		context.put("total_balance", totalBalance);
	}
	
	private void listRecordsByTransaction(Hashtable param, String feeItemId, String programId) {
		
		String cbNormal = request.getParameter("cb_normal");
		String cbCreditNote = request.getParameter("cb_creditnote");
		String cbDebitNote = request.getParameter("cb_debitnote");
		String cbRefund = request.getParameter("cb_refund");
		
		String paymentMode = getParam("paymentMode");
		
		boolean isNormal = cbNormal == null ? false : true;
		boolean isCreditNote = cbCreditNote == null ? false : true;
		boolean isDebitNote = cbDebitNote == null ? false : true;
		boolean isRefund = cbRefund == null ? false : true;
		
		Set<String> ids = new HashSet<String>();
		Map<String, R> pmap = new HashMap<String, R>();
		Map<String, R> imap = new HashMap<String, R>();
		
		{
			String sql1 = "";
			if ( "".equals(feeItemId)) {
				sql1 = "select new educate.sis.finance.module.R(i.student.id, i.student.matricNo, i.student.biodata.icno, i.student.biodata.name,  i.createDate, SUM(i.amount))" +
						" from Payment i where i.createDate between :date1 and :date2 ";
			}
			else {
				sql1 = "select new educate.sis.finance.module.R(i.student.id, i.student.matricNo, i.student.biodata.icno, i.student.biodata.name,  i.createDate, SUM(pi.amount))" +
				" from Payment i " +
				" join i.paymentItems pi " +
				" join pi.invoiceItem it" +
				" where i.createDate between :date1 and :date2 " +
				" and it.feeItem.id = '" + feeItemId + "' ";
			}
			
			if ( !"".equals(paymentMode)) sql1 += " and i.paymentMode = '" + paymentMode + "' ";
			
			if ( !"".equals(programId)) sql1 += " and i.student.program.id = '" + programId + "' ";
			if ( isCreditNote && isNormal ) {
				sql1 += " and (i.isCreditNote = 1 or i.isCreditNote = 0) ";
			}
			else if ( !isCreditNote && isNormal ) {
				sql1 += " and i.isCreditNote = 0 ";
			}
			else if ( isCreditNote && !isNormal ) {
				sql1 += " and i.isCreditNote = 1 ";
			}
			else if ( !isCreditNote && !isNormal ) {
				sql1 += " and i.isCreditNote = 99 ";
			}
			sql1 += " group by i.createDate order by i.createDate";
			
			
			List<R> rs = db.list(sql1, param);
			for ( R r : rs ) {
				ids.add(r.getDate().toString());
				pmap.put(r.getDate().toString(), r);
			}
			
		}

		{
			String sql2 = "";
			if ( "".equals(feeItemId)) {
				sql2 = "select new educate.sis.finance.module.R(i.student.id, i.student.matricNo, i.student.biodata.icno, i.student.biodata.name, i.createDate, SUM(i.amount))" +
				" from Invoice i where i.createDate between :date1 and :date2 ";
			}
			else {
				sql2 = "select new educate.sis.finance.module.R(i.student.id, i.student.matricNo, i.student.biodata.icno, i.student.biodata.name, i.createDate, SUM(it.amount))" +
				" from Invoice i join i.invoiceItems it" +
				" where i.createDate between :date1 and :date2 " +
				" and it.feeItem.id = '" + feeItemId + "' ";
			}
			
			if ( !"".equals(programId)) sql2 += " and i.student.program.id = '" + programId + "' ";
			
			if ( isDebitNote && isNormal ) {
				if ( isRefund ) sql2 += " and ( i.isRefund = 1 or i.isRefund = 0 )";
				else if ( !isRefund ) sql2 += "  and i.isRefund = 0 ";				
			}
			else if ( !isDebitNote && isNormal ) {
				if ( isRefund ) sql2 += " and (i.isDebitNote = 0 or i.isRefund = 1) ";
				else if ( !isRefund ) sql2 += " and i.isDebitNote = 0 and i.isRefund = 0 ";
				
			}
			else if ( isDebitNote && !isNormal ) {
				if ( isRefund ) sql2 += " and (i.isDebitNote = 1 or i.isRefund = 1) ";
				else if ( !isRefund ) sql2 += " and i.isDebitNote = 1 and i.isRefund = 0 ";
			}
			else if ( !isDebitNote && !isNormal ) {
				if ( !isRefund ) sql2 += " and i.isDebitNote = 99 ";
				else sql2 += " and i.isDebitNote = 0 and i.isRefund = 1 ";
			}
			
		
			//if ( isRefund ) sql2 += " and i.isRefund = 1 ";
			sql2 += " group by i.createDate order by i.createDate";		
			List<R> rs2 = db.list(sql2, param);
			
			//System.out.println(sql2);
			//System.out.println(rs2.size());

			for ( R r : rs2 ) {
				ids.add(r.getDate().toString());
				imap.put(r.getDate().toString(), r);
			}

		}
		List<Record> records = new ArrayList<Record>();
		context.put("records", records);
		double totalPaid = 0.0d;
		double totalInvoiced = 0.0d;
		double totalBalance = 0.0d;
		double cumulativeBalance = 0.0d;
		int cnt = 0;
		
		for ( String id : ids ) {
			double paid = pmap.get(id) != null ? pmap.get(id).getValue() : 0.0d;
			double invd = imap.get(id) != null ? imap.get(id).getValue() : 0.0d;
			R r = imap.get(id) != null ? imap.get(id) : pmap.get(id);
			Record record = new Record(id, r.getMatricNo(), r.getName(), r.getDate(), invd, paid);
			records.add(record);
			totalInvoiced += invd;
			totalPaid += paid;
			if ( cnt == 0 ) cumulativeBalance = invd - paid;
			else {
				cumulativeBalance = cumulativeBalance + invd - paid;
			}
			record.setBalance(cumulativeBalance);
			cnt++;
		}
		totalBalance = totalInvoiced - totalPaid;
		context.put("total_paid", totalPaid);
		context.put("total_invoiced", totalInvoiced);
		context.put("total_balance", totalBalance);
		
		Collections.sort(records);
		
		/*
		for ( Record r : records ) {
			System.out.println(r.getDate());
		}
		*/
	}	

	private void start() {
		
		//vm = path + "select_date.vm";
		vm = path + "start.vm";
		
		String dateFrom = request.getParameter("date_from") != null ? request.getParameter("date_from") : "";
		String dateTo = request.getParameter("date_to") != null ? request.getParameter("date_to") : "";
		context.put("date_from", dateFrom);
		context.put("date_to", dateTo);
		//list of fee items
		List<FeeItem> feeItems = db.list("select f from FeeItem f order by f.code");
		context.put("fee_items", feeItems);
		String feeItemId = request.getParameter("fee_item_id") != null ? request.getParameter("fee_item_id") : "";
		context.put("fee_item_id", feeItemId);
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		context.put("program_id", programId);
		
		//List of Program
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		
		List<StatusType> statusTypes = db.list("select s from StatusType s order by s.sequence");
		context.put("status_types", statusTypes);
	}
	
	private Date getDate(String date) {
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		return new GregorianCalendar(year, month, day).getTime();
	}
	
	private static void doA() {
		DbPersistence db = new DbPersistence();
		FinanceReportModule m = new FinanceReportModule();
		String dateFrom = "01-12-2005";
		String dateTo = "21-12-2013";
		Date date1 = m.getDate(dateFrom);
		Date date2 = m.getDate(dateTo);
		Hashtable param = new Hashtable();
		param.put("date1", date1);
		param.put("date2", date2);
		String sql = "select i from Invoice i where i.createDate between :date1 and :date2 " +
		"order by i.student.id";
		List<Invoice> invoices = db.list(sql, param);
		
		System.out.println(invoices.size());
		
		sql = "select i from Payment i where i.createDate between :date1 and :date2 " +
		"order by i.student.id";
		List<Payment> payments = db.list(sql, param);
		Set<Student> students = new HashSet<Student>();
		Map<String, Object> items = new HashMap<String, Object>();
		String tmp = "";
		X x = new X();
		for (Payment p : payments ) {
			students.add(p.getStudent());
			x.putPayment(p);
		}
		for ( Invoice i : invoices ) {
			students.add(i.getStudent());
			x.putInvoice(i);
		}

		for ( Student s : students ) {
			
			if ( s != null ) {
				
				List<Invoice> listI = x.getInvoiceMap().get(s.getId());
				if ( listI != null ) {
					for ( Invoice i : listI ) {
						System.out.println("Billed: " + i.getAmount());
					}
				}
				List<Payment> listP = x.getPaymentMap().get(s.getId());
				if ( listP != null ) {
					for ( Payment p : listP ) {
						System.out.println("Paid: " + p.getAmount());
					}
				}
				System.out.println("==");
			
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		
		doA();
		
		
	}

}
