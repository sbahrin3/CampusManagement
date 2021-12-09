package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.StudentStatus;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentStatusReportModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "report/status";
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
	}

	@Override
	public String start() {
		List<StatusType> types = db.list("select s from StatusType s where s.inActive = 1 order by s.sequence");
		context.put("statusTypes", types);
		
		return path + "/start.vm";
	}
	
	@Command("listStudents")
	public String listStudents() throws Exception {
		String statusId = getParam("status");
		context.put("statusId", statusId);
		
		StatusType statusType = db.find(StatusType.class, statusId);
		context.put("status", statusType);
		
		int year = Integer.parseInt(getParam("year"));
		context.put("year", year);
		
		int year1 = year - 1;
		int year2 = year + 1;
		Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + year1);
		Date date2 = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + year2);
		
		Hashtable h = new Hashtable();
		h.put("date1", date1);
		h.put("date2", date2);
		
		String sql = "select s from StudentStatus s where (s.type.id = '" + statusId + "' and s.statusDate > :date1 and s.statusDate < :date2) or (s.type.id = '" + statusId + "' and s.session.startDate > :date1 and s.session.startDate < :date2) order by s.student.biodata.name";
		List<StudentStatus> students = db.list(sql, h);
		
		context.put("students", students);
		request.getSession().setAttribute("students", students);
		
		return path + "/listStudents.vm";
	}
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		int year = 2013;
		int year1 = year - 1;
		int year2 = year + 1;
		Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-" + year1);
		Date date2 = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + year2);
		
		Hashtable h = new Hashtable();
		h.put("date1", date1);
		h.put("date2", date2);
		
		String sql = "select s from StudentStatus s where s.statusDate > :date1 and s.statusDate < :date2 and s.type.id = 'quit'";
		List<StudentStatus> statuses = db.list(sql, h);
		System.out.println(statuses.size());
		
		
		
	}

}
