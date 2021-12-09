package educate.sis.examreport.module;

import java.util.ArrayList;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.StudentRegistrationUtil;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class GradeAnalysisModule extends AjaxModule {

	private DbPersistence db = new DbPersistence();
	private static String path = "apps/examination_reports/analysis/";
	private String vm = "";

	@Override
	public String doAction() throws Exception {
		String command = request.getParameter("command");
		if (command == null || "".equals(command)) start();
		else if ("select_periods".equals(command)) selectPeriods();
		else if ("view_report".equals(command)) viewReport();
		return vm;
	}

	private void viewReport() {
		vm = path + "report.vm";

		String periodId = request.getParameter("period_id");
		String subjectId = request.getParameter("subject_id");
		String intakeId = request.getParameter("intake_id");
		String periodSchemeId = request.getParameter("period_scheme_id");

		System.out.println("periodId " + periodId);
		System.out.println("subjectId " + subjectId);
		System.out.println("intakeId " + intakeId);

		String sql = "select fr from FinalResult fr where " + "fr.period.id='"
				+ periodId + "' " + "and fr.student.intake.id = '" + intakeId
				+ "'";

		List<FinalResult> finalresults = db.list(sql);
		List<GradeAnalysis> results = new ArrayList<GradeAnalysis>();		
		context.remove("results");
		for(FinalResult fr: finalresults){
			GradeAnalysis ga = new GradeAnalysis();
			ga.setName(fr.getStudent().getBiodata().getName());
			ga.setNoMatric(fr.getStudent().getMatricNo());
			ga.setFinalresult(fr);
			ga.setPngkBefore(getSession(fr.getSession()));
			ga.setPngk(fr.getCgpa());
			ga.setPngs(fr.getGpa());
			ga.setStatus(fr.getStanding());
			results.add(ga);
		}		
		
		context.put("results", results);

		sql = "select sr from FinalResult sr where sr.period.id='" + periodId + "'";
		Session sessions = ((FinalResult) db.get(sql)).getSession();
		context.remove("session");
		context.put("session", sessions);

		sql = "select p from Program p where p.periodScheme.id='" + periodSchemeId + "'";
		Program program = ((Program) db.get(sql));
		context.remove("program");
		context.put("program", program);
	}

	private void selectPeriods() {
		vm = path + "select_periods.vm";

		String periodSchemeId = request.getParameter("period_scheme_id");
		PeriodScheme periodScheme = db.find(PeriodScheme.class,
				periodSchemeId);
		System.out.println("periodSchemeId " + periodSchemeId);
		List<Period> periods = null;
		if (periodScheme != null) {
			periods = periodScheme.getFunctionalPeriodList();
			context.put("periods", periods);
		} else
			context.remove("periods");
	}

	private void start() throws Exception {
		vm = path + "start.vm";
		context.put("period_schemes", db.list("select p from PeriodScheme p"));
		context.put("intakes", db.list("select s from Student st JOIN st.intake s group by s.id order by s.startDate"));
		Session currentSession = StudentRegistrationUtil.getCurrentSession();
		context.put("current_session", currentSession);
	}

	public static void main(String[] args) throws Exception {

		DbPersistence db = new DbPersistence();
		String sql = "select r from FinalSubjectResult r where r.subject.code = 'UW00302'";

		String periodId = "1232016147884";
		String subjectId = "AA2043";
		String intakeId = "1232073136316";

		sql = "select fr from FinalResult fr where " + "fr.period.id='"
				+ periodId + "' " + "and fr.student.intake.id = '" + intakeId
				+ "'";

		List<FinalResult> frs = db.list(sql);
		int i = 0;
		for (FinalResult result : frs) {
			// System.out.println(result.getSubject().getCode() +" " +
			// result.getSubject().getName() +" " + result.getTotalMark() + ", "
			// + result.getGrade() + ", " + result.getGradePoint());
			// System.out.println("Programme: "+frs.get(i).getStudent().getProgram()+" Subject: "+frs.get(i).getSubject(subjectId));
			System.out.println("subject: " + result.getSubject(subjectId)
					+ " total mark: "
					+ result.getSubject(subjectId).getTotalMark() + " Gred: "
					+ result.getSubject(subjectId).getGrade());
			System.out.println("No Matrik: "
					+ result.getStudent().getMatricNo() + " Student Name: "
					+ result.getStudent().getBiodata().getName());

			System.out.println("session id " + result.getSession().getId()
					+ " " + result.getSession().getEndDate());

			FinalResult frb4 = null;
			sql = "select fr from FinalResult fr where " + "fr.session ";

			System.out.println("" + result.getGpa());

			for (FinalSubjectResult fs : result.getSubjects()) {
				System.out.println("Subject code " + fs.getSubject().getCode()
						+ " gred " + fs.getGrade());

			}
			// i++;
		}

	}

	private double getSession(Session session) {
		
		String sql = "select r from Session r order by r.startDate";
		List<Session> sessions = db.list(sql);
		int getSession = 0;
		for (int i = 0; i < sessions.size(); i++) {
			if (session.getId().equals(sessions.get(i).getId())) {
				getSession= i-1;
			}
		}
		if(getSession < 0) {
			getSession = 0;
		}
		
		sql = "select fr from FinalResult fr where fr.session.id='"+sessions.get(getSession).getId()+"'";
		FinalResult finalresult = (FinalResult)db.get(sql);
		
		return finalresult.getCgpa();
	}

}
