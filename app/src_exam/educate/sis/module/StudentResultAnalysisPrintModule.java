package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.exam.entity.FinalResult;
import educate.sis.struct.entity.Session;
import lebah.portal.velocity.VTemplate;

public class StudentResultAnalysisPrintModule  extends VTemplate {
	
	String path = "apps/util/student_exam_transcript2/";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("now", new Date());
		lebah.util.Util util = new lebah.util.Util();
		context.put("util", util);
		Template template = engine.getTemplate(getExamTranscript());	
		return template;		
	}

	private String getExamTranscript() throws Exception {
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
        String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
        String serverUrl = http + server;
        context.put("serverUrl", serverUrl);
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		SessionUtil u = new SessionUtil();
		Session currentSession = u.getCurrentSession(student.getProgram().getLevel().getPathNo());
		context.put("current_session", currentSession);
		context.put("student", student);
		
		String type = request.getParameter("type");
		String sessionId = request.getParameter("session_id");
		
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		
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
		
		context.put("print_mode", true);
		return path + "download_transcript.vm";
	}



}
