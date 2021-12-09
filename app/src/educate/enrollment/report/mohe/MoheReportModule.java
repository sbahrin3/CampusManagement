package educate.enrollment.report.mohe;

import java.text.SimpleDateFormat;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.MoheRecord;
import educate.enrollment.entity.MoheRecordStatus;
import lebah.portal.action.AjaxModule;
/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0 
 * @date 27.8.2011
 */
public class MoheReportModule extends AjaxModule {
	
	String path = "mohe_report/";
	String vm = "";
	DbPersistence db = new DbPersistence();
	String userid = "";

	@Override
	public String doAction() throws Exception {
		userid = request.getSession().getAttribute("_portal_login") != null ? (String) request.getSession().getAttribute("_portal_login"): "";
		context.put("df", new SimpleDateFormat("dd-MM-yyyy"));
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		else if ( "create_report".equals(command)) createReport();
		else if ( "view_report_local".equals(command)) viewReportLocal();
		else if ( "view_report_international".equals(command)) viewReportInternational();
		return vm;
	}
	
	private void viewReportInternational() {
		List<MoheRecord> list = db.list("select r from MoheRecord r where r.reportType = 2");
		context.put("records", list);
		vm = path + "view_report.vm";
	}
	
	private void viewReportLocal() {
		List<MoheRecord> list = db.list("select r from MoheRecord r where r.reportType = 1");
		context.put("records", list);
		vm = path + "view_report.vm";
	}

	private void createReport() throws Exception {
		long timeTaken = GenerateMoheReport.createReport(userid);
		timeTaken = timeTaken / 1000;
		context.put("time_taken", Long.toString(timeTaken));
		MoheRecordStatus mrs = (MoheRecordStatus) db.get("select mrs from MoheRecordStatus mrs");
		if ( mrs != null ) context.put("report", mrs); else context.remove("report");
		vm = path + "report_status.vm";
	}

	private void start() {
		vm = path + "start.vm";
		MoheRecordStatus mrs = (MoheRecordStatus) db.get("select mrs from MoheRecordStatus mrs");
		if ( mrs != null ) context.put("report", mrs); else context.remove("report");
	}

}
