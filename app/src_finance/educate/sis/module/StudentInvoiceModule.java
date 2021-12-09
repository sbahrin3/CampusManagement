package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.InvoiceItem;
import educate.sis.finance.entity.SponsorInvoice;
import educate.sis.finance.module.FeeStructureNotAvailableException;
import educate.sis.finance.module.InvoiceUtil;
import lebah.portal.action.AjaxModule;

public class StudentInvoiceModule extends AjaxModule {
	
	String path = "apps/util/student_invoice/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		context.put("today", new Date());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( command == null || "".equals(command)) start();
		else if ( "get_student".equals(command)) getStudent();
		else if ( "create_invoice".equals(command)) createInvoice();
		else if ( "invoice_detail".equals(command)) invoiceDetail();
		else if ( "custom_invoice".equals(command)) customInvoice();
		else if ( "items_count".equals(command)) itemsCount();
		else if ( "save_custom".equals(command)) saveCustom();
		else if ( "save_debit_note".equals(command)) saveDebitNote();
		else if ( "get_invoices".equals(command)) getInvoices();
		else if ( "list_invoices".equals(command)) listInvoices();
		else if ( "attempt_delete_invoice".equals(command)) attemptDeleteInvoice();
		else if ( "delete_invoice".equals(command)) deleteInvoice();
		
		else if ( "give_discount".equals(command)) giveDiscount();
		return vm;
	}
	
	private void giveDiscount() throws Exception {
		String studentStatusId = request.getParameter("student_status_id");
		context.put("student_status_id", studentStatusId);
		
		String invoiceId = request.getParameter("invoice_id");
		Invoice invoice = (Invoice) db.find(Invoice.class, invoiceId);
		context.put("invoice", invoice);
		
		Set<InvoiceItem> items = invoice.getInvoiceItems();
		
		String discountAmount = getParam("discountAmount");
		double amount = 0.0d;
		try {
			amount = Double.parseDouble(discountAmount)*-1;
		} catch ( Exception e) { }
		
		db.begin();
		//get negative invoice item
		InvoiceItem discountItem = null;
		for ( InvoiceItem i : items ) {
			if ( i.getAmount() < 0 ) {
				discountItem = i;
				break;
			}
		}
		if ( discountItem == null ) {
			discountItem = new InvoiceItem();
			discountItem.setDescription("Discount Given");
			discountItem.setAmount(amount);
			items.add(discountItem);
		}
		else {
			discountItem.setAmount(amount);
		}

		
		db.commit();
		vm = path + "invoice_detail.vm";
	}

	private void attemptDeleteInvoice() {
		// 
		String studentStatusId = request.getParameter("student_status_id");
		context.put("student_status_id", studentStatusId);
		String invoiceId = request.getParameter("invoice_id");
		
		Invoice invoice = (Invoice) db.find(Invoice.class, invoiceId);
		context.put("invoice", invoice);
		
		vm = path + "attempt_delete_invoice.vm";
		
	}

	private void getInvoices() throws FeeStructureNotAvailableException {
		String statusId = request.getParameter("student_status_id");
		StudentStatus s = (StudentStatus) db.find(StudentStatus.class, statusId);
		StudentInvoice i = new StudentInvoice();
		i.setStudentStatus(s);
		List<InvoiceItem> items = new InvoiceUtil().getInvoiceItems(s.getStudent(), s.getPeriod());
		i.setInvoiceItems(items);
		List<Invoice> invoices = db.list("select i from Invoice i where i.student.id = '" + s.getStudent().getId() + "' " +
				"and i.session.id = '" + s.getSession().getId() + "' order by i.createDate, i.createTime");
		i.setInvoices(invoices);
		context.put("i", i);
		
		vm = path + "invoice_list_item.vm";
		
	}
	
	private void saveDebitNote() throws Exception {
		saveCustom(true);
	}
	
	private void saveCustom() throws Exception {
		saveCustom(false);
	}

	private void saveCustom(boolean dn) throws Exception {
		// 
		
		String statusId = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.find(StudentStatus.class, statusId);
		
		String invoiceDate = getParam("invoice_date");
		Date date = new SimpleDateFormat("dd-MM-yyyy").parse(invoiceDate);
		
		String[] itemDescs = request.getParameterValues("item_description");
		String[] itemAmounts = request.getParameterValues("item_amount");
		int i=0;
		List<InvoiceItem> items = new ArrayList<InvoiceItem>();
		for ( String descs : itemDescs ) {
			if ( !"".equals(descs.trim())) {
				String amt = itemAmounts[i];
				try {
					double amount = Double.parseDouble(amt);
					InvoiceItem item = new InvoiceItem();
					item.setDescription(descs);
					item.setAmount(amount);
					items.add(item);
				} catch ( Exception e ) {
					//amount is not valid numeric
				}
			}
			i++;
		}
		
		InvoiceUtil iu = new InvoiceUtil();
		if ( dn ) {
			iu.createDebitNote(studentStatus.getStudent(), items, studentStatus.getSession(), date);
		} else {
			iu.createCustomStudentInvoice((SponsorInvoice) null, studentStatus.getStudent(), items, studentStatus.getSession(), date);
		}
		
		vm = path + "empty.vm";
	}

	private void itemsCount() {
		// TODO Auto-generated method stub
		
		String[] itemDescs = request.getParameterValues("item_description");
		String[] itemAmounts = request.getParameterValues("item_amount");
		context.put("item_descs", itemDescs);
		context.put("item_amounts", itemAmounts);
		
		String cnt = request.getParameter("items_count");
		context.put("items_cnt", Integer.parseInt(cnt));
		vm = path + "items_count.vm";
		
	}

	private void customInvoice() {
		String statusId = request.getParameter("student_status_id");
		StudentStatus s = (StudentStatus) db.find(StudentStatus.class, statusId);
		context.put("student_status", s);
		context.put("student_status_id", statusId);
		
		context.remove("item_descs");
		context.remove("item_amounts");
		vm = path + "custom_invoice.vm";
		
	}

	private void deleteInvoice() throws Exception {
		
		String deleteReason = request.getParameter("delete_reason");
		
		
		if ( deleteReason == null || "".equals(deleteReason.trim())) {
			attemptDeleteInvoice();
			return;
		}
		
		String invoiceId = request.getParameter("invoice_id");
		Invoice invoice = (Invoice) db.find(Invoice.class, invoiceId);
		
		if ( invoice.getSponsorInvoice() == null ) {
		
			InvoiceUtil iu = new InvoiceUtil();
			iu.setUserId((String) session.getAttribute("_portal_login"));
			iu.deleteInvoice(invoice, deleteReason);
			
			String statusId = request.getParameter("student_status_id");
			StudentStatus s = (StudentStatus) db.find(StudentStatus.class, statusId);
			context.put("student_status_id", s);
			
			StudentInvoice i = new StudentInvoice();
			i.setStudentStatus(s);
			List<InvoiceItem> items = new InvoiceUtil().getInvoiceItems(s.getStudent(), s.getPeriod());
			i.setInvoiceItems(items);
			List<Invoice> invoices = db.list("select i from Invoice i where i.student.id = '" + s.getStudent().getId() + "' " +
					"and i.session.id = '" + s.getSession().getId() + "' order by i.createDate, i.createTime");
			i.setInvoices(invoices);
			context.put("i", i);
			
		} else {
			System.out.println("This Invoice Belong To a Sponsor, and will not be deleted here.");
		}
		
		//vm = path + "invoice_list_item.vm";
		vm = path + "empty.vm";
		
	}
	
	private void listInvoices() throws Exception {

		String statusId = request.getParameter("student_status_id");
		StudentStatus s = (StudentStatus) db.find(StudentStatus.class, statusId);
		context.put("student_status", s);
		
		StudentInvoice i = new StudentInvoice();
		i.setStudentStatus(s);
		List<InvoiceItem> items = new InvoiceUtil().getInvoiceItems(s.getStudent(), s.getPeriod());
		i.setInvoiceItems(items);
		List<Invoice> invoices = db.list("select i from Invoice i where i.student.id = '" + s.getStudent().getId() + "' " +
				"and i.session.id = '" + s.getSession().getId() + "' order by i.createDate, i.createTime");
		i.setInvoices(invoices);
		context.put("i", i);
		
		vm = path + "invoice_list_item.vm";
		
	}



	private void invoiceDetail() {
		// 
		String studentStatusId = request.getParameter("student_status_id");
		context.put("student_status_id", studentStatusId);
		
		String invoiceId = request.getParameter("invoice_id");
		Invoice invoice = (Invoice) db.find(Invoice.class, invoiceId);
		context.put("invoice", invoice);
		
		vm = path + "invoice_detail.vm";
		
	}


	private void createInvoice() throws Exception {
		vm = path + "invoice_list_item.vm";
		
		InvoiceUtil util = new InvoiceUtil();
		
		String statusId = request.getParameter("student_status_id");
		StudentStatus s = (StudentStatus) db.find(StudentStatus.class, statusId);
		context.put("student_status", s);
		
		String invoiceDate = getParam("invoice_date_" + statusId);
		System.out.println("date of invoice = " + invoiceDate);
		Date date = new SimpleDateFormat("dd-MM-yyyy").parse(invoiceDate);
		
		List<InvoiceItem> items = util.getInvoiceItems(s.getStudent(), s.getPeriod());
		
		util.createStudentInvoice(s.getStudent(), items, s.getSession(), date);
		
		StudentInvoice i = new StudentInvoice();
		i.setStudentStatus(s);
		i.setInvoiceItems(items);
		List<Invoice> invoices = db.list("select i from Invoice i where i.student.id = '" + s.getStudent().getId() + "' " +
		"and i.session.id = '" + s.getSession().getId() + "' order by i.createDate, i.createTime");
		i.setInvoices(invoices);
		context.put("i", i);

	}
	

	private void getStudent() throws Exception {
		vm = path + "student.vm";
		String studentNo = request.getParameter("student_no");
		context.put("student_no", studentNo);
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + studentNo + "'");
		if ( student != null ) {
			context.put("student", student);
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus currentStatus = u.getCurrentStudentStatus(student);
			context.put("student_status", currentStatus);
			List<StudentInvoice> studentInvoiceItems = new ArrayList<StudentInvoice>();
			context.put("invoice_items", studentInvoiceItems);
			//get list of statuses up tu current semester
			for ( StudentStatus s : u.getStudentStatuses(student) ) {
				
				StudentInvoice i = new StudentInvoice();
				studentInvoiceItems.add(i);
				i.setStudentStatus(s);
				List<InvoiceItem> items = new InvoiceUtil().getInvoiceItems(student, s.getPeriod());
				i.setInvoiceItems(items);
				List<Invoice> invoices = db.list("select i from Invoice i where i.student.id = '" + student.getId() + "' " +
				"and i.session.id = '" + s.getSession().getId() + "' order by i.createDate, i.createTime");
				i.setInvoices(invoices);
				
				//TODO: DISABLED THE BREAK
				//list until current status
				if ( s.getId().equals(currentStatus.getId())) {
					break;
				}

			}
			
			//invoices that is out-of-range -- created after student status was long expired
			//StudentStatus currentStatus = new StudentStatusUtil().getCurrentStudentStatus(student);
			//System.out.println("current status = " + currentStatus.getSession().getName() + ", " + currentStatus.getPastStatus());
			List<Invoice> extraInvoices = new ArrayList<Invoice>();
			context.put("extra_invoices", extraInvoices);
			List<Invoice> invoices = db.list("select i from Invoice i where i.student.id = '" + student.getId() + "'");	
			System.out.println("Invoices that are past due....");
			for ( Invoice i : invoices ) {
				if ( i.getSession() != null ) {
					if ( i.getSession().getStartDate().after(currentStatus.getSession().getEndDate())) {
						System.out.println(i.getCreateDate() + ", " + i.getSession().getName() + ", " + i.getAmount());
						extraInvoices.add(i);
					}
				}
			}
		}
		else context.remove("student");
	}
	

	private void start() {
		vm = path + "start.vm";
		
	}

}
