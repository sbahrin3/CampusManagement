package educate.admission.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.general.entity.Gender;
import educate.util.MyKad;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class XAdminUtilModule extends LebahModule {
	
	DbPersistence db = new DbPersistence();
	String path = "admission/xAdminUtil";

	@Override
	public String start() {

		return path + "/start.vm";
	}
	
	@Command("studentGender")
	public String studentGender() throws Exception {
		Gender male = db.find(Gender.class, "L");
		Gender female = db.find(Gender.class, "P");
		
		Date today = new Date();
		Hashtable h = new Hashtable();
		h.put("today", today);
		String sql = "select s.student from StudentStatus s JOIN s.session ss " +
		"where (ss.startDate <= :today " +
		"and ss.endDate >= :today) " +
		"and s.type.inActive = 0";
		
		List<Student> students = new ArrayList<Student>();
		context.put("students", students);
		List<Student> list = db.list(sql, h);
		MyKad mykad = null;
		for ( Student s : list ) {
			try {
				mykad = new MyKad(s.getBiodata().getIcno());
				
				db.begin();
				Gender gender = null;
				if ( "F".equals(mykad.getGender()) ) gender = female;
				else if ( "M".equals(mykad.getGender()) ) gender = male;
				s.getBiodata().setGender(gender);
				db.commit();
				students.add(s);
			} catch ( Exception e ) {
				System.out.println(e.getMessage());
			}
		}
		return path + "/empty.vm";
	}
	
	@Command("cleanUpTimetable")
	public String cleanUpTimetable() throws Exception {
		
		db.begin();
		db.executeUpdate("delete from educate.timetabling.entity.Classroom c");
		db.commit();
		db.begin();
		db.executeUpdate("delete from educate.timetabling.entity.SlotClassroom c");
		db.commit();
		db.begin();
		db.executeUpdate("delete from educate.timetabling.entity.FailedClassroomTemplate c");
		db.commit();		
		
		return path + "/empty.vm";
	}
	
	@Command("cleanUpClassroomTemplate")
	public String cleanUpClassroomTemplate() throws Exception {
		
		db.begin();
		db.executeUpdate("delete from educate.timetabling.entity.ClassroomTemplate c");
		db.commit();
		
		return path + "/empty.vm";
	}	

}
