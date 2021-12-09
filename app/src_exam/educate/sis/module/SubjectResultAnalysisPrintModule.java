package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.MarkingGrade;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import educate.sis.struct.entity.TeacherSubject;
import lebah.portal.action.Command;
import lebah.portal.velocity.VTemplate;

public class SubjectResultAnalysisPrintModule extends VTemplate {
	
	private String path = "apps/exam_subject_analysis";
	DbPersistence db = new DbPersistence();
	private String teacherId = "";
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		context.put("path", path);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#.00"));
		Template template = engine.getTemplate(getResultAnalysis());	
		return template;		
	}

	private String getResultAnalysis() throws Exception {
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
        String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
        String serverUrl = http + server;
        context.put("serverUrl", serverUrl);
        context.put("print_mode", true);
        
        listStudents();
        
		return path + "/printResults.vm";
	}
	
	@Command("listStudents")
	public String listStudents() throws Exception {
		
		context.remove("section");
		
		String subjectId = request.getParameter("subject_id");
		String sessionId = request.getParameter("session_id");
		String intakeId = request.getParameter("intake_id");
		String programId = request.getParameter("program_id");
		
		Program program = null;
		program = db.find(Program.class, programId);
		context.put("program", program);
		context.put("program_id", programId);
		
		Subject subject = null;
		SubjectSection section = null;
		if ( "".equals(teacherId)) {
			subject = db.find(Subject.class, subjectId);	
		}
		else {
			TeacherSubject teacherSubject = db.find(TeacherSubject.class, subjectId);
			subject = teacherSubject.getSubject();
			section = teacherSubject.getSection();
			context.put("section", section);
			subjectId = subject.getId();
		}
		context.put("subject", subject);
		context.put("subject_id", subject.getId());
		
		MarkingGrade marking = subject.getMarkingGrade();
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
		listStudents(subject, programId, sessionId, intakeId, sectionId, gradeList);
		
		return path + "/printResults.vm";
	}	
	
	private void listStudents(Subject subject, String programId, String sessionId, String intakeId, String sectionId, List<Grade> gradeList) throws Exception {
		
		context.put("session_id", sessionId);
		context.put("intake_id", intakeId);
		context.put("subject_id", subject.getId());
		context.put("section_id", sectionId);
		
		String sql = "select r from FinalSubjectResult r join r.parent f " +
				"where f.session.id = '" + sessionId + "' and r.subject.id = '" + subject.getId() + "' ";
		if ( programId != null && !"".equals(programId)) {
			sql += " and f.student.program.id = '" + programId + "' ";
		}
		sql += "order by f.student.matricNo";
		
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
        double sum = sum(a);
        double mean = 0;
        mean = sum / (a.size() * 1.0);
        return mean;
    }
    
    private double median(List<Double> a){
        int middle = a.size()/2;
        if (a.size() % 2 == 1) {
            return a.get(middle);
        } else {
           return (a.get(middle-1) + a.get(middle)) / 2.0;
        }
    }  
    
    private double sd(List<Double> a){
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
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		String sql = "select r from FinalSubjectResult r join r.parent f where f.session.id = '1401960688645' and r.subject.id = '1410934218180' order by f.student.matricNo";
		List<FinalSubjectResult> list = db.list(sql);
		System.out.println(list.size());
	}
		


}
