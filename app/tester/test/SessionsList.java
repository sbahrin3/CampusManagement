package test;

import lebah.db.*;
import educate.sis.struct.entity.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class SessionsList {
	
	public static void main(String[] args) throws Exception {
		
		PersistenceManager pm = new PersistenceManager();
		List<Session> sessions = pm.list("select s from Session s");
		System.out.println(sessions.size());
		
		for ( Iterator<Session> it = sessions.iterator(); it.hasNext(); ) {
			Session session = it.next();
			System.out.println(session.getCode() + ", " + 
					new SimpleDateFormat("dd-MM-yyyy").format(session.getStartDate())
					+ ", " + 
					new SimpleDateFormat("dd-MM-yyyy").format(session.getEndDate())
					
			);
		}
		
	    Date date = new GregorianCalendar(2008, Calendar.JULY, 25).getTime();
	    System.out.println(new SimpleDateFormat("dd-MM-yyyy").format(date));
	    Hashtable h = new Hashtable();
	    h.put("date", date);
	    
	    List<Session> list = pm.list("select s from Session s where :date BETWEEN s.startDate AND s.endDate", h);
		System.out.println(list.size());
		
		for ( Iterator<Session> it = list.iterator(); it.hasNext(); ) {
			Session session = it.next();
			System.out.println(session.getCode() + ", " + new SimpleDateFormat("dd-MM-yyyy").format(session.getStartDate()));
		}		
	    
	}

}
