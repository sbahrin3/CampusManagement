package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import educate.db.DbPersistence;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.MarkingGrade;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SubjectResultAnalysisModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/exam_subject_analysis";
	protected String teacherId = "";
	
	public void preProcess() {
		System.out.println(command);
		context.put("path", path);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#.00"));
		context.remove("print_mode");
	}

	@Override
	public String start() {
		if ( "".equals(teacherId))
			context.put("centres", db.list("select c from LearningCentre c order by c.code"));
		else
			context.put("centres", db.list("select c from educate.sis.struct.entity.Teacher t join t.centres c where t.userId = '" + teacherId + "' order by c.code"));

		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		
		Session currentSession;
		try {
			currentSession = new StudentStatusUtil().getCurrentSession(0);
			context.put("current_session", currentSession);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//params();
		context.put("sessions", db.list("select x from Session x ORDER BY x.pathNo, x.startDate"));
		return path + "/start.vm";
	}

	@Command("listSessions")
	public String listSessions() throws Exception {
		String programId = getParam("program_id");
		Program program = db.find(Program.class, programId);
		int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
		context.put("sessions", db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate"));
		
		Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
		context.put("current_session", currentSession);

		return path + "/divSessions.vm";
	}
	
	@Command("listSubjects")
	public String listSubjects() {
		String centreId = getParam("centreId");
		String sessionId = getParam("session_id");
		String programId = getParam("program_id");
		//String sql = "select s from StudentSubject ss join ss.subject s where ss.studentStatus.session.id = '" + sessionId + "' and ss.studentStatus.student.learningCenter.id = '" + centreId + "' group by s.code";
		
		String sql = "select r.subject from FinalSubjectResult r join r.parent f " +
				"where f.session.id = '" + sessionId + "' " +  
				"and f.student.learningCenter.id = '" + centreId + "' " +
				"and f.student.program.id = '" + programId + "' " +
				"group by r.subject.code";
		
		
		List<Subject> subjects = db.list(sql);
		context.put("subjects", subjects);
		return path + "/divListSubjects.vm";
	}	
	
	@Command("listStudents")
	public String listStudents() throws Exception {
		
		context.remove("section");
		
		String centreId = getParam("centreId");
		String programId = getParam("program_id");
		String subjectId = getParam("subject_id");
		String sessionId = getParam("session_id");
		String intakeId = getParam("intake_id");
		
		Subject subject = null;
		SubjectSection section = null;
		subject = db.find(Subject.class, subjectId);	
		context.put("subject", subject);
		context.put("subject_id", subject.getId());
		
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		MarkingGrade marking = subject.getMarkingGrade();
		
		if ( marking != null ) {
			context.put("marking", marking);
			Set<Grade> grades = marking.getGrades();
			List<Grade> gradeList = new ArrayList<Grade>();
			gradeList.addAll(grades);
			Collections.sort(gradeList, new GradeComparator());
			context.put("grades", gradeList);
	
			String sectionId = section != null ? section.getId() : "";
			context.put("section_id", sectionId);
			
			Session session = db.find(Session.class, sessionId);
			context.put("session", session);
			listStudents(subject, centreId, programId, sessionId, intakeId, sectionId, gradeList);
			return path + "/listResults.vm";
		}
		return path + "/resultsNotAvailable.vm";
	}	

	private void listStudents(Subject subject, String centreId, String sessionId, String intakeId, String sectionId, List<Grade> gradeList) throws Exception {
		listStudents(subject, centreId, null, sessionId, intakeId, sectionId, gradeList);
	}

	
	private void listStudents(Subject subject, String centreId, String programId, String sessionId, String intakeId, String sectionId, List<Grade> gradeList) throws Exception {
		
		context.put("session_id", sessionId);
		context.put("intake_id", intakeId);
		context.put("subject_id", subject.getId());
		context.put("section_id", sectionId);
		context.put("program_id", programId);
		
		String sql = "select r from FinalSubjectResult r join r.parent f " +
				"where f.session.id = '" + sessionId + "' " +  
				"and r.subject.id = '" + subject.getId() + "' " +
				"and f.student.learningCenter.id = '" + centreId + "' ";
		if ( programId != null ) {
			sql = sql + " and f.student.program.id = '" + programId + "' ";
		}
		sql = sql + " order by f.student.matricNo";
	
		List<FinalSubjectResult> results = db.list(sql);
		context.put("results", results);
		
		//statistic calculation
		double highMark = 0.0;		
		double lowMark = 0.0;	 	
		double averageMark = 0.0;	
		double sdMark = 0.0;		
		double totMark = 0.0;
		double medMark = 0.0;
		
		double highPoint = 0.0;		
		double lowPoint = 0.0;		
		double averagePoint = 0.0;	
		double sdPoint = 0.0;		
		double totPoint = 0.0;
		double medPoint = 0.0;
		
		List<Double> marks = new ArrayList<Double>();
		List<Double> points = new ArrayList<Double>();
		
		Map<String, Integer> gradeFreqs = new HashMap<String, Integer>();
		String grade = "";
		int cnt = 0;
		int totalGradeCount = 0;
		for ( FinalSubjectResult r : results ) {
			
			double cgpa = r.getParent().getCgpa();
			marks.add(r.getTotalMark());
			points.add(r.getPoint());
			
			cnt++;
			if ( cnt == 1 ) {
				lowMark = r.getTotalMark();
				lowPoint = r.getPoint();
			}
			highMark = r.getTotalMark() > highMark ? r.getTotalMark() : highMark;
			highPoint = r.getPoint() > highPoint ? r.getPoint() : highPoint;
			lowMark = r.getTotalMark() < lowMark ? r.getTotalMark() : lowMark;
			lowPoint = r.getPoint() < lowPoint ? r.getPoint() : lowPoint;
			
			grade = r.getGrade();
			Integer freq = gradeFreqs.get(grade);
			gradeFreqs.put(grade, freq == null ? 1 : freq.intValue() + 1);
			if ( grade != null ) totalGradeCount++;
		}
		
		context.put("totalGradeCount", totalGradeCount);
		
		averageMark = mean(marks);
		averagePoint = mean(points);
		medMark = median(marks);
		medPoint = median(points);
		sdMark = sd(marks);
		sdPoint = sd(points);
		
		
		context.put("highMark", highMark);
		context.put("lowMark", lowMark);
		context.put("averageMark", averageMark);
		context.put("sdMark", sdMark);	
		context.put("medMark", medMark);
		
		context.put("highPoint", highPoint);
		context.put("lowPoint", lowPoint);
		context.put("averagePoint", averagePoint);
		context.put("sdPoint", sdPoint);
		context.put("medPoint", medPoint);
		
		context.put("gradeFreqs", gradeFreqs);
		
		Map<String, Double> gradePct = new HashMap<String, Double>();
		context.put("gradePct", gradePct);
		for ( Grade g : gradeList ) {
			if ( gradeFreqs.get(g.getLetter()) != null ) {
				gradePct.put(g.getLetter(), ((double) gradeFreqs.get(g.getLetter())/(double) totalGradeCount)*100);
			}
		}
		
		
	}
	
	
	private void orderBy(String by, String sessionId, String subjectId, String centreId) {
		String sql = "select r from FinalSubjectResult r join r.parent f " +
				"where f.session.id = '" + sessionId + "' " +  
				"and r.subject.id = '" + subjectId + "' " +
				"and f.student.learningCenter.id = '" + centreId + "' ";
		if ( "matricNo".equals(by))
				sql += "order by f.student.matricNo";
		else if ( "name".equals(by))
			sql += "order by f.student.biodata.name";
		else if ( "grade".equals(by))
			sql += "order by r.totalMark desc";
		else if ( "cgpa".equals(by))
			sql += "order by r.parent.cgpa desc";
		else
			sql += "order by f.student.matricNo";

	
		List<FinalSubjectResult> results = db.list(sql);
		context.put("results", results);
	}
	
	@Command("orderResultBy")
	public String orderResultBy() throws Exception {
		String centreId = getParam("centreId");
		String subjectId = getParam("subject_id");
		String sessionId = getParam("session_id");
		
		String by = getParam("by");
		
		orderBy(by, sessionId, subjectId, centreId);
		
		return path + "/listStudentResult.vm";
	}
	
	@Command("filterCgpa")
	public String filterCgpa() throws Exception {
		String centreId = getParam("centreId");
		String subjectId = getParam("subject_id");
		String sessionId = getParam("session_id");
		
		String cgpa = getParam("cgpa");
		
		filterCgpa(cgpa, sessionId, subjectId, centreId);
		
		return path + "/listStudentResult.vm";
	}
	
	private void filterCgpa(String cgpa, String sessionId, String subjectId, String centreId) {
		String sql = "select r from FinalSubjectResult r join r.parent f " +
				"where f.session.id = '" + sessionId + "' " +  
				"and r.subject.id = '" + subjectId + "' " +
				"and f.student.learningCenter.id = '" + centreId + "' ";
		
				sql += " and r.parent.cgpa < " + cgpa;
				sql += " order by r.parent.cgpa desc";
		

	
		List<FinalSubjectResult> results = db.list(sql);
		context.put("results", results);
	}
	
    private double sum(List<Double> a){
        if (a.size() > 0) {
            double sum = 0;

            for (Double i : a) {
                sum += i;
            }
            return sum;
        }
        return 0;
    }
	
    private double mean(List<Double> a){
    	if ( a.size() == 0 ) return 0;
        double sum = sum(a);
        double mean = 0;
        mean = sum / (a.size() * 1.0);
        return mean;
    }
    
    private double median(List<Double> a){
    	if ( a.size() == 0 ) return 0;
        int middle = a.size()/2;
        if (a.size() % 2 == 1) {
            return a.get(middle);
        } else {
           return (a.get(middle-1) + a.get(middle)) / 2.0;
        }
    }  
    
    private double sd(List<Double> a){
    	if ( a.size() - 1 == 0 ) return 0;
        int sum = 0;
        double mean = mean(a);
 
        for (Double i : a)
            sum += Math.pow((i - mean), 2);
        return Math.sqrt( sum / ( a.size() - 1 ) );
    }    
    
	static class GradeComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			Grade g1 = (Grade) o1;
			Grade g2 = (Grade) o2;
			if ( g1.getMinMark() > g2.getMinMark() ) return -1;
			else if ( g1.getMinMark() < g2.getMinMark() ) return 1;
			return 0;
		}
		
	}
	


}
