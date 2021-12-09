package educate.sis.examreport.module;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.module.PeriodSessionLookup;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 *
 * @version 1.0
 */
public class CGPABySchoolStatisticModule extends AjaxModule {
	
	private DbPersistence db = new DbPersistence();
	private static String path = "apps/examination_reports/cgpa2/";
	private String vm = "";

	@Override
	public String doAction() throws Exception {
		context.put("nf", new DecimalFormat("0.00"));
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		else if ( "select_periods".equals(command)) selectPeriods();
		else if ( "view_report".equals(command)) viewReport();
		else if ( "students".equals(command)) students();
		else if ( "print_statistic".equals(command)) printStatistic();
		return vm;
	}
	
	private void printStatistic() {
		viewReport();
		context.put("print_mode", true);
	}

	private void students() {
		vm = path + "students.vm";
		String group = request.getParameter("group");
		String periodId = request.getParameter("period_id");
		String facultyId = request.getParameter("faculty_id");
		String intakeId = request.getParameter("intake_id");
		double cgpa1 = 0.0;
		double cgpa2 = 4.0;
		
		if ( "1".equals(group) ) {
			cgpa1 = 3.66; cgpa2 = 4.0;
		}
		else if ( "2".equals(group)) {
			cgpa1 = 2.99; cgpa2 = 3.66;
		}
		else if ( "3".equals(group)) {
			cgpa1 = 1.99; cgpa2 = 2.99;
		}
		else if ( "4".equals(group)) {
			cgpa1 = 0.99; cgpa2 = 1.99;
		}
		else if ( "5".equals(group)) {
			cgpa1 = -1.0; cgpa2 = 0.99;
		}
		else if ( "".equals(group)) {
			//use default
		}
		
		ExamResultStatistic stat = new ExamResultStatistic();
		List<GradeResult> results = stat.listResultByCGPA(intakeId, periodId, facultyId, cgpa1, cgpa2);
		context.put("results", results);
	}
	
	private int getGradeGroup(double grade) {
		if ( grade > 3.66 ) return 1;
		else if ( grade > 2.99 ) return 2;
		else if ( grade > 1.99 ) return 3;
		else if ( grade > 0.99 ) return 4;
		else return 5;
	}
	
	private void viewReport() {
		context.remove("print_mode");
		vm = path + "report.vm";
		String schemeId = request.getParameter("period_scheme_id");
		context.put("period_scheme_id", schemeId);
		String periodId = request.getParameter("period_id");
		context.put("period_id", periodId);
		String intakeId = request.getParameter("intake_id");
		context.put("intake_id", intakeId);
		
		Period period = db.find(Period.class, periodId);
		Session intake = db.find(Session.class, intakeId);
		PeriodSessionLookup lookup = new PeriodSessionLookup(schemeId, intakeId);
		Session session = lookup.getSession(period);
		
		context.put("period", period);
		context.put("intake", intake);
		context.put("session", session);
		
		ExamResultStatistic stat = new ExamResultStatistic();
		
		List<Hashtable> statResults = new ArrayList<Hashtable>();
		List<Faculty> faculties = db.list("select f from Faculty f order by f.name");
		for ( Faculty faculty : faculties ) {
			List<GradeResult> gradeResults = stat.getCGPASummary(intakeId, periodId, faculty.getId());
			Hashtable h = new Hashtable();
			h.put("faculty", faculty);
			h.put("grade_results", gradeResults);
			h.put("total", stat.getTotal());
			statResults.add(h);
		}
		context.put("stats", statResults);
	}

	private List<GradeResult> getCGPASummary(String intakeId, String periodId, String facultyId) {
		String sql = "select r from FinalResult r where r.student.program.course.faculty.id = '" + facultyId + "' " +
				"and r.period.id = '" + periodId + "' " +
				"and r.student.intake.id = '" + intakeId + "' ";
		List<FinalResult> results = db.list(sql);
		
		
		
		Hashtable<Integer, Integer> r = new Hashtable<Integer, Integer>();
		for ( FinalResult result : results ) {
			
			if ( r.get(getGradeGroup(result.getCgpa())) == null ) {
				r.put(getGradeGroup(result.getCgpa()), new Integer(1));
			}
			else {
				int i = r.get(getGradeGroup(result.getCgpa())) + 1;
				r.put(getGradeGroup(result.getCgpa()), i);
			}
		}
		
		List<GradeResult> gradeResults = new ArrayList<GradeResult>();
		
		for ( int group = 1; group < 6; group++ ) {
			int cnt = r.get(group) != null ? r.get(group) : 0;
			gradeResults.add(new GradeResult(group, cnt));
		}
		return gradeResults;
	}

	private void selectPeriods() {
		vm = path + "select_periods.vm";
	
		String periodSchemeId = request.getParameter("period_scheme_id");
		PeriodScheme periodScheme = db.find(PeriodScheme.class, periodSchemeId);
		
		List<Period> periods = null;
		if ( periodScheme != null ) {
			periods = periodScheme.getFunctionalPeriodList();
			context.put("periods", periods);
		}
		else context.remove("periods");
	}

	private void start() throws Exception {
		vm = path + "start.vm";
		context.put("period_schemes", db.list("select p from PeriodScheme p"));
		context.put("subjects", db.list("select s from Subject s order by s.code"));
		context.put("intakes", db.list("select s from Student st JOIN st.intake s group by s.id order by s.startDate"));
//		context.put("faculties", db.list("select f from Faculty f order by f.name"));
//		Session currentSession = StudentRegistrationUtil.getCurrentSession();
//		context.put("current_session", currentSession);
	}



}
