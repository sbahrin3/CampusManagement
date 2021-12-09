package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.general.entity.SchoolSubject;
import lebah.portal.action.AjaxModule;

public class SchoolSubjectSetupModule extends AjaxModule {
	
	String path = "apps/util/applicant_setup/subjects/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();	
		else if ( "list_subjects".equals(command)) listSubjects();
		else if ( "add_subject".equals(command)) addSubject();
		return vm;
	}

	private void listSubjects() {
		vm = path + "list_subjects.vm";
		String stype = request.getParameter("stype");
		List<SchoolSubject> subjects = db.list("select s from SchoolSubject s where s.stype = '" + stype + "'");
		context.put("subjects", subjects);
		
	}

	private void addSubject() throws Exception {
		vm = path + "list_subjects.vm";
		String code = request.getParameter("subject_code");
		String name = request.getParameter("subject_name");
		String stype = request.getParameter("stype");
		
		db.begin();
		SchoolSubject subject = new SchoolSubject();
		subject.setCode(code);
		subject.setName(name);
		subject.setType(stype);
		db.persist(subject);
		db.commit();
		
		List<SchoolSubject> subjects = db.list("select s from SchoolSubject s where s.stype = '" + stype + "'");
		context.put("subjects", subjects);
		
	}

	private void start() {
		vm = path + "start.vm";
	
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		List<SchoolSubject> subjects = db.list("select s from SchoolSubject s");
		for ( SchoolSubject subject : subjects ) {
			System.out.println(subject.getCode() + " " + subject.getName());
		}
		
	}

}
