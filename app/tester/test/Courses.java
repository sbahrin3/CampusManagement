package test;

import java.text.SimpleDateFormat;
import java.util.*;

import lebah.db.*;
import educate.sis.struct.entity.*;
import educate.enrollment.entity.*;
import educate.admission.entity.*;
public class Courses {
	
	public static void main(String[] args) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		pm.executeUpdate("delete from Course c");
		
		//get faculty by code
		Faculty faculty = Faculty.findByCode("FIT");
		System.out.println(faculty.getName());
		
		Course course = new Course();
		course.setCode("IT");
		course.setName("Information Technology");
		course.setFaculty(faculty);
		pm.add(course);
		
		List<Course> list = pm.list("select c from Course c");
		for ( Iterator<Course> it = list.iterator(); it.hasNext(); ) {
			course = it.next();
			System.out.println(course.getCode() + ", " + course.getName());
		}
		
	}

}
