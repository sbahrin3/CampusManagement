/**
 * 
 */
package educate.util;

import java.text.SimpleDateFormat;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.SessionBean;
import educate.sis.struct.entity.Session;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class SessionPathUtil {
	


	public static void copy(DbPersistence db, String pathFrom, String newPath) throws Exception {
		if ( !pathFrom.equals(newPath)) {
			db.begin();
			List<Session> sessions =  db.list("select s from Session s where s.pathNo = " + pathFrom + " order by s.startDate");
			for ( Session s : sessions ) {
				System.out.println(s.getCode() + ", " + new SimpleDateFormat("dd-MM-yyyy").format(s.getStartDate()));
				Session session = new Session();
				session.setPathNo(Integer.parseInt(newPath));
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
				db.persist(session);
			}
			db.commit();
		}
	}
	
	public static void clean(DbPersistence db, String pathNo) throws Exception {
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo);
		for ( Session s : sessions ) {
			String id = s.getId();
			new SessionBean().delete(id);
		}
	}

}
