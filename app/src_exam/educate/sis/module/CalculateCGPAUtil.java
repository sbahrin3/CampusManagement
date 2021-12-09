package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;

public class CalculateCGPAUtil {
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		List<Student> students = db.list("select s from Student s where s.matricNo = '0512-0052'");
		System.out.println(students.size());
		for ( Student s : students ) {
			System.out.println("Calculating: " + s.getMatricNo() + ": " + s.getBiodata().getName());
			ResultEntryUtil.calculateResultCGPA(db, s);
			System.out.println("Done");
			Thread.sleep(1000);
		}
	}

}
 