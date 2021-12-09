package test;

import lebah.db.*;
import educate.sis.struct.entity.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class SessionPeriod {
	
	public static void main(String[] args) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		
		//get a period scheme
		PeriodScheme scheme = (PeriodScheme) pm.find(PeriodScheme.class, "bit");
		System.out.println(scheme.getCode());
		List<Period> periods = scheme.getPeriodList();
		for ( Period p : periods ) {
			System.out.println(p.getName());
			if ( p.hasChild() ) {
				for ( Period child : p.getChildList() ) {
					System.out.print("+" + child.getName());
					System.out.println();
				}
			}
		}		
		
		//get begin session
		String code = "2008-2";
		List<Session> list = pm.list("select s from Session s where s.code = '" + code + "'");
		Session beginSession = null;
		if ( list.size() == 1 ) {
			beginSession = list.get(0);
			System.out.println("begin session is " + beginSession.getCode() + " - " + new SimpleDateFormat("dd-MM-yy").format(beginSession.getStartDate()));
		}
		
		//get a list of sessions, started from beginSession
		Hashtable h = new Hashtable();
		h.put("dateStart", beginSession.getStartDate());
		List<Session> sessions = pm.list("select s from Session s where s.startDate >= :dateStart order by s.startDate", h); 
		for ( Iterator<Session> it = sessions.iterator(); it.hasNext(); ) {
			Session session = it.next();
			System.out.println(session.getCode() + ", " 
					+ new SimpleDateFormat("dd-MM-yy").format(session.getStartDate())
					+ " -> "
					+ new SimpleDateFormat("dd-MM-yy").format(session.getEndDate())
			);
		}

	}

}
