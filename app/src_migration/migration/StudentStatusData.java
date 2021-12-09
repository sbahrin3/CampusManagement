package migration;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.general.entity.GradsLevel;
import educate.sis.module.ProgramUtil;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;

public class StudentStatusData {
	
	public static void main(String[] args) throws Exception {
		//updateStudentStatus();
		listStudentStatus();
	}

	private static void updateStudentStatus() throws Exception {
		DbPersistence db = new DbPersistence();
		String studentId = "0908-1226";
		
		fixStudentStatus(db, studentId);
	}

	public static void fixStudentStatus(DbPersistence db, String studentId) throws Exception {
		Student student = db.find(Student.class, studentId);
		Session intake = student.getIntake();
		List<StudentStatus> statuses = db.list("select ss from StudentStatus ss where ss.student.id = '" + studentId + "' order by ss.session.startDate");
		
		System.out.println("statuses = " + statuses.size());
		
		Program program = student.getProgram();
		if ( program == null ) {
			System.out.println("Program is null");
			return;
		}
		PeriodScheme periodScheme = program.getPeriodScheme();
		if ( periodScheme == null ) {
			System.out.println("Period Scheme is null");
			return;
		}
		List<Period> periods = periodScheme.getFunctionalPeriodList();
		System.out.println("periods - " + periods.size());
		
		int i = 0;
		db.begin();
		for ( StudentStatus s : statuses ) {
			if ( i < periods.size() ) {
				Period p = periods.get(i);
				System.out.println(s.getSession().getId() + ", " + p.getName());
				s.setPeriod(p);
			}
			i++;
		}
		db.commit();
	}
	

	
	
	private static void listStudentStatus() throws Exception {
		DbPersistence db = new DbPersistence();
		String studentId = "0908-1226";
		Student student = db.find(Student.class, studentId);
		List<StudentStatus> statuses = db.list("select ss from StudentStatus ss where ss.student.id = '" + studentId + "' order by ss.session.startDate");

		for ( StudentStatus s : statuses ) {
			System.out.println(s.getSession().getName() + ", " + s.getPeriod().getName());
		}
		System.out.println("====");
		ProgramUtil pu = new ProgramUtil();
		List<StudentStatus> statuses2 = pu.getStudentStatuses(student);
		for ( StudentStatus s : statuses2 ) {
			System.out.println(s.getSession().getName() + ", " + s.getPeriod().getName() + " - " + s.getSession().getPathNo());
		}
		
	}
	
	private static void setProgramPath(DbPersistence db, Program program, String sessionPathNo) throws Exception {
		GradsLevel g = db.find(GradsLevel.class, sessionPathNo);
		if ( g != null ) {
			db.begin();
			g.setName(sessionPathNo);
			db.commit();
		} 
		else {
			db.begin();
			g = new GradsLevel();
			g.setId(sessionPathNo);
			g.setCode(sessionPathNo);
			g.setName(sessionPathNo);
			g.setPathNo(Integer.parseInt(sessionPathNo));
			db.persist(g);
			db.commit();
		}
		if ( program != null ) {
			db.begin();
			program.setLevel(g);
			db.commit();
		}
	}
	
	public static void fixStudentStatus2(DbPersistence db, String studentId) throws Exception, MatchingPeriodNotAvailableException, ProgramNotAvailableException, StudentSessionsNotAvailableException {
		Student student = db.find(Student.class, studentId);
		Session intake = student.getIntake();
		
		fixStudentStatus2(db, student);
	}

	public static void fixStudentStatus2(DbPersistence db, Student student) throws StudentSessionsNotAvailableException,
			ProgramNotAvailableException, Exception,
			MatchingPeriodNotAvailableException {
		List<StudentStatus> statuses = db.list("select ss from StudentStatus ss where ss.student.id = '" + student.getId() + "' order by ss.session.startDate");
		int pathNo = 0;
		for ( StudentStatus s : statuses ) {
			pathNo = s.getSession().getPathNo();
		}
		int sessionCount = statuses.size();
		
		if ( sessionCount == 0 ) {
			throw new StudentSessionsNotAvailableException("Student " + student.getMatricNo() + " has no sessions defined.");
		}
		
		Program program = student.getProgram();
		if ( program == null ) {
			throw new ProgramNotAvailableException("Student " + student.getMatricNo() + " has no program defined.");
		} else {
			if ( program.getLevel() == null ) {
				setProgramPath(db, program, Integer.toString(pathNo));
			}
		}
		
		PeriodScheme periodScheme = program.getPeriodScheme();
		if ( periodScheme == null ) {
			periodScheme = setPeriodScheme(db, program, sessionCount);
		}
		
		if ( periodScheme != null ) {
			List<Period> periods = periodScheme.getFunctionalPeriodList();
			int i = 0;
			db.begin();
			for ( StudentStatus s : statuses ) {
				if ( i < periods.size() ) {
					Period p = periods.get(i);
					//System.out.println(s.getSession().getId() + ", " + p.getName());
					s.setPeriod(p);
				}
				i++;
			}
			db.commit();
		}
		else {
			throw new MatchingPeriodNotAvailableException("Matching number of " + sessionCount + " periods is not available for " + student.getProgram().getCode() + " - " + student.getProgram().getName() + ".");
		}
	}

	private static PeriodScheme setPeriodScheme(DbPersistence db, Program program, int sessionCount) throws Exception {
		//get all available periods
		List<PeriodScheme> periodSchemes = db.list("select p from PeriodScheme p");
		//get matching period based on sessionCount
		PeriodScheme periodScheme = null;
		for ( PeriodScheme ps : periodSchemes ) {
			if ( ps.getFunctionalPeriodList().size() == sessionCount ) {
				periodScheme = ps;
				break;
			}
		}
		if ( periodScheme != null ) {
			db.begin();
			program.setPeriodScheme(periodScheme);
			db.commit();
		}
		return periodScheme;
	}	

}
