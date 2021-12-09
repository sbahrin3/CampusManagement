package educate.util;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.general.entity.Gender;

public class CorrectStudentGender {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		Gender male = db.find(Gender.class, "L");
		Gender female = db.find(Gender.class, "P");
		
		Date today = new Date();
		Hashtable h = new Hashtable();
		h.put("today", today);
		String sql = "select s.student from StudentStatus s JOIN s.session ss " +
		"where (ss.startDate <= :today " +
		"and ss.endDate >= :today) " +
		"and s.type.inActive = 0";
		
		List<Student> list = db.list(sql, h);
		MyKad mykad = null;
		for ( Student s : list ) {
			System.out.print(s.getBiodata().getIcno() + " = ");
			try {
				mykad = new MyKad(s.getBiodata().getIcno());
				System.out.println(mykad.getGender());
				
				db.begin();
				Gender gender = null;
				if ( "F".equals(mykad.getGender()) ) gender = female;
				else if ( "M".equals(mykad.getGender()) ) gender = male;
				s.getBiodata().setGender(gender);
				db.commit();
				
			} catch ( Exception e ) {
				System.out.println(e.getMessage());
			}
		}
		System.out.println(list.size());
		
	}

}
