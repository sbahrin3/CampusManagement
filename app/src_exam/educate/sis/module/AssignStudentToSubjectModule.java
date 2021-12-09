package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.StudentRegistrationUtil;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import educate.timetabling.entity.Room;
import lebah.portal.action.AjaxModule;

public class AssignStudentToSubjectModule extends AjaxModule {
	
	String path = "apps/timetabling/assign_subject/";
	String vm = "";
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		else if ( "all_students".equals(command)) allStudents();
		else if ( "enrolled_students".equals(command)) enrolledStudents();
		else if ( "add_students".equals(command)) addStudents();
		else if ( "remove_students".equals(command)) removeStudents();
		return vm;
	}
	
	private void removeStudents() throws Exception {
		
		String[] studentIds = request.getParameterValues("students1");
		if ( studentIds != null ) {
			for ( String id : studentIds ) {
				StudentSubject studentSubject = db.find(StudentSubject.class, id);
				StudentStatus studentStatus = studentSubject.getStudentStatus();
				System.out.println(studentSubject.getStudentStatus().getStudent().getBiodata().getName());
				db.begin();
				studentStatus.getStudentSubjects().remove(studentSubject);
				db.remove(studentSubject);
				db.commit();
			}
		}
		enrolledStudents();
		
	}

	private void addStudents() throws Exception {
		String sessionId = request.getParameter("session_id");
		String subjectId = request.getParameter("subject_id");
		String sectionId = request.getParameter("section_id");
		
		List<StudentStatus> enrolledList = listStudents(sessionId, subjectId, sectionId);
		
		Subject subject = db.find(Subject.class, subjectId);
		SubjectSection section = db.find(SubjectSection.class, sectionId);

		String[] studentIds = request.getParameterValues("students2");
		if ( studentIds != null ) {
			for ( String id : studentIds ) {
				StudentSubject studentSubject = new StudentSubject();
				studentSubject.setSubject(subject);
				studentSubject.setSection(section);
				StudentStatus studentStatus = db.find(StudentStatus.class, id);
				if ( !enrolledList.contains(studentStatus)) {
					db.begin();
					studentStatus.addStudentSubject(studentSubject);
					db.commit();
				}
			}
		}
		enrolledStudents();
	}

	private void enrolledStudents() {
		vm = path + "enrolled_students.vm";
		String sessionId = request.getParameter("session_id");
		String subjectId = request.getParameter("subject_id");
		String sectionId = request.getParameter("section_id");
		List<StudentSubject> list = listStudents2(sessionId, subjectId, sectionId);
		context.put("enrolled_students", list);
	}

	private void allStudents() {
		vm = path + "all_students.vm";
		String sessionId = request.getParameter("session_id");
		//Session session = (Session) db.find(Session.class, sessionId);
		List<StudentStatus> list = listStudents(sessionId);
		context.put("students", list);
	}

	private List<StudentStatus> listStudents(String sessionId, String subjectId, String sectionId) {
		List<StudentStatus> students = db.list("select s.studentStatus from StudentSubject s " +
				"WHERE (s.studentStatus.student.fakeStudent is null OR s.studentStatus.student.fakeStudent = '') " +
				"and s.subject.id = '" + subjectId + "' " +
				"and s.section.id = '" + sectionId + "' " +
				"and s.studentStatus.session.id = '" + sessionId + "' order by s.studentStatus.student.biodata.name");
		return students;
	}
	
	private List<StudentSubject> listStudents2(String sessionId, String subjectId, String sectionId) {
		List<StudentSubject> students = db.list("select s from StudentSubject s " +
				"WHERE (s.studentStatus.student.fakeStudent is null OR s.studentStatus.student.fakeStudent = '') " +
				"and s.subject.id = '" + subjectId + "' " +
				"and s.section.id = '" + sectionId + "' " +
				"and s.studentStatus.session.id = '" + sessionId + "' order by s.studentStatus.student.biodata.name");
		return students;
	}
	
	private List<StudentStatus> listStudents(String sessionId) {
		List<StudentStatus> students = db.list("select s from StudentStatus s " +
				"WHERE (s.student.fakeStudent is null OR s.student.fakeStudent = '') " +
				"and s.session.id = '" + sessionId + "' order by s.student.biodata.name");
		return students;
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
		List<Room> rooms = db.list("select r from educate.timetabling.entity.Room r order by r.code");
		context.put("rooms", rooms);

	}

}
