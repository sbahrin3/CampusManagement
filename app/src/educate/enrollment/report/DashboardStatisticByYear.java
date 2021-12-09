package educate.enrollment.report;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.Result;
import educate.enrollment.entity.StudentYearCount;
import educate.fusionchart.Chart;
import educate.sis.struct.entity.Session;
import lebah.portal.action.LebahModule;

public class DashboardStatisticByYear extends LebahModule {
	
	protected final String path = "apps/util/student_stat/";
	DbPersistence db = new DbPersistence();
	String divChartDataName = "div_chart_data";
	
	
	static {
		try {
			generateStat();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String start() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.id, s.year, s.counts) " +
		" FROM StudentYearCount s ORDER BY s.year";
		List<Result> results = statistic(chart, sql, "byYear");
		context.put("year_results", results);
		context.put("year_chart", chart);
		return path + "dashboard/statistic_year.vm";
	}
	
	protected List<Result> statistic(Chart chart, String sql, String commandCall) {
		DbPersistence db = new DbPersistence();
		List<Result> results = db.list(sql);
		createChartValues(chart, results, commandCall);
		return results;
	}
	
	protected void createChartValues(Chart chart, List<Result> results, String commandCall) {
		for ( Result result : results ) {
			try {
				String link = "javascript:doPositionDivAjaxCall" + formName + "(&quot;" + divChartDataName + "&quot;, &quot;" + commandCall + "&quot;, &quot;id=" + result.getKey() + "&quot)";
				chart.add(URLEncoder.encode(result.getLabel(), "UTF-8"), result.getCounter(), link);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	public static void main(String[] args) throws Exception {
		generateStat();
	}

	private static void generateStat() throws Exception {
		DbPersistence db = new DbPersistence();
		//get years range - start and end
		List<Session> sessions = db.list("select s from Session s order by s.startDate");
		Session s1 = sessions.get(0);
		Session s2 = sessions.get(sessions.size()-1);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(s1.getStartDate());
		c2.setTime(s2.getStartDate());
		int y1 = c1.get(Calendar.YEAR) - 1;
		int y2 = c2.get(Calendar.YEAR) + 1;
		
		//delete first
		db.begin();
		db.executeUpdate("delete from StudentYearCount");
		db.commit();
		for ( int y = y1; y < y2; y++ ) {
			long count = getStudentCount(db, y);
			//System.out.println(y + " = " + count);
			db.begin();
			StudentYearCount syc = new StudentYearCount();
			syc.setYear(y);
			syc.setCounts(count);
			db.persist(syc);
			db.commit();
		}
	}


	private static long getStudentCount(DbPersistence db, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, 5, 30);
		
		Hashtable h = new Hashtable();
		h.put("reportDate", cal.getTime());
		
		String sql = "select count(s) " +
		"from StudentStatus s JOIN s.session ss " +
		"WHERE (ss.startDate <= :reportDate " +
		"and ss.endDate >= :reportDate) " +
		"and s.type.inActive = 0 " +
		"and (s.student.fakeStudent is null OR s.student.fakeStudent = '') ";
		
		List<Long> results = db.list(sql, h);  
		return results.get(0);
	}

}
