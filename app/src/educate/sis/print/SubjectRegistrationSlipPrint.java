package educate.sis.print;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.enrollment.entity.StudentStatus;
import lebah.portal.velocity.VTemplate;

public class SubjectRegistrationSlipPrint  extends VTemplate {
	
	String path = "apps/util/document_print/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public Template doTemplate() throws Exception {
		
		getAppUrl();
		
		setShowVM(false);
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		Template template = engine.getTemplate(getStudent());	
		return template;		
	}
	
	
	private String getStudent() {
		
		String id = request.getParameter("slip_student_status_id");
		StudentStatus studentStatus = db.find(StudentStatus.class, id);
		context.put("student_status", studentStatus);
		context.put("student", studentStatus.getStudent());
		
		context.put("student_subjects", studentStatus.getStudentSubjects());

		context.put("original_subjects", request.getSession().getAttribute("original_subjects"));
		context.put("added_subjects", request.getSession().getAttribute("added_subjects"));
		context.put("dropped_subjects", request.getSession().getAttribute("dropped_subjects"));
		
		
		
		return path + "subject_registration_slip.vm";
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


}
