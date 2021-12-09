package educate.studentaffair.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.studentaffair.entity.StudentCouncelling;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentCouncellingReportModule extends LebahModule {
	
	protected String path = "studentaffair/student_councelling_report";
	protected DbPersistence db = new DbPersistence();	
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
	}

	@Override
	public String start() {

		return path + "/start.vm";
	}
	
	@Command("getReport")
	public String getReport() throws Exception { 
		String fromDate = getParam("fromDate");
		String toDate = getParam("toDate");
		
		Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
		Date date2 = new SimpleDateFormat("dd-MM-yyyy").parse(toDate);
		
		context.put("date1", date1);
		context.put("date2", date2);
		
		Hashtable h = new Hashtable();
		h.put("date1", date1);
		h.put("date2", date2);
		
		String sql = "select s from StudentCouncelling s where s.referredDate between :date1 and :date2";

		List<StudentCouncelling> records = db.list(sql, h);
		context.put("records", records);
		
		request.getSession().setAttribute("records", records);
		
		return path + "/report.vm";
	}

}
