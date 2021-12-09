package educate.admission.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.module.ProgramUtil;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectReg;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.velocity.VTemplate;

public class SubjectRegistrationSlipPrint extends VTemplate {
	
	String path = "apps/util/subject_registration_student/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public Template doTemplate() throws Exception {
		
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String uri = request.getRequestURI();
		String s1 = uri.substring(1);
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
		String app = s1.substring(0, s1.indexOf("/"));  
		String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
		context.put("appUrl", http + server + "/" + app);

		setShowVM(false);
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		Template template = engine.getTemplate(getStudent());	
		return template;		
	}
	
	private String getStudent() {
		
		String studentStatusId = request.getParameter("studentStatusId");
		StudentStatus studentStatus = db.find(StudentStatus.class, studentStatusId);
		getListSubjectsValidated(studentStatus);
		
		Student student = studentStatus.getStudent();
		displayStudentStatus(student);
		
		return path + "print_subject_registration_slip.vm";
	}
	
	private void getListSubjectsValidated(StudentStatus studentStatus) {
		//list of subjects
		//separate between CORE and NON-CORE
		Student student = studentStatus.getStudent();
		String programId = student.getProgram().getId();
		String centreId = student.getLearningCenter().getId();
		String intakeId = student.getIntake().getId();
		String sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
				"and p.session.id = '" + intakeId + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		Map<String, SubjectReg> subjectMap = new HashMap<String, SubjectReg>();
		if ( ps != null ) {
			ProgramUtil pu = new ProgramUtil();
			Set<SubjectReg> subjectRegs = pu.getSubjectRegs(ps);
			for ( SubjectReg sr : subjectRegs ) {
				subjectMap.put(sr.getSubject().getId(), sr);
			}
		}
		
	
		List<SubjectReg> registeredSubjects = new ArrayList<SubjectReg>();
		context.put("registeredSubjects", registeredSubjects);
		
		Set<StudentSubject> studentSubjects = studentStatus.getStudentSubjects();
		for ( StudentSubject s : studentSubjects ) {
			Subject subject = s.getSubject();
			SubjectReg reg = subjectMap.get(subject.getId());
			registeredSubjects.add(reg);
		}
	}
	
	
	private void displayStudentStatus(Student student) {
		
		context.put("student", student);
		
		String sql;
		StudentStatus studentStatus = getCurrentStudentStatus(student);
		
		//if ( studentStatus == null ) return;
		
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
		else {
			//nullPointerException
		}
		
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.code");
		context.put("sections", sections);
		
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
			return studentStatus;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}	
	


}
