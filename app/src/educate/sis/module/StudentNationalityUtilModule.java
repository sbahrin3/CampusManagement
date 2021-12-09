package educate.sis.module;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.admission.entity.Applicant;
import educate.db.DbPersistence;
import educate.enrollment.StudentRegistrationUtil;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.fusionchart.Chart;
import educate.sis.general.entity.Nationality;
import educate.sis.general.entity.Race;
import lebah.portal.action.AjaxModule;

public class StudentNationalityUtilModule extends AjaxModule {
	
	private final String path = "apps/util/student_util/";
	private String vm = "default.vm";
	DbPersistence db = null;
	HttpSession session;
	
	
	public String doAction() throws Exception {
		db = new DbPersistence();
		try {
			session = request.getSession();
			//date formatter
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			context.put("dateFormat", dateFormat);
			String command = request.getParameter("command");
			vm = path + "search.vm";
			if ( "search_student".equals(command)) doSearchStudent();
			else if ( "get_student".equals(command)) doGetStudent();
			else if ( "reload_db".equals(command)) doReloadDb();
			else if ("malaysian".equals(command)) listMalaysian();
			else if ("non_malaysian".equals(command)) listInternational();
			else if ("undefined_nationality".equals(command)) listNationalityNA();
			else if ( "save_info".equals(command)) saveInfo();
			else if ( "statistic".equals(command)) statistic();
			else if ( "statistic_malaysian".equals(command)) statisticMalaysian();
			else if ( "statistic_international".equals(command)) statisticInternational();
			return vm;
		} finally {
			db.close();
		}
	}

	private void statistic() {
		context.put("report_title", "All Students");
		String sql = "SELECT new educate.enrollment.Result(s.program.code, s.program.name, COUNT(s)) " +
		"FROM Student s WHERE s.fakeStudent is null " +
		"GROUP BY s.program.code";
		statistic(sql);
		
	}

	private void statisticInternational() {
		context.put("report_title", "International Students");
		String sql = "SELECT new educate.enrollment.Result(s.program.code, s.program.name, COUNT(s)) " +
		"FROM Student s WHERE s.fakeStudent is null AND s.applicant.nationalityType ='2'" +
		"GROUP BY s.program.code";
		statistic(sql);
		
	}

	private void statisticMalaysian() {
		context.put("report_title", "Malaysian Students");
		String sql = "SELECT new educate.enrollment.Result(s.program.code, s.program.name, COUNT(s)) " +
		"FROM Student s WHERE s.fakeStudent is null AND s.applicant.nationalityType ='1'" +
		"GROUP BY s.program.code";
		statistic(sql);
		
	}

	private void statistic(String sql) {
		vm = path + "report.vm";
		DbPersistence db = new DbPersistence();
		List<Result> results = db.list(sql);
		context.put("results", results);
		createChartValues(results, "", "");
	}

	private void saveInfo() throws Exception {

		DbPersistence db = new DbPersistence();
		String nationalityId= request.getParameter("nationality");
		Nationality nationality = (Nationality) db.find(Nationality.class, nationalityId);
		
		String raceId = request.getParameter("race");
		Race race = (Race) db.find(Race.class, raceId);
		
		String id = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, id);
		
		db.begin();
		
		student.getBiodata().setNationality(nationality);
		student.getBiodata().setRace(race);
		
		String name = request.getParameter("name");
		student.getBiodata().setName(name);

		String dob = request.getParameter("dob");
		student.getBiodata().setDob(dob);
		
		String passportNo = request.getParameter("passport_no");
		student.getBiodata().setPassport(passportNo);
		
		String passportDateIssue = request.getParameter("passport_date_issue");
		student.getBiodata().setPassportIssueDate(passportDateIssue);
		
		String passportDateExpire = request.getParameter("passport_date_expire");
		student.getBiodata().setPassportExpireDate(passportDateExpire);
		
		String visaNo = request.getParameter("visa_no");
		student.getBiodata().setVisaNumber(visaNo);
		
		String visaDateIssue = request.getParameter("visa_date_issue");
		student.getBiodata().setVisaIssueDate(visaDateIssue);
		
		String visaDateExpire = request.getParameter("visa_date_expire");
		student.getBiodata().setVisaExpireDate(visaDateExpire);
		
		String telephone = request.getParameter("telephone");
		student.getBiodata().setTelephoneNo(telephone);
		
		String mobile = request.getParameter("mobile");
		student.getBiodata().setMobileNo(mobile);
		
		String email = request.getParameter("email");
		student.getBiodata().setEmail(email);
		
		String email2 = request.getParameter("email2");
		student.getBiodata().setEmail2(email2);
		
		db.commit();
	
		vm = path + "student.vm";
		String backCommand = request.getParameter("back_command");
		context.put("back_command", backCommand);
		String studentId = request.getParameter("student_id");
		Applicant applicant = student.getApplicant();
		applicant.getNationalityType();
		applicant.getBiodata().getNationality();
		context.put("student", student);
		context.put("applicant", applicant);
		
		context.put("nationalities", db.list("SELECT a from Nationality a order by a.name"));
		context.put("races", db.list("SELECT a from Race a"));
		
		getStudentStatus(student);

		
	}

	private void listNationalityNA() {
		vm = path + "search_result.vm";
		//String sql = "SELECT s FROM Student s WHERE s.biodata.nationality is null AND s.fakeStudent is null ORDER BY s.intake.startDate, s.biodata.name";
		String sql = "SELECT s FROM Student s WHERE s.applicant.nationalityType is null AND s.fakeStudent is null ORDER BY s.intake.startDate, s.biodata.name";
		List<Student> students = db.list(sql);
		context.put("students", students);
		context.put("back_command", "undefined_nationality");
	}

	private void listInternational() {
		vm = path + "search_result.vm";
		//String sql = "SELECT s FROM Student s WHERE s.biodata.nationality.name <> 'MALAYSIA' AND s.biodata.nationality is not null AND s.fakeStudent is null ORDER BY s.intake.startDate, s.biodata.name";
		String sql = "SELECT s FROM Student s WHERE s.applicant.nationalityType ='2' AND s.fakeStudent is null ORDER BY s.intake.startDate, s.biodata.name";
		List<Student> students = db.list(sql);
		context.put("students", students);
		context.put("back_command", "non_malaysian");
	}

	private void listMalaysian() {
		vm = path + "search_result.vm";
		//String sql = "SELECT s FROM Student s WHERE s.biodata.nationality.name = 'MALAYSIA' AND s.fakeStudent is null ORDER BY s.intake.startDate, s.biodata.name";
		String sql = "SELECT s FROM Student s WHERE s.applicant.nationalityType ='1' AND s.fakeStudent is null ORDER BY s.intake.startDate, s.biodata.name";
		List<Student> students = db.list(sql);
		context.put("students", students);
		context.put("back_command", "malaysian");
	}

	private void doReloadDb() {
		db.reload();
		
	}

	private void doGetStudent() {
		vm = path + "student.vm";
		String backCommand = request.getParameter("back_command");
		context.put("back_command", backCommand);
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		Applicant applicant = student.getApplicant();
		context.put("student", student);
		context.put("applicant", applicant);
		
		DbPersistence db = new DbPersistence();
		context.put("nationalities", db.list("SELECT a from Nationality a order by a.name"));
		context.put("races", db.list("SELECT a from Race a"));
		
		getStudentStatus(student);
	}

	private void getStudentStatus(Student student) {
		context.remove("student_status");
		try {
			StudentStatus studentStatus = StudentRegistrationUtil.getCurrentStudentStatus(student);
			context.put("student_status", studentStatus);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void doSearchStudent() throws Exception {
		vm = path + "search_result.vm";
		String search = request.getParameter("search_name");
		String sql = "SELECT s FROM Student s WHERE s.fakeStudent is null ";
		sql += !search.equals("") ? "AND s.biodata.name LIKE '%" + search + "%' " + " ORDER BY s.intake.startDate, s.biodata.name" : "";
		List<Student> students = db.list(sql);
		context.put("students", students);
		context.put("search", search);
		context.put("back_command", "search_student");
		
	}
	
	private void createChartValues(List<Result> results, String strUrl, String keyLabel) {
		//Create fusion chart object, false = use free version, true = use enterprise version
		Chart chart = new Chart(true);
		context.put("chart", chart);
		try {
			chart.setYAxisName(URLEncoder.encode("Number Of Students" , "UTF-8"));
		} catch (UnsupportedEncodingException e1) {	}
		for ( Result result : results ) {
			String link = strUrl;
			try {
				if ( !"".equals(keyLabel)) link += "&" + keyLabel + "=" + result.getKey();
				else link = "";
				
				chart.add(result.getKey(), result.getCounter(), !"".equals(link) ? URLEncoder.encode(link , "UTF-8") : "");
			} catch (UnsupportedEncodingException e) {
				
			}
		}

	}
	
	public static void main(String[] args) {
		DbPersistence db = new DbPersistence();
		String sql = "SELECT new educate.enrollment.Result(s.program.code, s.program.name, COUNT(s)) FROM Student s WHERE s.fakeStudent is null " +
				"GROUP BY s.program.code";
		List<Result> results = db.list(sql);
		for ( Result result : results ) {
			System.out.println(result.getKey() + " " + result.getLabel() + " = " + result.getCounter());
		}
		

	}

}
