package educate.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.general.entity.Gender;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class UpdateBirthDateModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "/apps/update/student/";

	@Override
	public String start() {
		
		return path + "/start.vm";
	}
	
	@Command("update")
	public String update() {
		List<Student> students = db.list("select s from educate.enrollment.entity.Student s");
		long i = 0;
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		for ( Student s : students ) {
			if ( s.getBiodata().getIcno() != null ) {
				try {
					MyKad mykad = new MyKad(s.getBiodata().getIcno());
					Date dob = mykad.getDateOfBirth();
					String genderId = mykad.getGender().equals("M") ? "L" : "P";
					Gender gender = db.find(Gender.class, genderId);
					db.begin();
					s.getBiodata().setDob(dob);
					s.getBiodata().setGender(gender);
					db.commit();
					//System.out.println(++i + ")" + s.getBiodata().getName() + " - " + df.format(s.getBiodata().getDob()));
				} catch ( Exception e ) {
					System.out.println(e.getMessage() + " " + s.getBiodata().getIcno());
				}
			}
		}
		return path + "/update.vm";
	}

}
