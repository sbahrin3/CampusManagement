package educate.studentaffair.module;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.studentaffair.entity.ClubActivity;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ClubActivityReportModule extends LebahModule {
	
	protected String path = "studentaffair/clubActivityReport";
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
		String proposalStatus = getParam("proposalStatus");
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
		Date date2 = new SimpleDateFormat("dd-MM-yyyy").parse(toDate);
		
		context.put("date1", date1);
		context.put("date2", date2);
		
		c1.setTime(date1);
		c2.setTime(date2);
		
		c1.add(Calendar.DATE, -1);
		c2.add(Calendar.DATE, 1);
		
		Hashtable h = new Hashtable();
		h.put("date1", c1.getTime());
		h.put("date2", c2.getTime());
		
		String sql = "";
		if ( "".equals(proposalStatus))
			sql = "select c from ClubActivity c where c.startDate > :date1 and c.endDate < :date2 order by c.startDate";
		else
			sql = "select c from ClubActivity c where c.startDate > :date1 and c.endDate < :date2 and c.proposalStatus = '" + proposalStatus + "' order by c.startDate";
		
		List<ClubActivity> activities = db.list(sql, h);
		context.put("activities", activities);
		
		request.getSession().setAttribute("activities", activities);
		
		return path + "/report.vm";
	}

}
