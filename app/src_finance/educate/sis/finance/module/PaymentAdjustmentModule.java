package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Payment;
import educate.sis.finance.entity.PaymentItem;
import educate.sis.finance.entity.PaymentSchedule;
import educate.sis.finance.entity.PaymentScheduleItem;
import educate.sis.finance.entity.XPayment;
import educate.sis.module.StudentStatusUtil;
import lebah.portal.action.AjaxModule;

public class PaymentAdjustmentModule  extends AjaxModule {
	
	String path = "apps/util/finance/payment_adjustment/";
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
		else if ( "get_student".equals(command)) listPayments();
		else if ( "list_payments".equals(command)) listPayments2();
		else if ( "payment_info".equals(command)) paymentInfo();
		else if ( "make_adjustment".equals(command)) makeAdjustment();
		return vm;
	}

	private void makeAdjustment() throws Exception {
		vm = path + "adjustment.vm";
		String userId = request.getSession().getAttribute("_portal_login") != null ?
				(String) request.getSession().getAttribute("_portal_login") : "";
		String paymentId = request.getParameter("payment_id");
		Payment payment = db.find(Payment.class, paymentId);
		context.put("payment", payment);
		context.put("student", payment.getStudent());
		
		//detect changes first
		boolean changed = false;
		Set<PaymentItem> items = payment.getPaymentItems();
		for ( PaymentItem i : items ) {
			String amt = request.getParameter("item_" + i.getId());
			System.out.println(i.getAmount() + " -> " + amt);
			double amount = 0.0d;
			try {
				amount = Double.parseDouble(amt);
			} catch ( Exception e ) {}
			if ( i.getAmount() != amount ) {
				changed = true;
				break;
			}
		}
		
		if ( changed ) {
			
			//remarks
			String adjustmentRemarks = getParam("adjustmentRemarks");
			
			//get paymentadjustment if there's any
			//old payment must be created for archive
			db.begin();
			XPayment xpayment = new XPayment();
			xpayment.setPayment(payment);
			xpayment.setPaymentNo(payment.getPaymentNo());
			//amount will be calculated when items are added
			xpayment.setStudent(payment.getStudent());
			xpayment.setCreateDate(payment.getCreateDate());
			xpayment.setCreateTime(payment.getCreateTime());
			if ( payment.getAdjustDate() != null ) xpayment.setAdjustDate(payment.getAdjustDate());
			if ( payment.getAdjustTime() != null ) xpayment.setAdjustTime(payment.getAdjustTime());
			xpayment.setAdjusted(payment.getAdjusted());
			xpayment.setAdjustSeq(payment.getAdjustSeq());
			if ( payment.getAdjustUserId() != null ) xpayment.setAdjustUserId(payment.getAdjustUserId());
			if ( payment.getCreateUserId() != null ) xpayment.setCreateUserId(payment.getCreateUserId());
			//copy all the items
			Set<PaymentItem> paymentItems = payment.getPaymentItems();
			for ( PaymentItem i : paymentItems ) {
				PaymentItem paymentItem = new PaymentItem();
				paymentItem.setAmount(i.getAmount());
				paymentItem.setDescription(i.getDescription());
				xpayment.addPaymentItems(paymentItem);	
			}
			db.persist(xpayment);
			db.commit();
			
			//now update the real payment
			db.begin();
			items = payment.getPaymentItems();
			double total = 0.0d;
			for ( PaymentItem i : items ) {
				double originalAmount = i.getAmount();
				String amt = request.getParameter("item_" + i.getId());
				double amount = 0.0d;
				try {
					amount = Double.parseDouble(amt);
				} catch ( Exception e ) {}
				if ( i.getAmount() != amount ) {
					i.setAmount(amount);
				}
				double adjustedAmount = amount - originalAmount;
				i.setAdjustedAmount(adjustedAmount);
				total += i.getAmount();
			}
			//remarks
			payment.setAdjustmentRemarks(adjustmentRemarks);
			
			payment.setAmount(total);
			payment.setAdjustDate(new Date());
			payment.setAdjustTime(new Date());
			int seq = payment.getAdjustSeq() + 1;
			payment.setAdjustSeq(seq);
			payment.setAdjustUserId(userId);
			payment.setAdjusted(true);
			db.commit();
			
			
			//update payment schedule items
			PaymentSchedule paymentSchedule = null;
			for ( PaymentItem i : items ) {
				PaymentScheduleItem scheduleItem = i.getScheduleItem();
				if ( scheduleItem != null ) {
					if ( paymentSchedule == null ) paymentSchedule = scheduleItem.getPaymentSchedule();
					double amountPaid = scheduleItem.getAmountPaid() + i.getAdjustedAmount();
					db.begin();
					scheduleItem.setAmountPaid(amountPaid);
					db.commit();
				}
			}
			if ( paymentSchedule != null ) {
				PaymentUtil.calculatePaymentSchedules(db, paymentSchedule);
			}
			
			//list all adjustments
			String sql = "select x from XPayment x where x.payment.id = '" + payment.getId() + "' order by x.adjustSeq";
			List<XPayment> xpayments = db.list(sql);
			context.put("xpayments", xpayments);

		}
		else {
			vm = path + "payment_info.vm";
			System.out.println("No changes... NO ADJUSTMENT!");
		}
	}

	private void paymentInfo() {
		vm = path + "payment_info.vm";
		String paymentId = request.getParameter("payment_id");
		Payment payment = db.find(Payment.class, paymentId);
		context.put("payment", payment);
		context.put("student", payment.getStudent());
		
	}

	private void listPayments() throws Exception {
		vm = path + "list_payments.vm";
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
		
		String sql = "select p from Payment p where p.student.id = '" + student.getId() + "' order by p.createDate";
		List<Payment> payments = db.list(sql);
		context.put("payments", payments);
	}
	
	private void listPayments2() throws Exception {
		vm = path + "list_payments2.vm";

		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);

		
		String sql = "select p from Payment p where p.student.id = '" + student.getId() + "' order by p.createDate";
		List<Payment> payments = db.list(sql);
		context.put("payments", payments);
	}
	
	

	private void start() {
		vm = path + "get_student.vm";
		
	}

}
