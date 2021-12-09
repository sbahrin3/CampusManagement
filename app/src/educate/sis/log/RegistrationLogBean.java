package educate.sis.log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import educate.sis.log.entity.RegistrationLog;
import lebah.db.PersistenceManager;
import lebah.util.DateUtil;

public class RegistrationLogBean {
	private PersistenceManager pm;
	
	public void logs(String desc,String matricNo,String operator) throws Exception{
		Calendar cal = new GregorianCalendar();
		Date time = cal.getTime();
		pm = new PersistenceManager();
		RegistrationLog log = new RegistrationLog();
		log.setDescription(desc);
		log.setLogDate(new DateUtil().getToday());
		log.setMatricNo(matricNo);
		log.setOperator(operator);
		log.setLogTime(time);
		PersistenceManager.add(log);
	}
}
