package educate.sis.exam.entity;

import lebah.db.*;
import educate.enrollment.entity.*;
import java.util.*;

public class ListStudentStatusId {
	
	public static void main(String[] args) throws Exception {
		PersistenceManager2 pm = new PersistenceManager2();
		String studentId = "1221362824111";
		Student student = (Student) pm.find(Student.class, studentId);
		
		Set<StudentStatus> list = student.getStatus();
		for ( StudentStatus s : list ) {
			System.out.println(s.getId() + " - " + s.getSession().getName());
		}
	}

}
