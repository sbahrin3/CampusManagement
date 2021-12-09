package educate.enrollment.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import lebah.portal.action.LebahModule;

public class DashboardStudentStat extends LebahModule {
	
	private String path = "apps/util/student_stat/dashboard/";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		
		Date today = new Date();
		Hashtable h = new Hashtable();
		h.put("today", today);
		
		String sql = "select new educate.enrollment.report.Record(s.period.name, count(s)) from StudentStatus s JOIN s.session ss " +
		"where (ss.startDate <= :today " +
		"and ss.endDate >= :today) " +
		"and s.type.inActive = 0 group by s.period.id";
		
		
		List<Record> list = db.list(sql, h);
		context.put("stats", list);
		
		sql = "select new educate.enrollment.report.Record(s.period.name, s.student.biodata.gender.code, count(s)) from StudentStatus s JOIN s.session ss " +
		"where (ss.startDate <= :today " +
		"and ss.endDate >= :today) " +
		"and s.type.inActive = 0 group by s.period.id, s.student.biodata.gender.code";
		
		
		List<Record> list2 = db.list(sql, h);
		context.put("stats2", list2);
		
		List<String> periods = new ArrayList<String>();
		context.put("periods", periods);
		String t = "";
		for ( Record r : list2 ) {
			if ( !t.equals(r.s1)) {
				periods.add(r.s1);
				t = r.s1;
			}
		}
		
		return path + "student_stat.vm";
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		Date today = new Date();
		Hashtable h = new Hashtable();
		h.put("today", today);
		
		String sql = "select new educate.enrollment.report.Record(s.period.name, count(s)) from StudentStatus s JOIN s.session ss " +
		"where (ss.startDate <= :today " +
		"and ss.endDate >= :today) " +
		"and s.type.inActive = 0 group by s.period.id";
		
		
		List<Record> list = db.list(sql, h);
		System.out.println(sql);
		for ( Record r : list ) {
			System.out.println(r.s1 + "=" + r.cnt);
		}

		sql = "select new educate.enrollment.report.Record(s.period.name, s.student.biodata.gender.code, count(s)) from StudentStatus s JOIN s.session ss " +
		"where (ss.startDate <= :today " +
		"and ss.endDate >= :today) " +
		"and s.type.inActive = 0 group by s.period.id, s.student.biodata.gender.code";
		
		
		List<Record> list2 = db.list(sql, h);
		System.out.println(sql);
		for ( Record r : list2 ) {
			System.out.println(r.s1 + ", " + r.s2 + "=" + r.cnt);
		}
		
	}

}
