package educate.sis.examreport.module;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.exam.entity.FinalResult;
import educate.sis.struct.entity.Session;

public class TestReport {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		String sql = "select f from FinalResult f where f.cgpa >= 1 and f.cgpa <= 2";
		List<FinalResult> results = db.list(sql);
		System.out.println(results.size());
		
		
	}

	private static void getSessions(DbPersistence db) {
		Calendar c = Calendar.getInstance();
		c.set(2005, Calendar.APRIL, 20);
		Date today = new Date();
		Hashtable h = new Hashtable();
		h.put("date", c.getTime());
		
		String sql = "select s from Session s where s.startDate <= :date and s.endDate >= :date";
		List<Session> sessions = db.list(sql, h);
		for ( Session s : sessions ) {
			System.out.println(s.getName());
		}
	}

}
