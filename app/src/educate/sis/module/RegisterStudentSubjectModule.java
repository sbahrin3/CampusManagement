package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.SubjectReg;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.AjaxModule;

public class RegisterStudentSubjectModule extends AjaxModule {
	
	String path = "apps/util/subject_registration3/";
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
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		
		if ( null == command || "".equals(command)) start();
		else if ( "get_student_by_matric".equals(command)) getStudentByMatric();
		else if ( "add_subjects".equals(command)) addSubjects();
		else if ( "remove_subjects".equals(command)) removeSubjects();
		else if ( "save_sections".equals(command)) saveSections();
		else if ( "next_semester".equals(command)) nextSemester();
		else if ( "prev_semester".equals(command)) prevSemester();
		return vm;
	}
	
	private void prevSemester() {
		vm = path + "list_subjects.vm";
		String statusId = request.getParameter("student_status_id");
		StudentStatus status = db.find(StudentStatus.class, statusId);
		//find next
		Student student = status.getStudent();
		context.put("student", student);
		context.put("student_no", student.getMatricNo());
		StudentStatusUtil u = new StudentStatusUtil();
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		int i = 0;
		StudentStatus prevStatus = null;
		for ( StudentStatus s : statuses ) {
			if ( s.getId().equals(status.getId())) break;
			i++;
		}
		if ( i > 0 ) prevStatus = statuses.get(i-1);
		if ( prevStatus != null ) {
			displayStudentStatus(prevStatus);
		}
	}

	private void nextSemester() {
		vm = path + "list_subjects.vm";
		String statusId = request.getParameter("student_status_id");
		StudentStatus status = db.find(StudentStatus.class, statusId);
		//find next
		Student student = status.getStudent();
		context.put("student", student);
		context.put("student_no", student.getMatricNo());
		StudentStatusUtil u = new StudentStatusUtil();
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		boolean getNext = false;
		StudentStatus nextStatus = null;
		int i = 0;
		for ( StudentStatus s : statuses ) {
			if ( getNext) {
				nextStatus = s;
				break;
			}
			if ( s.getId().equals(status.getId())) getNext = true;
			i++;
		}
		if ( nextStatus != null ) {
			displayStudentStatus(nextStatus);
		}
	}

	private void saveSections() throws Exception {
		vm = path + "list_subjects.vm";
		String[] sectionIds = request.getParameterValues("section_ids");

		String studentStatusId = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.get("select s from StudentStatus s where s.id = '" + studentStatusId + "'");
		
		if ( sectionIds != null ) {
			
			for ( String id : sectionIds ) {
				String subjectId = id.substring(0, id.indexOf("_"));
				String sectionId = id.substring(id.indexOf("_") + 1);
				StudentSubject subject = db.find(StudentSubject.class, subjectId);
				SubjectSection section = db.find(SubjectSection.class, sectionId);
				db.begin();
				subject.setSection(section);
				db.commit();
			}
			
		}
		
		Student student = studentStatus.getStudent();
		context.put("student", student);
		context.put("student_no", student.getMatricNo());
		displayStudentStatus(studentStatus);	
	}

	private void removeSubjects() throws Exception {
		vm = path + "list_subjects.vm";
		String[] deleteSubjects = request.getParameterValues("delete_subjects");

		String studentStatusId = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.get("select s from StudentStatus s where s.id = '" + studentStatusId + "'");
		
		if ( deleteSubjects != null ) {
			
			for ( String id : deleteSubjects ) {
				StudentSubject subject = db.find(StudentSubject.class, id);
				db.begin();
				studentStatus.delStudentSubject(subject);
				db.remove(subject);
				db.commit();
			}
			
			if ( studentStatus.getStudentSubjects().size() == 0 ) {
//				ProgramUtil pu = new ProgramUtil();
//				StatusType defaultType = pu.getDefaultStatus();
//				db.begin();
//				studentStatus.setType(defaultType);
//				db.commit();
			}
			
		}
		
		Student student = studentStatus.getStudent();
		context.put("student", student);
		context.put("student_no", student.getMatricNo());
		displayStudentStatus(studentStatus);
		
	}

	private void addSubjects() throws Exception {
		vm = path + "list_subjects.vm";
		String[] selectedSubjects = request.getParameterValues("selected_subjects");

		String studentStatusId = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.get("select s from StudentStatus s where s.id = '" + studentStatusId + "'");
		
		StatusType regType = (StatusType) db.get("select s from StatusType s where s.code = 'REG'");

		if ( selectedSubjects != null ) {
			db.begin();
			for ( String id : selectedSubjects ) {
				SubjectReg sr = db.find(SubjectReg.class, id);
				StudentSubject subject = new StudentSubject();
				subject.setCategory(sr.getCategory());
				subject.setSubject(sr.getSubject());
				studentStatus.addStudentSubject(subject);
			}
			if ( regType != null ) studentStatus.setType(regType);
			db.commit();
			
			
		}
		
		Student student = studentStatus.getStudent();
		context.put("student", student);
		context.put("student_no", student.getMatricNo());
		displayStudentStatus(studentStatus);
		
	}

	private void getStudentByMatric() {
		vm = path + "list_subjects.vm";
		String studentNo = request.getParameter("student_no");
		context.put("student_no", studentNo);
		String sql = "select s from Student s where s.matricNo = '" + studentNo + "'";
		Student student = (Student) db.get(sql);
		if ( student != null ) {
			context.put("student", student);
			displayStudentStatus(student);
		}
		else context.remove("student");
		
		
		
	}
	
	private void displayStudentStatus(StudentStatus studentStatus) {
		context.put("student_status", studentStatus);
		context.put("student_subjects", studentStatus.getStudentSubjectsUnchecked());
		String sql;
		Student student = studentStatus.getStudent();
		
		List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		String programId = student.getProgram().getId();
		String centreId = student.getLearningCenter().getId();
		String intakeId = student.getIntake().getId();
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		if ( ps != null ) {
			context.put("programStructure", ps);
			ProgramUtil pu = new ProgramUtil();
			Set<SubjectReg> subjects = pu.getSubjectRegs(ps, studentStatus.getPeriod().getId());
			context.put("subjects", subjects);
			
		} else {
			context.remove("programStructure");
			context.remove("subjects");
		}
		
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.code");
		context.put("sections", sections);
		
		StudentStatusUtil u = new StudentStatusUtil();
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		int i = 0;
		for ( StudentStatus s : statuses ) {
			if ( studentStatus.getId().equals(s.getId())) break;
			i++;
		}
		
		if ( i == 0 ) context.put("bof", true);
		else context.remove("bof");
		if ( i == statuses.size() - 1 ) context.put("eof", true);
		else context.remove("eof");
		
	}

	private void displayStudentStatus(Student student) {
		String sql;
		StudentStatus studentStatus = getCurrentStudentStatus(student);
		
		if ( studentStatus == null ) return;
		
		List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		String programId = student.getProgram().getId();
		String centreId = student.getLearningCenter().getId();
		String intakeId = student.getIntake().getId();
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		if ( ps != null ) {
			context.put("programStructure", ps);
			ProgramUtil pu = new ProgramUtil();
			Set<SubjectReg> subjects = pu.getSubjectRegs(ps, studentStatus.getPeriod().getId());
			context.put("subjects", subjects);
			
		} else {
			context.remove("programStructure");
			context.remove("subjects");
		}
		
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.code");
		context.put("sections", sections);
	}

	private void start() {
		vm = path + "list_subjects.vm";
		
	}
	
	private StudentStatus getCurrentStudentStatus(Student student) {
		context.remove("student_status");
		try {
			StudentStatusUtil pu = new StudentStatusUtil();
			StudentStatus studentStatus = pu.getCurrentStudentStatus(student); //StudentRegistrationUtil.getCurrentStudentStatus(student);
			context.put("student_status", studentStatus);
			context.put("student_subjects", studentStatus.getStudentSubjectsUnchecked());
			return studentStatus;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


}
