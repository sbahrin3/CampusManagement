package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.module.FeeStructureNotAvailableException;
import educate.sis.finance.module.InvoiceUtil;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class BatchCreateInvoiceModule extends AjaxModule {
	
	String path = "apps/util/batch_create_invoice/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
		else if ( "start_create".equals(command)) startCreate();
		else if ( "create_invoice".equals(command)) createInvoice();
		return vm;
	}

	private void startCreate() {
		String statusId = request.getParameter("status_id");
		context.put("status_id", statusId);
		vm = path + "create_invoice.vm";
	}

	private void createInvoice() throws Exception {
		// 
		String studentStatusId = request.getParameter("student_status_id");
		StudentStatus studentStatus = db.find(StudentStatus.class, studentStatusId);
		
		InvoiceUtil iu = new InvoiceUtil();
		Invoice invoice = null;
		try {
			invoice = iu.createStudentInvoice(studentStatus);
			context.put("invoice", invoice);
		} catch (FeeStructureNotAvailableException e) {
			context.remove("invoice");
			context.put("error", "Fee Structure Not Defined.");
			System.out.println(e.getMessage());
		}
		
		
		String count = request.getParameter("count");
		int index = Integer.parseInt(count) + 1;
		context.put("count", index);
		
		List<StudentStatus> statusList = (List) request.getSession().getAttribute("_status_list");
		String nextId = "";
		if ( statusList != null && index < statusList.size() ) {
			StudentStatus nextStatus = statusList.get(index);
			if ( nextStatus != null ) {
				nextId = nextStatus.getId();
				context.put("next_id", nextId);
			} else context.remove("next_id");
		} else {
			context.remove("next_id");
		}
		
		vm = path + "create_invoice2.vm";
		
	}

	private void listStudents() throws Exception {
		String programId = request.getParameter("program_id");
		context.put("programId", programId);
		String intakeId = request.getParameter("intake_id");
		context.put("intakeId", intakeId);
		String sessionId = request.getParameter("session_id");
		context.put("sessionId", sessionId);
		
		Program program = db.find(Program.class, programId);
		Session intake = null;
		if ( !"".equals(intakeId)) {
			intake = db.find(Session.class, intakeId);
			context.put("intake", intake);
		} else context.remove("intake");
		Session session = db.find(Session.class, sessionId);
		
		context.put("program", program);
		context.put("session", session);
		
		request.getSession().setAttribute("program", program);
		request.getSession().setAttribute("intake", intake);
		request.getSession().setAttribute("session", session);
		
		Hashtable h = new Hashtable();
		h.put("program", program);
		h.put("session", session);
		
		if ( intake != null ) {
			h.put("intake", intake);
			List<StudentStatus> list = db.list("select s from StudentStatus s where s.student.program = :program and s.student.intake = :intake and s.session = :session and s.type.inActive = 0 order by s.student.biodata.name", h);
			context.put("students", list);
			request.getSession().setAttribute("_status_list", list);
		}
		else {
			List<StudentStatus> list = db.list("select s from StudentStatus s where s.student.program = :program and s.session = :session and s.type.inActive = 0 order by s.student.biodata.name", h);
			context.put("students", list);
			request.getSession().setAttribute("_status_list", list);
		}
		
		vm = path + "list_students.vm";
		
	}

	private void start() {
		
		listPrograms();
		
		vm = path + "start.vm";
		
	}
	
	private void listIntakes() {
		
		String programId = request.getParameter("program_id");
		context.put("program_id", programId);
		Program program = db.find(Program.class, programId);
		
		if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
		
		int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
		
		List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("intakes", intakes);
		vm = path + "list_intakes.vm";
		
	}
	
	private void listAllSessions() throws Exception {
		
		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		
		int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
		
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("sessions", sessions);

		Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
		context.put("current_session", currentSession);
		
		vm = path + "list_sessions.vm";
	}
	
	private void listSessions() throws Exception {
		
		String programId = request.getParameter("program_id");
		Program program = db.find(Program.class, programId);
		
		String intakeId = request.getParameter("intake_id");
		Session intake = db.find(Session.class, intakeId);
		
		int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
		
		Hashtable h = new Hashtable();
		h.put("date", intake.getStartDate());
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " and s.startDate >= :date order by s.startDate", h);
		context.put("sessions", sessions);

		Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
		context.put("current_session", currentSession);
		
		vm = path + "list_sessions.vm";
	}

	private void listPrograms() {
		
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs",programs);
		
		vm = path + "list_programs.vm";
	}
	
}
