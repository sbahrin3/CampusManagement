package educate.sis.log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import educate.sis.log.entity.BillingLog;
import lebah.db.PersistenceManager;
import lebah.util.DateUtil;

public class BillingLogBean {
	private PersistenceManager pm;
	public static final String INVOICE_TYPE="INVOICE";
	public static final String PAYMENT_TYPE="PAYMENT";
	public static final String SHOPING_TYPE="SHOPING_CART";
	
	public void doLog(String user,String no,String transaction)throws Exception{
		System.out.println("LOGGING BLLING");
		Calendar cal = new GregorianCalendar();
		Date time = cal.getTime();
		pm = new PersistenceManager();
		BillingLog log = new BillingLog();
		log.setDate(new DateUtil().getToday());
		log.setUser(user);
		log.setType(transaction);
		log.setNo(no);
		log.setTime(time);
		PersistenceManager.add(log);
	}
	
	public Vector<BillingLog> listLog(String orderBy)throws Exception{
		pm = new PersistenceManager();
		if(orderBy.equals("")|| orderBy == null){
			orderBy="date";
		}
		List<BillingLog>l = pm.list("SELECT a FROM BillingLog a ORDER BY a."+orderBy);
		Vector<BillingLog> c = new Vector<BillingLog>();
		c.addAll(l);
		return c;
	}
}
