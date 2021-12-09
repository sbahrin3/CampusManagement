package educate.enrollment.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Program;
import lebah.portal.action.LebahModule;

public class StudentStatisticReportModule2 extends LebahModule {
	
	DbPersistence db = new DbPersistence();
	String path = "apps/student_statistic";
	String[] pellets = {
						"#ff0000","#ff3399","#009900","#2db300","#ff9900","#cc6600","#990033","#800040","#6600ff","#d1b3ff",
						"#0099ff","#004d80","#006622","#ff8c1a","#994d00","#006bb3","#99d6ff","#99b3e6","#2952a3","#ff5500"
					  };
	
	
	@Override
	public String start() {
		try {
			String startDate = "15-01-2013";
			List<Hashtable> dataList = createReport(startDate, "", "active");
			context.put("dataList", dataList);
			
			createReportByProgram(startDate);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path + "/start.vm";
	}
	
	private void createReportByProgram(String startDate) throws Exception {
		List<Program> programs = db.list("select p from Program p order by p.name");
		context.put("programs", programs);
		Hashtable h = new Hashtable();
		context.put("programData", h);
		List<String> colors = new ArrayList<String>();
		context.put("colors", colors);
		int i = 0;
		for ( Program p : programs ) {
			List<Hashtable> dataList = createReport(startDate, p.getId(), "active");
			h.put(p.getId(), dataList);
			colors.add(pellets[i]);
			i++;
		}
		
	}
	


	private List<Hashtable> createReport(String startDate, String programId, String statusId) throws Exception {
		
		List<Hashtable> dataList = new ArrayList<Hashtable>();
		
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		String sql = "";
		Date dateNow = new Date();
		Date date1 = df.parse(startDate);
		Calendar c = Calendar.getInstance();
		c.setTime(date1);
		List<Date> dates = new ArrayList<Date>();
		int i = 0;
		while (true) {
			i++;
			c.add(Calendar.MONTH, 1);
			Date date = c.getTime();
			dates.add(date);
			c.setTime(date);
			if ( date.after(dateNow)) break;
		}
		Calendar cal = Calendar.getInstance();
		i = 0;
		for ( Date date : dates ) {
			long cnt = getResult(db, date, programId, statusId);
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			long value = cnt;
			
			Hashtable h = new Hashtable();
			h.put("year", year);
			h.put("month", month);
			h.put("day", day);
			h.put("value", value);
			
			dataList.add(h);
			
			i++;
		}
		return dataList;
	}
	
	

	public static long getResult(DbPersistence db, Date reportDate, String programId, String statusId) throws Exception {
		String sql = "";
		Hashtable param = new Hashtable();
		param.put("reportDate", reportDate);
		sql = "select count(s) ";
		sql += " from StudentStatus s JOIN s.session ss ";
		sql += "WHERE (ss.startDate <= :reportDate and ss.endDate >= :reportDate) " +
		"and (s.student.fakeStudent is null OR s.student.fakeStudent = '') ";
		
		if ( !"".equals(programId)) {
			param.put("program_id", programId);
			sql += _sql(sql, "s.student.program.id = :program_id");
		}
		
		if ( !"".equals(statusId)) {
			param.put("status_id", statusId);
			sql += _sql(sql, "s.type.id = :status_id");
		}
		
		Long cnt = (Long) db.get(sql, param);
		if ( cnt != null ) {
			return cnt.longValue();
		} else
			return 0;
		
	}
	
	private static String _sql(String sql, String s) {
		return " AND " + s;
	}



}
