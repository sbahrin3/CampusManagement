package educate.studentaffair.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.studentaffair.entity.StudentDiscipline;

public class StudentDisciplineListModule extends StudentDisciplineModule {
	
	private String path = "studentaffair/student_discipline";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		List<StudentDiscipline> studentCases = db.list("select s from StudentDiscipline s order by s.reportedDate desc");
		context.put("studentCases", studentCases);
		return path + "/list/start.vm";
	}
	
}
