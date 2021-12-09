package educate.admission.module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SubjectRegistrationListModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "admission/subject_registration_list";
	
	public void preProcess() {
		System.out.println(command);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
	}

	@Override
	public String start() {
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		//List<Subject> subjects = db.list("select s from Subject s order by s.code");
		//context.put("subjects", subjects);
		context.put("statuses", db.list("select x from StatusType x order by x.sequence"));
		context.put("date", new Date());
		return path + "/start.vm";
	}
	
	@Command("listSubjects")
	public String listSubjects() throws Exception {
		String programId = getParam("programId");
		String sql = "select s from ProgramStructure ps Join ps.subjectPeriod sp Join sp.subjectRegs sr Join sr.subject s "
				+ "where ps.program.id = '" + programId + "' group by s";
		
		List<Subject> subjects = db.list(sql);
		List<Subject> subjectList = new ArrayList<Subject>();
		subjectList.addAll(subjects);
		Collections.sort(subjectList, new SubjectComparator());
		context.put("subjects", subjectList);
		return path + "/listSubjects.vm";
	}
	
	static class SubjectComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			Subject s1 = (Subject) o1;
			Subject s2 = (Subject) o2;
			if ( s1 == null || s2 == null ) return 0;
			if ( s1.getCode() == null || s2.getCode() == null ) return 0;
			return s1.getCode().compareTo(s2.getCode());
		}
		
	}
	
	@Command("listIntakes")
	public String listIntakes() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		if ( program != null && program.getLevel() != null ) {
			int pathNo = program.getLevel().getPathNo();
			List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo);
			context.put("intakes", intakes);
		} else {
			context.remove("intakes");
		}
		return path + "/listIntakes.vm";
	}

	
	@Command("listStudents")
	public String listStudents() throws Exception {
		String programId = getParam("programId");
		String intakeId = getParam("intakeId");
		String subjectId = getParam("subjectId");
		String statusId = getParam("statusId");
		String sql = "";
		
		Program program = null;
		Session intake = null;
		Subject subject = null;
		StatusType statusType = null;
		
		if ( !"".equals(programId)) program = db.find(Program.class, programId);
		if ( !"".equals(intakeId)) intake = db.find(Session.class, intakeId);
		if ( !"".equals(subjectId)) subject = db.find(Subject.class, subjectId);
		if ( !"".equals(statusId)) statusType = db.find(StatusType.class, statusId);
		
		context.put("program", program);
		context.put("intake", intake);
		context.put("subject", subject);
		context.put("statusType", statusType);
		
		Set<Student> students = new HashSet<Student>();
		List<StudentStatus> studentList = new ArrayList<StudentStatus>();
		
		Date date = new Date();
		String reportDate = getParam("reportDate");
		if ( !"".equals(reportDate) ) {
			try {
				date = new SimpleDateFormat("dd-MM-yyyy").parse(reportDate);
			} catch ( Exception e ) { }
		}
		
		context.put("reportDate", date);
		
		Hashtable h = new Hashtable();
		h.put("date", date);
		
		sql = "select ss from StudentSubject ss join ss.subject s join ss.studentStatus st " +
		"where :date between st.session.startDate and st.session.endDate ";
		if ( !"".equals(programId)) {
			sql += " and st.student.program.id = '" + programId + "' ";
		}
		if ( !"".equals(subjectId)) {
			sql += " and ss.subject.id = '" + subjectId + "' ";
		}
		if ( !"".equals(intakeId)) {
			sql += " and ss.studentStatus.student.intake.id = '" + intakeId + "' ";
		}
		if ( !"".equals(statusId)) {
			sql += " and ss.studentStatus.type.id = '" + statusId + "' ";
		}
		List<Subject> subjectList = new ArrayList<Subject>();
		context.put("subjects", subjectList);
		Map<String, String> subjectMap = new HashMap<String, String>();
		context.put("subjectMap", subjectMap);
		
		
		List<StudentSubject> subjects = db.list(sql, h);
		for ( StudentSubject s : subjects ) {
			
			if ( s.getStudentStatus() != null && s.getStudentStatus().getStudent() != null ) {
				if ( !subjectList.contains(s.getSubject())) subjectList.add(s.getSubject());
				
				subjectMap.put(s.getSubject().getId() + "_" + s.getStudentStatus().getStudent().getId(), s.getSubjectStatus() != null ? s.getSubjectStatus() : "-");
			
				students.add(s.getStudentStatus().getStudent());
				
				if ( !studentList.contains(s.getStudentStatus())) studentList.add(s.getStudentStatus());
			}
		}
		
		Collections.sort(subjectList, new SubjectComparator());
		
		
		Collections.sort(studentList, new StudentStatusNameComparator());
	
		context.put("students", studentList);
		
		request.getSession().setAttribute("_students", studentList);
		request.getSession().setAttribute("_subjects", subjectList);
		request.getSession().setAttribute("_subjectMap", subjectMap);
		
		return path + "/listStudents.vm";
	}
	

	static class StudentStatusNameComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			StudentStatus s1 = (StudentStatus) o1;
			StudentStatus s2 = (StudentStatus) o2;
			return s1.getStudent().getBiodata().getName().compareTo(s2.getStudent().getBiodata().getName());
		}
	}	
	

}
