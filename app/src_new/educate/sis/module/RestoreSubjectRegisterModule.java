package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import lebah.portal.action.LebahModule;

public class RestoreSubjectRegisterModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "";

	@Override
	public String start() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String matricNo = "0001DPS0010114FT";
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		String studentId = student.getId();
		
		List<StudentStatus> studentStatuses = db.list("select ss from StudentStatus ss where ss.student.id = '" + studentId + "'");
		for ( StudentStatus ss : studentStatuses ) {
			System.out.println(ss.getPeriod().getParent().getName() + ", " + ss.getPeriod().getName());
		}
		
	}

}
