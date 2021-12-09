/**
 * 
 */
package educate.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.SessionResult;
import educate.sis.finance.entity.Invoice;
import educate.sis.general.entity.GradsLevel;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class SessionPathMigration {
	
	
	private DbPersistence db;
	private String pathFrom = "0";
	private String pathTo = "0";
	private Program program;
	private Map<String, Session> sessionLookup = new HashMap<String, Session>();
	
	public SessionPathMigration(DbPersistence db, Program program, String pathTo) {
		this.db = db;
		this.pathFrom = Integer.toString(program.getLevel().getPathNo());
		this.pathTo = pathTo;
		this.program = program;
		
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathTo + " order by s.startDate");
		
		for ( Session session : sessions ) {
			sessionLookup.put(session.getCode(), session);
		}
	}
	
	
	public void migratePath() throws Exception {
		
		GradsLevel level = db.find(GradsLevel.class, pathTo);
		db.begin();
		program.setLevel(level);
		db.commit();
		
	}

	public void migrateStudent(Student student) throws Exception {
		
		String matricNo = student.getMatricNo();
		
		db.begin();
		student.setIntake(sessionLookup.get(student.getIntake().getCode()));
		db.commit();


		List<StudentStatus> studentStatuses = db.list("select s from StudentStatus s where s.session.pathNo = " + pathFrom + " and s.student.matricNo = '" + matricNo + "' order by s.session.startDate");
		db.begin();
		for ( StudentStatus studentStatus : studentStatuses ) {
			studentStatus.setSession(sessionLookup.get(studentStatus.getSession().getCode()));
		}
		db.commit();
		
		List<SessionResult> sessionResults = db.list("select sr from SessionResult sr where sr.session.pathNo = " + pathFrom + " and sr.result.student.matricNo = '" + matricNo + "' order by sr.session.startDate");	
		db.begin();
		for ( SessionResult sessionResult : sessionResults ) {
			sessionResult.setSession(sessionLookup.get(sessionResult.getSession().getCode()));
			
		}
		db.commit();
		
		List<FinalResult> finalResults = db.list("select fr from FinalResult fr where fr.session.pathNo = " + pathFrom + " and fr.student.matricNo = '" + matricNo + "' order by fr.session.startDate");		
		db.begin();
		for ( FinalResult finalResult : finalResults ) {
			finalResult.setSession(sessionLookup.get(finalResult.getSession().getCode()));
		}
		db.commit();
		
		List<Invoice> invoices = db.list("select i from Invoice i where i.session.pathNo = " + pathFrom + " and i.student.matricNo = '" + matricNo + "' order by i.session.startDate");
		db.begin();
		for ( Invoice invoice : invoices ) {
			invoice.setSession(sessionLookup.get(invoice.getSession().getCode()));
		}
		db.commit();
	}
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		String pathTo = "3";
		String pathFrom = "0";
		String programId = "1401960688660";
		
		Program program = db.find(Program.class, programId);
		SessionPathMigration m = new SessionPathMigration(db, program, pathTo);
		m.migratePath();
		String matricNo = "0103DID0090113FT";
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		m.migrateStudent(student);
		
	}
	

}
