package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.RefundLog;
import educate.sis.finance.module.InvoiceUtil;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class StudentRefundModule extends AjaxModule {
	
	protected String path = "apps/util/student_refund/";
	protected  String vm = "default.vm";
	protected HttpSession session;
	protected DbPersistence db = new DbPersistence();
	protected boolean outstandingModule = false;

	@Override
	public String doAction() throws Exception {
		if ( outstandingModule ) context.put("list_outstanding", true); else context.remove("list_outstanding");
		session = request.getSession();
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( command == null || "".equals(command)) start();
		else if ( "list_intakes".equals(command)) listIntakes();
		else if ( "list_all_sessions".equals(command)) listAllSessions();
		else if ( "list_sessions".equals(command)) listSessions();
		else if ( "list_students".equals(command)) listStudents();
		else if ( "make_refund".equals(command)) makeRefund();	
		else if ( "bulk_refunds".equals(command)) bulkRefunds();
		return vm;
	}
	
	private void bulkRefunds() {
		vm = path + "bulk_refunds.vm";
		
		try {
			context.put("cnts", Integer.parseInt(request.getParameter("cnts")));
		} catch ( Exception e ) { context.put("cnts", 0) ;}
		
		
		String sessionId = request.getParameter("session_id");
		String[] ids = request.getParameterValues("student_ids");
		
		String remark = request.getParameter("bulk_remark");
		Map<String, Double> amounts = new HashMap<String, Double>();
		Map<String, Invoice> invoices = new HashMap<String, Invoice>();
		context.put("amounts", amounts);
		context.put("invoices", invoices);
		if ( ids != null ) {
			context.put("student_ids", ids);
			Session session = (Session) db.find(Session.class, sessionId);
			for ( String id : ids ) {
				String _balanceAmount = request.getParameter("balance_amount_" + id);
				_balanceAmount = _balanceAmount.replaceAll(",", "");
				double balanceAmount = 0.0d;
				try {
					 balanceAmount = Double.parseDouble(_balanceAmount);
				} catch ( Exception e ) {}
				
				String _refundAmount = request.getParameter("amount_" + id);
				_refundAmount = _refundAmount.replaceAll(",", "");
				double refundAmount = 0.0d;
				try {
					 refundAmount = Double.parseDouble(_refundAmount);
				} catch ( Exception e ) {}
				double newbalanceAmount = balanceAmount - refundAmount;
				amounts.put(id, newbalanceAmount);
				Student student = (Student) db.find(Student.class, id);
				
				Invoice invoice = createRefund(student, session, refundAmount, remark);
				invoices.put(id, invoice);
			}
		}
		
	}

	private void makeRefund() {
		String studentId = request.getParameter("student_id");
		context.put("student_id", studentId);
		
		Student student = (Student) db.find(Student.class, studentId);
		String sessionId = request.getParameter("session_id");
		Session session = (Session) db.find(Session.class, sessionId);
		String _totalRefundAmount = request.getParameter("total_refund_amount");
		double totalRefundAmount = 0.0d;
		try {
			totalRefundAmount = Double.parseDouble(_totalRefundAmount);
		} catch ( Exception e ) {}
		
		String _refundAmount = request.getParameter("refund_amount_" + studentId);
		_refundAmount = _refundAmount.replaceAll(",", "");
		double refundAmount = 0.0d;
		try {
			 refundAmount = Double.parseDouble(_refundAmount);
		} catch ( Exception e ) {}
		
		double balanceAmount = totalRefundAmount - refundAmount;
		context.put("balance_amount", balanceAmount);
		
		String remark = request.getParameter("refund_remark_" + studentId);
		
		createRefund(student, session, refundAmount, remark);
		
		
		
		vm =  path + "refund_created.vm";
		
	}

	private Invoice createRefund(Student student, Session session, double refundAmount, String remark) {
		InvoiceUtil u = new InvoiceUtil();
		String userId = (String) request.getSession().getAttribute("_portal_login");
		u.setUserId(userId);
		Invoice invoice = null;
		try {
			invoice = u.createRefund(student, session, refundAmount, remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
		context.put("invoice", invoice);
		//add to RefundLog
		if ( invoice != null ) {
			db.begin();
			RefundLog log = new RefundLog();
			log.setCreateDate(new Date());
			log.setCreateTime(new Date());
			log.setUserId(userId);
			log.setInvoiceId(invoice.getId());
			log.setInvoiceNo(invoice.getInvoiceNo());
			log.setRefundAmount(invoice.getAmount());
			log.setStudentId(invoice.getStudent().getId());
			log.setStudentMatricNo(invoice.getStudent().getMatricNo());
			log.setStudentName(invoice.getStudent().getBiodata().getName());
			log.setRemark(remark);
			db.persist(log);
			try {
				db.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else context.remove("invoice");
		return invoice;
	}

	private void listStudents() {
		//System.out.println("free memory = " + Runtime.getRuntime().freeMemory() + "/" + Runtime.getRuntime().totalMemory());
		
		
		String programId = getParam("program_id");
		String intakeId = getParam("intake_id");
		//String sessionId = getParam("session_id");
		//String listType = getParam("list_type");
		
		context.put("programId", !"".equals(programId) ?  programId : "null");
		context.put("intakeId", !"".equals(intakeId) ?  intakeId : "null");
		//context.put("sessionId", !"".equals(sessionId) ?  sessionId : "null");
		//context.put("listType", !"".equals(listType) ?  listType : "null");
		
		List<RefundItem> students = RefundItem.getRefundList(db, programId, intakeId);
		//System.out.println("Refund List: " + students.size());
		context.put("students", students);
		vm = path + "list_students.vm";
		
		//System.out.println("free memory = " + Runtime.getRuntime().freeMemory() + "/" + Runtime.getRuntime().totalMemory());
		
	}



	private void start() {
		listPrograms();
		vm = path + "start.vm";
		
	}
	
	private void listIntakes() {
		
		String programId = request.getParameter("program_id");
		context.put("program_id", programId);
		if ( "all".equals(programId)) {
			List<Session> sessions = db.list("select s from Session s order by  s.pathNo, s.startDate");
			context.put("intakes", sessions);
			
		}
		else {
			
			Program program = (Program) db.find(Program.class, programId);
			
			if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
			
			String sql = "select distinct i from StudentStatus ss join ss.student s join s.intake i where ss.type.inActive = 0 and s.program.id = '" + programId + "'  order by i.startDate";
			List<Session> intakes = db.list(sql);
			context.put("intakes", intakes);
		}
		vm = path + "list_intakes.vm";
		
	}
	
	private void listAllSessions() throws Exception {
		
		String programId = request.getParameter("program_id");
		
		if ( "all".equals(programId)) {
			
			List<Session> sessions = db.list("select s from Session s order by  s.pathNo, s.startDate");
			context.put("sessions", sessions);
			
			Session currentSession = new StudentStatusUtil().getCurrentSession(0);
			context.put("current_session", currentSession);
			
		}
		else {
			Program program = (Program) db.find(Program.class, programId);
			
			int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
			
			List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
			context.put("sessions", sessions);
			
			Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
			context.put("current_session", currentSession);

		}

		
		vm = path + "list_sessions.vm";
	}
	
	private void listSessions() throws Exception {

		String intakeId = request.getParameter("intake_id");
		Session intake = (Session) db.find(Session.class, intakeId);

		String programId = request.getParameter("program_id");
		if ( "all".equals(programId)) {
			
			List<Session> sessions = db.list("select s from Session s order by  s.pathNo, s.startDate");
			context.put("sessions", sessions);
			
			Session currentSession = new StudentStatusUtil().getCurrentSession(0);
			context.put("current_session", currentSession);

			
		}
		else {
			Program program = (Program) db.find(Program.class, programId);
			
			int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
			
			Hashtable h = new Hashtable();
			h.put("date", intake.getStartDate());
			String sql = "select distinct s from StudentStatus ss join ss.session s join ss.student st where ss.type.inActive = 0 and st.program.id = '" + programId + "' and st.intake.id ='" + intakeId + "' and s.pathNo = " + pathNo + " and s.startDate >= :date order by s.startDate";
			
			List<Session> sessions = db.list(sql, h);
			context.put("sessions", sessions);
	
			Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
			context.put("current_session", currentSession);
		}
		vm = path + "list_sessions.vm";
	}

	private void listPrograms() {
		
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs",programs);
		
		vm = path + "list_programs.vm";
	}
	
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		
		String sql2 = "select new educate.sis.module.SAmount(i.invoice.student.matricNo, SUM(i.amount), i.invoice.student) " +
		"from educate.sis.finance.entity.InvoiceItem i where i.invoice.student.matricNo = '0908-1213' group by i.invoice.student.id";
		
		List<SAmount> list1 = db.list(sql2);
		for ( SAmount s : list1 ) {
			System.out.println(s.getAmount());
		}
		
		sql2 = "select new educate.sis.module.SAmount(i.payment.student.matricNo, SUM(i.amount), i.payment.student) " +
		"from educate.sis.finance.entity.PaymentItem i where i.payment.student.matricNo = '0908-1213' group by i.payment.student.id";
		
		List<SAmount> list2 = db.list(sql2);
		for ( SAmount s : list2 ) {
			System.out.println(s.getAmount());
		}
		
		
	}
	
	

}
