package educate.sis.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.GroupResult;
import educate.sis.exam.entity.TranscriptEndorsedDate;
import educate.sis.exam.module.SpecialSubjectStatus;
import educate.sis.exam.module.SubjectStatus;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.SubjectGroup;

public class StudentExamTranscriptUtil {
	
	HttpServletRequest request;
	VelocityContext context;
	DbPersistence db;
	
	public StudentExamTranscriptUtil(HttpServletRequest request, VelocityContext context, DbPersistence db) {
		this.request = request;
		this.context = context;
		this.db = db;
	}
	
	private String getParam(String n) {
		return request.getParameter(n) != null ? request.getParameter(n) : "";
	}
	
	
	public void showExamTranscript(Student student) throws Exception {
		
		String twocolumns = getParam("twocolumns");
		context.put("twocolumns", "yes".equals(twocolumns) ? true : false);
		String summary = getParam("summary");
		context.put("summary", "yes".equals(summary) ? true : false);
		
		String hideF = getParam("hideF");
		context.put("hideF", hideF);
		
		SessionUtil u = new SessionUtil();
		Session currentSession = u.getCurrentSession(student.getProgram().getLevel().getPathNo());
		context.put("current_session", currentSession);
		
		context.put("student", student);
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		List<FinalResult> results = db.list(sql);
		System.out.println("results = " + results.size());
		if ( results.size() > 0 ) {
			context.put("results", results);
			listEndorsedDates(student.getProgram());
		}
		else {
			context.remove("results");
		}
		
		System.out.println("result = " + results.size());
		
		//create average transcript
		context.remove("groupResults");
		createGroupAverageTranscript(db, student, context);
		
		//student statuses
		Map<String, StudentStatus> studentStatusMap = new HashMap<String, StudentStatus>();
		context.put("statusMap", studentStatusMap);
		StudentStatusUtil statusUtil = new StudentStatusUtil();
		List<StudentStatus> studentStatusList = statusUtil.getStudentStatuses(student);
		for ( StudentStatus s : studentStatusList ) {
			if ( s.getSession() != null ) studentStatusMap.put(s.getSession().getId(), s);
		}
		
	}
	
	private void listEndorsedDates(Program program) {
		
		String programId = program.getId();
		int pathNo = program.getLevel().getPathNo();

		Map<String, TranscriptEndorsedDate> endorsedDateMap = new HashMap<String, TranscriptEndorsedDate>();
		context.put("endorsedDateMap", endorsedDateMap);
		List<TranscriptEndorsedDate> endorsedDates = db.list("select d from TranscriptEndorsedDate d where d.program.id = '" + programId + "' order by d.session.id");
		for ( TranscriptEndorsedDate d : endorsedDates ) {
			endorsedDateMap.put(d.getSession().getId(), d);
		}
	}	
	
	
	private static void createGroupAverageTranscript(DbPersistence db, Student student, VelocityContext context) {
		
		Map<String, Integer> specialStatusMap = new HashMap<String, Integer>();
		context.put("specialStatusMap", specialStatusMap);
		SubjectStatus[] statusList = SpecialSubjectStatus.getListOfStatus();
		for ( SubjectStatus subjectStatus : statusList ) {
			specialStatusMap.put(subjectStatus.getCode(), new Integer(0));
		}
		
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		Set<SubjectGroup> avgGroups = new HashSet<SubjectGroup>();
		List<FinalSubjectResult> avgSubjects = new ArrayList<FinalSubjectResult>();
		List<FinalResult> results = db.list(sql);
		if ( results.size() > 0 ) {
			for ( FinalResult r : results ) {
				List<FinalSubjectResult> subjectResults = r.getSubjects();
				for ( FinalSubjectResult sr : subjectResults ) {
					for ( SubjectStatus subjectStatus : statusList ) {
						if ( specialStatusMap.get(subjectStatus.getCode()) != null ) {
							
							if ( sr.getSubjectStatus() != null && sr.getSubjectStatus().equals(subjectStatus.getCode())) {
								int cnt = specialStatusMap.get(subjectStatus.getCode());
								cnt = cnt + sr.getSubject().getCredithrs();
								specialStatusMap.put(subjectStatus.getCode(), cnt);
							}
						}
					}					
					
					//CREATE SUBJECT GROUP HERE
					if ( sr.getSubject() != null ) {
						if ( sr.getSubject().getSubjectGroup() != null && sr.getSubject().getSubjectGroup().getCreateAverage() == 1 ) { 
							//search for repeat or resit
							boolean found = false;
							int index = 0;
							for ( FinalSubjectResult sr2 : avgSubjects ) {
								if ( sr2.getSubject().getId().equals(sr.getSubject().getId())) {
									found = true;
									//strategy better grade point
									if ( sr.getGradePoint() > sr2.getGradePoint()) {
										avgSubjects.remove(index);
									}
									break;
								}
								index++;
							}
							if ( !found ) {
								avgSubjects.add(sr);
								avgGroups.add(sr.getSubject().getSubjectGroup());
							}
						}
					}
				}
			}
		}
		
		
		//subject groups
		List<GroupResult> groupResults = new ArrayList<GroupResult>();
		if ( context != null ) context.put("groupResults", groupResults);
		double point = 0.0d;
		int creditHrs = 0;
		int counter = 0;
		for ( SubjectGroup g : avgGroups ) {
			
			if ( g.getMarkingGrade() != null ) {
				
				point = 0.0d;
				counter = 0;
				creditHrs = 0;
				for ( FinalSubjectResult sr : avgSubjects ) {
					if ( sr.getSubject().getSubjectGroup().getId().equals(g.getId())) {
						point += sr.getPoint();
						creditHrs += sr.getCreditHour();
						counter++;
						
						//System.out.println(sr.getSubject().getCode() + ", " + sr.getPoint() + ", " + sr.getCreditHour());
					}
				}
				
				double avgPoint = point/creditHrs;
				
				String markingId = g.getMarkingGrade().getId();
				Grade grade = ExamResultUtil.getGradeByPoint(markingId, avgPoint);
				
				//System.out.println(g.getName() + " = " + point + "/" + creditHrs + " = " + avgPoint + " = " + grade.getLetter());
				
				GroupResult gr = new GroupResult();
				gr.setGroupName(g.getName());
				gr.setCreditHours(creditHrs);
				gr.setPoint(avgPoint);
				gr.setGrade(grade != null ? grade.getLetter() : "");
				
				gr.setSequence(g.getSequence());
				
				gr.setAveragePoint(avgPoint);
				gr.setTotalPoint(point);
				
				groupResults.add(gr);
			
			}
			
		}
				
		//get best three group result
		Collections.sort(groupResults, new GradeComparator());
		int cnt = 0;
		double bestThreePoint = 0;
		for ( GroupResult gr : groupResults ) {
			cnt++;
			bestThreePoint += gr.getAveragePoint();
			if ( cnt == 3 ) break;
		}
		
		//reorder for display
		Collections.sort(groupResults, new GroupComparator());
		
		context.put("bestThreePoint", bestThreePoint);
		context.put("bestThreeCGPA", bestThreePoint/cnt);
	}
	
	
	static class GradeComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			GroupResult g1 = (GroupResult) o1;
			GroupResult g2 = (GroupResult) o2;
			if ( g1.getAveragePoint() > g2.getAveragePoint() ) return -1;
			else if ( g1.getAveragePoint() < g2.getAveragePoint() ) return 1;
			return 0;
		}
		
	}
	
	static class GroupComparator extends educate.util.MyComparator2<GroupResult> {

		@Override
		public int compare(GroupResult r1, GroupResult r2) {
			return r1.getSequence() == r2.getSequence() ? 0 : r1.getSequence() > r2.getSequence() ? 1 : -1;
		}

		

		
	}


}
