package educate.sis.module;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.exam.entity.AssessmentResult;
import educate.sis.exam.entity.Coursework;
import educate.sis.exam.entity.CourseworkItem;
import educate.sis.exam.entity.ExamResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.SessionResult;
import educate.sis.exam.entity.SubjectResult;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;

public class AssessmentResultRequest {
	
	DbPersistence db;
	
	public AssessmentResultRequest(DbPersistence db) {
		this.db = db;
	}
	
	public List<CourseworkItem> getCourseworkItem(String subjectId, String sessionId, String centreId) throws Exception {
		String sql;
		Coursework coursework = null;
		sql = "select c from Coursework c where c.subject.id = '" + subjectId + "' and c.session.id = '" + sessionId + "' and c.centre.id = '" + centreId + "'";
		coursework = (Coursework) db.get(sql);
		if ( coursework != null ) {
			return coursework.getCourseworkItems();
		}
		return new ArrayList<CourseworkItem>();
	}
	
	public List<List> listStudents(Subject subject, String programId, String sessionId, String intakeId, String centreId, String sectionId, List<CourseworkItem> markItems) throws Exception {
		
		String sql = "select c from Coursework c where c.subject.id = '" + subject.getId() + "' and c.session.id = '" + sessionId + "' and c.centre.id = '" + centreId + "'";
		Coursework coursework = (Coursework) db.get(sql);
		int roundType = coursework != null ? coursework.getRoundType() : 0;
		
		DecimalFormat numFormat =  new DecimalFormat("#.00");
		DecimalFormat resultFormat = new DecimalFormat("##");
		
		
		sql = "select subject from StudentStatus s JOIN s.studentSubjects subject " +
				"where (s.student.fakeStudent is null OR s.student.fakeStudent = '') "+ 
				"and s.type.inActive = 0 " + //do not includes inactive students
				"and s.student.program.id = '" + programId + "' " +
				"and s.session.id = '" + sessionId + "' " +
				"and s.student.learningCenter.id = '" + centreId + "'";
		if ( !"".equals(intakeId)) sql += " and s.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(sectionId)) sql += " and subject.section.id = '" + sectionId + "' ";
		sql += " and subject.subject.id = '" + subject.getId() + "' " +
				"order by s.student.matricNo";
		
		//System.out.println(sql);
		
		List<StudentSubject> list = db.list(sql);
		
		List<List> rows =  new ArrayList<List>();
		for ( StudentSubject s : list ) {
			
			Hashtable h = getAssessmentResults(s.getStudentStatus(), subject, sessionId, centreId);
			List<AssessmentResult> results = (List<AssessmentResult>) h.get("assessment_results");
			String status = (String) h.get("result_status");
			FinalSubjectResult finalResult = (FinalSubjectResult) h.get("final_result");
			
			List cols = new ArrayList();	
			cols.add(s.getStudentStatus().getStudent().getMatricNo());
			cols.add(s.getStudentStatus().getStudent().getBiodata().getName());
			for ( CourseworkItem i : markItems ) {
				if ( i != null ) {
					for ( AssessmentResult r : results ) {
						if ( r.getCourseworkItem() != null ) {
							System.out.println("r = " + r.getCourseworkItem().getCode());
							System.out.println("i = " + i);
							if ( r.getCourseworkItem().getCode().equals(i.getCode())) {
								cols.add(r.getMarks());
								break;
							}
						}
					}
				}
			}
			
			if ( finalResult != null ) {
				String totalMark = "";
				int iTotalMark = 0;
				double dTotalMark = 0.0;
				
				if ( roundType == 0 ) {
					
					totalMark = numFormat.format(finalResult.getTotalMark());
					dTotalMark = Double.parseDouble(totalMark);
					cols.add(dTotalMark);
					
				} else {
					
					totalMark = resultFormat.format(finalResult.getTotalMark());
					iTotalMark = Integer.parseInt(totalMark);
					cols.add(iTotalMark);
					
				}

				cols.add(finalResult.getGrade());
			}
			else {
				cols.add("");
				cols.add("");
			}
			if ( status != null ) 
				cols.add(status);
			else
				cols.add("");
			
			rows.add(cols);
		}
		return rows;
		
	}
	
	private Hashtable getAssessmentResults(StudentStatus studentStatus, Subject subject, String sessionId, String centreId) throws Exception {
		List<AssessmentResult> ars = new ArrayList<AssessmentResult>();
		String sql = "";
		SubjectResult subjectResult = null;
		//get list of subjects
		sql = "select su from ExamResult e JOIN e.sessions sr JOIN sr.subjects su " +
				"where sr.session.id = '" + studentStatus.getSession().getId() + "' " +
				"and su.subject.id = '" + subject.getId() + "' " +
				"and e.student.id = '" + studentStatus.getStudent().getId() + "' " +
				"and e.student.learningCenter.id = '" + centreId + "'";
		List<SubjectResult> results = db.list(sql);
		
		if ( results.size() == 0 ) {
			//need to create exam result for this student
			subjectResult = createSubjectResult(studentStatus.getStudent(), studentStatus.getSession(), studentStatus.getPeriod(), subject);
		}
		else {
			subjectResult = results.get(0);
		}
		
		if ( subjectResult != null ) {
			List<CourseworkItem> courseworkItems = getCourseworkItem(subject.getId(), sessionId, centreId);
			Set<AssessmentResult> assessments = subjectResult.getAssessments();
			createAssessmentResults(subjectResult, courseworkItems, assessments);
			
			assessments = subjectResult.getAssessments();
			//show all assessments
			for ( AssessmentResult ar : assessments ) {
				if ( ar.getCourseworkItem() != null ) {
					ars.add(ar);
				}
			}
			Collections.sort(ars, new AssessmentSequenceComparator());
		}
		
		
		sql = "select s from FinalResult r join r.subjects s" +
		" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
		" and r.session.id = '" + studentStatus.getSession().getId() + "' " +
		" and r.period.id = '" + studentStatus.getPeriod().getId() + "' " +
		" and s.subject.id = '" + subject.getId() + "'";
		FinalSubjectResult result = (FinalSubjectResult) db.get(sql);
		if ( result != null && result.getResultStatus() != null ) System.out.println(result.getResultStatus().getCode());
		
		Hashtable h = new Hashtable();
		h.put("assessment_results", ars);
		
		if ( result != null && result.getResultStatus() != null ) 
			h.put("result_status", result.getResultStatus().getName());
		else 
			h.put("result_status", "");
		
		if ( result != null ) h.put("final_result", result);

		return h;

	}
	
	private void createAssessmentResults(SubjectResult subjectResult, List<CourseworkItem> courseworkItems, Set<AssessmentResult> assessments) throws Exception {
		if ( courseworkItems.size() > 0 ){
			//look for assessmentResult
			
			{
				boolean f = false;
				for ( CourseworkItem cw : courseworkItems ) {
					f = false;
					for ( AssessmentResult ar : assessments ) {
						if ( ar.getCourseworkItem() != null && ar.getCourseworkItem().getId().equals(cw.getId())) {
							f = true;
							break;
						}
					}
					if ( !f ) {
						db.begin();
						AssessmentResult ar = new AssessmentResult();
						ar.setCourseworkItem(cw);
						ar.setSubjectResult(subjectResult);
						ar.setMarks(0.0d);
						subjectResult.addAssessment(ar);
						db.persist(ar);
						db.commit();	
					}
				}

			}
		}
	}
	
	private SessionResult createSessionResult(Student student, Session session, Period period) throws Exception {
		String sql;
		ExamResult examResult;
		SessionResult sessionResult;
		//check for examResult
		sql = "select e from ExamResult e " +
		"where e.student.id = '" + student.getId() + "'";
		List<ExamResult> exams = db.list(sql);
		if ( exams.size() == 0 ) { //examResult not exist
			//System.out.println("examResult not exist");
			db.begin();
			examResult = new ExamResult();
			examResult.setStudent(student);
			db.persist(examResult);
			db.commit();
		}
		else {
			examResult = exams.get(0); //there is only one examResult per student
			//System.out.println("ExamResult EXITS.. " + examResult.getStudent().getBiodata().getName());
		}
		
		db.begin();
		sessionResult = new SessionResult();
		sessionResult.setResult(examResult);
		sessionResult.setSession(session);
		sessionResult.setPeriod(period);
		examResult.addSession(sessionResult);
		db.commit();
		
		return sessionResult;
	}
	
	
	
	private SubjectResult createSubjectResult(Student student, Session session, Period period, Subject subject) throws Exception {
		String sql;
		SubjectResult subjectResult;
		SessionResult sessionResult;
		//check for sessionResult
		sql = "select sr from ExamResult e JOIN e.sessions sr " +
		"where sr.session.id = '" + session.getId() + "' " +
		"and e.student.id = '" + student.getId() + "'";
		List<SessionResult> sessions = db.list(sql);
		if ( sessions.size() == 0 ) { //sessionResult not exist
			//System.out.println("sessionResult not exist");
			sessionResult = createSessionResult(student, session, period);
		}
		else {
			sessionResult = sessions.get(0);
		}
		
		
		db.begin();
		subjectResult = new SubjectResult();
		subjectResult.setSession(sessionResult);
		subjectResult.setSubject(subject);
		sessionResult.addSubject(subjectResult);
		db.commit();
		
		return subjectResult;
	}
	

	
	static class AssessmentSequenceComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			AssessmentResult r1 = (AssessmentResult) o1;
			AssessmentResult r2 = (AssessmentResult) o2;
			return r1.getCourseworkItem().getSequence() > r2.getCourseworkItem().getSequence() ? 1 : -1;
		}
		
	}


}
