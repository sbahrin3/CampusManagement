package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.exam.entity.FinalResult;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class StudentExamResultAnalysisModule  extends AjaxModule {
	
	String path = "apps/util/student_exam_transcript2/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	protected boolean studentMode = false;

	
	@Override
	public String doAction() throws Exception {
		
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
		context.put("now", new Date());
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		context.remove("print_mode");
		if ( null == command || "".equals(command)) start();
		else if ( "get_transcript".equals(command)) getTranscript();
		else if ( "print_transcript".equals(command)) printTranscript();
		return vm;
	}

	private void printTranscript() throws Exception {
		vm = path + "print_transcript.vm";
		
		context.put("print_mode", true);
		String type = request.getParameter("type");
		
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		
		String sessionId = request.getParameter("session_id");
		context.put("student", student);
		
		String sql = "";
		
		if ( "1".equals(type)) sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' and r.session.id = '" + sessionId + "'";
		else if ( "2".equals(type)) {
			sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' and r.session.id <= '" + sessionId + "'";
		}
		
		List<FinalResult> results = db.list(sql);
		if ( results.size() > 0 ) {
			context.put("results", results);
		}
		else {
			context.remove("results");
		}
		
	}

	private void start() throws Exception {
		vm = path + "start.vm";
		if ( studentMode ) {
			getTranscript();
		}
	}
	
	private void getTranscript() throws Exception {
		vm = path + "transcript.vm";
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
	}
	
	public void showExamTranscript(Student student) throws Exception {
		vm = path + "transcript.vm";
		
		StudentStatusUtil s = new StudentStatusUtil();
		StudentStatus studentStatus = s.getCurrentStudentStatus(student);
		
		if ( studentStatus != null ) {
			context.put("student_status", studentStatus);
			context.put("current_semester", studentStatus.getPeriod());
		}
		else {
			context.remove("student_status");
			context.remove("current_semester");
		}
		
		SessionUtil u = new SessionUtil();
		Session currentSession = u.getCurrentSession(student.getProgram().getLevel().getPathNo());
		context.put("current_session", currentSession);
		
		context.put("student", student);
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		
		List<FinalResult> results = db.list(sql);
		if ( results.size() > 0 ) {
			context.put("results", results);
		}
		else {
			context.remove("results");
		}
		
		//student statuses
		Map<String, StudentStatus> studentStatusMap = new HashMap<String, StudentStatus>();
		context.put("statusMap", studentStatusMap);
		StudentStatusUtil statusUtil = new StudentStatusUtil();
		List<StudentStatus> studentStatusList = statusUtil.getStudentStatuses(student);
		for ( StudentStatus status : studentStatusList ) {
			studentStatusMap.put(status.getSession().getId(), status);
		}		
	}


}
