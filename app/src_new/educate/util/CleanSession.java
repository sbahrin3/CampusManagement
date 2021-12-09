/**
 * 
 */
package educate.util;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.SessionBean;
import educate.sis.struct.entity.Session;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class CleanSession {
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		List<Session> sessions = db.list("select s from Session s where s.pathNo = 0");
		for ( Session s : sessions ) {
			String id = s.getId();
			new SessionBean().delete(id);
		}
	}

}
