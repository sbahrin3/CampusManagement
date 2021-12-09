/**
 * 
 */
package educate.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.ProgramUtil;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class StudentStatusChecker {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		String matricNo = "0014DTM0030913FT"; //"0071DID0070512FT";
		String sql = "select s from Student s where s.matricNo = '" + matricNo + "'";
		Student student = (Student) db.get(sql);
		repairUndefinedStatus(db, student);
		
	}
	
	
	public static void repairUndefinedStatus(DbPersistence db, Student student) throws Exception {
		
		Period entryLevel = student.getEntryLevel();
		
		ProgramUtil pu = new ProgramUtil();
		Program program = student.getProgram();
		List<Period> periods = new ArrayList<Period>();
		List<Period> periods2 = program.getPeriodScheme().getFunctionalPeriodList();
		if ( entryLevel != null ) {
			boolean begin = false;
			for ( Period p : periods2 ) {
				if ( p.getId().equals(entryLevel.getId())) begin = true;
				if ( begin ) periods.add(p);
			}
		} else {
			periods = periods2;
		}
		
		StudentStatusUtil u = new StudentStatusUtil();
		List<StudentStatus> statuses = u.getStudentStatuses(student); 
		//context.put("statuses", statuses);
		
		int numberOfPeriods = periods.size();
		int numberOfStatuses = statuses.size();
		int totalCount = numberOfPeriods > numberOfStatuses ? numberOfPeriods : numberOfStatuses;

		
		Hashtable param = new Hashtable();
		param.put("dateStart", student.getIntake().getStartDate());
		param.put("pathNo", student.getProgram().getLevel().getPathNo());
		List<Session> sessions = db.list("select s from Session s where s.pathNo = :pathNo and s.startDate >= :dateStart order by s.startDate", totalCount, param);
		//context.put("sessions", sessions);
		
		//get status for each session
		
		//store student status for each session
		Map<String, StudentStatus> statusMap = new HashMap<String, StudentStatus>();
		int count = 0;
		int i = 0;
		Session firstSession = null;
		for ( Session session : sessions ) {
			i++;
			if ( i == 1 ) {
				firstSession = session;
			}
			StudentStatus studentStatus =  pu.getStudentStatus(student, session);
			if ( studentStatus != null ) {
				statusMap.put(session.getId(), studentStatus);
				count++;
			}
			if ( count > totalCount ) break;
			
		}
		
		//------
		int undefined = 0;
		for ( Session session : sessions ) {
			StudentStatus status = statusMap.get(session.getId());
			if ( status == null ) {
				undefined++;
			}
		}
		
		if ( undefined > 0 ) {
			System.out.println("got undefined status.. create all statuses");
			u.cleanUpStudentStatuses(student.getId());
			u.setUseSubjectTemp(true);
			u.addStatus(student, firstSession);
		}
		
	}

}
