package educate.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;

public class SaveBirthDate {
	
	public static void main(String[] args) {
		
		DbPersistence db = new DbPersistence();
		List<Student> students = db.list("select s from educate.enrollment.entity.Student s");
		long i = 0;
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		for ( Student s : students ) {
			if ( s.getBiodata().getIcno() != null ) {
				try {
					MyKad mykad = new MyKad(s.getBiodata().getIcno());
					Date dob = mykad.getDateOfBirth();
					db.begin();
					s.getBiodata().setDob(dob);
					db.commit();
					System.out.println(++i + ")" + s.getBiodata().getName() + " - " + df.format(s.getBiodata().getDob()));
				} catch ( Exception e ) {
					System.out.println(e.getMessage() + " " + s.getBiodata().getIcno());
				}
			}
		}
	}

}
