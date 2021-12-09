package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.SubjectReg;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.AjaxModule;

public class StudentStatusInfoModule extends AjaxModule {
	
	String path = "apps/util/student_semester_info/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	protected boolean studentMode = false;

	
	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "get_status".equals(command)) getStatus();
		return vm;
	}
	
	private void start() {
		vm = path + "start.vm";
		if ( studentMode ) {
			getStatus();
		}
	}

	private void getStatus() {
		vm = path + "semester_info.vm";
		String studentNo = "";
		if ( studentMode ) {
			studentNo = (String) session.getAttribute("_portal_login");
		}
		else {
			studentNo = request.getParameter("student_no");
		}
		
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + studentNo + "'");
		context.put("student", student);
		getCurrentStudentStatus(student);
		
	}
	
	private StudentStatus getCurrentStudentStatus(Student student) {
		context.remove("student_status");
		try {
			StudentStatusUtil pu = new StudentStatusUtil();
			StudentStatus studentStatus = pu.getCurrentStudentStatus(student); //StudentRegistrationUtil.getCurrentStudentStatus(student);
			context.put("student_status", studentStatus);
			context.put("student_subjects", studentStatus.getStudentSubjects()); //.getStudentSubjectsUnchecked());
			return studentStatus;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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

}
