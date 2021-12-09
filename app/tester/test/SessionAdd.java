package test;

import lebah.db.*; 
import educate.sis.struct.entity.*;
import java.util.*;

public class SessionAdd {

	public static void main(String[] args) throws Exception {
		
		PersistenceManager pm = new PersistenceManager();
		pm.executeUpdate("delete from Session s");
		
		String code;
		Calendar d1, d2;
		Session session;
		
		code = "2008-1";
		d1 = new GregorianCalendar(2008, Calendar.JANUARY, 1);
		d2 = new GregorianCalendar(2008, Calendar.MAY, 31);
		session = new Session();
		session.setCode(code);
		session.setName(code);
		session.setStartDate(d1.getTime());
		session.setEndDate(d2.getTime());
		pm.add(session);
		
		
		code = "2008-2";
		d1 = new GregorianCalendar(2008, Calendar.JUNE, 1);
		d2 = new GregorianCalendar(2008, Calendar.DECEMBER, 31);
		session = new Session();
		session.setCode(code);
		session.setName(code);
		session.setStartDate(d1.getTime());
		session.setEndDate(d2.getTime());
		pm.add(session);
		
		
		code = "2009-1";
		d1 = new GregorianCalendar(2009, Calendar.JANUARY, 1);
		d2 = new GregorianCalendar(2009, Calendar.MAY, 31);
		session = new Session();
		session.setCode(code);
		session.setName(code);
		session.setStartDate(d1.getTime());
		session.setEndDate(d2.getTime());
		pm.add(session);
		
		
		code = "2009-2";
		d1 = new GregorianCalendar(2009, Calendar.JUNE, 1);
		d2 = new GregorianCalendar(2009, Calendar.DECEMBER, 31);
		session = new Session();
		session.setCode(code);
		session.setName(code);
		session.setStartDate(d1.getTime());
		session.setEndDate(d2.getTime());
		pm.add(session);			
		
		code = "2010-1";
		d1 = new GregorianCalendar(2010, Calendar.JANUARY, 1);
		d2 = new GregorianCalendar(2010, Calendar.MAY, 31);
		session = new Session();
		session.setCode(code);
		session.setName(code);
		session.setStartDate(d1.getTime());
		session.setEndDate(d2.getTime());
		pm.add(session);
		
	}
	
}
