package educate.sis.struct;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Session;
import lebah.db.PersistenceManager;

public class SessionBean {
	private PersistenceManager pm;
	private DbPersistence db = new DbPersistence();
	private Session session;
	
	public void save(Session session) throws Exception{
		//pm = new PersistenceManager();
		//pm.add(session);
		
		db.begin();
		db.persist(session);
		db.commit();
	}
	
	public void update(String id, Session s)throws Exception{
		//pm = new PersistenceManager();
		//session =(Session)pm.find(Session.class).whereId(id).forUpdate();
		
		session = db.find(Session.class, id);
		
		db.begin();
		session.setCode(s.getCode());
		session.setName(s.getName());
		session.setStartDate(s.getStartDate());
		session.setEndDate(s.getEndDate());
		session.setStartDateDay(s.getStartDateDay());
		session.setStartDateMonth(s.getStartDateMonth());
		session.setStartDateYear(s.getStartDateYear());
		session.setEndDateDay(s.getEndDateDay());
		session.setEndDateMonth(s.getEndDateMonth());
		session.setEndDateYear(s.getEndDateYear());
		session.setPathNo(s.getPathNo());
		db.commit();
		
		//pm.update();
	}
	
	public Session getSession(String id)throws Exception{
		//pm = new PersistenceManager();
		//session =(Session)pm.find(Session.class,id);
		session = db.find(Session.class, id);
		return session;
	}
	
	public  String getSessionIdByDate(String dateStr) throws Exception {
		
		//System.out.println("dateStr = " +dateStr);
		int dd = Integer.parseInt(dateStr.substring(8, 9));
		int mm = Integer.parseInt(dateStr.substring(5, 6));
		int yyyy = Integer.parseInt(dateStr.substring(0, 3));
		
		Calendar dateObj = new GregorianCalendar(yyyy, mm, dd);
	    Date date = dateObj.getTime();
	    
	    Hashtable h = new Hashtable();
	    h.put("date", date);
		
		//System.out.println("SELECT a FROM Session a WHERE a.startDate <= :date AND a.endDate >= :date");
		//pm = new PersistenceManager();
		List<Session> l = db.list("SELECT a FROM Session a WHERE a.startDate <= :date AND a.endDate >= :date", h);
		if(l.size()>0){
			Session s = l.get(0);
			return s.getId();
		}else{
			return "";
		}
	}
	
	public Vector<Session> getSessionList()throws Exception {
		//pm = new PersistenceManager();
		List<Session> l = db.list("SELECT a FROM Session a ");
		Vector<Session> v = new Vector<Session>();
		v.addAll(l);
		return v;
	}
	
	public void delete(String id)throws Exception{
		//pm = new PersistenceManager();
		//session =(Session)pm.find(Session.class,id);
		//pm.delete(session);
		
		boolean canDelete = true;
		//is this session in used in student status?
		if ( db.list("select s from StudentStatus s where s.session.id = '" + id + "'").size() > 0 ) canDelete = false;

		if ( canDelete ) {
			session = db.find(Session.class, id);
			db.begin();
			db.remove(session);
			db.commit();
		} 
		else {
			System.out.println("Can't remove this session...");
		}
	}
	
	public Vector<Session> getList(int path_no, String direct) throws Exception {
		Vector<Session> v;
		//PersistenceManager pm = new PersistenceManager();
		String q="SELECT a FROM Session a WHERE a.pathNo =  " + path_no;
		
		if("desc".equals(direct)){
			 q=q+" ORDER BY a.startDate DESC";
		}
		else {
			 q=q+" ORDER BY a.startDate";
		}
		
		//System.out.println("QUERY :"+q);
		List<Session> l = db.list(q);
		v = new Vector<Session>();
		v.addAll(l);
		return v;
	}
}
