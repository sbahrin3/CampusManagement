package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.sis.exam.entity.CourseworkItem;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import lebah.portal.db.User;
import lebah.portal.velocity.VTemplate;

public class AssessmentResultPDFModule extends VTemplate {
	
	String path = "apps/util/assessment_result/";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		
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
        
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		//context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		
		context.put("numFormat", new DecimalFormat("#.00"));
		context.put("resultFormat", new DecimalFormat("##"));
		
		context.put("util", new lebah.util.Util());
		Template template = engine.getTemplate(getExamResultEntries());	
		return template;		
	}

	private String getExamResultEntries() throws Exception {
		
		String userId = getParam("userId");
		String programId = getParam("programId");
		String subjectId = getParam("subjectId");
		String sessionId = getParam("sessionId");
		String intakeId = getParam("intakeId");
		String sectionId = getParam("sectionId");
		String centreId = getParam("centreId");
		
		//get teacher information
		if ( !"".equals(userId)) {
			User user = lebah.portal.db.UserData.getUser(userId);
			context.put("user", user);
		} else 
			context.remove("user");
		
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		Session session = db.find(Session.class, sessionId);
		context.put("session", session);
		Session intake = db.find(Session.class, intakeId);
		context.put("intake", intake);
		
		AssessmentResultRequest resultRequest = new AssessmentResultRequest(db);

		List<CourseworkItem> markItems = resultRequest.getCourseworkItem(subjectId, sessionId, centreId);
		context.put("markItems", markItems);
		//List<List> entries = listStudents(subject, programId, sessionId, intakeId, centreId, sectionId, markItems);
		List<List> entries = resultRequest.listStudents(subject, programId, sessionId, intakeId, centreId, sectionId, markItems);
		context.put("entries", entries);
		
		/*
		String sql = "select c from Coursework c where c.subject.id = '" + subjectId + "' and c.session.id = '" + sessionId + "' and c.centre.id = '" + centreId + "'";
		Coursework coursework = (Coursework) db.get(sql);
		int roundType = coursework != null ? coursework.getRoundType() : 0;
		context.put("roundType", roundType);
		*/
		
		
		return path + "print_result_entries.vm";
	}
	


}
