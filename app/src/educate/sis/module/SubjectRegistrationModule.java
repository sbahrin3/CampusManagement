package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.module.SpecialSubjectStatus;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.SubjectReg;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.AjaxModule;

public class SubjectRegistrationModule  extends AjaxModule {
	
	String path = "apps/util/subject_registration/";
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
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "get_student_by_matric".equals(command)) getStudentByMatric();
		else if ( "add_subjects".equals(command)) addSubjects();
		else if ( "remove_subjects".equals(command)) removeSubjects();
		else if ( "save_sections".equals(command)) saveSections();
		else if ( "next_semester".equals(command)) nextSemester();
		else if ( "prev_semester".equals(command)) prevSemester();
		else if ( "updateSubjectStatus".equals(command)) updateSubjectStatus();
		return vm;
	}
	
	private void updateSubjectStatus() throws Exception {
		
		String subjectRegisterId = getParam("subjectRegisterId");
		String subjectStatus = getParam("subjectStatus_" + subjectRegisterId);

		StudentSubject studentSubject = db.find(StudentSubject.class, subjectRegisterId);
		db.begin();
		studentSubject.setSubjectStatus(subjectStatus);
		db.commit();

		StudentStatus studentStatus = studentSubject.getStudentStatus();
		if ( SpecialSubjectStatus.isMatch(subjectStatus)) {
			ResultEntryUtil.saveResult(db, studentStatus, studentSubject.getSubject(), 0.0d, subjectStatus);
		}
		vm = path + "updateSubjectStatus.vm";
	}

	private void prevSemester() {
		vm = path + "div_list_subjects.vm";
		String statusId = request.getParameter("student_status_id");
		StudentStatus status = (StudentStatus) db.find(StudentStatus.class, statusId);

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
		vm = path + "div_list_subjects.vm";
		String statusId = request.getParameter("student_status_id");
		StudentStatus status = (StudentStatus) db.find(StudentStatus.class, statusId);
		
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
		vm = path + "div_student_subjects.vm";
		String[] sectionIds = request.getParameterValues("section_ids");

		String studentStatusId = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.get("select s from StudentStatus s where s.id = '" + studentStatusId + "'");
		context.put("student_status", studentStatus);
		
		if ( sectionIds != null ) {
			
			for ( String id : sectionIds ) {
				String subjectId = id.substring(0, id.indexOf("_"));
				String sectionId = id.substring(id.indexOf("_") + 1);
				StudentSubject subject = (StudentSubject) db.find(StudentSubject.class, subjectId);
				SubjectSection section = (SubjectSection) db.find(SubjectSection.class, sectionId);
				db.begin();
				subject.setSection(section);
				db.commit();
			}
			
		}
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.code");
		context.put("sections", sections);
	}

	private void removeSubjects() throws Exception {
		vm = path + "div_student_subjects.vm";
		
		String[] deleteSubjects = request.getParameterValues("delete_subjects");

		String studentStatusId = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.get("select s from StudentStatus s where s.id = '" + studentStatusId + "'");
		context.put("student_status", studentStatus);
		
		List<FinalSubjectResult> deletedSubjectResults = new ArrayList<FinalSubjectResult>();
		context.put("deletedSubjectResults", deletedSubjectResults);
		
		if ( deleteSubjects != null ) {

			
			for ( String id : deleteSubjects ) {
				StudentSubject studentSubject = (StudentSubject) db.find(StudentSubject.class, id);
				//---for each subject, check whether exist FinalSubjectResult
				
				if ( studentSubject.getSubject() != null ) {
					FinalSubjectResult subjectResult = (FinalSubjectResult) db.get("select f from FinalSubjectResult f where f.parent.student.id = '" + studentStatus.getStudent().getId() + "' and f.subject.id = '" + studentSubject.getSubject().getId() + "' and f.parent.session.id = '" + studentStatus.getSession().getId() + "'");
					if ( subjectResult != null ) {
						if ( subjectResult.getTotalMark() > 0 )
							deletedSubjectResults.add(subjectResult);
					}
					db.begin();
					studentStatus.delStudentSubject(studentSubject);
					db.remove(studentSubject);
					db.commit();
					
				} else {
					System.out.println("ERROR when delete because student subject has NULL subject!");
					//just remove this student subject anyway
					db.begin();
					studentStatus.delStudentSubject(studentSubject);
					db.remove(studentSubject);
					db.commit();
				}
					
				
					
				
			}
						
			System.out.println("deleted size = " + deletedSubjectResults.size());
		}
		
		
		
		context.put("specialSubjectStatusList", SpecialSubjectStatus.getListOfStatus());
		
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.code");
		context.put("sections", sections);
		
	}

	private void addSubjects()  {
		vm = path + "div_list_subjects.vm";
		
		String[] selectedSubjects = request.getParameterValues("selected_subjects");
		List<StudentSubject> subjectList = new ArrayList<StudentSubject>();
		if ( selectedSubjects != null ) {
			for ( String id : selectedSubjects ) {
				SubjectReg sr = (SubjectReg) db.find(SubjectReg.class, id);
				StudentSubject studentSubject = new StudentSubject();
				studentSubject.setSubject(sr.getSubject());
				studentSubject.setCategory(sr.getCategory());
				studentSubject.setSubjectStatus("RG");
				
				System.out.println("subject: " + studentSubject.getSubject().getCode());
				subjectList.add(studentSubject);
			}
		}

		String studentStatusId = request.getParameter("student_status_id");
		//StudentStatus studentStatus = (StudentStatus) db.get("select s from StudentStatus s where s.id = '" + studentStatusId + "'");
		StudentStatus studentStatus = (StudentStatus) db.find(StudentStatus.class, studentStatusId);

		try {
			System.out.println("Saving student subjects:");
			for ( StudentSubject s : subjectList ) {
				
				if ( studentStatus.addStudentSubject(s) ) {
					System.out.println("add " + s.getSubject().getCode());
					s.setStudentStatus(studentStatus);
					db.begin();
					db.persist(s);
					db.commit();
	
				}
				
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("error: " + e);
		}
		
		System.out.println("===========");
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
		
		
		//System.out.println("STUDENT STATUS");
		//System.out.println("Program = " + studentStatus.getStudent().getProgram().getId());
		//System.out.println("Semester = " + studentStatus.getPeriod().getId());
		
		context.put("specialSubjectStatusList", SpecialSubjectStatus.getListOfStatus());
		
		List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		String programId = student.getProgram().getId();
		String centreId = student.getLearningCenter().getId();
		String intakeId = student.getIntake().getId();
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		context.remove("subjects");
		if ( ps != null ) {
			context.put("programStructure", ps);
			ProgramUtil pu = new ProgramUtil();
			//System.out.println("Period = " + studentStatus.getPeriod());
			if ( studentStatus.getPeriod() != null ) {
				Set<SubjectReg> subjects = pu.getSubjectRegs(ps, studentStatus.getPeriod().getId());
				context.put("subjects", subjects);
			}
			else {
				context.remove("subjects");
			}
			
		} else {
			context.remove("programStructure");
			
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
		
		
		System.out.println("get student status for " + student.getMatricNo());
		System.out.println("student status = " + studentStatus.getId());
		System.out.println("" + studentStatus.getSession().getName() + ", " + studentStatus.getPeriod().getName());
		
		System.out.println("subjects");
		Set<StudentSubject> studentSubjects = studentStatus.getStudentSubjects();
		for ( StudentSubject studentSubject : studentSubjects ) {
			System.out.println("subject = " + studentSubject.getSubject());
		}
	
		context.remove("error_message");
		if ( studentStatus == null ) {
			context.put("error_message", "Student Status is NULL.");
			return;
		}
		if ( studentStatus.getPeriod() == null ) {
			context.put("error_message", "Student Status is has no defined Periods.");
			return;
		}
		
		context.put("specialSubjectStatusList", SpecialSubjectStatus.getListOfStatus());
		
		List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		String programId = student.getProgram() != null ? student.getProgram().getId() : null;
		String centreId = student.getLearningCenter() != null ? student.getLearningCenter().getId() : null;
		String intakeId = student.getIntake() != null ? student.getIntake().getId() : null;
		
		if ( programId != null && centreId != null && intakeId != null ) {
			sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
				"and p.session.id = '" + intakeId + "'";
			ProgramStructure ps = (ProgramStructure) db.get(sql);
			if ( ps != null ) {
				context.put("programStructure", ps);
				ProgramUtil pu = new ProgramUtil();
				if ( studentStatus != null ) {
					Set<SubjectReg> subjects = pu.getSubjectRegs(ps, studentStatus.getPeriod().getId());
					context.put("subjects", subjects);
				} else context.remove("subjects");
			} else {
				context.remove("programStructure");
				context.remove("subjects");
			}
		}
		
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.code");
		context.put("sections", sections);
		
	}

	private void start() {
		vm = path + "start.vm";
		
	}
	
	private StudentStatus getCurrentStudentStatus(Student student) {
		StudentStatus studentStatus = null;
		context.remove("student_status");
		try {
			StudentStatusUtil pu = new StudentStatusUtil();
			studentStatus = pu.getCurrentStudentStatus(student); //StudentRegistrationUtil.getCurrentStudentStatus(student);
			if ( studentStatus != null ) {
				context.put("student_status", studentStatus);
				context.put("student_subjects", studentStatus.getStudentSubjectsUnchecked());
			}
			else {
				context.remove("student_status");
				context.remove("student_subjects");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( studentStatus == null ) studentStatus = getLastStudentStatus(student);
		return studentStatus;
	}
	
	private StudentStatus getLastStudentStatus(Student student) {
		System.out.println("get last student status....");
		context.remove("student_status");
		try {
			StudentStatusUtil pu = new StudentStatusUtil();
			StudentStatus studentStatus = pu.getLastStudentStatus(student); 
			if ( studentStatus != null ) {
				context.put("student_status", studentStatus);
				context.put("student_subjects", studentStatus.getStudentSubjectsUnchecked());
			}
			else {
				context.remove("student_status");
				context.remove("student_subjects");
			}
			
			System.out.println("student status = " + studentStatus);
			
			return studentStatus;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}

}
