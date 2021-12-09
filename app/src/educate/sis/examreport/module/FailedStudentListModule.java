package educate.sis.examreport.module;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.StudentRegistrationUtil;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.exam.entity.FinalResult;
import educate.sis.module.ResultEntryUtil;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class FailedStudentListModule extends AjaxModule {
	
	private DbPersistence db = new DbPersistence();
	private static String path = "apps/examination_util/failed_students/";
	private String vm = "";

	@Override
	public String doAction() throws Exception {
		context.put("nf", new DecimalFormat("0.00"));
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		else if ( "view_report".equals(command)) viewReport();
		else if ( "list_students".equals(command)) listStudents();
		else if ( "view_transcript".equals(command)) viewTranscript();
		else if ( "print_statistic".equals(command)) printStatistic();
		else if ( "print_students".equals(command)) printStudents();
		return vm;
	}
	
	
	
	private void viewTranscript() throws Exception {
		vm = path + "transcript.vm";
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		showExamTranscript(student);
		
	}
	
	public void showExamTranscript(Student student) throws Exception {
		vm = path + "transcript.vm";
		context.put("student", student);
		
		StudentStatusUtil pu = new StudentStatusUtil();
		StudentStatus studentStatus = pu.getCurrentStudentStatus(student);
		if ( studentStatus == null ) studentStatus = pu.getLastStudentStatus(student);
		Session currentSession = studentStatus.getSession();
	
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		List<FinalResult> results = db.list(sql);
		if ( results.size() > 0 ) {
			
			List<FinalResult> finalResults = new ArrayList<FinalResult>();
			for ( FinalResult r : results ) {
				finalResults.add(r);
				if ( r.getSession().getId().equals(currentSession.getId()) ) break;
			}
			context.put("results", finalResults);
			
		}
		else {
			context.remove("results");
		}
	}



	private void printStudents() throws Exception {
		listStudents();
		context.put("print_mode", true);
	}



	private void printStatistic() {
		viewReport();
		context.put("print_mode", true);
	}

	private void listStudents() throws Exception {
		context.remove("print_mode");
		vm = path + "students.vm";
		String intakeId = request.getParameter("intake_id");
		context.put("intake_id", intakeId);
		Session intake = db.find(Session.class, intakeId);
		context.put("intake", intake);
		String sql = "";
		List<Student> students = new ArrayList<Student>();
		sql = "select distinct s from FinalResult r join r.student s where " +
		"r.student.intake.id = '" + intakeId + "' " +
		"and r.standing = '" + ResultEntryUtil.FAIL + "' " +
		"order by r.student.biodata.name";
		List<Student> results = db.list(sql);
		students.addAll(results);
		
		Session currentSession = StudentRegistrationUtil.getCurrentSession();
		Hashtable d = new Hashtable();
		d.put("session_date", currentSession.getStartDate());
		List<GPAData> gpaResults = new ArrayList<GPAData>();
		for ( Student student : students ) {
			System.out.println(student.getBiodata().getName());
			sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' " +
			"and r.session.startDate <= :session_date order by r.session.startDate desc ";
			List<FinalResult> frList = db.list(sql, d);
			FinalResult currentResult = null, lastResult = null;
			if ( frList.size() > 0 ) {
				currentResult = frList.get(0);
				if ( frList.size() > 1 ) lastResult = frList.get(1);
			}
			gpaResults.add(new GPAData(student, currentResult, lastResult));
		}
		context.put("results", gpaResults);
	}

	private void viewReport() {
		context.remove("print_mode");
		vm = path + "report.vm";
		String intakeId = request.getParameter("intake_id");
		context.put("intake_id", intakeId);
		Session intake = db.find(Session.class, intakeId);
		context.put("intake", intake);
		String sql = "";
		//list of schools
		List<Faculty> faculties = db.list("select f from Faculty f order by f.name");
		List<Hashtable> reports = new ArrayList<Hashtable>();
		for ( Faculty f : faculties ) {
			Hashtable faculty = new Hashtable();
			String facultyId = f.getId();
			faculty.put("faculty", f);
			List<FailedCount> list = new ArrayList<FailedCount>();
			List<Program> programs = db.list("select p from Program p where p.course.faculty.id = '" + facultyId + "'");
			for ( Program p : programs ) {
				sql = "select distinct s from FinalResult r join r.student s where " +
				"r.student.intake.id = '" + intakeId + "' " +
				"and r.standing = '" + ResultEntryUtil.FAIL + "' " +
				"and s.program.id = '" + p.getId() + "'" +
				"order by r.student.biodata.name";
				List<Student> results = db.list(sql);
				int count = results.size();
				list.add(new FailedCount(p.getCode(), count));
			}
			faculty.put("data", list);
			reports.add(faculty);
		}
		context.put("reports", reports);
	}

	private void start() throws Exception {
		vm = path + "start.vm";
		context.put("intakes", db.list("select s from Student st JOIN st.intake s group by s.id order by s.startDate"));
		Session currentSession = StudentRegistrationUtil.getCurrentSession();
		context.put("current_session", currentSession);
	}
	
	public static void main(String[] args) throws Exception {
		String intakeId = "1232073136312";
		String sql = "";
		DbPersistence db = new DbPersistence();
		List<Student> students = new ArrayList<Student>();
		sql = "select distinct s from FinalResult r join r.student s where " +
		"r.student.intake.id = '" + intakeId + "' " +
		"and r.standing = '" + ResultEntryUtil.FAIL + "' " +
		"order by r.student.biodata.name";
		List<Student> results = db.list(sql);
		students.addAll(results);
		
		Session currentSession = StudentRegistrationUtil.getCurrentSession();
		Hashtable d = new Hashtable();
		d.put("session_date", currentSession.getStartDate());
		List<GPAData> gpaResults = new ArrayList<GPAData>();
		for ( Student student : students ) {
			System.out.println(student.getBiodata().getName());
			sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' " +
			"and r.session.startDate <= :session_date order by r.session.startDate desc ";
			List<FinalResult> frList = db.list(sql, d);
			FinalResult currentResult = null, lastResult = null;
			if ( frList.size() > 0 ) {
				currentResult = frList.get(0);
				if ( frList.size() > 1 ) lastResult = frList.get(1);
			}
			gpaResults.add(new GPAData(student, currentResult, lastResult));
		}
		
		for ( GPAData gpaData : gpaResults ) {
			System.out.print(gpaData.getStudent().getBiodata().getName());
			System.out.print(gpaData.getLastResult().getCgpa());
			System.out.println(gpaData.getCurrentResult().getCgpa());
		}
	}

}
