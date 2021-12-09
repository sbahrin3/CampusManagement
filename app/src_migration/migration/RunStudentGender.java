package migration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.general.entity.Gender;
import educate.util.MyKad;

public class RunStudentGender {
	
	public static void main(String[] args) throws Exception {
		
		
		
		int totalCount = 0;
		int genderYes = 0;
		int genderNo = 0;
		
		DbPersistence db = new DbPersistence();
		
		Gender male = db.find(Gender.class, "L");
		Gender female = db.find(Gender.class, "P");
		
		Map<String, Gender> gm = new HashMap<String, Gender>();
		gm.put("F", female);
		gm.put("M", male);
		
		List<Student> students = db.list("select s from Student s");
		for ( Student s : students ) {
			totalCount++;
			String icno = s.getBiodata().getIcno();
			if ( icno != null && !"".equals(icno)) {
				try {
					MyKad mykad = new MyKad(icno);
					String gender = mykad.getGender();
					if ( "M".equals(gender) || "F".equals(gender)) {
						genderYes++;
						System.out.println(s.getBiodata().getName() + " - " + gender);
						db.begin();
						s.getBiodata().setGender(gm.get(gender));
						db.commit();
					} 
					else {
						genderNo++;
						System.out.println(s.getBiodata().getName() + " - unspecified");
						
					}
						
					
				} catch ( Exception e ) {

					genderNo++;
					System.out.println(s.getBiodata().getName() + " - unspecified");
					
				
				}
			}
		}
		
		System.out.println("total = " + totalCount);
		System.out.println("got gender = " + genderYes);
		System.out.println("no gender = " + genderNo);
		
	}

}
