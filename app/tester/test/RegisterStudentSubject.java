package test;

import javax.persistence.*;
import educate.enrollment.entity.*;
import educate.sis.struct.entity.*;
import java.util.*;
import lebah.db.*;

public class RegisterStudentSubject {
	
	public static void main(String[] args) throws Exception {
		
		PersistenceManager pm = new PersistenceManager();
		String matricNo = "BIT-2008-2-0005";
		List<Student> list = pm.list("select s from Student s where s.matricNo = '" + matricNo + "'");
		Student student = list.get(0);
		System.out.println(student.getBiodata().getName());
		
		String sessionCode = "2008-2";
		List<StudentStatus> statusList = pm.list("select s from StudentStatus s where s.student.matricNo = '" + matricNo + "' and s.session.code = '" + sessionCode + "'");
		StudentStatus status = statusList.get(0);
		System.out.println(status.getPeriod().getName() + ", " + status.getPeriod().getParent().getName());

		
		sessionCode = "2009-1";
		statusList = pm.list("select s from StudentStatus s where s.student.matricNo = '" + matricNo + "' and s.session.code = '" + sessionCode + "'");
		status = statusList.get(0);
		System.out.println(status.getPeriod().getName() + ", " + status.getPeriod().getParent().getName());
		
		
		for ( Iterator<StudentStatus> it = student.getStatus().iterator(); it.hasNext(); ) {
			status = it.next();
			System.out.println(status.getSession().getCode() + "-" + status.getPeriod().getName() + ", " + status.getPeriod().getParent().getName());
		}
	}

}
