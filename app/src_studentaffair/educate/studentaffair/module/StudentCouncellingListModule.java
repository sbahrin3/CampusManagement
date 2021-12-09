package educate.studentaffair.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.studentaffair.entity.StudentCouncelling;

public class StudentCouncellingListModule extends StudentCouncellingModule {
	
	private String path = "studentaffair/student_councelling";
	private DbPersistence db = new DbPersistence();	

	@Override
	public String start() {
		List<StudentCouncelling> studentCases = db.list("select s from StudentCouncelling s order by s.referredDate desc");
		context.put("studentCases", studentCases);
		return path + "/list/start.vm";
	}
	
}
