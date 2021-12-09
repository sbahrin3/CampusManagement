package educate.sis.exam.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.Subject;

@Entity
@Table(name="exam_subject_result")
public class SubjectResult{
	
	@Id
	private String id;
	@ManyToOne(cascade=CascadeType.ALL)
	private SessionResult session;
	@OneToOne(fetch= FetchType.LAZY)
	private Subject subject;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="subjectResult")
	private Set<AssessmentResult> assessments;
	private String resultStatus;
	private String grade;
	private double totalMark;
	private double point;
	private double totalAssessment;
	
	private double part1;
	private double part2;
	private int examDeferStatus;
	private String typeStatus;
		
	public SubjectResult() {
		setId(lebah.util.UIDGenerator.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getPoint() {
		return point;
	}
	
	public double getPointValue() {
		double totalPoint = subject.getCredithrs() * point;
		return totalPoint;
	}	

	public void setPoint(double point) {
		this.point = point;
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	
	public void setResultStatusByGrade(String grade){
		if(grade.equals("F")){
			this.resultStatus = "FAIL";
		}else{
			this.resultStatus = "PASS";
		}
	}
	
	public SessionResult getSession() {
		return session;
	}
 
	public void setSession(SessionResult session) {
		this.session = session;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public double getTotalMark() {
		double mark = part1 + part2 + totalAssessment;
		if ( mark > 0 ) totalMark = mark;
		return totalMark;
	}

	public void setTotalMark(double totalMark) {
		this.totalMark = totalMark;
	}
	
	public void setTotalMark(String mark, double totalMark) {
		totalMark = Double.parseDouble(mark.trim());
		this.totalMark = totalMark;
		
	}
	
	@Override
	public boolean equals(Object o) {
		SubjectResult result = (SubjectResult)o;
		if(result.getSubject().getId().equals(subject.getId()))
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		if ( subject != null )
			return subject.hashCode();
		else
			return super.hashCode();
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public double getPart1() {
		return part1;
	}

	public void setPart1(double part1) {
		this.part1 = part1;
	}

	public double getPart2() {
		return part2;
	}

	public void setPart2(double part2) {
		this.part2 = part2;
	}

	public void setPart1(String part) {
		part1 = Double.parseDouble(part.trim());
	}
	
	public void setPart2(String part) {
		part2 = Double.parseDouble(part.trim());
	}

	public Set<AssessmentResult> getAssessments() {
		if ( assessments == null ) assessments = new HashSet<AssessmentResult>();
		return assessments;
	}

	public void setAssessments(Set<AssessmentResult> assessments) {

		this.assessments = assessments;
	} 
	
	public void addAssessment(AssessmentResult ass) {

		if ( assessments == null ) assessments = new HashSet<AssessmentResult>();
		ass.setSubjectResult(this);
		assessments.remove(ass);
		assessments.add(ass);
	}
	
	public AssessmentResult getAssessmentItem(String id) {
		AssessmentResult a = null;
		for ( AssessmentResult assmt : assessments ) {
			if ( assmt.getCourseworkItem().getId().equals(id)) {
				a = assmt;
				break;
			}
		}
		return a;
	}
	
	public AssessmentResult getAssessmentItem(CourseworkItem item) {
		AssessmentResult a = null;
		for ( AssessmentResult assmt : assessments ) {
			if ( assmt.getCourseworkItem().getId().equals(item.getId())) {
				a = assmt;
				break;
			}
		}
		return a;
	}

	public double getTotalAssessment() {
		return totalAssessment;
	}

	public void setTotalAssessment(double totalAssessment) {
		this.totalAssessment = totalAssessment;
	}

	public int getExamDeferStatus() {
		return examDeferStatus;
	}

	public void setExamDeferStatus(int examDeferStatus) {
		this.examDeferStatus = examDeferStatus;
	}

	public String getTypeStatus() {
		if ( typeStatus == null ) return "";
		return typeStatus;
	}

	public void setTypeStatus(String typeStatus) {
		this.typeStatus = typeStatus;
	}

	
	
}
