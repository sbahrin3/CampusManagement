package demo;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.registration.StudentData;

public class StudentLogin {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		List<Student> students = db.list("select s from Student s");
		for ( Student s : students ) {
			System.out.println(s.getMatricNo());
			if ( s.getMatricNo() != null && s.getBiodata() != null)
				StudentData.createPortalLogin(s.getMatricNo(), s.getMatricNo(), s.getBiodata().getName());
		}
	}
	
	

}
