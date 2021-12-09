package educate.enrollment.report;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.Result;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.AjaxModule;

public class SubjectRegistrationReportModule extends AjaxModule {
	
	private final String path = "apps/util/subjects_registration/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		String command = request.getParameter("command");
		
		if ( command == null || "".equals(command) || "statistic".equals(command)) doReport();
		if ( "list_students".equals(command)) listStudents();
		
		return vm;
	}

	private void listStudents() {
		vm = path + "list_students.vm";
		String subjectId = request.getParameter("subject_id");
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		Hashtable param = new Hashtable();
		param.put("date", new Date());
		param.put("subject_id", subjectId);
		String centreId = request.getParameter("centre_id");
		if ( centreId != null && !"".equals(centreId)) {
			context.put("centre", db.find(LearningCentre.class, centreId));
			param.put("centre_id", centreId);
		} else {
			context.remove("centre");
		}
		String sql = "";

		sql = "select s " +
				"from Student s join s.status st " +
				"join st.studentSubjects sub " +
				"where (s.fakeStudent is null OR s.fakeStudent = '') " +
				"and :date between st.session.startDate and st.session.endDate " +
				"and sub.subject.id = :subject_id ";
		if ( centreId != null && !"".equals(centreId)) {
			sql += "and s.learningCenter.id = :centre_id ";
		}
		sql += "order by s.biodata.name";
		List<Student> list = db.list(sql, param);
		context.put("students", list);
		
	}

	private void doReport() {
		vm = path + "report.vm";
		List<LearningCentre> centres = db.list("select x from LearningCentre x order by x.name");
		context.put("centres", centres);
		String centreId = request.getParameter("centre_id");
		if ( centreId != null && !"".equals(centreId)) {
			context.put("centre", db.find(LearningCentre.class, centreId));
		} else {
			context.remove("centre");
		}
		String sql = "";
		Hashtable param = new Hashtable();
		param.put("date", new Date());
		sql = "select new educate.enrollment.Result(sub.subject.id, sub.subject.code, sub.subject.name, count(s)) " +
				"from Student s join s.status st " +
				"join st.studentSubjects sub " +
				"where (s.fakeStudent is null OR s.fakeStudent = '') " +
				"and :date between st.session.startDate and st.session.endDate ";
		if ( centreId != null && !"".equals(centreId)) {
			param.put("centre_id", centreId);
			sql += "and s.learningCenter.id = :centre_id ";
		}
		sql += "group by sub.subject.id order by sub.subject.code";
		List<Result> list = db.list(sql, param);
		context.put("results", list);
	}

}
