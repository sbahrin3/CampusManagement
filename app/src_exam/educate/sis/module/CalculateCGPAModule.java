package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.exam.entity.FinalResult;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class CalculateCGPAModule extends AjaxModule {
	
	String path = "apps/util/cgpa/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "list_sessions".equals(command)) listSessions();
		else if ( "list_students".equals(command)) listStudents();
		else if ( "calculate_cgpa_all".equals(command)) calCGPAall();
		else if ( "calculate_cgpa_student".equals(command)) calGPAstudent();
		else if ( "cgpa_student".equals(command)) cgpaStudent();
		return vm;
	}

	private void cgpaStudent() {
		vm = path + "cal_student.vm";
		String studentId = request.getParameter("student_id");
		String programId = request.getParameter("program_id");
		String sessionId = request.getParameter("session_id");
		
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		
		List<FinalResult> results = db.list(sql);
		if ( results.size() > 0 ) {
			context.put("results", results);
		}
		else {
			context.remove("results");
		}
		
	}

	private void calGPAstudent() throws Exception {
		vm = path + "cal_student.vm";
		String studentId = request.getParameter("student_id");
		String programId = request.getParameter("program_id");
		String sessionId = request.getParameter("session_id");
		
		
		Student student = db.find(Student.class, studentId);

		context.put("student", student);
		ResultEntryUtil.calculateAllCGPA(db, student);
		
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		
		List<FinalResult> results = db.list(sql);
		if ( results.size() > 0 ) {
			context.put("results", results);
		}
		else {
			context.remove("results");
		}
	
	}
	


	private void calCGPAall() throws Exception {
		
		vm = path + "list_students.vm";

		String programId = request.getParameter("program_id");
		String sessionId = request.getParameter("session_id");
		
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		int pathNo = program.getLevel().getPathNo();
		
		Session session = db.find(Session.class, sessionId);
		context.put("intake", session);
		
		List<Student> students = db.list("select s FROM StudentStatus st JOIN st.student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
				"and s.program.id = '" + programId + "' and st.session.id = '" + sessionId + "' order by s.biodata.name");
		context.put("students", students);
		
		for ( Student student : students ) {
			System.out.println(student.getBiodata().getName());
			ResultEntryUtil.calculateAllCGPA(db, student);
		}
		

	}

	private void listStudents() {
		
		vm = path + "list_students.vm";

		String programId = request.getParameter("program_id");
		String sessionId = request.getParameter("session_id");
		
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		int pathNo = program.getLevel().getPathNo();
		
		Session session = db.find(Session.class, sessionId);
		context.put("intake", session);
		
		List<Student> students = db.list("select s FROM StudentStatus st JOIN st.student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
				"and s.program.id = '" + programId + "' and st.session.id = '" + sessionId + "' order by s.biodata.name");
		context.put("students", students);
		

		
	}

	private void listSessions() {
		vm = path + "select_param.vm";
		
		String programId = request.getParameter("program_id");
		
		if ( !"".equals(programId)) {
		
			Program program = db.find(Program.class, programId);
			context.put("program", program);
			
			int pathNo = program.getLevel().getPathNo();
			
			List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
			context.put("sessions", sessions);
		
		}
		
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		
		context.remove("students");
		
	}

	private void start() {
		vm = path + "select_param.vm";
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		context.remove("sessions");
		context.remove("students");
	}

}
