package test;

import java.text.SimpleDateFormat;
import java.util.*;

import lebah.db.*;
import educate.sis.struct.entity.*;
import educate.enrollment.entity.*;
import educate.admission.entity.*;

public class StudentEnrollmentStatus {
	
	public static void main(String[] args) throws Exception {
		
		String matricNo = "123";
		PersistenceManager pm = new PersistenceManager();
		List<Student> queries = pm.list("select s from Student s where s.matricNo = '" + matricNo + "'");
		Student student = null;
		if ( queries.size() == 1 ) {
			student = queries.get(0);
		}
		String id = student.getId();
		System.out.println("Name=" + student.getBiodata().getName());
		System.out.println("Program=" + student.getProgram().getName());
		System.out.println("Intake=" + student.getIntake().getCode());
		System.out.println("Faculty=" + student.getProgram().getCourse().getFaculty().getCode());
		System.out.println("Period Scheme=" + student.getProgram().getPeriodScheme().getCode());
		
		
		Vector<Period> periodList = new Vector();
		
		
		PeriodScheme periodScheme = student.getProgram().getPeriodScheme();
		//get all period
		List<Period> periods = periodScheme.getFunctionalPeriodList();
		for ( Iterator<Period> it = periods.iterator(); it.hasNext(); ) {
			Period p = it.next();
			periodList.add(p);
		}
		
		Session intake = student.getIntake();
		//list all sessions begining from this intake
		Hashtable h = new Hashtable();
		h.put("dateStart", intake.getStartDate());
		
		Vector<Session> sessionList = new Vector();
		
		List<Session> sessions = pm.list("select s from Session s where s.startDate >= :dateStart order by s.startDate", h); 
		for ( Iterator<Session> it = sessions.iterator(); it.hasNext(); ) {
			Session session = it.next();
			sessionList.add(session);
		}

		Set<StudentStatus> statusList = student.getStatus();
		
		for ( Iterator<StudentStatus> it = statusList.iterator(); it.hasNext(); ) {
			StudentStatus status = it.next();
			Period p = status.getPeriod();
			Session s = status.getSession();
			System.out.println(p.getName() + "=" + s.getCode() + ", " 
					+ new SimpleDateFormat("dd-MM-yy").format(s.getStartDate())
					+ " -> "
					+ new SimpleDateFormat("dd-MM-yy").format(s.getEndDate())
			);
		}
		
	
		
		
		
	}

}
