package educate.sis.module;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.QuickRegisterLog;
import educate.enrollment.entity.Student;
import lebah.portal.action.AjaxModule;

public class QuickRegistrationLogModule extends AjaxModule {
	
	String path = "apps/util/student_quick_register_log/";
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
		else if ( "view_registration".equals(command)) viewRegistration();
		return vm;
	}
	
	private void viewRegistration() {
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		if ( student != null ) {
			context.put("student", student);
			vm = path + "registration.vm";
		} else {
			vm = path + "nodata.vm";
		}
	}

	private void reload() {
		
		String logDate = request.getParameter("log_date");
		String logDate2 = request.getParameter("log_date2");
		Date date = new Date();
		try {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(logDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date date2 = new Date();
		try {
			date2 = new SimpleDateFormat("dd-MM-yyyy").parse(logDate2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Hashtable h = new Hashtable();
		h.put("log_date", date);
		h.put("log_date2", date2);
		List<QuickRegisterLog> logs = db.list("select x from QuickRegisterLog x where x.logDate between :log_date and :log_date2 order by x.logDate, x.logTime", h);
		context.put("logs", logs);
		context.put("log_date", date);
		context.put("log_date2", date2);

		vm = path + "result.vm";
	}

	private void start() {
		//when started only show log for today
		
		Hashtable h = new Hashtable();
		h.put("log_date", new Date());
		List<QuickRegisterLog> logs = db.list("select x from QuickRegisterLog x where x.logDate = :log_date order by x.logTime", h);
		context.put("logs", logs);
		context.put("log_date", new Date());
		context.put("log_date2", new Date());
		vm = path + "start.vm";
		
	}

}
