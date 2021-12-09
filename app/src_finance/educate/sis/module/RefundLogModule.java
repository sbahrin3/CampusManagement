package educate.sis.module;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.finance.entity.RefundLog;
import lebah.portal.action.AjaxModule;

public class RefundLogModule extends AjaxModule {
	
	String path = "apps/util/student_refund_log/";
	private String vm = path + "empty.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("timeFormat", new SimpleDateFormat("h:mm a"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.remove("message_id");
		context.remove("error");
		String command = request.getParameter("command");
		System.out.println(command);
		if ( command == null || "".equals(command)) start();
		else if ( "reload".equals(command)) reload();
		return vm;
	}
	
	private void reload() {
		
		String logDate = request.getParameter("log_date");
		Date date1 = new Date();
		try {
			date1 = new SimpleDateFormat("dd-MM-yyyy").parse(logDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		context.put("log_date", date1);
		
		String logDate2 = request.getParameter("log_date2");
		Date date2 = null;
		try {
			date2 = new SimpleDateFormat("dd-MM-yyyy").parse(logDate2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		List<RefundLog> logs = null;
		Hashtable h = new Hashtable();
		h.put("log_date", date1);
		if ( date2 == null ) {
			logs = db.list("select x from RefundLog x where x.createDate = :log_date order by x.createTime", h);
			context.remove("log_date2");
		}
		else {
			h.put("log_date2", date2);
			logs = db.list("select x from RefundLog x where x.createDate between :log_date and :log_date2 order by x.createTime", h);
			context.put("log_date2", date2);
		} 
		context.put("logs", logs);
		

		vm = path + "result.vm";
	}

	private void start() {
		//when started only show log for today
		
		Hashtable h = new Hashtable();
		h.put("log_date", new Date());
		List<RefundLog> logs = db.list("select x from RefundLog x where x.createDate = :log_date order by x.createTime", h);
		context.put("logs", logs);
		context.put("log_date", new Date());
		context.remove("log_date2");
		vm = path + "start.vm";
		
	}


}
