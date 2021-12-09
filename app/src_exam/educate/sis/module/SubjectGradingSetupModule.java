package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.exam.entity.MarkingGrade;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.AjaxModule;

public class SubjectGradingSetupModule  extends AjaxModule {
	
	String path = "apps/util/setup_subjects_grades/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) listSchools();
		else if ( "list_subjects".equals(command)) listSubjects();
		else if ( "select_grades".equals(command)) selectGrades();
		else if ( "assign_all".equals(command)) assignAll();
		return vm;
	}



	private void assignAll() throws Exception {
		String markingId = getParam("marking_all");
		MarkingGrade marking = db.find(MarkingGrade.class, markingId);
		if ( marking != null ) {
			String facultyId = getParam("faculty_id");
			String sql = "select s from Subject s where s.faculty.id = '" + facultyId + "'";
			List<Subject> subjects = db.list(sql);
			db.begin();
			for ( Subject s : subjects ) {
				s.setMarkingGrade(marking);
			}
			db.commit();
				
		}
		listSubjects();
		vm = path + "subject_grades.vm";
	}



	private void selectGrades() throws Exception {
		vm = path + "select_markings.vm";
		String subjectId = request.getParameter("subject_id");
		Subject subject = db.find(Subject.class, subjectId);
		context.put("s", subject);
		
		String markingId = request.getParameter("marking_" + subject.getId());
		MarkingGrade marking = db.find(MarkingGrade.class, markingId);
		if ( marking != null ) {
			db.begin();
			subject.setMarkingGrade(marking);
			db.commit();
		}
		
		String sql = "select m from MarkingGrade m";
		List<MarkingGrade> markings = db.list(sql);
		context.put("markings", markings);	
		
		
		
	}



	private void listSubjects() {
		vm = path + "subjects.vm";
		List<Faculty> faculties = db.list("select f from Faculty f order by f.code");
		context.put("faculties", faculties);
		
		String facultyId = request.getParameter("faculty_id");
		if ( "".equals(facultyId)) {
			context.remove("faculty");
			return;
		}
		Faculty faculty = db.find(Faculty.class, facultyId);
		context.put("faculty", faculty);
		
		Hashtable p = new Hashtable();
		p.put("faculty", faculty);
		String sql = "select s from Subject s where s.faculty = :faculty order by s.name";
		List<Subject> subjects = db.list(sql, p);
		context.put("subjects", subjects);
		
		sql = "select m from MarkingGrade m";
		List<MarkingGrade> markings = db.list(sql);
		context.put("markings", markings);	
		
	}

	private void listSchools() {
		vm = path + "schools.vm";
		List<Faculty> faculties = db.list("select f from Faculty f order by f.code");
		context.put("faculties", faculties);
		context.remove("faculty");
	}

}
