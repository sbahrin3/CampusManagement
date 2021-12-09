package educate.sis.module;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Session;

public class SessionUtil {
	
	DbPersistence db = new DbPersistence();
	
	public Session getSessionByDate(Date date, int pathNo) throws Exception {
		Session session = null;
		Hashtable h = new Hashtable();
		h.put("date", date);
		List<Session> list = db.list("select s from Session s where (:date BETWEEN s.startDate AND s.endDate) AND s.pathNo="+pathNo, h);
		if ( list.size() == 0 ) return null;
		
		System.out.println("Path no = " + pathNo);
		
		session = list.get(0);
		System.out.println("session is " + session.getName());
		return session;
	}
	
	public Session getCurrentSession(int pathNo) throws Exception {
		Calendar cal = new GregorianCalendar();
		Date date = cal.getTime();
		return getSessionByDate(date,pathNo);
	}	
	
	public List<Session> getIntakeList() throws Exception {
		String sql = "select s.intake from StudentStatus st JOIN st.student s group by s.intake order by s.intake.startDate desc";
		List<Session> intakes = db.list(sql);
		return intakes;
	}
	
	public List<Session> getSessionList() throws Exception {
		String sql = "select st.session from StudentStatus st JOIN st.student s group by s.intake order by s.intake.startDate desc";
		List<Session> intakes = db.list(sql);
		return intakes;
	}
	
	public void getPeriodList(String sessionId) throws Exception {
		String sql = "select st.period from StudentStatus st where st.session.id = '" + sessionId + "' group by st.period order by st.period.sequence";
		DbPersistence db = new DbPersistence();
		List<Period> periods = db.list(sql);
		
	}
	
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String sql = "select s.intake from StudentStatus st join st.student s group by s.intake order by s.intake.startDate desc";
		List<Session> sessions = db.list(sql);
		System.out.println(sessions.size());
		for ( Session s : sessions ) {
			System.out.println(s.getCode());
		}
		
		
	}
	

}
