package test;

import lebah.db.*;
import educate.sis.struct.entity.*;
import educate.admission.entity.*;
import java.util.*;

public class Applicants {
	
	public static void main(String[] args) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<Applicant> apps = pm.list("select apps from Applicant apps");
		System.out.println(apps.size());
		for ( Applicant app : apps ) {
			System.out.println(app.getReferenceNo() + ", " + app.getBiodata().getName());
			Program program = app.getProgramOffered();
			System.out.println(program.getCode() + " - " + program.getName());
			
			Course course = program.getCourse();
			System.out.println(course.getName());
			
			Faculty faculty = course.getFaculty();
			System.out.println(faculty.getName());
			
			List<Subject> subjects = pm.list("SELECT s FROM Subject s WHERE s.faculty.id='" + faculty.getId() + "'");
			System.out.println(subjects.size());
			
			//pm.find(Applicant.class).whereId("").forUpdate();
			
			
			
		}
	}

}
