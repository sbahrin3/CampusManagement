package educate.enrollment.report;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.admission.entity.Applicant;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import lebah.portal.action.AjaxModule;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 * 
 * This module is a direct conversion from Groovy at 
 * apps/enrollment_reports/education_and_employment_report/
 */
public class EducationEmploymentReportModule extends AjaxModule {
	
	private final String path = "apps/enrollment_reports/education_and_employment_report/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		context.put("dateFormat", dateFormat);
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) doReport("student");
		else if ( "student".equals(command)) doReport("student");
		else if ( "offer".equals(command)) doReport("offer");
		
		return vm;
	}

	private void doReport(String type) {
		if ( "student".equals(type)) vm = path + "index.vm";
		else vm = path + "offer.vm";
		String sql = "";
		if ( "student".equals(type) ) {
			sql = "select s from Student s where (s.fakeStudent is null OR s.fakeStudent = '') order by s.biodata.name";
			List<Student> list = db.list(sql);
			context.put("slist", list);
			list = null;
			
		}
		else {
			sql = "select s from Applicant s where s.status='OFFERED' order by s.biodata.name";
			List<Applicant> list = db.list(sql);
			context.put("slist", list);
			list = null;
		}

	}
	

}
