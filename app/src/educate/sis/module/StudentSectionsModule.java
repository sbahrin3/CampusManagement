package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.AjaxModule;


public class StudentSectionsModule  extends AjaxModule {
	
	String path = "apps/util/student_sections/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;

	
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
		else if ( "list_subjects".equals(command)) listSubjects();
		else if ( "list_students".equals(command)) listStudents();
		else if ( "update_section".equals(command)) updateSection();
		return vm;
	}
	
	private void updateSection() throws Exception {
		
		String id = request.getParameter("student_subject_id");
		StudentSubject studentSubject = (StudentSubject) db.find(StudentSubject.class, id);
		
		String sectionId = request.getParameter("section_" + id);
		SubjectSection subjectSection = (SubjectSection) db.find(SubjectSection.class, sectionId);
		
		db.begin();
		studentSubject.setSection(subjectSection);
		db.commit();
		
		vm = path + "empty.vm";
		
		
	}

	private void listStudents() throws Exception {
		
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.code");
		context.put("sections", sections);
		
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
		String periodId = request.getParameter("period_id") != null ? request.getParameter("period_id") : "";
		String subjectId = request.getParameter("subject_id") != null ? request.getParameter("subject_id") : "";
		
		Program program = !"".equals(programId) ? (Program) db.find(Program.class, programId) : null;
		Session session = !"".equals(sessionId) ? (Session) db.find(Session.class, sessionId) : null;
		Period period =  !"".equals(periodId) ? (Period) db.find(Period.class, periodId) : null;
		Subject subject = !"".equals(subjectId) ? (Subject) db.find(Subject.class, subjectId) : null;
		
		if ( program != null ) context.put("program", program); else context.remove("program");
		if ( session != null ) context.put("session", session); else context.remove("session");
		if ( period != null ) context.put("period", period); else context.remove("period");
		if ( subject != null ) context.put("subject", subject); else context.remove("subject");
		
		Hashtable param = new Hashtable();
		param.put("session", session);
		param.put("period", period);
		param.put("program", program);
		param.put("subject", subject);
		
		String sql = "";
		sql = "select ss from StudentStatus st join st.student s join st.studentSubjects ss " +
			"WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
			" and st.session = :session and st.period = :period and s.program = :program" +
			" and ss.subject = :subject ";
		sql += " and st.type.inActive = 0 ";
		sql += " order by s.biodata.name";
		List<StudentSubject> students = db.list(sql, param);
		context.put("students", students);

		
		vm = path + "list_students.vm";
	}

	private void listSubjects() {
		vm = path + "list_subjects.vm";
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
		String periodId = request.getParameter("period_id") != null ? request.getParameter("period_id") : "";
		
		Program program = !"".equals(programId) ? (Program) db.find(Program.class, programId) : null;
		Session session = !"".equals(sessionId) ? (Session) db.find(Session.class, sessionId) : null;
		Period period =  !"".equals(periodId) ? (Period) db.find(Period.class, periodId) : null;
		
		if ( program != null ) context.put("program", program); else context.remove("program");
		if ( session != null ) context.put("session", session); else context.remove("session");
		if ( period != null ) context.put("period", period); else context.remove("period");
		
		Hashtable param = new Hashtable();
		param.put("program", program);
		param.put("session", session);
		param.put("period", period);
		
		String sql = "";

		sql = "select distinct s from StudentStatus st join st.studentSubjects ss join ss.subject s " +
			  "where st.student.program = :program " +
			  "and st.session = :session " +
			  "and st.period = :period " +
			  "order by s.name";
		List<Subject> subjects = db.list(sql, param);
		if ( subjects.size() > 0 ) {
			context.put("subjects", subjects);
		}
		else context.remove("subjects");
		
		
	}

	private void start() {
		// TODO Auto-generated method stub
		vm = path + "select.vm";
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
		String periodId = request.getParameter("period_id") != null ? request.getParameter("period_id") : "";
		
		Program program = !"".equals(programId) ? (Program) db.find(Program.class, programId) : null;
		Session session = !"".equals(sessionId) ? (Session) db.find(Session.class, sessionId) : null;
		Period period =  !"".equals(periodId) ? (Period) db.find(Period.class, periodId) : null;
		
		if ( program != null ) context.put("program", program); else context.remove("program");
		if ( session != null ) context.put("session", session); else context.remove("session");
		if ( period != null ) context.put("period", period); else context.remove("period");
		
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		
		List<Session> sessions = null;
		if ( program != null ) {
			sessions = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
			context.put("sessions", sessions);
		}
		else context.remove("sessions");
		
		List<Period> periods = null;
		if ( program != null ) {
			periods = program.getPeriodScheme().getFunctionalPeriodList();
			context.put("periods", periods);
		}
		else context.remove("periods");
		
	}
	
}
