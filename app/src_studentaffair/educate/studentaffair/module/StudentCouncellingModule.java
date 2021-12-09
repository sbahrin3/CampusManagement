package educate.studentaffair.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.StudentStatusUtil;
import educate.studentaffair.entity.CouncellingIssue;
import educate.studentaffair.entity.Councellor;
import educate.studentaffair.entity.StudentCouncelling;
import educate.studentaffair.entity.StudentCouncellingSession;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentCouncellingModule extends LebahModule {
	
	private String path = "studentaffair/student_councelling";
	private DbPersistence db = new DbPersistence();	

	@Override
	public String start() {
		// TODO Auto-generated method stub
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
		
		List<StudentCouncelling> studentCases = db.list("select s from StudentCouncelling s where s.student.id = '" + student.getId() + "'");
		context.put("studentCases", studentCases);
		
		return path + "/student.vm";
	}	
	
	@Command("addCase")
	public String addCase() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		context.put("cases", db.list("select c from CouncellingIssue c"));
		context.put("councellors", db.list("select c from Councellor c"));
		context.remove("studentCase");
		return path + "/addCase.vm";
	}	
	
	@Command("getStudentCase")
	public String getStudentCase() throws Exception {
		String studentCaseId = getParam("studentCaseId");
		StudentCouncelling studentCase = db.find(StudentCouncelling.class, studentCaseId);
		context.put("studentCase", studentCase);
		context.put("student", studentCase.getStudent());
		context.put("cases", db.list("select c from CouncellingIssue c"));
		context.put("councellors", db.list("select c from Councellor c"));
		return path + "/addCase.vm";
	}
	
	@Command("studentCase")
	public String studentCase() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String _referredDate = getParam("referredDate");
		Date referredDate = null;
		try {
			referredDate = new SimpleDateFormat("dd-MM-yyyy").parse(_referredDate);
		} catch ( Exception e ) {}	
		
		String issueId = getParam("issueId");
		CouncellingIssue issue = db.find(CouncellingIssue.class, issueId);
		
		String councellorId = getParam("councellorId");
		Councellor councellor = db.find(Councellor.class, councellorId);
		
		db.begin();
		StudentCouncelling sc = new StudentCouncelling();
		sc.setStudent(student);
		sc.setReferredDate(referredDate);
		sc.setIssue(issue);
		sc.setIssueSummary(getParam("issueSummary"));
		sc.setReferralType(getParam("referralType"));
		sc.setReferralId(getParam("referralId"));
		sc.setReferralName(getParam("referralName"));
		sc.setReferralContactNo(getParam("referralContactNo"));
		sc.setReferralEmail(getParam("referralEmail"));
		sc.setReferralReason(getParam("referralReason"));
		sc.setCouncellor(councellor);
		sc.setCouncellorRemark(getParam("councellorRemark"));
		sc.setStudentRemark(getParam("studentRemark"));
		db.persist(sc);
		db.commit();

		List<StudentCouncelling> studentCases = db.list("select s from StudentCouncelling s where s.student.id = '" + studentId + "'");
		context.put("studentCases", studentCases);
		return path + "/studentCase.vm";
	}
	
	@Command("saveStudentCase")
	public String saveStudentCase() throws Exception {
		
		String studentCaseId = getParam("studentCaseId");
		StudentCouncelling sc = db.find(StudentCouncelling.class, studentCaseId);
		
		String _referredDate = getParam("referredDate");
		Date referredDate = null;
		try {
			referredDate = new SimpleDateFormat("dd-MM-yyyy").parse(_referredDate);
		} catch ( Exception e ) {}	
		
		String issueId = getParam("issueId");
		CouncellingIssue issue = db.find(CouncellingIssue.class, issueId);
		
		String councellorId = getParam("councellorId");
		Councellor councellor = db.find(Councellor.class, councellorId);
		
		db.begin();
		sc.setReferredDate(referredDate);
		sc.setIssue(issue);
		sc.setIssueSummary(getParam("issueSummary"));
		sc.setReferralType(getParam("referralType"));
		sc.setReferralId(getParam("referralId"));
		sc.setReferralName(getParam("referralName"));
		sc.setReferralContactNo(getParam("referralContactNo"));
		sc.setReferralEmail(getParam("referralEmail"));
		sc.setReferralReason(getParam("referralReason"));
		sc.setCouncellor(councellor);
		sc.setCouncellorRemark(getParam("councellorRemark"));
		sc.setStudentRemark(getParam("studentRemark"));
		db.commit();

		List<StudentCouncelling> studentCases = db.list("select s from StudentCouncelling s where s.student.id = '" + sc.getStudent().getId() + "'");
		context.put("studentCases", studentCases);
		return path + "/studentCase.vm";
	}	
	
	@Command("deleteCase")
	public String deleteStudentCouncelling() throws Exception {
		
		String studentCaseId = getParam("studentCaseId");
		StudentCouncelling sc = db.find(StudentCouncelling.class, studentCaseId);
		String studentId = sc.getStudent().getId();
		
		db.begin();
		db.remove(sc);
		db.commit();
		
		List<StudentCouncelling> studentCases = db.list("select s from StudentCouncelling s where s.student.id = '" + studentId + "'");
		context.put("studentCases", studentCases);
		return path + "/studentCase.vm";
		
	}	
	
	@Command("getCouncellingSessions")
	public String getCouncellingSessions() throws Exception {
		String studentCaseId = getParam("studentCaseId");
		StudentCouncelling councelling = db.find(StudentCouncelling.class, studentCaseId);
		context.put("councelling", councelling);
		context.put("student", councelling.getStudent());
		context.put("cases", db.list("select c from CouncellingIssue c"));
		context.put("councellors", db.list("select c from Councellor c"));
		context.remove("councellingSession");
		context.put("councellingSessions", db.list("select s from StudentCouncellingSession s where s.studentCouncelling.id = '" + councelling.getId() + "' order by s.sessionDate"));
		return path + "/councellingSessions.vm";
	}	
	
	@Command("addNewSession")
	public String addNewSession() throws Exception {
		String councellingId = getParam("councellingId");
		StudentCouncelling councelling = db.find(StudentCouncelling.class, councellingId);
		context.put("councelling", councelling);
		context.put("councellors", db.list("select c from Councellor c"));
		context.remove("councellingSession");
		return path + "/session.vm";
	}
	
	@Command("addSession")
	public String addSession() throws Exception {
		String councellingId = getParam("councellingId");
		StudentCouncelling councelling = db.find(StudentCouncelling.class, councellingId);
		context.put("councelling", councelling);
		
		String _sessionDate = getParam("sessionDate");
		Date sessionDate = null;
		try {
			sessionDate = new SimpleDateFormat("dd-MM-yyyy").parse(_sessionDate);
		} catch ( Exception e ) {}	
		String councellorId = getParam("councellorId");
		Councellor councellor = db.find(Councellor.class, councellorId);
		
		db.begin();
		StudentCouncellingSession cs = new StudentCouncellingSession();
		cs.setSessionDate(sessionDate);
		cs.setStudentCouncelling(councelling);
		cs.setCouncellor(councellor);
		cs.setSessionReport(getParam("sessionReport"));
		cs.setSessionSummary(getParam("sessionSummary"));
		councelling.getSessions().add(cs);
		db.commit();
		
		context.put("councellingSessions", db.list("select s from StudentCouncellingSession s where s.studentCouncelling.id = '" + councelling.getId() + "' order by s.sessionDate"));
		return path + "/listSessions.vm";
	}
	
	@Command("editSession")
	public String editSession() throws Exception {
		String sessionId = getParam("sessionId");
		StudentCouncellingSession councellingSession = db.find(StudentCouncellingSession.class, sessionId);
		context.put("councellingSession", councellingSession);
		context.put("councellors", db.list("select c from Councellor c"));
		return path + "/session.vm";
	}
		
	@Command("saveSession")
	public String saveSession() throws Exception {
		
		String sessionId = getParam("sessionId");
		StudentCouncellingSession cs = db.find(StudentCouncellingSession.class, sessionId);
		
		context.put("councelling", cs.getStudentCouncelling());
		
		String _sessionDate = getParam("sessionDate");
		Date sessionDate = null;
		try {
			sessionDate = new SimpleDateFormat("dd-MM-yyyy").parse(_sessionDate);
		} catch ( Exception e ) {}	
		String councellorId = getParam("councellorId");
		Councellor councellor = db.find(Councellor.class, councellorId);
		
		db.begin();
		cs.setSessionDate(sessionDate);
		cs.setCouncellor(councellor);
		cs.setSessionReport(getParam("sessionReport"));
		cs.setSessionSummary(getParam("sessionSummary"));
		db.commit();
		
		context.put("councellingSessions", db.list("select s from StudentCouncellingSession s where s.studentCouncelling.id = '" + cs.getStudentCouncelling().getId() + "' order by s.sessionDate"));
		return path + "/listSessions.vm";
	}
	
	@Command("deleteSession")
	public String deleteSession() throws Exception {
		
		String sessionId = getParam("sessionId");
		StudentCouncellingSession cs = db.find(StudentCouncellingSession.class, sessionId);
		
		StudentCouncelling councelling = cs.getStudentCouncelling();
		context.put("councelling", councelling);
		
		int i = 0;
		for ( StudentCouncellingSession s : councelling.getSessions() ) {
			if ( s.getId().equals(sessionId)) {
				break;
			}
			i++;
		}
	
		db.begin();
		councelling.getSessions().remove(i);
		db.remove(cs);
		db.commit();
		
		context.put("councellingSessions", db.list("select s from StudentCouncellingSession s where s.studentCouncelling.id = '" + councelling.getId() + "' order by s.sessionDate"));
		return path + "/listSessions.vm";
	}	
	
	@Command("listSessions")
	public String listSessions() throws Exception {
		String councellingId = getParam("councellingId");
		StudentCouncelling councelling = db.find(StudentCouncelling.class, councellingId);
		context.put("councelling", councelling);
		context.put("councellingSessions", db.list("select s from StudentCouncellingSession s where s.studentCouncelling.id = '" + councelling.getId() + "' order by s.sessionDate"));
		return path + "/listSessions.vm";
	}

}
