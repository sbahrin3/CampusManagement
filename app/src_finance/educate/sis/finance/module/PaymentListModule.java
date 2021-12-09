package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Payment;
import educate.sis.finance.entity.XPayment;
import educate.sis.module.StudentStatusUtil;
import lebah.portal.action.AjaxModule;

public class PaymentListModule  extends AjaxModule {
	
	String path = "apps/util/finance/payment_list/";
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
		return vm;
	}

	private void paymentInfo() {
		vm = path + "payment_info.vm";
		String paymentId = request.getParameter("payment_id");
		Payment payment = db.find(Payment.class, paymentId);
		context.put("payment", payment);
		context.put("student", payment.getStudent());
		//list all adjustments
		String sql = "select x from XPayment x where x.payment.id = '" + payment.getId() + "' order by x.adjustSeq desc";
		List<XPayment> xpayments = db.list(sql);
		context.put("xpayments", xpayments);
		
	}
	
	private void listPayments2() throws Exception {
		vm = path + "payments.vm";
		String studentId = request.getParameter("student_id");
		String sql = "select p from Payment p where p.student.id = '" + studentId + "' order by p.createDate";
		List<Payment> payments = db.list(sql);
		context.put("payments", payments);
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
	
	

	private void start() {
		vm = path + "start.vm";
		
	}

}
