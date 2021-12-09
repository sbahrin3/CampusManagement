package educate.sis.module;

import java.util.List;

import educate.admission.entity.SpmResult;
import educate.admission.entity.StpmResult;
import educate.db.DbPersistence;
import educate.sis.general.entity.ExamGradeScheme;
import educate.sis.general.entity.SchoolSubject;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ApplicationSubjectSetupModule extends LebahModule {
	
	String path = "apps/util/application_subject/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public String start() {
		List<String> types = db.list("select distinct s.stype from SchoolSubject s group by s.stype");
		context.put("types", types);
		return path + "start.vm";
	}

	@Command("list_subjects")
	public String listSubjects() throws Exception {
		List<String> types = db.list("select distinct s.stype from SchoolSubject s group by s.stype");
		context.put("types", types);
		String type = request.getParameter("type");
		context.put("type", type);
		List<SchoolSubject> subjects = db.list("select s from SchoolSubject s where s.stype = '" + type + "' order by s.subjectOrder");
		context.put("subjects", subjects);
		return path + "subjects.vm";
	}
	
	@Command("save_order")
	public String saveOrder() throws Exception {
		String[] ids = request.getParameterValues("subject_ids");
		int i = 0;
		for ( String id : ids ) {
			SchoolSubject s = db.find(SchoolSubject.class, id);
			db.begin();
			s.setSubjectOrder(i++);
			db.commit();
		}
		return path + "empty.vm";
	}

	@Command("add_subject")
	public String addSubject() throws Exception {
		String type = request.getParameter("type");
		String code = request.getParameter("code");
		String name = request.getParameter("name");
		String d = request.getParameter("cb_default") != null ? request.getParameter("cb_default") : "";
		
		db.begin();
		SchoolSubject s = new SchoolSubject();
		s.setCode(code);
		s.setName(name);
		s.setType(type);
		if ( "yes".equals(d) ) s.setDefault(true);
		else s.setDefault(false);
		db.persist(s);
		db.commit();
		
		return listSubjects();
	}
	
	@Command("delete_subject")
	public String deleteSubject() throws Exception {
		String subjectId = request.getParameter("subject_id");
		
		boolean found = false;
		List<SpmResult> spmResults = db.list("select s from SpmResult s where s.subject.id = '" + subjectId + "'");
		if ( spmResults.size() == 0 ) {
			List<StpmResult> stpmResults = db.list("select s from StpmResult s where s.subject.id = '" + subjectId + "'");
			if ( stpmResults.size() > 0 ) found = true;
		} else found = true;
		
		if ( !found ) {
			SchoolSubject subject = db.find(SchoolSubject.class, subjectId);
			db.begin();
			db.remove(subject);
			db.commit();
		} else {
			System.out.println("Can't delete School Subject!");
		}
		return listSubjects();
	}
	
	@Command("update_code")
	public String updateCode() throws Exception {
		String subjectId = request.getParameter("subject_id");
		SchoolSubject subject = db.find(SchoolSubject.class, subjectId);
		String code = request.getParameter("code_" + subjectId);
		db.begin();
		subject.setCode(code);
		db.commit();
		return path + "empty.vm";
	}

	@Command("update_name")
	public String updateName() throws Exception {
		String subjectId = request.getParameter("subject_id");
		SchoolSubject subject = db.find(SchoolSubject.class, subjectId);
		String name = request.getParameter("name_" + subjectId);
		db.begin();
		subject.setName(name);
		db.commit();
		return path + "empty.vm";
	}
	
	@Command("update_default")
	public String updateDefault() throws Exception {
		String subjectId = request.getParameter("subject_id");
		SchoolSubject subject = db.find(SchoolSubject.class, subjectId);
		
		db.begin();
		String d = request.getParameter("default");
		if ( "yes".equals(d)) subject.setDefault(true);
		else subject.setDefault(false);
		db.commit();
		
		return path + "empty.vm";
	}

	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		ExamGradeScheme s = new ExamGradeScheme();
		s.setId("1900");
		s.setStartYear(1990);
		s.setCode("SPM1900");
		s.setName("SPM");
		db.begin();
		db.persist(s);
		db.commit();
	}
}
