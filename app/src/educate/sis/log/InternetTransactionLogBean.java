package educate.sis.log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import educate.sis.log.entity.InternetTransactionLog;
import lebah.db.PersistenceManager;
import lebah.util.DateUtil;

public class InternetTransactionLogBean {
	private PersistenceManager pm;
	public void doLog(double amount,String user,String referNo,String status,String remarks)throws Exception{
		pm = new PersistenceManager();
		Calendar cal = new GregorianCalendar();
		Date time = cal.getTime();
		InternetTransactionLog log = new InternetTransactionLog();
		log.setAmount(amount);
		log.setUser(user);
		log.setReferenceNo(referNo);
		log.setStatus(status);
		log.setRemarks(remarks);
		log.setDate(new DateUtil().getToday());
		log.setTime(time);
		PersistenceManager.add(log);
	}
}
