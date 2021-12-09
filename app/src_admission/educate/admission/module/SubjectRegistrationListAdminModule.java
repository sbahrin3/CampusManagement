package educate.admission.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SubjectRegistrationListAdminModule extends LebahModule {

	private DbPersistence db = new DbPersistence();
	private String path = "admission/subject_registration_admin";

	@Override
	public String start() {
		List<Program> programs = db.list("select p from Program p");
		context.put("programs", programs);
		return path + "/start.vm";
	}
	
	@Command("listSessions")
	public String listSessions() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		int pathNo = program.getLevel().getPathNo();
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("sessions", sessions);
		return path + "/listSessions.vm";
	}
	
	@Command("listStudents")
	public String listStudents() throws Exception {
		String programId = getParam("programId");
		String sessionId = getParam("sessionId");
		
		String sql = "select s from Student s join s.status st where st.session.id = '" + sessionId + 
		"' and s.program.id = '" + programId + "'" + 
		"order by s.biodata.name";
		List<Student> students = db.list(sql);
		context.put("students", students);
		
		sql = "select ss from StudentSubject ss join ss.subject s join ss.studentStatus st " +
		"where st.session.id = '" + sessionId +
		"' and st.student.program.id = '" + programId + "'";
		Set<Subject> subjectList = new HashSet<Subject>();
		context.put("subjects", subjectList);
		Map<String, Integer> subjectMap = new HashMap<String, Integer>();
		context.put("subjectMap", subjectMap);
		
		List<StudentSubject> subjects = db.list(sql);
		for ( StudentSubject s : subjects ) {
			subjectList.add(s.getSubject());
			Integer cnt = subjectMap.get(s.getSubject().getId());
			if ( cnt == null ) {
				subjectMap.put(s.getSubject().getId(), new Integer(1));
			} else {
				cnt = cnt + 1;
				subjectMap.put(s.getSubject().getId(), new Integer(cnt));
			}
		}
		
		return path + "/listStudents.vm";
	}
	

}
