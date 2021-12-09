package educate.enrollment.report;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.AgeCalc;
import educate.enrollment.Result;
import educate.enrollment.StudentRegistrationUtil;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.fusionchart.Chart;
import educate.sis.finance.entity.SponsorStudent;
import educate.sis.general.entity.Disability;
import educate.sis.general.entity.EducationLevel;
import educate.sis.general.entity.Gender;
import educate.sis.general.entity.Partner;
import educate.sis.general.entity.Race;
import educate.sis.general.entity.Sponsor;
import educate.sis.general.entity.State;
import educate.sis.module.StudentInfoModule;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 */
public class StudentStatisticModule extends AjaxModule {
	
	protected final String path = "apps/util/student_stat/";
	protected String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	String divChartDataName = "div_chart_data";
	
	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		db = new DbPersistence();
		try {
			session = request.getSession();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			context.put("dateFormat", dateFormat);
			String command = request.getParameter("command");
			vm = path + "search.vm";
			
			System.out.println(command);
			
			Date reportDate = new Date();
			context.put("reportDate", reportDate);
			
			if ( command == null || "".equals(command)) command = "filter_students";
			if ( "statistic_chart".equals(command)) doStatisticChart();
			else if ( "statistic_table".equals(command)) doStatisticTable();
			else if ( "filter_students".equals(command)) filterParameters();
			else if ( "list_filtered_students".equals(command)) filterStudents();
			else if ( "student_info".equals(command)) studentInfo();
			else if ( "byProgram".equals(command)) listStudentsByProgram();
			else if ("byIntake".equals(command)) listStudentsByIntake();
			else if ("byDisability".equals(command)) listStudentsByDisability();
			else if ("byRace".equals(command)) listStudentsByRace();
			else if ("byGender".equals(command)) listStudentsByGender();
			else if ("byCentre".equals(command)) listStudentsByCentre();
			else if ("byPartner".equals(command)) listStudentsByPartner();
			else if ("byEducation".equals(command)) listStudentsByEducation();
			else if ("bySponsor".equals(command)) listStudentsBySponsor();
			else if ("byAgeGroup".equals(command)) listStudentsByAgeGroup();
			
			else if ( "listSessions".equals(command)) listSessions();
			
			else if ("get_student_by_id".equals(command)) return new StudentInfoModule(request, context).doAction();
			
		
			return vm;
		} finally {
			db.close();
		}
	}
	
	/**
	 * 
	 */
	private void listSessions() {
		String programId = getParam("program_id");
		if ( !"".equals(programId)) {
			Program program = db.find(Program.class, programId);
			List<Session> sessions = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
			context.put("sessions", sessions);
		} else {
			context.remove("sessions");
		}
		vm = path + "list_sessions.vm"; 
	}

	private Date parseDate(String dateTxt) {
		if ( dateTxt != null && !"".equals(dateTxt)) {
			try {
				return new SimpleDateFormat("dd-MM-yyyy").parse(dateTxt);
			} catch (ParseException e) {
				return null;
			}
		}
		return null;
	}

	protected void studentInfo() throws Exception {
		vm = path + "profile.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		Session currentSession = StudentRegistrationUtil.getCurrentSession(student.getProgram().getLevel().getPathNo());
		StudentStatus currentStatus = student.getStatus(currentSession.getId());
		context.put("currentStatus", currentStatus);
		context.put("student", student);
		

		statisticParameters();
		
	}

	protected void statisticParameters() {
		String programId = request.getParameter("program_id");
		context.put("program_id", programId);
		String intakeId = request.getParameter("intake_id");
		context.put("intake_id", intakeId);
		String educationId = request.getParameter("education_id");
		context.put("education_id", educationId);
		String raceId = request.getParameter("race_id");
		context.put("race_id", raceId);
		String disabilityId = request.getParameter("disability_id");
		context.put("disability_id", disabilityId);
		String stateId = request.getParameter("state_id");
		context.put("state_id", stateId);
		String countryId = request.getParameter("country_id");
		context.put("country_id", countryId);
		String centreId = request.getParameter("centre_id");
		context.put("centre_id", centreId);
		String partnerId = request.getParameter("partner_id");
		context.put("partner_id", partnerId);
		String genderId = request.getParameter("gender_id");
		context.put("gender_id", genderId);
		String sponsorId = request.getParameter("sponsor_id");
		context.put("sponsor_id", sponsorId);
		String nationalityId = request.getParameter("nationality_id");
		context.put("nationality_id", nationalityId);
		String surveyId = request.getParameter("survey_id");
		context.put("survey_id", surveyId);
		String openId = request.getParameter("open_id");
		context.put("open_id", openId);
	}

	protected String _sql(String sql, String s) {
		return " AND " + s;
	}

	protected void filterStudents() {
		
		vm = path + "filtered_students.vm";
		
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		String raceId = request.getParameter("race_id");
		String stateId = request.getParameter("state_id");
		String centreId = request.getParameter("centre_id");
		String genderId = request.getParameter("gender_id");
		String nationalityId = request.getParameter("nationality_id");
		
		String statusId = request.getParameter("status_id");
		
		String statusTag = request.getParameter("status_tag");
		
		String[] sponsorCheck = request.getParameterValues("sponsorCheck");
		boolean filterSponsor = false;
		if ( sponsorCheck != null ) {
			filterSponsor = "YES".equals(sponsorCheck[0]);
		}
		context.put("filterSponsor", filterSponsor);
		
		String _reportDate = request.getParameter("reportDate");
		Date reportDate = parseDate(_reportDate);
		if ( reportDate == null ) reportDate = new Date();
		context.put("queryDate", reportDate);

		Hashtable param = new Hashtable();
		param.put("reportDate", reportDate);
		
		String sql = "";
		
		if ( !filterSponsor ) {
			sql = "select s ";
			sql += " from StudentStatus s JOIN s.session ss ";
		} else {
			sql = "select s, sp ";
			sql += " from SponsorStudent sp JOIN sp.student st JOIN st.status s JOIN s.session ss ";
		}
		
		sql += "WHERE (ss.startDate <= :reportDate and ss.endDate >= :reportDate) " +
		//"and s.type.inActive = 0 " +
		"and (s.student.fakeStudent is null OR s.student.fakeStudent = '') ";
		
	
		context.put("program_id", programId);
		
		if ( !"".equals(programId)) {
			param.put("program_id", programId);
			sql += _sql(sql, "s.student.program.id = :program_id");
			Program program = db.find(Program.class, programId);
			context.put("program", program);
		} else
			context.remove("program");
		
		context.put("intake_id", intakeId);
		if ( !"".equals(intakeId)) {
			param.put("intake_id", intakeId);
			sql += _sql(sql, "s.student.intake.id = :intake_id");
			Session intake = db.find(Session.class, intakeId);
			context.put("intake", intake);
		} else
			context.remove("intake");
		
		context.put("race_id", raceId);
		if ( !"".equals(raceId)) {
			param.put("race_id", raceId);
			sql += !"".equals(raceId) ? _sql(sql, "s.student.applicant.biodata.race.id = :race_id") : "";
			Race race = db.find(Race.class, raceId);
			context.put("race", race);
		} else
			context.remove("race");
		
		
		context.put("status_tag", statusTag);
		if ( !"".equals(statusTag)) {
			param.put("statusTag", statusTag);
			sql += !"all".equals(statusTag) ? _sql(sql, "s.student.statusTag = :statusTag") : "";
			context.put("statusTag", statusTag);
		} else
			context.remove("statusTag");
		
		
		context.put("state_id", stateId);
		if ( !"".equals(stateId)) {
			param.put("state_id", stateId);
			sql += _sql(sql, "s.student.address.state.id = :state_id");
			State state = db.find(State.class, stateId);
			context.put("state", state);
		} else
			context.remove("state");
		
		context.put("centre_id", centreId);
		if ( !"".equals(centreId)) {
			param.put("centre_id", centreId);
			sql += _sql(sql, "s.student.learningCenter.id = :centre_id");
			LearningCentre centre = db.find(LearningCentre.class, centreId);
			context.put("centre", centre);
		} else
			context.remove("centre");
		
		context.put("gender_id", genderId);
		if ( !"".equals(genderId)) {
			param.put("gender_id", genderId);
			sql += _sql(sql, "s.student.biodata.gender.id = :gender_id");
			Gender gender = db.find(Gender.class,genderId);
			context.put("gender", gender);
		} else
			context.remove("gender");
		
		context.put("nationality_id", nationalityId);
		if ( !"".equals(nationalityId)) {
			param.put("nationality_id", nationalityId);
			sql += _sql(sql, "s.student.applicant.nationalityType = :nationality_id");
			if ( "1".equals(nationalityId)) context.put("nationality", "local");
			else if ( "2".equals(nationalityId)) context.put("nationality", "international");
		} else
			context.remove("nationality");
		
		if ( !"".equals(statusId)) {
			param.put("status_id", statusId);
			sql += _sql(sql, "s.type.id = :status_id");
			StatusType statusType = db.find(StatusType.class, statusId);
			context.put("statusType", statusType);
		} else
			context.remove("statusType");
		
		sql += " order by s.student.biodata.name";
		
		//System.out.println(sql);
		

		if ( !filterSponsor) {
			List<StudentStatus> students = db.list(sql, param);
			context.put("students", students);
			context.put("param", param);
			session.setAttribute("students", students);
		} else {
			List<educate.enrollment.report.Data> students = new ArrayList<educate.enrollment.report.Data>();
			List<Object[]> list = db.list(sql, param);
			for ( Object[] o : list ) {
				
				StudentStatus st = (StudentStatus) o[0];
				SponsorStudent sp = (SponsorStudent) o[1];
				//System.out.println(sp.getSponsor().getName() + ", " + st.getStudent().getMatricNo() + ", " + st.getSession().getName());
				students.add(new educate.enrollment.report.Data(st, sp));
			}

			context.put("students", students);
			context.put("param", param);
			session.setAttribute("students", students);
		}

		
		//
		
		//display fields
		String[] fieldNames = request.getParameterValues("field_names");
		context.put("fieldNames", fieldNames);
		session.setAttribute("fieldNames", fieldNames);
		context.remove("fields");
		if ( fieldNames != null ) {
			Hashtable h = new Hashtable();
			context.put("fields", h);
			for ( String fieldName : fieldNames ) {
				h.put(fieldName, true);
			}
		}
		
	}

	protected void filterParameters() {
		vm = path + "filter_students.vm";
		request.getSession().setAttribute("attributes", null);
		getFilterParameters();

	}

	protected void getFilterParameters() {
		
		context.put("programs", db.list("select distinct x from Student st JOIN st.program x order by x.code"));
		context.put("centres", db.list("select x from LearningCentre x order by x.name"));
		context.put("countries", db.list("select x from Country x order by x.name"));
		context.put("genders", db.list("select x from Gender x order by x.name"));
		context.put("races", db.list("select x from Race x order by x.name"));
		context.put("states", db.list("select x from State x order by x.name"));
		context.put("sponsors", db.list("select x from Sponsor x order by x.name"));
		context.put("statuses", db.list("select x from StatusType x order by x.sequence"));
	}

	protected void listStudentsByAgeGroup() {
		vm = path + "students_age_groups.vm";
		String ageId = request.getParameter("id");
		List<Student> students = null;
		
		context.put("ageCalc", new AgeCalc());
		
		if ( "20-30".equals(ageId)) {
			students = listStudentsByAgeGroup(20, 30);
			context.put("title", "20 to 30");
		}
		else if ( "31-40".equals(ageId)) {
			students = listStudentsByAgeGroup(31, 40);
			context.put("title", "31 to 40");
		}
		else if ( "41-54".equals(ageId)) {
			students = listStudentsByAgeGroup(41, 54);
			context.put("title", "41 to 54");
		}
		else if ( "55-100".equals(ageId)) {
			students = listStudentsByAgeGroup(55, 100);
			context.put("title", "55 to 100");
		}
		context.put("students", students);
	}

	protected void listStudentsBySponsor() {
		sql("s.applicant.sponsor.id");
		Sponsor sponsor = (Sponsor) db.find(Sponsor.class, request.getParameter("id"));
		context.put("title", sponsor.getName());
	}

	protected void listStudentsByEducation() {
		sql("s.applicant.highestEducation.id");
		EducationLevel edu = (EducationLevel) db.find(EducationLevel.class, request.getParameter("id"));
		context.put("title", edu.getName());
	}

	protected void listStudentsByPartner() {
		sql("s.partner.id");
		Partner partner = (Partner) db.find(Partner.class, request.getParameter("id"));
		context.put("title", partner.getName());
	}

	protected void listStudentsByCentre() {
		sql("s.learningCenter.id");
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, request.getParameter("id"));
		context.put("title", centre.getName());
	}

	protected void listStudentsByGender() {
		sql("s.biodata.gender.id");
		Gender gender = (Gender) db.find(Gender.class, request.getParameter("id"));
		context.put("title", gender.getName());
	}

	protected void listStudentsByRace() {
		sql("s.biodata.race.id");
		Race race = (Race) db.find(Race.class, request.getParameter("id"));
		context.put("title", race.getName());
	}

	protected void listStudentsByDisability() {
		sql("s.applicant.disability.id");
		Disability disability = (Disability) db.find(Disability.class, request.getParameter("id"));
		context.put("title", disability.getName());
	}

	protected void listStudentsByIntake() {
		sql("s.intake.id");
		Session intake = (Session) db.find(Session.class, request.getParameter("id"));
		context.put("title", intake.getName());
		
	}

	protected void listStudentsByProgram() {
		sql("s.program.id");
		Program program = (Program) db.find(Program.class, request.getParameter("id"));
		context.put("title", program.getName() + "(" + program.getCode() + ")");
	}
	
	protected void sql(String filter) {
		vm = path + "students.vm";
		String sql = "SELECT s FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" AND " + filter + " = '" + request.getParameter("id") + "' " + " ORDER BY s.biodata.name";
		List<Student> students = db.list(sql);
		context.put("students", students);
	}

	protected void doStatisticChart() {
		vm = path + "report.vm";
		statistic();
	}
	protected void doStatisticTable() {
		vm = path + "report_table.vm";
		statistic();
	}

	protected void statistic() {
		this.statByCountry();
		this.statByDisability();
		this.statByGender();
		this.statByHighestEducation();
		this.statByIntake();
		this.statByLearningCentre();
		this.statByNationalityType();
		this.statByPartner();
		this.statByProgram();
		this.statByRace();
		this.statBySponsor();
		this.statByState();
		this.statBySurvey();
		this.statByAgeGroup();
	}
	

	
	protected List<Result> statistic(Chart chart, String sql, String commandCall) {
		DbPersistence db = new DbPersistence();
		List<Result> results = db.list(sql);
		
		createChartValues(chart, results, commandCall);
		return results;
	}

	protected void createChartValues(Chart chart, List<Result> results, String commandCall) {
		for ( Result result : results ) {
			System.out.println(result.getLabel() + ", " + result.getKey() + ", " + result.getCounter());
			try {
				//String link = "javascript:$(&quot;" + divChartDataName + "&quot;).style.visibility='visible';doDivAjaxCall" + formName + "(&quot;" + divChartDataName + "&quot;, &quot;" + commandCall + "&quot;, &quot;id=" + result.getKey() + "&quot)";
				String link = "javascript:doPositionDivAjaxCall" + formName + "(&quot;" + divChartDataName + "&quot;, &quot;" + commandCall + "&quot;, &quot;id=" + result.getKey() + "&quot)";
				if ( result.getLabel() != null ) {
					chart.add(URLEncoder.encode(result.getLabel(), "UTF-8"), result.getCounter(), link);
				}
				else {
					chart.add(URLEncoder.encode("no label", "UTF-8"), result.getCounter(), link);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

	}

	protected void statByProgram() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.program.id, s.program.code, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') GROUP BY s.program.code";
		List<Result> results = statistic(chart, sql, "byProgram");
		context.put("program_results", results);
		context.put("program_chart", chart);
	}
	
	protected void statByIntake() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.intake.name, s.intake.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.intake.name ORDER BY s.intake.startDate";
		List<Result> results = statistic(chart, sql, "byIntake");
		context.put("intake_results", results);
		context.put("intake_chart", chart);
	}
	
	protected void statByLearningCentre() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.learningCenter.id, s.learningCenter.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.learningCenter.code ";
		List<Result> results = statistic(chart, sql, "byCentre");
		context.put("centre_results", results);
		context.put("centre_chart", chart);
	}
	
	protected void statByState() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.address.state.id, s.address.state.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.address.state.code";
		List<Result> results = statistic(chart, sql, "byState");
		context.put("state_results", results);
		context.put("state_chart", chart);
	}
	
	protected void statByCountry() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.address.country.id, s.address.country.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.address.country.code";
		List<Result> results = statistic(chart, sql, "byCountry");
		context.put("country_results", results);
		context.put("country_chart", chart);
	}
	
	protected void statByPartner() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.partner.id, s.partner.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.partner.code";
		List<Result> results = statistic(chart, sql, "byPartner");
		context.put("partner_results", results);
		context.put("partner_chart", chart);
	}
	
	protected void statBySponsor() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.applicant.sponsor.id, s.applicant.sponsor.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.applicant.sponsor.code";
		List<Result> results = statistic(chart, sql, "bySponsor");
		context.put("sponsor_results", results);
		context.put("sponsor_chart", chart);
	}
	
	protected void statByHighestEducation() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.applicant.highestEducation.id, s.applicant.highestEducation.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.applicant.highestEducation.name";
		List<Result> results = statistic(chart, sql, "byEducation");
		context.put("edu_results", results);
		context.put("edu_chart", chart);
	}
	
	protected void statByRace() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.biodata.race.id, s.biodata.race.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.biodata.race.code";
		List<Result> results = statistic(chart, sql, "byRace");
		context.put("race_results", results);
		context.put("race_chart", chart);
	}
	
	protected void statByGender() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.biodata.gender.id, s.biodata.gender.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.biodata.gender.code";
		List<Result> results = statistic(chart, sql, "byGender");
		context.put("sex_results", results);
		context.put("sex_chart", chart);
	}
	
	protected void statByDisability() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.applicant.disability.id, s.applicant.disability.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.applicant.disability.code";
		List<Result> results = statistic(chart, sql, "byDisability");
		context.put("dis_results", results);
		context.put("dis_chart", chart);
	}
	
	protected void statByNationalityType() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.applicant.nationalityType, s.applicant.nationalityType, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.applicant.nationalityType";
		List<Result> results = statistic(chart, sql, "byNationalityType");
		context.put("natype_results", results);
		context.put("natype_chart", chart);
	}
	
	protected void statBySurvey() {
		Chart chart = new Chart(true);
		String sql = "SELECT new educate.enrollment.Result(s.applicant.survey.id, s.applicant.survey.name, COUNT(s)) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" GROUP BY s.applicant.survey.code";
		List<Result> results = statistic(chart, sql, "bySurvey");
		context.put("survey_results", results);
		context.put("survey_chart", chart);
	}
	
	protected void statByAgeGroup() {
		DbPersistence db = new DbPersistence();
		long age20_30 = ageCounter(db, 20, 30);
		long age31_40 = ageCounter(db, 31, 40);
		long age41_54 = ageCounter(db, 41, 54);
		long age55_100 = ageCounter(db, 55, 100);

		List<Result> results = new ArrayList<Result>();
		results.add(new Result("20-30", "20 - 30", age20_30));
		results.add(new Result("31-40", "31 - 40", age31_40));
		results.add(new Result("41-54", "41 - 54", age41_54));
		results.add(new Result("55-100", "55 - 100", age55_100));

		Chart chart = new Chart();
		createChartValues(chart, results, "byAgeGroup");
		
		context.put("age_results", results);
		context.put("age_chart", chart);
	}

	protected long ageCounter(DbPersistence db, int age1, int age2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.YEAR, -age1);
		Date date1 = cal1.getTime();
		Hashtable h = new Hashtable();
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.YEAR, -age2);
		Date date2 = cal2.getTime();
		h.put("date1", date1);
		h.put("date2", date2);
		String sql = "SELECT COUNT(s) " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" AND s.biodata.dob between :date2 and :date1";
		List<Long> list = db.list(sql, h);
		return list.get(0);
	}

	protected List<Student> listStudentsByAgeGroup(int age1, int age2) {
		//DbPersistence db = new DbPersistence();
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.YEAR, -age1);
		Date date1 = cal1.getTime();
		Hashtable h = new Hashtable();
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.YEAR, -age2);
		Date date2 = cal2.getTime();
		h.put("date1", date1);
		h.put("date2", date2);
		String sql = "SELECT s " +
		" FROM Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
		" AND s.biodata.dob between :date2 and :date1 ORDER BY s.biodata.name";
		return db.list(sql, h);

	}
	
	
	public static void main(String[] args) {
		Hashtable h = new Hashtable();
		h.put("reportDate", new Date());
		
		DbPersistence db = new DbPersistence();
		String programId = "1401960689912";
		String intakeId= "1420699929591"; //1401960688646
		Program program = db.find(Program.class, programId);
		Session session = db.find(Session.class, intakeId);
		System.out.println(program.getName());
		System.out.println(session.getName() + " " + session.getPathNo());
		//String sql = "select s  from StudentStatus s JOIN s.session ss WHERE (ss.startDate <= :reportDate and ss.endDate >= :reportDate)  AND s.student.program.id = '" + programId + "' AND s.student.intake.id = '" + intakeId + "'";
		String sql = "select s  from StudentStatus s JOIN s.session ss WHERE s.student.program.id = '" + programId + "' AND s.student.intake.id = '" + intakeId + "'";
		
		List<Student> list = db.list(sql);
		System.out.println(list.size());

	}


}
