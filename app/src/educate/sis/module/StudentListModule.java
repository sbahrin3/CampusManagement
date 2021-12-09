package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class StudentListModule extends AjaxModule {
	
	String path = "apps/util/student_list/";
	private String vm = "empty.vm";
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
		if ( "list_all_sessions".equals(command)) System.out.println("WTH");
		if ( command == null || "".equals(command)) start();
		else if ( "list_intakes".equals(command)) listIntakes();
		else if ( "list_sessions".equals(command)) listSessions();
		else if ( "list_students".equals(command)) listStudents();
		return vm;
	}

	private void listStudents() {
		String programId = getParam("program_id");
		String intakeId = getParam("intake_id");
		String sessionId = getParam("session_id");
		String statusId = getParam("status_id");
		
		Program program = !"".equals(programId) ? (Program) db.find(Program.class, programId) : null;
		Session intake = !"".equals(intakeId) ? (Session) db.find(Session.class, intakeId) : null;
		Session session = !"".equals(sessionId) ? (Session) db.find(Session.class, sessionId) : null;
		StatusType status = !"".equals(statusId) ? (StatusType) db.find(StatusType.class, statusId) : null;
		
		if ( program != null ) context.put("program", program); else context.remove("program");
		if ( intake != null ) context.put("intake", intake); else context.remove("intake");
		if ( session != null ) context.put("session", session); else context.remove("session");
		if ( status != null ) context.put("status", status); else context.remove("status");
		
		Hashtable h = new Hashtable();
		List<StudentStatus> studentStatus = new ArrayList<StudentStatus>();
		if ( program != null && intake != null && session != null ) {
			h.put("program", program);
			h.put("intake", intake);
			h.put("session", session);
			
			if ( status != null ) {
				h.put("status", status);
				studentStatus = db.list("select s from StudentStatus s where s.student.program = :program and s.student.intake = :intake and s.session = :session and s.type = :status and (s.student.fakeStudent is null OR s.student.fakeStudent = '') order by s.student.biodata.name", h);
			} else
				studentStatus = db.list("select s from StudentStatus s where s.student.program = :program and s.student.intake = :intake and s.session = :session and (s.student.fakeStudent is null OR s.student.fakeStudent = '') order by s.student.biodata.name", h);
		}
		else if ( program == null && intake != null && session != null ) {
			h.put("intake", intake);
			h.put("session", session);
			
			if ( status != null ) {
				h.put("status", status);
				studentStatus = db.list("select s from StudentStatus s where s.student.intake = :intake and s.session = :session and s.type = :status and (s.student.fakeStudent is null OR s.student.fakeStudent = '') order by s.student.biodata.name", h);
			}else
				studentStatus = db.list("select s from StudentStatus s where s.student.intake = :intake and s.session = :session and (s.student.fakeStudent is null OR s.student.fakeStudent = '') order by s.student.biodata.name", h);
					
					
		}
		else if ( program == null && intake == null && session != null ) {
			h.put("session", session);
			
			if ( status != null ) {
				h.put("status", status);
				studentStatus = db.list("select s from StudentStatus s where s.session = :session and s.type = :status and (s.student.fakeStudent is null OR s.student.fakeStudent = '') order by s.student.biodata.name", h);
			} else
				studentStatus = db.list("select s from StudentStatus s where s.session = :session and (s.student.fakeStudent is null OR s.student.fakeStudent = '') order by s.student.biodata.name", h);
		}
		else if ( program != null && intake == null && session != null ) {
			h.put("program", program);
			h.put("session", session);
			
			
			if ( status != null ) {
				h.put("status", status);
				studentStatus = db.list("select s from StudentStatus s where s.student.program = :program and s.session = :session and s.type = :status and (s.student.fakeStudent is null OR s.student.fakeStudent = '') order by s.student.biodata.name", h);
			} else
				studentStatus = db.list("select s from StudentStatus s where s.student.program = :program and s.session = :session and (s.student.fakeStudent is null OR s.student.fakeStudent = '') order by s.student.biodata.name", h);
		
	
		}
		
		List<Map<String, Object>> studentData = new ArrayList<Map<String, Object>>();
		context.put("studentData", studentData);
		
		DecimalFormat f = new DecimalFormat("#,###,###.00");
		for ( StudentStatus ss : studentStatus ) {
			Student student = ss.getStudent();
			double _amount = 0.0d;
			Double amount = (Double) db.get("select sum(p.amount) from Payment p where p.student.id = '" + student.getId() + "'");
			if ( amount != null ) {
				_amount = amount.doubleValue();
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("matricno", ss.getStudent().getMatricNo());
			map.put("icno", ss.getStudent().getBiodata().getIcno());
			map.put("name", ss.getStudent().getBiodata().getName());
			map.put("session", ss.getSession().getName());
			map.put("semester", ss.getPeriod().getParent() != null ? ss.getPeriod().getName() + "(" + ss.getPeriod().getParent().getName() + ")" : ss.getPeriod().getName() );
			map.put("status", ss.getType().getName());
			map.put("amountPaid", f.format(_amount));
			map.put("amountPaidValue", _amount);
			
			studentData.add(map);
		}
		
		context.put("students", studentStatus);
		
		//excel
		request.getSession().setAttribute("studentData", studentData);
	
		vm = path + "list_students.vm";
	}

	private void start() throws Exception {
		
		listPrograms();
		//listIntakes();
		//listSessions();
		listStatuses();
		
		vm = path + "start.vm";
		
	}
	
	private void listStatuses() {
		List<StatusType> statuses = db.list("select t from StatusType t order by t.sequence");
		context.put("statuses", statuses);
		
	}

	private void listIntakes() throws Exception {

		String sql = "";
		String programId = request.getParameter("program_id");
		context.put("program_id", programId);

		if ( programId != null && !"".equals(programId)) {
			Program program = db.find(Program.class, programId);
			if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
			//int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
			sql = "select distinct i from StudentStatus ss join ss.student s join s.intake i where s.program.id = '" + programId + "'  order by i.startDate";
		}
		else {
			sql = "select s from Session s order by s.startDate";
		}

		List<Session> intakes = db.list(sql);
		context.put("intakes", intakes);
		
		vm = path + "list_intakes.vm";
		
	}
	
	private void listSessions() throws Exception {
		
		String sql = "";
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String intakeId = request.getParameter("intake_id") != null ? request.getParameter("intake_id") : "";

		if ( !"".equals(programId) && !"".equals(intakeId) ) {
			Program program = db.find(Program.class, programId);
			int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
			Session intake = db.find(Session.class, intakeId);
			if ( intake.getStartDate() != null ) {
				Hashtable h = new Hashtable();
				h.put("date", intake.getStartDate());
				sql = "select distinct s from StudentStatus ss join ss.session s join ss.student st where st.program.id = '" + programId + "' and st.intake.id ='" + intakeId + "' and s.pathNo = " + pathNo + " and s.startDate >= :date order by s.startDate";
				List<Session> sessions = db.list(sql, h);
				context.put("sessions", sessions);
			} else {
				context.remove("sessions");
				System.out.println("Intake " + intake.getCode() + " has no start date");
			}
			Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
			context.put("current_session", currentSession);
		}
		else if ( "".equals(programId) && !"".equals(intakeId) ) {
			Session intake = db.find(Session.class, intakeId);
			if ( intake.getStartDate() != null ) {
				Hashtable h = new Hashtable();
				h.put("date", intake.getStartDate());
				sql = "select s from Session s where s.startDate >= :date order by s.startDate";
				List<Session> sessions = db.list(sql, h);
				context.put("sessions", sessions);
			} else {
				context.remove("sessions");
				System.out.println("Intake " + intake.getCode() + " has no start date");
			}
			Session currentSession = new StudentStatusUtil().getCurrentSession(0);
			context.put("current_session", currentSession);
		}
		else if ( !"".equals(programId) && "".equals(intakeId) ) {
			Program program = db.find(Program.class, programId);
			int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
			sql = "select s from Session s where s.pathNo = " + pathNo + " order by s.startDate";
			List<Session> sessions = db.list(sql);
			context.put("sessions", sessions);
			Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
			context.put("current_session", currentSession);
		}
		else if ( "".equals(programId) && "".equals(intakeId) ) {
			sql = "select s from Session s order by s.startDate";
			List<Session> sessions = db.list(sql);
			context.put("sessions", sessions);
			Session currentSession = new StudentStatusUtil().getCurrentSession(0);
			context.put("current_session", currentSession);
		}
		vm = path + "list_sessions.vm";
	}

	private void listPrograms() {
		
		context.remove("intakes");
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs",programs);
		
		vm = path + "list_programs.vm";
	}
	

}
