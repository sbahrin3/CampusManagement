package howto;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.sis.module.ProgramUtil;
import educate.sis.module.RefundItem;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.velocity.VTemplate;

public class SamplePrintToPDFModule extends VTemplate {
	
	String path = "apps/util/student_refund/";
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
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		Template template = engine.getTemplate(getSponsorInvoice());	
		return template;		
	}

	private String getSponsorInvoice() {
        context.put("currentDate", new Date());
        
        listStudents();
		
		return path + "print_template.vm";
	}
	
	private void listStudents() {
		
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		
		String data = request.getParameter("data");
		System.out.println("data = " + data);
		
		String programId = "";
		String intakeId = "";
		//String sessionId = "";
		//String listType = "";
		
		StringTokenizer st = new StringTokenizer(data, ",");
		int token = 0;
		while ( st.hasMoreTokens() ) {
			if ( token == 0 ) {
				String s = st.nextToken();
				programId = !"null".equals(s) ? s : "";
			}
			else if ( token == 1 ) {
				String s = st.nextToken();
				intakeId = !"null".equals(s) ? s : "";
			}
			/*
			else if ( token == 2 ) {
				String s = st.nextToken();
				sessionId = !"null".equals(s) ? s : "";
			}
			else if ( token == 3 ) {
				String s = st.nextToken();
				listType = !"null".equals(s) ? s : "";
				
			}
			*/
			token++;
		}

		Program program = !"null".equals(programId) ? db.find(Program.class, programId) : null;
		Session intake = !"null".equals(intakeId) ? db.find(Session.class, intakeId) : null;
		//Session session = !"null".equals(sessionId) ? db.find(Session.class, sessionId) : null;

		context.put("program", program);
		context.put("intake", intake);
		//context.put("session", session);
		
		List<RefundItem> students = RefundItem.getRefundList(db, programId, intakeId);
		
		context.put("students", students);
	}	


}
