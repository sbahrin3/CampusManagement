package migration;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.parent.module.PortalData;

public class RunStudentLogin {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();

		List<Student> students = db.list("select s from Student s");
		for ( Student s : students ) {
			System.out.println(s.getMatricNo() + ", " + s.getBiodata().getName());
			PortalData.createPortalLogin(s.getMatricNo(), s.getMatricNo(), s.getBiodata().getName(), "student");
		}
		
	}

}
