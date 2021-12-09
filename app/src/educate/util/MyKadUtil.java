package educate.util;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.general.entity.Gender;

public class MyKadUtil {
	
	public static void correctMyKadNumber() throws Exception {
		DbPersistence db = new DbPersistence();
		
		Gender F = db.find(Gender.class, "P");
		Gender M = db.find(Gender.class, "L");
		
		List<Student> students = db.list("select s from Student s");
		db.begin();
		for ( Student s : students ) {
			String icno = s.getBiodata().getIcno();
			if ( icno != null ) {
				icno = icno.trim();
				icno = icno.replaceAll("-", "");
				try {
					MyKad mykad = new MyKad(icno);
					s.getBiodata().setIcno(icno);
					if ( "F".equals(mykad.getGender())) s.getBiodata().setGender(F);
					else if ( "M".equals(mykad.getGender())) s.getBiodata().setGender(M);
				} catch ( Exception e ) {
					System.out.println(s.getBiodata().getName() + ", " + s.getBiodata().getIcno() + " - "+ e.getMessage());
				}
			}
		}
		db.commit();
	}
	
	public static void main(String[] args) throws Exception {
		
		MyKadUtil.correctMyKadNumber();
		
	}	

}
