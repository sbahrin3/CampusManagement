package test;

import java.text.SimpleDateFormat;
import java.util.*;

import lebah.db.*;
import educate.sis.struct.entity.*;
import educate.enrollment.entity.*;
import educate.admission.entity.*;

public class RegisterStudent {

	public static void add(String name, String icno, String matricNo, String programCode, String sessionCode) throws Exception {
		
		PersistenceManager pm = new PersistenceManager();
		
		List<Program> programs = pm.list("select p from Program p where p.code = '" + programCode + "'");
		Program program = null;
		if ( programs.size() == 1 ) {
			program = programs.get(0);
			System.out.println(program.getName());
		}
		
		//get intake session
		List<Session> list = pm.list("select s from Session s where s.code = '" + sessionCode + "'");
		Session intakeSession = null;
		if ( list.size() == 1 ) {
			intakeSession = list.get(0);
			System.out.println("intake session is " + intakeSession.getCode() + " - " + new SimpleDateFormat("dd-MM-yy").format(intakeSession.getStartDate()));
		}
		
		Student student = new Student();
		student.setMatricNo(matricNo);
		student.setBiodata(new Biodata());
		student.getBiodata().setName(name);
		student.getBiodata().setIcno(icno);
		
		student.setProgram(program);
		student.setIntake(intakeSession);
		
		Vector<Period> periodList = new Vector();
		PeriodScheme periodScheme = student.getProgram().getPeriodScheme();
		//get all period
		List<Period> periods = periodScheme.getFunctionalPeriodList();
		Collections.sort(periods);
		//for ( Iterator<Period> it = periods.iterator(); it.hasNext(); ) {
		for ( int i=0; i < periods.size(); i++ ) {
			//Period p = it.next();
			Period p = periods.get(i);
			System.out.println(p.getParent().getName() + " - " + p.getName());
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
		//match period with session
		int i = 0;
		for ( Period p : periodList ) {
			Session s = sessionList.elementAt(i);
			System.out.println(p.getParent().getName() + ", " + p.getName() + "=" + s.getCode() + ", " 
					+ new SimpleDateFormat("dd-MM-yy").format(s.getStartDate())
					+ " -> "
					+ new SimpleDateFormat("dd-MM-yy").format(s.getEndDate())
			);
			
			StudentStatus status = new StudentStatus();
			status.setPeriod(p);
			status.setSession(s);
			
			statusList.add(status);
			i++;
			
		}		
		
		student.setStatus(statusList);
		pm.add(student);

	}
	
	
	
	public static void main(String[] args) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		pm.executeUpdate("delete from Student s");
		

		add("JUNDAK", "123", "123", "BIT", "2008-2");
		add("ADRI", "456", "456", "BIT", "2008-2");
		add("SHAIFUL", "876", "876", "BIT", "2008-2");
	
		
	}

}
