package educate.sis.exam.module;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.Session;

public class PeriodSessionLookup {
	
	private DbPersistence db = new DbPersistence();
	private Hashtable<Period, Session> ps = new Hashtable<Period, Session>();
	private String periodSchemeId;
	private String intakeId;
	
	public PeriodSessionLookup(String periodSchemeId, String intakeId) {
		this.periodSchemeId = periodSchemeId;
		this.intakeId = intakeId;
		createLookup();
	}
	
	private void createLookup() {
		PeriodScheme periodScheme = db.find(PeriodScheme.class, periodSchemeId);
		Session intake = db.find(Session.class, intakeId);
		Vector<Period> periodList = new Vector<Period>();
		//get all period
		List<Period> periods = periodScheme.getFunctionalPeriodList();
		for ( Iterator<Period> it = periods.iterator(); it.hasNext(); ) {
			Period p = it.next();
			periodList.add(p);
		}
		
		Hashtable<String, Object> h = new Hashtable<String, Object>();
		h.put("dateStart", intake.getStartDate());
		Vector<Session> sessionList = new Vector<Session>();
		List<Session> sessions = db.list("select s from Session s where s.startDate >= :dateStart order by s.startDate", h); 
		for ( Iterator<Session> it = sessions.iterator(); it.hasNext(); ) {
			Session session = it.next();
			sessionList.add(session);
		}
		
		//match period with session
		int i = 0;
		for ( Period p : periodList ) {
			Session s = sessionList.elementAt(i);
			ps.put(p, s);
			i++;
		}
	}
	
	public Session getSession(Period period) {
		return ps.get(period);
	}
	
	public Session getSession(String periodId) {
		Period period = db.find(Period.class, periodId);
		return ps.get(period);
	}

}
