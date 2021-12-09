package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.exam.entity.AchievementLevel;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.MarkingGrade;
import educate.sis.exam.entity.Standing;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SubjectResultAnalysisListModule  extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/exam_subject_analysis2";
	protected String teacherId = "";

	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#.00"));
		context.put("number", new DecimalFormat("0"));
		context.remove("print_mode");
		
		System.out.println(command);
	}
	
	@Override
	public String start() {
		if ( "".equals(teacherId))
			context.put("centres", db.list("select c from LearningCentre c order by c.code"));
		else
			context.put("centres", db.list("select c from educate.sis.struct.entity.Teacher t join t.centres c where t.userId = '" + teacherId + "' order by c.code"));

		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		return path + "/start.vm";
	}
	
	
	
	@Command("listSessions")
	public String listSessions() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		int pathNo = program.getLevel().getPathNo();
		
		String intakeId = getParam("intakeId");
		if ( "".equals(intakeId)) {
			List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
			context.put("sessions", intakes);
		} else {
			Session intake = db.find(Session.class, intakeId);
			Date startDate = intake.getStartDate();
			Hashtable h = new Hashtable();
			h.put("startDate", startDate);
			List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " and s.startDate >= :startDate order by s.startDate", h);
			context.put("sessions", intakes);
		}
		
		return path + "/listSessions.vm";
	}
	
	@Command("listIntakes")
	public String listIntakes() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		int pathNo = program.getLevel().getPathNo();
		
		List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("intakes", intakes);
		
		return path + "/listIntakes.vm";
	}
	
	@Command("listStudents")
	public String listStudents() throws Exception {
		String centreId = getParam("centreId");
		String programId = getParam("programId");
		String sessionId = getParam("sessionId");
		String intakeId = getParam("intakeId");
		String cgpaRange = getParam("cgpaRange");
		
		Program program = db.find(Program.class, programId);
		Session session = db.find(Session.class, sessionId);
		context.put("program", program);
		context.put("session", session);

		
		/*
		String sql = "select s from Student s join s.status st where st.session.id = '" + sessionId + "' " +
		" and s.intake.id = '" + intakeId + "' " +
		" and s.program.id = '" + programId + "'" + 
		" and s.learningCenter.id = '" + centreId + "'" +
		" and st.type.inActive = 0 " +
		" order by s.biodata.name";
		*/
		
		//String sql = "select s from FinalResult fr join fr.student s join s.status st where st.session.id = '" + sessionId + "' " +
		String sql = "select s from FinalResult fr join fr.student s join s.status st where st.session.id = fr.session.id " +
				" and s.intake.id = '" + intakeId + "' " +
				" and s.program.id = '" + programId + "'" + 
				" and s.learningCenter.id = '" + centreId + "'";
				if ( !"".equals(sessionId) ) sql += " and fr.session.id = '" + sessionId + "'";
				sql += " and st.type.inActive = 0 ";
		if ( "1".equals(cgpaRange)) {
			sql += " and fr.cgpa < 2 ";
		} else if ( "2".equals(cgpaRange)) {
			sql += " and (fr.cgpa >= 2 and fr.cgpa < 3) ";
		} else if ( "3".equals(cgpaRange)) {
			sql += " and (fr.cgpa >= 3 and fr.cgpa < 3.5) ";
		} else if ( "4".equals(cgpaRange)) {
			sql += " and (fr.cgpa >= 3.5) ";
		}
		sql += " group by s.id ";

		
		List<Student> students = db.list(sql);
		List<Student> list = new ArrayList<Student>();
		list.addAll(students);
		Collections.sort(list, new ByNameComparator());
				
		context.put("students", list);
		context.put("totalStudentCount", students.size());
		
		String sql2 = "select r from FinalSubjectResult r " +
		"where r.parent.student.program.id = '" + programId + "' " +
		"and r.parent.student.intake.id = '" + intakeId + "' " +
		"and r.parent.student.learningCenter.id = '" + centreId + "' ";
		if ( !"".equals(sessionId) ) sql2 += "and r.parent.session.id = '" + sessionId + "' ";
		
		
		Set<Subject> subjectList = new HashSet<Subject>();
		context.put("subjects", subjectList);
		
		Map<String, SubjectResultData> subjectMap = new HashMap<String, SubjectResultData>();
		context.put("subjectMap", subjectMap);
		
		List<FinalSubjectResult> subjects = db.list(sql2);
		for ( FinalSubjectResult r : subjects ) {
			if ( r.getSubject() != null ) {
				MarkingGrade marking = r.getSubject().getMarkingGrade();
				if ( marking != null ) {
					subjectList.add(r.getSubject());
					SubjectResultData data = new SubjectResultData(r.getTotalMark(), r.getPoint(), r.getGrade());
					subjectMap.put(r.getSubject().getId() + "_" + r.getParent().getStudent().getId(), data);
				}
			}
		}

		//XLS
		request.getSession().setAttribute("students", students);
		request.getSession().setAttribute("subjects", subjectList);
		request.getSession().setAttribute("subjectMap", subjectMap);
		
		return path + "/listStudents1.vm";
	}
	
	@Command("listStudents2")
	public String listStudents2() throws Exception {
		String centreId = getParam("centreId");
		String programId = getParam("programId");
		String sessionId = getParam("sessionId");
		String intakeId = getParam("intakeId");
		String cgpaRange = getParam("cgpaRange");
		
		Program program = db.find(Program.class, programId);   
		Session session = db.find(Session.class, sessionId);
		context.put("program", program);
		context.put("session", session);
		
		/*
		String sql = "select s from Student s join s.status st where st.session.id = '" + sessionId + 
		"' and s.program.id = '" + programId + "'" + 
		" and s.intake.id = '" + intakeId + "' " +
		" and s.learningCenter.id = '" + centreId + "'" +
		" and st.type.inActive = 0 " +
		" order by s.biodata.name";
		*/
		String sql = "select s from FinalResult fr join fr.student s join s.status st where st.session.id = fr.session.id " +
		" and s.program.id = '" + programId + "'" + 
		" and s.intake.id = '" + intakeId + "' " +
		" and s.learningCenter.id = '" + centreId + "'";
		
		if ( !"".equals(sessionId)) sql += " and fr.session.id = '" + sessionId + "'";
		
		sql += " and st.type.inActive = 0 ";
		if ( "1".equals(cgpaRange)) {
			sql += " and fr.cgpa < 2 ";
		} else if ( "2".equals(cgpaRange)) {
			sql += " and (fr.cgpa >= 2 and fr.cgpa < 3) ";
		} else if ( "3".equals(cgpaRange)) {
			sql += " and (fr.cgpa >= 3 and fr.cgpa < 3.5) ";
		} else if ( "4".equals(cgpaRange)) {
			sql += " and (fr.cgpa >= 3.5) ";
		}
		sql += " group by s.id";
		
		
		List<Student> students = db.list(sql);
		List<Student> list = new ArrayList<Student>();
		list.addAll(students);
		Collections.sort(list, new ByNameComparator());
		
		context.put("students", list);
		context.put("totalStudentCount", students.size());
		
		
		String sql2 = "select r from FinalResult r " +
		"where r.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(sessionId)) sql2 += "and r.session.id = '" + sessionId + "' ";
		sql2 += "and r.student.program.id = '" + programId + "' and r.student.learningCenter.id = '" + centreId + "' order by r.student.biodata.name";
		
		Map<String, ExamResultData> resultMap = new HashMap<String, ExamResultData>();
		context.put("resultMap", resultMap);
		
		List<FinalResult> results = db.list(sql2);
		
		Map<String, Integer> gpaFreq = new HashMap<String, Integer>();
		context.put("gpaFreq", gpaFreq);
		
		
		gpaFreq.put("x >= 3.75", 0);
		gpaFreq.put("3.5 <= x > 3.75", 0);
		gpaFreq.put("3.0 <= x > 3.5", 0);
		gpaFreq.put("2.5 <= x > 3.0", 0);
		gpaFreq.put("2.0 <= x > 2.5", 0);
		gpaFreq.put("x > 2.0", 0);
		
		Map<String, Integer> standingFreqs = new HashMap<String, Integer>();
		context.put("standingFreqs", standingFreqs);
		
		Map<String, Integer> levelFreqs = new HashMap<String, Integer>();
		context.put("levelFreqs", levelFreqs);
		
		int totalResultCount = 0;
		for ( FinalResult r : results ) {
			ExamResultData data = new ExamResultData();
			data.setCgpa(r.getCgpa());
			data.setGpa(r.getGpa());
			data.setGradePoints(r.getCurrentPoints());
			data.setCreditHours(r.getCurrentHours());
			data.setStatus(r.getStanding());
			data.setLevel(r.getAchievementLevel() != null ? r.getAchievementLevel().getName() : "");
			resultMap.put(r.getStudent().getId(), data);
			
			if ( r.getGpa() >= 3.75 ) {
				int i = gpaFreq.get("x >= 3.75");
				gpaFreq.put("x >= 3.75", i + 1);
				totalResultCount++;
			} else if ( r.getCgpa() >= 3.5 && r.getCgpa() < 3.75 ) {
				int i = gpaFreq.get("3.5 <= x > 3.75");
				gpaFreq.put("3.5 <= x > 3.75", i + 1);
				totalResultCount++;
			} else if ( r.getCgpa() >= 3.0 && r.getCgpa() < 3.5 ) {
				int i = gpaFreq.get("3.0 <= x > 3.5");
				gpaFreq.put("3.0 <= x > 3.5", i + 1);
				totalResultCount++;
			} else if ( r.getCgpa() >= 2.5 && r.getCgpa() < 3.0 ) {
				int i = gpaFreq.get("2.5 <= x > 3.0");
				gpaFreq.put("2.5 <= x > 3.0", i + 1);
				totalResultCount++;
			} else if ( r.getCgpa() >= 2.0 && r.getCgpa() < 2.5 ) {
				int i = gpaFreq.get("2.0 <= x > 2.5");
				gpaFreq.put("2.0 <= x > 2.5", i + 1);
			} else if ( r.getCgpa() < 2.0 ) {
				totalResultCount++;
				int i = gpaFreq.get("x > 2.0");
				gpaFreq.put("x > 2.0", i + 1);
				totalResultCount++;
			}
			
			r.getStanding();
			
			Integer freq = standingFreqs.get(r.getStanding());
			standingFreqs.put(r.getStanding(), freq == null ? 1 : freq.intValue() + 1);
			
			if ( r.getAchievementLevel() != null ) {
				Integer freq2 = levelFreqs.get(r.getAchievementLevel().getName());
				levelFreqs.put(r.getAchievementLevel().getName(), freq2 == null ? 1 : freq2.intValue() + 1);
			}
			else {
				Integer freq2 = levelFreqs.get("");
				levelFreqs.put("", freq2 == null ? 1 : freq2.intValue() + 1);
			}
		
		}
		context.put("totalResultCount", totalResultCount);
		List<Standing> standings = db.list("select s from Standing s order by s.sequence");
		context.put("standings", standings);
		
		List<AchievementLevel> levels = db.list("select a from AchievementLevel a where a.program.id = '" + programId + "' order by a.gpaValue desc");
		context.put("levels", levels);
		
		
		String gpaValues =	gpaFreq.get("x >= 3.75") + "," +
							gpaFreq.get("3.5 <= x > 3.75") + "," +
							gpaFreq.get("3.0 <= x > 3.5") + "," +
							gpaFreq.get("2.5 <= x > 3.0") + "," +
							gpaFreq.get("2.0 <= x > 2.5") + "," +
							gpaFreq.get("x > 2.0");
		context.put("gpaValues", gpaValues);
		
		context.put("pct", new Pct());
		
		//XLS
		request.getSession().setAttribute("resultMap", resultMap);
		request.getSession().setAttribute("gpaFreq", gpaFreq);
		request.getSession().setAttribute("standingFreqs", standingFreqs);
		request.getSession().setAttribute("standings", standings);
		
		return path + "/listStudents2.vm";
	}
	
	
	
	@Command("resultStat")
	public String resultStat() throws Exception {
		
		String displayType = getParam("displayType");
		
		String centreId = getParam("centreId");
		String subjectId = getParam("subjectId");
		String sessionId = getParam("sessionId");
		String programId = getParam("programId");
		String intakeId = getParam("intakeId");
		
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		MarkingGrade marking = subject.getMarkingGrade();
		if ( marking != null ) {
			context.put("marking", marking);
			Set<Grade> grades = marking.getGrades();
			List<Grade> gradeList = new ArrayList<Grade>();
			gradeList.addAll(grades);
			Collections.sort(gradeList, new GradeComparator());
			context.put("grades", gradeList);
			String sql = "select r from FinalSubjectResult r join r.parent f " +
					"where r.subject.id = '" + subject.getId() + "' " +
					"and f.student.program.id = '" + programId + "' and f.student.learningCenter.id = '" + centreId + "'";
			if ( !"".equals(intakeId)) sql += " and f.student.intake.id = '" + intakeId + "'";
			if ( !"".equals(sessionId)) sql += " and f.session.id = '" + sessionId + "'";
			
		
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

		return displayType.equals("1") ? path + "/resultStat.vm" : path + "/gradeFreq.vm";
		
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
    
	static class GradeComparator extends educate.util.MyComparator{

		public int compare(Object o1, Object o2) {
			Grade g1 = (Grade) o1;
			Grade g2 = (Grade) o2;
			if ( g1.getMinMark() > g2.getMinMark() ) return -1;
			else if ( g1.getMinMark() < g2.getMinMark() ) return 1;
			return 0;
		}
		
	}
	
	static class ByNameComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			Student s1 = (Student) o1;
			Student s2 = (Student) o2;
			return s1.getBiodata().getName().compareTo(s2.getBiodata().getName());
		}
		
	}
	


}
