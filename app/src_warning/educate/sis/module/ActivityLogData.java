/**
 * 
 */
package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import educate.admission.entity.UserActivityLog;
import educate.db.DbPersistence;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class ActivityLogData {
	
	public static void main(String[] args) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		DbPersistence db = new DbPersistence();
		Hashtable h = new Hashtable();
		Calendar date = Calendar.getInstance();
		date.set(2014, 0, 1);
		h.put("date", date.getTime());
		List<UserActivityLog> logs = db.list("select l from UserActivityLog l where l.dateTime > :date order by l.dateTime desc", h);
		for ( UserActivityLog log : logs ) {
			System.out.println(df.format(log.getDateTime()) + ", " + log.getUserId() + ", " + log.getModule() + ", "+ log.getRemoteAddress() + ", " + log.getDescription());
		}
	}

}
