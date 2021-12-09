package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.exam.entity.CourseworkScheme;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class XAssignAssessmentSchemeToSubjectModule extends LebahModule {
	

	String path = "apps/util/assessment_subject_scheme_setup/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public void preProcess() {
		System.out.println("command = " + command);
	}
	
	@Override
	public String start() {
		List<Subject> subjects = db.list("select s from Subject s order by s.code");
		context.put("subjects", subjects);
		List<CourseworkScheme> schemes = db.list("select s from CourseworkScheme s");
		context.put("schemes", schemes);
		return path + "start.vm";
	}
	
	@Command("select_scheme")
	public String selectScheme() throws Exception {
		String subjectId = request.getParameter("subject_id");
		String schemeId = request.getParameter("scheme_id_" + subjectId);
		
		Subject subject = db.find(Subject.class, subjectId);
		CourseworkScheme scheme = db.find(CourseworkScheme.class, schemeId);
		
		db.begin();
		subject.setCourseworkScheme(scheme);
		db.commit();
		
		return path + "empty.vm";
	}
	
	@Command("assign_scheme_all")
	public String assignSchemeToAll() throws Exception {
		
		String schemeId = request.getParameter("scheme_id");
		CourseworkScheme scheme = db.find(CourseworkScheme.class, schemeId);
		
		List<Subject> subjects = db.list("select s from Subject s order by s.code");
		for ( Subject subject : subjects ) {
			db.begin();
			subject.setCourseworkScheme(scheme);
			db.commit();
		}
		context.put("subjects", subjects);
		List<CourseworkScheme> schemes = db.list("select s from CourseworkScheme s");
		context.put("schemes", schemes);
		return path + "subjects.vm";
	}
}
