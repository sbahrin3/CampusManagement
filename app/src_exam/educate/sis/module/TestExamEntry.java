/**
 * 
 */
package educate.sis.module;

import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.Session;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class TestExamEntry {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		String sessionId = "1401960688641";
		String centreId= "CY";
		String programId= "1401960689894";
		String intakeId= "1401960688638";
		String subjectId= "1401960690448";
		
		/*
		String sql = "select subject from StudentStatus s JOIN s.studentSubjects subject " +
				"where (s.student.fakeStudent is null OR s.student.fakeStudent = '') "+ 
				"and s.type.inActive = 0 " + //do not includes inactive students
				"and s.session.id = '" + sessionId + "' " + 
				"and s.student.learningCenter.id = '" + centreId + "' " +
				"and s.student.program.id = '" + programId + "' ";
		if ( !"".equals(intakeId)) sql += " and s.student.intake.id = '" + intakeId + "' ";
		sql += " and subject.subject.id = '" + subjectId + "' " +
				"order by s.student.matricNo";
		
		*/
		intakeId = "";
		programId = "";
		
		Session session = db.find(Session.class, sessionId);
		
		Hashtable h = new Hashtable();
		h.put("date", session.getStartDate());
		
		String sql = "select subject from StudentStatus s JOIN s.studentSubjects subject " +
				"where (s.student.fakeStudent is null OR s.student.fakeStudent = '') "+ 
				"and s.type.inActive = 0 " + //do not includes inactive students
				"and :date between s.session.startDate and s.session.endDate " +
				"and s.student.learningCenter.id = '" + centreId + "' ";
		//if ( !"".equals(sessionId)) sql += "and s.session.id = '" + sessionId + "' ";
		if ( !"".equals(programId)) sql  += "and s.student.program.id = '" + programId + "' ";
		if ( !"".equals(intakeId)) sql += " and s.student.intake.id = '" + intakeId + "' ";
		sql += " and subject.subject.id = '" + subjectId + "' " +
				"order by s.student.matricNo";
		
		List<StudentSubject> studentSubjects = db.list(sql, h);
		System.out.println(studentSubjects.size());
		
		
	}

}
