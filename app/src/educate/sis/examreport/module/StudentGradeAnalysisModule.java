package educate.sis.examreport.module;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.module.PeriodSessionLookup;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.AjaxModule;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 *
 * @version 1.0
 */
public class StudentGradeAnalysisModule extends AjaxModule {
	
	private DbPersistence db = new DbPersistence();
	private static String path = "apps/examination_reports/grade/";
	private String vm = "";

	@Override
	public String doAction() throws Exception {
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) start();
		else if ( "select_periods".equals(command)) selectPeriods();
		else if ( "view_report".equals(command)) viewReport();
		else if ( "students".equals(command)) students();
		else if ( "print_statistic".equals(command)) printStatistic();
		else if ( "print_students".equals(command)) printStudents();
		return vm;
	}
	
	private void printStudents() {
		students();
		context.put("print_mode", true);
		
	}

	private void printStatistic() {
		viewReport();
		context.put("print_mode", true);
		
	}

	private void students() {
		context.remove("print_mode");
		vm = path + "students.vm";
		String grade = request.getParameter("grade");
		String periodId = request.getParameter("period_id");
		String subjectId = request.getParameter("subject_id");
		String intakeId = request.getParameter("intake_id");
		String schemeId = request.getParameter("period_scheme_id");
		
		
		context.put("grade", grade);
		context.put("period_id", periodId);
		context.put("subject_id", subjectId);
		context.put("intake_id", intakeId);
		context.put("period_scheme_id", schemeId);
		
		Period period = db.find(Period.class, periodId);
		Session intake = db.find(Session.class, intakeId);
		PeriodSessionLookup lookup = new PeriodSessionLookup(schemeId, intakeId);
		Session session = lookup.getSession(period);
		Subject subject = db.find(Subject.class, subjectId);
		
		context.put("period", period);
		context.put("intake", intake);
		context.put("session", session);
		context.put("subject", subject);
		
		ExamResultStatistic stat = new ExamResultStatistic();
		List<GradeResult> results = stat.listResultByGrade(intakeId, periodId, subjectId, grade);
		context.put("results", results);
	}

	private void viewReport() {
		context.remove("print_mode");
		vm = path + "report.vm";
		String schemeId = request.getParameter("period_scheme_id");
		String periodId = request.getParameter("period_id");
		String subjectId = request.getParameter("subject_id");
		String intakeId = request.getParameter("intake_id");
		
		context.put("period_scheme_id", schemeId);
		context.put("period_id", periodId);
		context.put("subject_id", subjectId);
		context.put("intake_id", intakeId);
		
		Period period = db.find(Period.class, periodId);
		Session intake = db.find(Session.class, intakeId);
		PeriodSessionLookup lookup = new PeriodSessionLookup(schemeId, intakeId);
		Session session = lookup.getSession(period);
		Subject subject = db.find(Subject.class, subjectId);
		
		context.put("period", period);
		context.put("intake", intake);
		context.put("session", session);
		context.put("subject", subject);
		
		ExamResultStatistic stat = new ExamResultStatistic();
		List<GradeResult> gradeResults = stat.getGradeSummary(intakeId, periodId, subjectId);
		context.put("grade_results", gradeResults);
		context.put("total", stat.getTotal());
		
	}

	private List<GradeResult> getGradeSummary(String intakeId, String periodId, String subjectId) {
		String sql = "select r from FinalSubjectResult r where r.subject.id = '" + subjectId + "' " +
				"and r.parent.period.id = '" + periodId + "' " +
				"and r.parent.student.intake.id = '" + intakeId + "' ";
		List<FinalSubjectResult> results = db.list(sql);
		
		Hashtable<String, Integer> r = new Hashtable<String, Integer>();
		for ( FinalSubjectResult result : results ) {
			if ( r.get(result.getGrade()) == null ) {
				r.put(result.getGrade(), new Integer(1));
			}
			else {
				int i = r.get(result.getGrade()) + 1;
				r.put(result.getGrade(), i);
			}
		}
		
		List<GradeResult> gradeResults = new ArrayList<GradeResult>();
		List<Grade> grades = db.list("select g from Grade g order by g.point desc");  //NOTE: multiple MarkingGrades available
		for ( Grade grade : grades ) {
			int cnt = r.get(grade.getLetter()) != null ? r.get(grade.getLetter()) : 0;
			boolean found = false;
			for ( GradeResult gr : gradeResults ) {
				if ( grade.getLetter().equals(gr.grade)) {
					found = true; break;
				}
			}
			if ( !found ) {
				gradeResults.add(new GradeResult(grade.getLetter(), cnt));
			}
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
//		Session currentSession = StudentRegistrationUtil.getCurrentSession();
//		context.put("current_session", currentSession);
	}
	
	public static void main(String[] args) throws Exception {

	}

}
