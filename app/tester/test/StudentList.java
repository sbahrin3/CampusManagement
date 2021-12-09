package test;

import lebah.db.*;
import educate.enrollment.entity.*;
import educate.sis.struct.entity.*;
import java.util.*;

public class StudentList {
	
	public static void main(String[] args) throws Exception {
		
		PersistenceManager pm = new PersistenceManager();
		List<Student> students = pm.list("select s from Student s");
		System.out.println(students.size());
		
		for ( Iterator<Student> it = students.iterator(); it.hasNext(); ) {
			Student student = it.next();
			System.out.println(student.getMatricNo() + ", " + student.getBiodata().getName() + ", " + student.getIntake().getCode());
		}
		
	}

}
