package educate.studentaffair.module;

import educate.enrollment.entity.Student;

public class StudentClubActivityModule extends ClubActivityModule {
	
	public void preProcess() {
		setStudentMode(true);
		String matricNo = (String) request.getSession().getAttribute("_portal_login");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		setStudentId(student.getId());
		super.preProcess();
	}

}
