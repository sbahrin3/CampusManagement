package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.TranscriptEndorsedDate;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class StudentExamTranscriptModule extends AjaxModule {
	
	String path = "apps/util/student_exam_transcript/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	protected boolean studentMode = false;

	
	@Override
	public String doAction() throws Exception {
		
		String userLogin = (String) request.getSession().getAttribute("_portal_login");
		context.put("userLogin", userLogin);
		
		if ( studentMode ) {
			context.put("student_mode", true);
			context.remove("admin_mode");
		}
		else {
			context.remove("student_mode");
			context.put("admin_mode", true);
		}
		
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println("command = " + command);
		context.remove("print_mode");
		if ( null == command || "".equals(command)) start();
		else if ( "get_transcript".equals(command)) getTranscript();
		else if ( "recalculate".equals(command)) recalculate();
		else if ( "print_transcript".equals(command)) printTranscript();
		
		else if ( "delete_semester".equals(command)) deleteSemester();
		return vm;
	}

	private void printTranscript() throws Exception {
		vm = path + "print_transcript.vm";
		
		context.put("print_mode", true);
		String type = request.getParameter("type");
		
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String sessionId = request.getParameter("session_id");
		Session session = db.find(Session.class, sessionId);
		
		listEndorsedDates(student.getProgram());
		
		String sql = "";
		List<FinalResult> results = new ArrayList<FinalResult>();
		if ( "1".equals(type)) {
			sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' and r.session.id = '" + sessionId + "'";
			results = db.list(sql);
		}
		else if ( "2".equals(type)) {
			Hashtable h = new Hashtable();
			h.put("date", session.getStartDate());
			sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' and r.session.startDate <= :date";
			results = db.list(sql, h);
		}
		
		if ( results.size() > 0 ) {
			context.put("results", results);
			SessionUtil u = new SessionUtil();
			context.put("current_session", u.getCurrentSession(student.getProgram().getLevel().getPathNo()));
		}
		else {
			context.remove("results");
		}
		
	}
	
	private void listEndorsedDates(Program program) {
		
		String programId = program.getId();
		int pathNo = program.getLevel().getPathNo();

		Map<String, TranscriptEndorsedDate> endorsedDateMap = new HashMap<String, TranscriptEndorsedDate>();
		context.put("endorsedDateMap", endorsedDateMap);
		List<TranscriptEndorsedDate> endorsedDates = db.list("select d from TranscriptEndorsedDate d where d.program.id = '" + programId + "' order by d.session.id");
		for ( TranscriptEndorsedDate d : endorsedDates ) {
			endorsedDateMap.put(d.getSession().getId(), d);
		}
	}		

	private void start() throws Exception {
		vm = path + "start.vm";
		if ( studentMode ) {
			getTranscript();
		}
	}
	
	

	private void recalculate() throws Exception {
		String studentNo = request.getParameter("student_no");
		String hideF = getParam("hideF");
		boolean excludeGradeF = "yes".equals(hideF);
		
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + studentNo + "'");
		if ( student != null ) {
			ResultEntryUtil.recalculateResult(db, student, excludeGradeF);
			//create exam transcript
			showExamTranscript(student);
		}
		vm = path + "print_transcript.vm";
	}

	
	private void getTranscript() throws Exception {
		
		String studentNo = "";
		if ( studentMode ) {
			studentNo = (String) session.getAttribute("_portal_login");
		}
		else {
			studentNo = request.getParameter("student_no");
		}
		
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + studentNo + "'");
		
		if ( student != null ) {
			
			showExamTranscript(student);
		}
		else {
			context.remove("student");
		}
		
		vm = path + "print_transcript.vm";
	}
	
	public void showExamTranscript(Student student) throws Exception {
		vm = path + "transcript.vm";
		
		//NEED REVISION
		//StudentStatusChecker.repairUndefinedStatus(db, student);
		
		StudentExamTranscriptUtil u = new StudentExamTranscriptUtil(request, context, db);
		u.showExamTranscript(student);
		
		StudentStatusUtil su = new StudentStatusUtil();
		StudentStatus currentStatus = su.getCurrentStudentStatus(student);
		
		context.put("currentStatus", currentStatus);
		
		Session currentSession = currentStatus != null ? currentStatus.getSession() : null;
		context.put("current_session", currentSession);
		context.put("display_session", currentSession);
		
		List<StudentStatus> studentStatuses = su.getStudentStatuses(student);
		Session prevSession = null;
		List<Session> sessions = new ArrayList<Session>();
		context.put("prevSessions", sessions);
		if ( currentSession != null ) {
			for ( StudentStatus s : studentStatuses ) {
				if ( s.getSession().getId().equals(currentSession.getId())) {
					break;
				}
				prevSession = s.getSession();
				sessions.add(s.getSession());
			}
		}
		
		//prevSession must not equal current session, to avoid student with only one session
		if ( prevSession == null ) {
			context.remove("previous_session");
		} else {
			if ( !prevSession.getId().equals(currentSession.getId()) )
				context.put("previous_session", prevSession);
			else
				context.remove("previous_session");
		
		}
		
	}
	
	public void deleteSemester() throws Exception {
		
		String studentId = getParam("studentId");
		String sessionId = getParam("sessionId");
		String periodId = getParam("periodId");
		
		String sql = "select r from FinalResult r "
				+ "where r.student.id = '" + studentId + "' " 
				+ "and r.session.id = '" + sessionId + "' "
				+ "and r.period.id = '" + periodId + "' "
				+ "order by r.session.startDate";
		
		List<FinalResult> results = db.list(sql);
		if ( results.size() > 0 ) {
			FinalResult result = results.get(0);
			if ( result != null ) {
				db.begin();
				db.remove(result);
				db.commit();
			}
		}
		
		getTranscript();
	}
	

}
