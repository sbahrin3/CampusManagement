package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.velocity.Template;

import educate.sis.struct.MatricNumGenerator;
import educate.sis.struct.entity.Graduate;
import lebah.portal.velocity.VTemplate;
import lebah.template2.DbPersistence;

public class PrintGraduationInvitationModule extends VTemplate {
	
	String path = "apps/util/student_graduation2/";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("today", new Date());
		lebah.util.Util util = new lebah.util.Util();
		context.put("util", util);
		Template template = engine.getTemplate(getStudent());	
		return template;		
	}

	private String getStudent() throws Exception {
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
		String graduateId = request.getParameter("graduateId");
		Graduate graduate = db.find(Graduate.class, graduateId);
		context.put("graduate", graduate);
		
		if ( graduate.getReferenceNo() == null || "".equals(graduate.getReferenceNo())) {
			String ref = new MatricNumGenerator().generateReferenceNo("convocation");
			db.begin();
			graduate.setReferenceNo(ref);
			db.commit();
		}
		
		context.put("currentDate", new Date());
		return path + "printGraduationInvitationLetter.vm";
	}


}
