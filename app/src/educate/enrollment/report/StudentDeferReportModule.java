package educate.enrollment.report;

import java.text.SimpleDateFormat;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.StdWithdrawApp;
import lebah.portal.action.AjaxModule;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 */
public class StudentDeferReportModule extends AjaxModule {
	
	private final String path = "apps/util/app_defer/";
	private String vm = "default.vm";
	DbPersistence db = new DbPersistence();
	
	
	@Override
	public String doAction() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		context.put("dateFormat", dateFormat);
		String command = request.getParameter("command");
		
		if ( command == null || "".equals(command)) doReport();
		return vm;
	}

	private void doReport() {
		vm = path + "report.vm";
		String sql = "select a from DeferStudy a " +
				"JOIN a.student s " +
				"JOIN s.status st " +
				"WHERE a.reqDate BETWEEN st.session.startDate AND st.session.endDate " +
				"ORDER BY a.reqDate DESC";
		List<StdWithdrawApp> list = db.list(sql);
		context.put("apps", list);
		list = null;
		System.gc();
	}
	

}
