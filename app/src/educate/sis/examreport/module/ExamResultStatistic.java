package educate.sis.examreport.module;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import educate.db.DbPersistence;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.Grade;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.Session;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 *
 * @version 1.0
 */
public class ExamResultStatistic {
	
	private DbPersistence db = new DbPersistence();
	private int total;
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	public List<GradeResult> listResultByCGPA(String intakeId, String periodId, String facultyId, double cgpa1, double cgpa2) {
		String sql = "select new educate.sis.exam.module.GradeResult(s.biodata.name, s.matricNo, r.cgpa) " +
		"from FinalResult r join r.student s " +
		"where r.student.program.course.faculty.id = '" + facultyId + "' " +
		"and r.period.id = '" + periodId + "' " +
		"and r.student.intake.id = '" + intakeId + "' " +
		"and r.cgpa > " + cgpa1 + " and r.cgpa <= " + cgpa2;
		sql += " order by s.biodata.name";
		List<GradeResult> results = db.list(sql);
		return results;
	}

	public List<GradeResult> getCGPASummary(String intakeId, String periodId, String facultyId) {
		String sql = "select r from FinalResult r where r.student.program.course.faculty.id = '" + facultyId + "' " +
				"and r.period.id = '" + periodId + "' " +
				"and r.student.intake.id = '" + intakeId + "' ";
		List<FinalResult> results = db.list(sql);

		Hashtable<Integer, Integer> r = new Hashtable<Integer, Integer>();
		for ( FinalResult result : results ) {
			
			if ( r.get(getGradeGroup(result.getCgpa())) == null ) {
				r.put(getGradeGroup(result.getCgpa()), new Integer(1));
			}
			else {
				int i = r.get(getGradeGroup(result.getCgpa())) + 1;
				r.put(getGradeGroup(result.getCgpa()), i);
			}
		}
		
		List<GradeResult> gradeResults = new ArrayList<GradeResult>();
		
		for ( int group = 1; group < 6; group++ ) {
			int cnt = r.get(group) != null ? r.get(group) : 0;
			gradeResults.add(new GradeResult(group, cnt));
		}
		//calculate percentage
		total = 0;
		for ( GradeResult gr : gradeResults ) total += gr.getCount();
		for ( GradeResult gr : gradeResults ) gr.setPercent(((double)gr.getCount())/(double)total*100);
		return gradeResults;
	}
	
	private int getGradeGroup(double grade) {
		if ( grade > 3.66 ) return 1;
		else if ( grade > 2.99 ) return 2;
		else if ( grade > 1.99 ) return 3;
		else if ( grade > 0.99 ) return 4;
		else return 5;
	}
	
	public List<GradeResult> listResultByGrade(String intakeId, String periodId, String subjectId, String grade) {
		String sql = "select new educate.sis.exam.module.GradeResult(s.biodata.name, s.matricNo, r.grade, r.totalMark)" +
		" from FinalSubjectResult r " +
		"join r.parent.student s" +
		" where r.subject.id = '" + subjectId + "' " +
		"and r.parent.period.id = '" + periodId + "' " +
		"and r.parent.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(grade)) sql += "and r.grade = '" + grade + "' ";
		sql += "order by s.biodata.name";
		List<GradeResult> students = db.list(sql);
		return students;
		
	}
	
	public List<GradeResult> getGradeSummary(String intakeId, String periodId, String subjectId) {
		String sql = "select r from FinalSubjectResult r where r.subject.id = '" + subjectId + "' " +
				"and r.parent.period.id = '" + periodId + "' " +
				"and r.parent.student.intake.id = '" + intakeId + "' ";
		List<FinalSubjectResult> results = db.list(sql);
		
		Hashtable<String, Integer> r = new Hashtable<String, Integer>();
		for ( FinalSubjectResult result : results ) {
			if ( r.get(result.getGrade()) == null ) {
				r.put(result.getGrade(), new Integer(1));
			}
			else {
				int i = r.get(result.getGrade()) + 1;
				r.put(result.getGrade(), i);
			}
		}
		
		List<GradeResult> gradeResults = new ArrayList<GradeResult>();
		List<Grade> grades = db.list("select g from Grade g order by g.point desc");  //NOTE: multiple MarkingGrades available
		for ( Grade grade : grades ) {
			int cnt = r.get(grade.getLetter()) != null ? r.get(grade.getLetter()) : 0;
			boolean found = false;
			for ( GradeResult gr : gradeResults ) {
				if ( grade.getLetter().equals(gr.grade)) {
					found = true; break;
				}
			}
			if ( !found ) {
				gradeResults.add(new GradeResult(grade.getLetter(), cnt));
			}
		}
		//calculate percentage
		total = 0;
		for ( GradeResult gr : gradeResults ) total += gr.getCount();
		for ( GradeResult gr : gradeResults ) gr.setPercent(((double)gr.getCount())/(double)total*100);
		return gradeResults;
	}
	
	public void getSessionByPeriod(String periodSchemeId, String periodId, String intakeId) {
		Period period = db.find(Period.class, periodId);
		PeriodScheme periodScheme = db.find(PeriodScheme.class, periodSchemeId);

		Session intake = db.find(Session.class, intakeId);
		
		Vector<Period> periodList = new Vector<Period>();
		//get all period
		List<Period> periods = periodScheme.getFunctionalPeriodList();
		for ( Iterator<Period> it = periods.iterator(); it.hasNext(); ) {
			Period p = it.next();
			periodList.add(p);
		}
		
		Hashtable<String, Object> h = new Hashtable<String, Object>();
		h.put("dateStart", intake.getStartDate());
		Vector<Session> sessionList = new Vector<Session>();
		List<Session> sessions = db.list("select s from Session s where s.startDate >= :dateStart order by s.startDate", h); 
		for ( Iterator<Session> it = sessions.iterator(); it.hasNext(); ) {
			Session session = it.next();
			sessionList.add(session);
		}
		
		//match period with session
		Hashtable<Period, Session> ps = new Hashtable<Period, Session>();
		int i = 0;
		for ( Period p : periodList ) {
			Session s = sessionList.elementAt(i);
			ps.put(p, s);
			i++;
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		String periodSchemeId="1232016147875";
		String periodId="1232016147887";
		String intakeId="1232073136312";
		String subjectId="AA2192";
		String grade = "A";
		

	}

}
