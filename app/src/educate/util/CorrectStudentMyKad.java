package educate.util;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;

public class CorrectStudentMyKad {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		List<Student> students = db.list("select s from Student s");
		System.out.println(students.size());
		db.begin();
		for ( Student s : students ) {
			String icno = s.getBiodata().getIcno();
			if ( icno != null ) {
				icno = icno.replaceAll("-", "");
				try {
					MyKad mykad = new MyKad(icno);
					s.getBiodata().setIcno(icno);
					
				} catch ( Exception e ) {
					System.out.println(e.getMessage());
				}
			}
			
		}
		db.commit();
		
	}

}
