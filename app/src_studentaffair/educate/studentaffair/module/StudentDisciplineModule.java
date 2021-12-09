package educate.studentaffair.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.StudentStatusUtil;
import educate.studentaffair.entity.DisciplinaryCase;
import educate.studentaffair.entity.StudentDiscipline;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentDisciplineModule  extends LebahModule {
	
	private String path = "studentaffair/student_discipline";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}
	
	@Command("getStudent")
	public String getStudent() throws Exception {
		String matricNo = getParam("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		if ( studentStatus == null ) context.remove("student_status");
		else context.put("studentStatus", studentStatus);
		
		List<StudentDiscipline> studentCases = db.list("select s from StudentDiscipline s where s.student.id = '" + student.getId() + "'");
		context.put("studentCases", studentCases);
		
		return path + "/student.vm";
	}

	@Command("addCase")
	public String addCase() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		context.put("cases", db.list("select c from DisciplinaryCase c"));
		context.remove("studentCase");
		return path + "/addCase.vm";
	}
	
	
	@Command("getStudentCase")
	public String getStudentCase() throws Exception {
		String studentCaseId = getParam("studentCaseId");
		StudentDiscipline studentCase = db.find(StudentDiscipline.class, studentCaseId);
		context.put("studentCase", studentCase);
		context.put("student", studentCase.getStudent());
		context.put("cases", db.list("select c from DisciplinaryCase c"));
		return path + "/addCase.vm";
	}
	
	@Command("studentCase")
	public String studentCase() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String caseId = getParam("caseId");
		DisciplinaryCase disciplinaryCase = db.find(DisciplinaryCase.class, caseId);
		
		String _reportedDate = getParam("reportedDate");
		Date reportedDate = null;
		String _hearingDate = getParam("hearingDate");
		Date hearingDate = null;
		
		try {
			hearingDate = new SimpleDateFormat("dd-MM-yyyy").parse(_hearingDate);
		} catch ( Exception e ) {}
		
		try {
			reportedDate = new SimpleDateFormat("dd-MM-yyyy").parse(_reportedDate);
		} catch ( Exception e ) {}			
		
		db.begin();
		StudentDiscipline sd = new StudentDiscipline();
		sd.setStudent(student);
		sd.setDisciplinaryCase(disciplinaryCase);
		sd.setReportedDate(reportedDate);
		sd.setHearingDate(hearingDate);
		sd.setComplainantTestimony(getParam("complainantTestimony"));
		sd.setStudentTestimony(getParam("studentTestimony"));
		sd.setCrossExaminationResult(getParam("crossExaminationResult"));
		sd.setCaseClosed("1".equals(getParam("caseClosed")) ? true : false);
		sd.setPenaltyImposed("1".equals(getParam("penaltyImposed")) ? true : false);
		
		db.persist(sd);
		db.commit();
			

		List<StudentDiscipline> studentCases = db.list("select s from StudentDiscipline s where s.student.id = '" + studentId + "'");
		context.put("studentCases", studentCases);
		return path + "/studentCase.vm";
	}
	
	
	@Command("saveStudentCase")
	public String saveStudentDiscipline() throws Exception {
		String studentCaseId = getParam("studentCaseId");
		StudentDiscipline sd = db.find(StudentDiscipline.class, studentCaseId);
		
		String caseId = getParam("caseId");
		DisciplinaryCase disciplinaryCase = db.find(DisciplinaryCase.class, caseId);
		
		String _reportedDate = getParam("reportedDate");
		Date reportedDate = null;
		String _hearingDate = getParam("hearingDate");
		Date hearingDate = null;
		
		try {
			hearingDate = new SimpleDateFormat("dd-MM-yyyy").parse(_hearingDate);
		} catch ( Exception e ) {}
		
		try {
			reportedDate = new SimpleDateFormat("dd-MM-yyyy").parse(_reportedDate);
		} catch ( Exception e ) {}			
		
		db.begin();
		sd.setDisciplinaryCase(disciplinaryCase);
		sd.setReportedDate(reportedDate);
		sd.setHearingDate(hearingDate);
		sd.setComplainantTestimony(getParam("complainantTestimony"));
		sd.setStudentTestimony(getParam("studentTestimony"));
		sd.setCrossExaminationResult(getParam("crossExaminationResult"));		
		sd.setCaseClosed("1".equals(getParam("caseClosed")) ? true : false);
		sd.setPenaltyImposed("1".equals(getParam("penaltyImposed")) ? true : false);
		db.commit();
		
		List<StudentDiscipline> studentCases = db.list("select s from StudentDiscipline s where s.student.id = '" + sd.getStudent().getId() + "'");
		context.put("studentCases", studentCases);
		return path + "/studentCase.vm";
	}
	
	@Command("deleteCase")
	public String deleteStudentDiscipline() throws Exception {
		
		String studentCaseId = getParam("studentCaseId");
		StudentDiscipline sd = db.find(StudentDiscipline.class, studentCaseId);
		String studentId = sd.getStudent().getId();
		
		db.begin();
		db.remove(sd);
		db.commit();
		
		List<StudentDiscipline> studentCases = db.list("select s from StudentDiscipline s where s.student.id = '" + studentId + "'");
		context.put("studentCases", studentCases);
		return path + "/studentCase.vm";
		
	}


}
