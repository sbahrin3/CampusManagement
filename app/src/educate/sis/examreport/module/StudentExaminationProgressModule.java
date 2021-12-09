package educate.sis.examreport.module;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.StudentRegistrationUtil;
import educate.enrollment.entity.Student;
import educate.sis.exam.entity.FinalResult;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class StudentExaminationProgressModule extends AjaxModule {
	
	private DbPersistence db = new DbPersistence();
	private static String path = "apps/examination_reports/student_progress/";
	private String vm = "";

	@Override
	public String doAction() throws Exception {
		context.put("nf", new DecimalFormat("0.00"));
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		else if ( "view_report".equals(command)) viewReport();
		else if ( "print_report".equals(command)) printReport();
		return vm;
	}

	private void printReport() throws Exception {
		viewReport();
		context.put("print_mode", true);
	}

	private void viewReport() throws Exception {
		vm = path + "view_report.vm";
		context.remove("print_mode");
		String programId = request.getParameter("program_id");
		context.put("program_id", programId);
		String intakeId = request.getParameter("intake_id");
		context.put("intake_id", intakeId);
		
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		String sql = "";
		List<Student> students = new ArrayList<Student>();
		sql = "select distinct s from FinalResult r join r.student s where " +
		"r.student.intake.id = '" + intakeId + "' " +
		"and r.student.program.id = '" + programId + "' " +
		"order by r.student.biodata.name";
		List<Student> results = db.list(sql);
		students.addAll(results);
		
		Session currentSession = StudentRegistrationUtil.getCurrentSession();
		Hashtable d = new Hashtable();
		d.put("session_date", currentSession.getStartDate());
		List<GPAData> gpaResults = new ArrayList<GPAData>();
		for ( Student student : students ) {
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

	private void start() throws Exception {
		vm = path + "start.vm";
		context.put("intakes", db.list("select s from Student st JOIN st.intake s group by s.id order by s.startDate"));
		context.put("programs", db.list("select p from Program p order by p.name"));
		Session currentSession = StudentRegistrationUtil.getCurrentSession();
		context.put("current_session", currentSession);
	}

}
