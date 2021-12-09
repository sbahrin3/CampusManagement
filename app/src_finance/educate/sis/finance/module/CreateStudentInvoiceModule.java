package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.InvoiceItem;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class CreateStudentInvoiceModule  extends AjaxModule {
	
	String path = "apps/util/finance/student_invoice/";
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
		else if ( "select_period".equals(command)) selectPeriod();
		else if ( "create_invoice".equals(command)) createInvoice();
		else if ( "delete_invoice".equals(command)) deleteInvoice();
		return vm;
	}

	private void deleteInvoice() throws Exception {
		vm = path + "select_period.vm";

		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		Period currentPeriod = studentStatus.getPeriod();
		Session currentSession = studentStatus.getSession();
		
		context.put("current_period", currentPeriod);
		context.put("current_session", currentSession);
		
		Program program = student.getProgram();
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		String invoiceId = request.getParameter("invoice_id");
		Invoice invoice = db.find(Invoice.class, invoiceId);
		db.begin();
		db.remove(invoice);
		db.commit();
		
		String sql = "select inv from Invoice inv where inv.student.id = '" + student.getId() + "' order by inv.createDate, inv.createTime";
		List<Invoice> invoices = db.list(sql);
		context.put("invoices", invoices);
		
	}


	private void createInvoice() throws Exception {
		vm = path + "select_period.vm";

		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		Period currentPeriod = studentStatus.getPeriod();
		Session currentSession = studentStatus.getSession();
		
		context.put("current_period", currentPeriod);
		context.put("current_session", currentSession);
		
		String periodId = request.getParameter("period_id");
		Period period = db.find(Period.class, periodId);
		
		InvoiceUtil iu = new InvoiceUtil();
		iu.createStudentInvoice(student, currentSession);

		
		String sql = "select inv from Invoice inv where inv.student.id = '" + student.getId() + "' order by inv.createDate, inv.createTime";
		List<Invoice> invoices = db.list(sql);
		context.put("invoices", invoices);
		
	}
	
	private void selectPeriod() throws Exception {
		vm = path + "select_period.vm";
		String studentNo = request.getParameter("student_no");
		String sql = "select s from Student s where s.matricNo = '" + studentNo + "'";
		Student student = (Student) db.get(sql);
		if ( student == null ) {
			return;
		}
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		Period currentPeriod = studentStatus.getPeriod();
		Session currentSession = studentStatus.getSession();
		
		context.put("current_period", currentPeriod);
		context.put("current_session", currentSession);
		
		Program program = student.getProgram();
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		Set<StudentStatus> statuses = student.getStatus();
		
		sql = "select inv from Invoice inv where inv.student.id = '" + student.getId() + "' order by inv.createDate, inv.createTime";
		List<Invoice> invoices = db.list(sql);
		context.put("invoices", invoices);
		
	}

	private void start() {
		vm = path + "get_student.vm";
		
	}
	

	public static void main(String[] args) throws FeeStructureNotAvailableException {
		DbPersistence db = new DbPersistence();
		CreateStudentInvoiceModule m = new CreateStudentInvoiceModule();
		String studentNo = "E60102090056";
		String sql = "select s from Student s where s.matricNo = '" + studentNo + "'";
		Student student = (Student) db.get(sql);
		List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
		Period period = periods.get(0);
		InvoiceUtil u = new InvoiceUtil();
		List<InvoiceItem> items = new InvoiceUtil().getInvoiceItems(student, period, false); // new InvoiceUtil().getInvoiceItems(student.getProgram(), student.getIntake(), period, student.getLearningCenter());
		
		for ( InvoiceItem item : items ) {
			String code = "", description = "";
			double amount = 0.0;
			if ( item.getFeeItem() != null ) code = item.getFeeItem().getCode();
			else code = "";
			description = item.getDescription();
			amount = item.getAmount();
			System.out.println(code + ", " + description + ", " + amount);
		}
	}

}
