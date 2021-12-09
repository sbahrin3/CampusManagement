package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.struct.entity.Session;
import lebah.portal.velocity.VTemplate;

public class PrintStudentExamTranscriptModule extends VTemplate {
	
	String path = "apps/util/student_exam_transcript/";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		
		getAppUrl();   
        
		Template template = engine.getTemplate(getStudentTranscript());	
		return template;		
	}

	private void getAppUrl() {
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
        String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
        String serverUrl = http + server;
        context.put("serverUrl", serverUrl);
        String uri = request.getRequestURI();
        String appname = uri.substring(1);
        appname = appname.substring(0, appname.indexOf("/"));
        context.put("appUrl", serverUrl.concat("/").concat(appname));
	}

	private String getStudentTranscript() throws Exception {
		String twocolumns = getParam("twocolumns");
		String summary = getParam("summary");
		String smode = getParam("smode");
		
		String sessionId = getParam("sessionId");
		context.put("print_sessionId", sessionId);
		if ( !"".equals(sessionId)) {
			context.put("print_semester", true);
		} else {
			context.remove("print_semester");
		}
		
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		if ( student != null ) {
			showExamTranscript(student, twocolumns, summary);
		}
		else {
			context.remove("student");
		}
		if ( "yes".equals(smode)) context.put("student_mode", true);
		else context.remove("student_mode");
		
		String logo = getParam("logo");
		context.put("show_header", logo);
		
		
		return path + "print_transcript2.vm";
	}
	
	public void showExamTranscript(Student student, String twocolumns, String summary) throws Exception {
		StudentExamTranscriptUtil u = new StudentExamTranscriptUtil(request, context, db);
		u.showExamTranscript(student);
		
		context.put("twocolumns", "true".equals(twocolumns) ? true : false);
		context.put("summary", "true".equals(summary) ? true : false);	
		
		StudentStatusUtil su = new StudentStatusUtil();
		StudentStatus currentStatus = su.getCurrentStudentStatus(student);
		context.put("currentStatus", currentStatus);
		
		Session currentSession = currentStatus != null ? currentStatus.getSession() : null;
		
		List<StudentStatus> studentStatuses = su.getStudentStatuses(student);
		Session prevSession = null;
		if ( currentStatus != null ) {
			for ( StudentStatus s : studentStatuses ) {
				if ( s.getSession().getId().equals(currentSession.getId())) {
					break;
				}
				prevSession = s.getSession();
			}
		}
		
		context.put("current_session", prevSession);
				
	}
	

}
