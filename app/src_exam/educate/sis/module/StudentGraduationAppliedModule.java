package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class StudentGraduationAppliedModule extends AjaxModule {
	
	String path = "apps/util/graduation_list/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");

		if ( null == command || "".equals(command)) listPrograms();
		else if ( "list_intakes".equals(command)) listIntakes();
		else if ( "list_students".equals(command)) listStudents();
		else if ( "study_type".equals(command)) studyType();
		return vm;
	}
	

	private void studyType() throws Exception {
		vm = path + "study_type.vm";
		String studentId = request.getParameter("student_id");
		String studyType = request.getParameter("study_type_" + studentId);
		
		Student student = db.find(Student.class, studentId);
		db.begin();
		student.setStudyType(studyType);
		db.commit();
		
		System.out.println(student.getStudyType());
		context.put("s", student);
	}


	private void listStudents() {
		vm = path + "list_students.vm";
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		
		Program program = db.find(Program.class, programId);
		Session intake = db.find(Session.class, intakeId);
		
		String sql = "";
		Hashtable param = new Hashtable();
		param.put("program", program);
		param.put("intake", intake);
		sql = "select s from Graduate g JOIN g.student s where (s.fakeStudent is null OR s.fakeStudent = '') and s.program = :program and s.intake = :intake and g.status = 'APPLIED' order by s.biodata.name";
		List<Student> students = db.list(sql, param);
		context.put("students", students);
		
	}


	private void listPrograms() {
		vm = path + "list_programs.vm";
		context.remove("intakes");
		params();
	}
	private void params() {
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs",programs);
		session.setAttribute("programs", programs);
	}
	
	private void listIntakes() {
		vm = path + "list_programs.vm";
		String programId = request.getParameter("program_id");
		if ( "".equals(programId)) {
			listPrograms();
			return;
		}
		context.put("program_id", programId);
		Program program = db.find(Program.class, programId);
		List<Session> intakes = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
		context.put("intakes", intakes);
		session.setAttribute("intakes", intakes);
		
		Hashtable param = new Hashtable();
		param.put("date", new Date());
		param.put("path_no", program.getLevel().getPathNo());
		List list = db.list("select s from Session s where :date between s.startDate and s.endDate and s.pathNo = :path_no", param);
		if ( list.size() > 0 ) {
			Session currentSession = (Session) list.get(0);
			context.put("current_session", currentSession);
			context.put("intake_id", currentSession.getId());
		}
		else {
			context.remove("current_session");
			context.remove("intake_id");
		}
		
		params();
		
	}


}
