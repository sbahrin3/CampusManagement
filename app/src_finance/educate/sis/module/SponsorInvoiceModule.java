package educate.sis.module;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.Payment;
import educate.sis.finance.entity.Sponsor;
import educate.sis.finance.entity.SponsorInvoice;
import educate.sis.finance.entity.SponsorPayment;
import educate.sis.finance.entity.SponsorStudent;
import educate.sis.finance.module.CPaymentItem;
import educate.sis.finance.module.InvoiceUtil;
import educate.sis.finance.module.PaymentUtil;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class SponsorInvoiceModule extends AjaxModule {
	
	String path = "apps/sponsor_invoice/";
	private String vm = "start.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	protected boolean payment = false;
	String userId= "";

	@Override
	public String doAction() throws Exception {
		context.put("is_payment", payment);
		userId = (String) request.getSession().getAttribute("_portal_login");
		context.put("userId", userId);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "get_invoices".equals(command)) getInvoices();
		else if ( "create_invoice".equals(command)) createInvoice();
		else if ( "save_invoice".equals(command)) saveInvoice();
		else if ( "delete_invoice".equals(command)) deleteInvoice();
		else if ( "list_students".equals(command)) listStudents();
		else if ( "print_invoice".equals(command)) printInvoice();
		else if ( "make_payments".equals(command)) makePayments();
		else if ( "save_payments".equals(command)) savePayments();
		else if ( "delete_payment".equals(command)) deletePayment();
		else if ( "cancel_payments".equals(command)) cancelPayments();
		else if ( "student_receipts".equals(command)) studentReceipts();
		
		else if ( "getSessions".equals(command)) getSessions();
		else if ( "getStudents".equals(command)) getStudents();
		else if ( "viewInvoices".equals(command)) viewInvoices();
		return vm;
	}
	
	private void viewInvoices() {
		String invoiceId = getParam("invoice_id");
		SponsorInvoice invoice = (SponsorInvoice) db.find(SponsorInvoice.class, invoiceId);
		context.put("invoice", invoice);
		
		vm = path + "view_invoices.vm";
	}

	private void getStudents() {
		String programId = getParam("programId");
		List<SponsorStudent> students = db.list("select sp from SponsorStudent sp Join sp.student s where s.program.id = '" + programId + "' order by s.biodata.name");
		context.put("students", students);
		
		String sponsorId = getParam("sponsor_id");
		Sponsor sponsor = db.find(Sponsor.class, sponsorId);
		context.put("sponsor", sponsor);
		
		vm = path + "divStudents.vm";
		
	}

	private void getSessions() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		int pathNo = program.getLevel().getPathNo();
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("sessions", sessions);
		SessionUtil su = new SessionUtil();
		Session currentSession = su.getCurrentSession(pathNo);
		context.put("currentSession", currentSession);
		
		vm = path + "divSessions.vm";
		
	}

	private void studentReceipts() {
		
		String cnt = request.getParameter("cnt");
		context.put("cnt", cnt);
		
		String invoiceId = request.getParameter("invoice_id");
		Invoice invoice = db.find(Invoice.class, invoiceId);
		
		
		String sponsorInvoiceId = request.getParameter("sponsor_invoice_id");
		SponsorInvoice sponsorInvoice = db.find(SponsorInvoice.class, sponsorInvoiceId);
		
		String sql = "select s from SponsorPayment s join s.sponsorInvoice si join si.invoices i where i.id = '" + invoiceId + "'";
		List<SponsorPayment> sponsorPayments = db.list(sql);
		List<Payment> payments = new ArrayList<Payment>();
		for ( SponsorPayment sp : sponsorPayments ) {
			List<Payment> list = sp.getPayments();
			for ( Payment p : list ) {
				if ( p.getStudent().getId().equals(invoice.getStudent().getId())) {
					payments.add(p);
				}
			}
		}
		context.put("payments", payments);
		
		vm = path + "student_receipts.vm";
		
	}


	private void cancelPayments() throws Exception {
		// 
		
		String sponsorInvoiceId = request.getParameter("sponsor_invoice_id");
	
		List<SponsorPayment> sponsorPayments = db.list("select sp from SponsorPayment sp where sp.sponsorInvoice.id = '" + sponsorInvoiceId + "'");
		
		for ( SponsorPayment sp : sponsorPayments ) {
			//delete payment first
			for ( Payment p : sp.getPayments() ) {
				db.begin();
				db.remove(p);
				db.commit();
			}
			//remove sponsor payment
			db.begin();
			db.remove(sp);
			db.commit();
		}
		
		SponsorInvoice sponsorInvoice = (SponsorInvoice) db.find(SponsorInvoice.class, sponsorInvoiceId);
		listInvoices(sponsorInvoice);
	}

	private void deletePayment() {
		// 
		
		String sponsorInvoiceId = request.getParameter("sponsor_invoice_id");
		SponsorInvoice sponsorInvoice = (SponsorInvoice) db.find(SponsorInvoice.class, sponsorInvoiceId);
		
		String invoiceId = request.getParameter("invoice_id");
		
		Invoice invoice = (Invoice) db.get("select i from SponsorInvoice s JOIN s.invoices i where i.id = '" + invoiceId + "'");
		if ( invoice != null ) {
			System.out.println("Delete payment for: " + invoice.getStudent().getMatricNo());
		
		} else {
			System.out.println("Can't find invoice.");
		}
		
		
		listInvoices(sponsorInvoice);
		
	}

	private void savePayments() throws Exception {
		//
		String sponsorInvoiceId = request.getParameter("sponsor_invoice_id");
		SponsorInvoice sponsorInvoice = (SponsorInvoice) db.find(SponsorInvoice.class, sponsorInvoiceId);
		
		String paymentRemark = getParam("paymentRemark");
		String _paymentDate = getParam("paymentDate");
		Date paymentDate = null;
		try {
			paymentDate = new SimpleDateFormat("dd-MM-yyyy").parse(_paymentDate);
		} catch ( Exception e ) { }
		
		String[] invoiceIds = request.getParameterValues("invoice_ids");
		if ( invoiceIds != null ) {
			
			SponsorPayment sp = new SponsorPayment();
			
			double totalAmt = 0.0d;
			List<Payment> paymentList = new ArrayList<Payment>();
			PaymentUtil pu = new PaymentUtil();
			for ( String id : invoiceIds ) { 
				
				double amount = 0.0d;
				try {
					String _amount = request.getParameter("amount_" + id);
					_amount = _amount.replaceAll(",", "");
					amount = Double.parseDouble(_amount);
				} catch ( Exception e ) {}
				
				if ( amount > 0.0 ) {
					Invoice invoice = (Invoice) db.find(Invoice.class, id);
					Student student = invoice.getStudent();
					
					System.out.println(student.getMatricNo() + " , " + invoice.getAmount());
					
					String descr = "PAYMENT FOR INVOICE NO: " + invoice.getInvoiceNo();
					//CPaymentItem pi = new CPaymentItem(descr, invoice.getAmount());
					CPaymentItem pi = new CPaymentItem(descr, amount);
					Payment payment = pu.createPayment(student, pi, sp, paymentDate);
					paymentList.add(payment);
					totalAmt += payment.getAmount();
					
					db.begin();
					double initAmount = invoice.getBalance();
					double balanceAmount = initAmount - amount;
					invoice.setBalance(balanceAmount);
					db.commit();
				}
				
			}

			if ( totalAmt > 0.0 ) {
				db.begin();
	
				sp.setPaymentDate(new Date());
				sp.setSponsorInvoice(sponsorInvoice);
				sp.setPayments(paymentList);
				sp.setAmount(totalAmt);
				sp.setPaymentRemark(paymentRemark);
				sponsorInvoice.getSponsorPayments().add(sp);
	
				db.commit();
			}
			
		}
		
		listInvoices(sponsorInvoice);
		
	}

	private void makePayments() {
		// 
		String invoiceId = request.getParameter("invoice_id");
		
		SponsorInvoice sponsorInvoice = (SponsorInvoice) db.find(SponsorInvoice.class, invoiceId);
		listInvoices(sponsorInvoice);
		
		
		
	}

	private void listInvoices(SponsorInvoice sponsorInvoice) {
		Sponsor sponsor = sponsorInvoice.getSponsor();
		
		context.put("sponsor_invoice", sponsorInvoice);
		context.put("sponsor", sponsor);
		
		List<Student> paidStudents = new ArrayList<Student>();
		List<SponsorPayment> sponsorPayments = db.list("select s from SponsorPayment s where s.sponsorInvoice.id = '" + sponsorInvoice.getId() + "'");
		
		Map<String, Double> paids = new HashMap<String, Double>();
		context.put("paids", paids);
		
		for ( SponsorPayment sp : sponsorPayments ) {
			List<Payment> payments = sp.getPayments();
			if ( payments != null ) {
				for ( Payment p : payments ) {
					double amt = paids.get(p.getStudent().getId()) != null ? paids.get(p.getStudent().getId()) : 0.0d;
					amt = amt + p.getAmount();
					paids.put(p.getStudent().getId(), amt);
					paidStudents.add(p.getStudent());
				}
			}
			else {
				System.out.println("null");
			}
		}
		
		List<Invoice> unpaidInvoices = new ArrayList<Invoice>();
		List<Invoice> paidInvoices = new ArrayList<Invoice>();
		List<Invoice> invoices = sponsorInvoice.getInvoices();
		context.put("invoices", invoices);
		
		for ( Invoice inv : invoices ) {
			if ( paidStudents.contains(inv.getStudent())) {
				paidInvoices.add(inv);
			} else {
				unpaidInvoices.add(inv);
			}
		}
		context.put("paid_invoices", paidInvoices);
		context.put("unpaid_invoices", unpaidInvoices);
		
		vm = path + "list_payments.vm";
	}

	private void printInvoice() {
		//
		String invoiceId = request.getParameter("invoice_id");
		SponsorInvoice invoice = (SponsorInvoice) db.find(SponsorInvoice.class, invoiceId);
		context.put("invoice", invoice);

		vm = path + "print_invoice.vm";
	}

	private void listStudents() {
		// 
		String sponsorId = request.getParameter("sponsor_id");
		
		List sponsorStudents = db.list("select s from SponsorStudent s where s.sponsor.id = '" + sponsorId + "'");
		context.put("sponsor_students", sponsorStudents);
		
		vm = path + "list_students.vm";
		
		
	}

	private void deleteInvoice() throws Exception {
		//
		String invoiceId = request.getParameter("invoice_id");
		SponsorInvoice invoice = (SponsorInvoice) db.find(SponsorInvoice.class, invoiceId);
		
		Session session = invoice.getSession();

		List<Invoice> studentInvoices = invoice.getInvoices();
		for ( Invoice inv : studentInvoices ) {
			db.begin();
			db.remove(inv);
			db.commit();
		}
		
		//remove this invoice
		db.begin();
		db.remove(invoice);
		db.commit();
		
		//delete Sponsor
		
		getInvoices();
		
	}

	private void saveInvoice() throws Exception  {
		String sponsorId = getParam("sponsor_id");
		Sponsor sponsor = (Sponsor) db.find(Sponsor.class, sponsorId);
		
		String programId = getParam("programId");
		Program program = (Program) db.find(Program.class, programId);
		
		String invoiceDate = request.getParameter("invoice_date");
		String invoiceDateFrom = getParam("invoice_date_from");
		String invoiceDateTo = getParam("invoice_date_to");
		
		String sessionId = request.getParameter("invoice_session");
		
		
		
		List<SponsorStudent> students = new ArrayList<SponsorStudent>();
		String[] checkStudentIds = request.getParameterValues("checkStudentIds");
		if ( checkStudentIds != null ) {
			for ( String studentId : checkStudentIds ) {
				SponsorStudent student = db.find(SponsorStudent.class, studentId);
				students.add(student);
			}
		}

		if ( students.size() > 0 ) {
			Date date = null;
			try {
				date = new SimpleDateFormat("dd-MM-yyyy").parse(invoiceDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				date = new Date();
			}
			
			Date dateFrom = null;
			try {
				dateFrom = new SimpleDateFormat("dd-MM-yyyy").parse(invoiceDateFrom);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			Date dateTo = null;
			try {
				dateTo = new SimpleDateFormat("dd-MM-yyyy").parse(invoiceDateTo);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
			
			Session session = (Session) db.find(Session.class, sessionId);
			//create a SponsorInvoice
			db.begin();
			SponsorInvoice invoice = new SponsorInvoice();
			invoice.setSponsor(sponsor);
			invoice.setSession(session);
			invoice.setProgram(program);
			invoice.setInvoiceDate(date);
			invoice.setDateFrom(dateFrom);
			invoice.setDateTo(dateTo);
			db.persist(invoice);
			db.commit();
			
			//keep it's id for later update
			String id = invoice.getId();
			//create invoices for individual students
			double totalAmt = 0.0d;
			List<Invoice> invoiceList = new ArrayList<Invoice>();
			InvoiceUtil iu = new InvoiceUtil();
			for ( SponsorStudent s : students ) {
				//set to false if don't waht to remove previously created invoice
				boolean deleteIfExist = false;
				Invoice studentInvoice = iu.createStudentInvoice(invoice, s, session, deleteIfExist); 
				if ( studentInvoice != null ) {
					invoiceList.add(studentInvoice);
					totalAmt += studentInvoice.getAmount();
				}
			}
			
			//update the SponsorInvoice
			invoice = (SponsorInvoice) db.find(SponsorInvoice.class, id);
			db.begin();
			invoice.setAmount(totalAmt);
			invoice.setInvoices(invoiceList);
			db.commit();

			getInvoices();
		
		}
		
	}

	private void createInvoice() throws Exception {
		//
		String sponsorId = request.getParameter("sponsor_id");
		Sponsor sponsor = (Sponsor) db.find(Sponsor.class, sponsorId);
		context.put("sponsor", sponsor);
			
		List<Program> programs = db.list("select p from Program p order by p.name");
		context.put("programs", programs);
		
		vm = path + "invoice_entry.vm";
		
	}

	private void getInvoices() {
		// 
		String sponsorId = request.getParameter("sponsor_id");
		Sponsor sponsor = (Sponsor) db.find(Sponsor.class, sponsorId);
		context.put("sponsor", sponsor);
		
		List<SponsorInvoice> invoices = db.list("select i from SponsorInvoice i where i.sponsor.id = '" + sponsorId + "'");
		context.put("invoices", invoices);
		
		List<Hashtable> invoiceList = new ArrayList<Hashtable>();
		for ( SponsorInvoice i : invoices ) {
			Hashtable h = new Hashtable();
			h.put("sponsor_invoice", i);
			double amt = 0.0d;
			for ( SponsorPayment p : i.getSponsorPayments() ) amt += p.getAmount(); 
			h.put("amount_paid", amt);
			invoiceList.add(h);
		}
		
		context.put("invoice_list", invoiceList);
		

		vm = path + "list_invoices.vm";
	}

	private void start() {
		List<Sponsor> sponsors = db.list("select s from educate.sis.finance.entity.Sponsor s");
		context.put("sponsors", sponsors);
		
		vm = path + "start.vm";
		
	}

}
