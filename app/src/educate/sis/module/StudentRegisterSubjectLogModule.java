package educate.sis.module;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.struct.entity.SubjectAddDropLog;
import lebah.portal.action.AjaxModule;

public class StudentRegisterSubjectLogModule extends AjaxModule {
	
	String path = "apps/util/student_subject_register_log/";
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
		else if ( "get_student".equals(command)) getStudent();
		else if ( "view_registration".equals(command)) viewRegistration();
		return vm;
	}
	
	private void getStudent() {

		String matricNo = request.getParameter("matric_no");
		
		Hashtable h = new Hashtable();
		h.put("matric_no", matricNo);
		context.put("matric_no", matricNo);
		List<SubjectAddDropLog> logs = db.list("select x from SubjectAddDropLog x where x.student.matricNo = :matric_no order by x.date, x.time", h);
		context.put("logs", logs);
		context.remove("log_date");
		context.remove("log_date2");

		vm = path + "result.vm";
		
	}

	private void viewRegistration() {
		
		String id = request.getParameter("log_id");
		SubjectAddDropLog log = db.find(SubjectAddDropLog.class, id);
		context.put("log", log);
		context.put("student", log.getStudent());
		context.put("original_subjects", log.getOriginalSubjects());
		context.put("added_subjects", log.getAddedSubjects());
		context.put("dropped_subjects", log.getDroppedSubjects());
		context.put("current_subjects", log.getCurrentSubjects());
		
		vm = path + "add_drop.vm";
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
		List<SubjectAddDropLog> logs = db.list("select x from SubjectAddDropLog x where x.date between :log_date and :log_date2 order by x.date, x.time", h);
		context.put("logs", logs);
		context.put("log_date", date);
		context.put("log_date2", date2);
		context.remove("matric_no");

		vm = path + "result.vm";
	}

	private void start() {
		//when started only show log for today
		
		Hashtable h = new Hashtable();
		h.put("log_date", new Date());
		List<SubjectAddDropLog> logs = db.list("select x from SubjectAddDropLog x where x.date = :log_date order by x.time", h);
		context.put("logs", logs);
		context.put("log_date", new Date());
		context.put("log_date2", new Date());
		vm = path + "start.vm";
		
	}

}
