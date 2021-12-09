package educate.sis.module;

import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.StudentRegistrationUtil;
import educate.enrollment.entity.StudentStatus;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.AjaxModule;

public class ListStudentsBySubjectModule extends AjaxModule {
	
	String path = "apps/util/list_students_by_subjects/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		else if ( "list_result".equals(command)) listResult();
		return vm;
	}

	private void listResult() {
		vm = path + "div_result.vm";
		String subjectId = request.getParameter("subject_id");
		String sectionId = request.getParameter("section_id");
		String sessionId = request.getParameter("session_id");
		
		List<StudentStatus> students = db.list("select s.studentStatus from StudentSubject s " +
				"WHERE (s.studentStatus.student.fakeStudent is null OR s.studentStatus.student.fakeStudent = '') " +
				"and s.subject.id = '" + subjectId + "' " +
				"and s.section.id = '" + sectionId + "' " +
				"and s.studentStatus.session.id = '" + sessionId + "' order by s.studentStatus.student.biodata.name");
		context.put("students", students);
		
		
	}

	private void start() throws Exception {
		vm = path + "start.vm";
		List<Subject> subjects = db.list("select s from Subject s order by s.code");
		context.put("subjects", subjects);
		
		List<SubjectSection> sections = db.list("select s from SubjectSection s");
		context.put("sections", sections);
		
		List<Session> sessions = db.list("select s from Session s order by s.startDate");
		context.put("sessions", sessions);
		
		Session currentSession = StudentRegistrationUtil.getCurrentSession();
		context.put("current_session", currentSession);
		
	}

}
