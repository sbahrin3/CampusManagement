package educate.admission.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpSession;

import educate.admission.Application;
import educate.db.DbPersistence;
import lebah.portal.action.AjaxModule;

public class ApplicantManagementModule  extends AjaxModule {
	
	String path = "apps/util/admission/application/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();		
		return vm;
	}

	private void start() {
		//display list of status, and number of applicants beside each status
		vm = "statuses.vm";
		Application application = new Application();
		String[] statuses = application.getStatuses();
		context.put("statuses", statuses);
		
		
		
	}
	
	public static void main(String[] args) throws Exception {
		Application application = new Application();
		String[] statuses = application.getStatuses();
		String sql = "";
		for ( String status : statuses ) {
			System.out.println(status);
			sql = "";
			
		}
	}

}
