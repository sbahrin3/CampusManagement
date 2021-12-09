package educate.sis.finance.module;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.InvoiceItem;
import educate.sis.finance.entity.PaidItem;
import educate.sis.finance.entity.Payment;
import educate.sis.finance.entity.PaymentItem;
import educate.sis.finance.entity.PaymentSchedule;
import educate.sis.module.StudentStatusUtil;
import lebah.portal.action.AjaxModule;

public class StudentCustomPaymentModule extends AjaxModule {
	
	
	String path = "apps/util/finance/payment_custom/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	String userId = "";

	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		userId = (String) request.getSession().getAttribute("_portal_login");
		context.put("userId", userId);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		
		context.remove("view_receipt");
		
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "get_student".equals(command)) getStudentAcctStatement();
		else if ( "make_payment".equals(command)) makePayment();
		else if ( "make_credit_note".equals(command)) makeCreditNote();
		else if ( "list_payments".equals(command)) listPayments();
		else if ( "view_payment".equals(command)) viewPayment();
		
		else if ( "add_payment_item".equals(command)) addPaymentItem();
		else if ( "delete_payment_item".equals(command)) deletePaymentItem();
		else if ( "confirm_payment".equals(command)) confirmPayment();
		
		else if ( "add_credit_note_item".equals(command)) addCreditNoteItem();
		else if ( "delete_credit_note_item".equals(command)) deleteCreditNoteItem();
		else if ( "confirm_credit_note".equals(command)) confirmCreditNote();
		
		
		else if ( "delete_payment".equals(command)) deletePayment();
		else if ( "uploadFile".equals(command)) uploadFile();
		else if ( "viewReceipt".equals(command)) viewReceipt();
		return vm;
	}
	
	private void viewReceipt() throws Exception {
        
		context.put("view_receipt", true);
		context.put("currentDate", new Date());
        
		String paymentId = request.getParameter("payment_id");
		Payment payment = db.find(Payment.class, paymentId);
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus currentStatus = u.getCurrentStudentStatus(payment.getStudent());
		context.put("student_status", currentStatus);
		context.put("student", payment.getStudent());
		context.put("payment", payment);
		context.put("payment_items", payment.getPaymentItems());
		
		double totalPaid = 0.0d;
		
		for ( PaymentItem p : payment.getPaymentItems()) {
			totalPaid += p.getAmount();
		}
		context.put("totalPaid", totalPaid);
		MoneyInWord w = new MoneyInWord();
		context.put("totalPaidInWord", w.toWord(totalPaid));		
		vm = path + "view_receipt.vm";
		
	}

	private void deletePayment() throws Exception {
		context.remove("is_credit_note");
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);

		String paymentId = getParam("payment_id");
		Payment payment = db.find(Payment.class, paymentId);
		db.begin();
		db.remove(payment);
		db.commit();
		
		getStudentAcctStatement2(student);
	}

	private void viewPayment() throws Exception {
		
		String paymentId = request.getParameter("payment_id");
		Payment payment = db.find(Payment.class, paymentId);
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus currentStatus = u.getCurrentStudentStatus(payment.getStudent());
		context.put("student_status", currentStatus);
		context.put("student", payment.getStudent());
		context.put("payment", payment);
		context.put("payment_items", payment.getPaymentItems());
		
		vm = path + "edit_receipt.vm";
	}

	private void listPayments() {
		String studentId = request.getParameter("student_id");
		List<Payment> payments = db.list("select p from Payment p where p.student.id = '" + studentId + "' order by p.createDate desc, p.createTime desc");
		context.put("payments", payments);
		
		vm = path + "list_payments.vm";
		
	}
	
	private void confirmCreditNote() throws Exception {
		context.put("is_credit_note", true);
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		List<CPaymentItem> list = (List) session.getAttribute("_payment_items");
		
		String _paymentDate = getParam("creditNoteDate");
		Date paymentDate = null;
		try {
			paymentDate = new SimpleDateFormat("dd-MM-yyyy").parse(_paymentDate);
		} catch ( Exception e ) { }
		
		PaymentUtil pu = new PaymentUtil();
		pu.createCreditNote(student, list, paymentDate);
		
		getStudentAcctStatement2(student);

	}


	

	private void confirmPayment() throws Exception {
		context.remove("is_credit_note");
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		List<CPaymentItem> paymentItems = (List) session.getAttribute("_payment_items");
		
		String paymentMode = getParam("payment_mode");
		String remark = getParam("remark");
		String checkNo = getParam("checkNo");
		String bankName = getParam("bankName");
		String documentFileName = getParam("documentFileName");
		String _paymentDate = getParam("paymentDate");
		Date paymentDate = null;
		try {
			paymentDate = new SimpleDateFormat("dd-MM-yyyy").parse(_paymentDate);
		} catch ( Exception e ) { }
		
		String sql = "select it" +
		" from Invoice i " +
		" join i.invoiceItems it " +
		" where i.student.id = '" + studentId + "' " +
		" order by i.createDate";
		
		List<InvoiceItem> invoiceItems = db.list(sql);
		
		PaymentUtil pu = new PaymentUtil();
		Payment payment = pu.createPayment(student, paymentItems, paymentDate);
		
		//update payment mode
		System.out.println("payment mode = " + paymentMode);
		db.begin();
		payment.setPaymentMode(paymentMode);
		payment.setRemark(remark);
		payment.setCheckNo(checkNo);
		payment.setBankName(bankName);
		payment.setDocumentFileName(documentFileName);
		
		for ( InvoiceItem it : invoiceItems ) {
			String itemAmount = getParam("itemAmount_" + it.getId());
			double amount = 0.0d;
			try {
				amount = Double.parseDouble(itemAmount);
			} catch ( Exception e ) { }
			PaidItem paidItem = new PaidItem();
			paidItem.setAmount(amount);
			paidItem.setInvoiceItem(it);
			paidItem.setPayment(payment);
			payment.getPaidItems().add(paidItem);
		}
		
		db.commit();
		
		getStudentAcctStatement2(student);

	}


	private void addPaymentItem() {
		context.remove("is_credit_note");
		
		context.put("now", new Date());
		
		String studentId = getParam("student_id");
		
		String description = getParam("description");
		String amount = getParam("amount");
		String flexi = getParam("flexi");
		String ptptn = getParam("ptptn");
		String resourceFee = getParam("resourceFee");
		
		double _amount = 0.0d;
		try {
			_amount = Double.parseDouble(amount);
		} catch ( Exception e ) {
			_amount = 0.0d;
		}
		if ( _amount != 0.0d ) {
			List<CPaymentItem> paymentItems = (List) session.getAttribute("_payment_items");
			paymentItems.add(new CPaymentItem(description, _amount, flexi, ptptn, resourceFee));
			context.put("payment_items", paymentItems);
			
			double total = 0.0d;
			for ( CPaymentItem p : paymentItems ) {
				total += p.getPaymentAmount();
			}
			context.put("totalPayment", total);
		}
		
		String sql = "select it" +
		" from Invoice i " +
		" join i.invoiceItems it " +
		" where i.student.id = '" + studentId + "' " +
		" order by i.createDate";
		
		List<InvoiceItem> invoiceItems = db.list(sql);
		context.put("invoiceItems", invoiceItems);
		
		PaymentSchedule paymentSchedule = (PaymentSchedule) db.get("select p from PaymentSchedule p where p.student.id = '" + studentId + "'");
		context.put("paymentSchedule", paymentSchedule);
		
		vm = path + "payment.vm";
		
	}
	
	private void addCreditNoteItem() {
		context.put("is_credit_note", true);
		context.put("now", new Date());
				
		String description = request.getParameter("description");
		String amount = request.getParameter("amount");
		double _amount = 0.0d;
		try {
			_amount = Double.parseDouble(amount);
		} catch ( Exception e ) {
			_amount = 0.0d;
		}
		if ( _amount != 0.0d ) {
			List<CPaymentItem> list = (List) session.getAttribute("_payment_items");
			list.add(new CPaymentItem(description, _amount));
			context.put("payment_items", list);
			
			double total = 0.0d;
			for ( CPaymentItem p : list ) {
				total += p.getPaymentAmount();
			}
			context.put("totalPayment", total);
		}
		vm = path + "payment.vm";
		
	}
	
	private void deletePaymentItem() {
		context.remove("is_credit_note");
		
		context.put("now", new Date());
		
		String studentId = getParam("student_id");
		
		String id = getParam("payment_id");
		List<CPaymentItem> list = (List) session.getAttribute("_payment_items");
		int i = 0;
		for ( CPaymentItem p : list ) {
			if ( id.equals(p.getId()) ) {
				list.remove(i);
				break;
			}
			i++;
		}
		context.put("payment_items", list);
		double total = 0.0d;
		for ( CPaymentItem p : list ) {
			total += p.getPaymentAmount();
		}
		context.put("totalPayment", total);
		
		String sql = "select it" +
		" from Invoice i " +
		" join i.invoiceItems it " +
		" where i.student.id = '" + studentId + "' " +
		" order by i.createDate";
		
		List<InvoiceItem> invoiceItems = db.list(sql);
		context.put("invoiceItems", invoiceItems);
		
		
		PaymentSchedule paymentSchedule = (PaymentSchedule) db.get("select p from PaymentSchedule p where p.student.id = '" + studentId + "'");
		context.put("paymentSchedule", paymentSchedule);
		
		vm = path + "payment.vm";
		
	}
	
	private void deleteCreditNoteItem() {
		context.put("is_credit_note", true);
		
		context.put("now", new Date());
		
		String id = getParam("payment_id");
		List<CPaymentItem> list = (List) session.getAttribute("_payment_items");
		int i = 0;
		for ( CPaymentItem p : list ) {
			if ( id.equals(p.getId()) ) {
				list.remove(i);
				break;
			}
			i++;
		}
		context.put("payment_items", list);
		double total = 0.0d;
		for ( CPaymentItem p : list ) {
			total += p.getPaymentAmount();
		}
		context.put("totalPayment", total);
		
		vm = path + "payment.vm";
		
	}

	private void makePayment() {

		context.remove("is_credit_note");
		context.put("today", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		
		session.setAttribute("_payment_items", new ArrayList<CPaymentItem>());
		context.remove("payment_items");
		
		String studentId = getParam("student_id");
		PaymentSchedule paymentSchedule = (PaymentSchedule) db.get("select p from PaymentSchedule p where p.student.id = '" + studentId + "'");
		context.put("paymentSchedule", paymentSchedule);
		
		vm = path + "payment.vm";
		
	}
	
	private void makeCreditNote() {

		context.put("is_credit_note", true);
		context.put("today", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		
		session.setAttribute("_payment_items", new ArrayList<CPaymentItem>());
		context.remove("payment_items");
		
		vm = path + "payment.vm";
	}

	private void getStudentAcctStatement() throws Exception {
		Student student = null;
		String matricNo = request.getParameter("matric_no") != null ? request.getParameter("matric_no") : "" ;
		if ( !"".equals(matricNo)) {
			String sql = "select s from Student s where s.matricNo = '" + matricNo + "'";
			student = (Student) db.get(sql);
		}
		else {
			String studentId = request.getParameter("student_id");
			student = db.find(Student.class, studentId);
		}
		context.put("student", student);
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus currentStatus = u.getCurrentStudentStatus(student);
		context.put("student_status", currentStatus);
		
		getStudentAcctStatement2(student);
		
	}

	private void getStudentAcctStatement2(Student student) {
		context.put("student", student);
		AccountStatementUtil util = new AccountStatementUtil();
		AccountStatement acct = util.getAccountStatement(student);
		
		context.put("account_statement", acct);
		
		context.put("records", acct.getRecords());
		context.put("total_invoiced", acct.getTotalInvoiced());
		context.put("total_paid", acct.getTotalPaid());
		context.put("total_balance", acct.getTotalBalance());
		
		vm = path + "statement.vm";
	}

	private void start() {
		vm = path + "start.vm";
		
	}
	
	
	private void uploadFile() throws Exception {
		String divUploadStatusName = getParam("divUploadStatusName");
		context.put("divUploadStatusName", divUploadStatusName);
		String documentId = getParam("documentId");
		String uploadDir = "c:/finance_uploaded/";
		File dir = new File(uploadDir);
		if ( !dir.exists() ) dir.mkdir();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = upload.parseRequest(request);
		Iterator itr = items.iterator();
		List<FileItem> files = new ArrayList<FileItem>();
		while (itr.hasNext()) {
			FileItem item = (FileItem)itr.next();
			if ((!(item.isFormField())) && (item.getName() != null) && (!("".equals(item.getName())))) {
				if ( documentId.equals(item.getFieldName())) {
					files.add(item);
				}
			}
		}
		for ( FileItem item : files ) {
			String fileName = item.getName();
			String savedName = uploadDir + fileName;
			savedName = savedName.replaceAll(" ", "_");
			context.put("serverFileName", savedName);
			item.write(new File(savedName));
			
			if ( "supportingDocument".equals(documentId)) { 
				System.out.println("uploaded supportingDocument! " + savedName);
				/*
				try {
					InputStream is1 = new FileInputStream(savedName);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				*/
				vm = path + "uploaded.vm";
				break;
			}
			else if ( "supportingDocument2".equals(documentId)) {
				System.out.println("change uploaded supportingDocument! " + savedName);
				String paymentId = getParam("paymentId");
				Payment payment = db.find(Payment.class, paymentId);
				context.put("payment", payment);
				db.begin();
				payment.setDocumentFileName(savedName);
				db.commit();
				
				vm = path + "uploaded2.vm";
				break;
			}
		}
		
	}

}
