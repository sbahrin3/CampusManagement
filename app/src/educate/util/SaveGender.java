package educate.util;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.general.entity.Gender;

public class SaveGender {
	
	public static void main(String[] args) {
		
		DbPersistence db = new DbPersistence();
		List<Student> students = db.list("select s from educate.enrollment.entity.Student s");
		long i = 0;
		for ( Student s : students ) {
			if ( s.getBiodata().getIcno() != null ) {
				try {
					MyKad mykad = new MyKad(s.getBiodata().getIcno());
					String genderId = mykad.getGender().equals("M") ? "L" : "P";
					Gender gender = db.find(Gender.class, genderId);
					db.begin();
					s.getBiodata().setGender(gender);
					db.commit();
					System.out.println(++i + ")" + s.getBiodata().getName() + " - " +  s.getBiodata().getGender().getName());
				} catch ( Exception e ) {
					System.out.println(e.getMessage() + " " + s.getBiodata().getIcno());
				}
			}
		}
	}

}
