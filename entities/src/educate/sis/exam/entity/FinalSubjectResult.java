package educate.sis.exam.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.Subject;

import lebah.db.UniqueID;

@Entity
@Table(name="exam_finalsubjectresult")
public class FinalSubjectResult {
	@Id
	private String id;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private FinalResult parent;
	private String grade;
	private double assesmentMark;
	private double finalMark;
	private double totalMark; //totalMark = assessmentMark + finalMark, or totalMark can be alone
	private int creditHour;
	private double point;
	@ManyToOne(fetch= FetchType.LAZY)
	private Subject subject;
	private String status;
	private double gradePoint;
	
	@ManyToOne(fetch= FetchType.LAZY)
	private SubjectResultStatus resultStatus;
	
	@Column(length=50)
	private String subjectStatus;	
	
	private int excludeGPA;
	
	private static String[] nonNumericValues = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C", 
		"D+", "D", "D-", "E+", "E", "E-", "F" };


	public FinalSubjectResult(){
		setId(UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public FinalResult getParent() {
		return parent;
	}
	public void setParent(FinalResult parent) {
		this.parent = parent;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public double getAssesmentMark() {
		return assesmentMark;
	}
	public void setAssesmentMark(double assesmentMark) {
		this.assesmentMark = assesmentMark;
	}
	public double getFinalMark() {
		return finalMark;
	}
	public void setFinalMark(double finalMark) {
		this.finalMark = finalMark;
	}
	
	public double getTotalMark() {
		if ( totalMark == 0.0 ) {
			totalMark = finalMark + assesmentMark;
		}
		return totalMark;
	}
	
	public String getTotalMarkDisplay() {
		String markDisplay = "";
		double mark = getTotalMark();
		if ( mark < 0 ) {
			markDisplay = nonNumericalDisplay(mark);
		}
		else {
			markDisplay = Double.toString(mark);
		}
		return markDisplay;
	}
	
	public static String nonNumericalDisplay(double mark) {
		String s = "";
		//if ( mark == -1 ) return "A+";
		int i = (int) (-1 * mark);
		s = nonNumericValues[i-1];
		return s;
	}		

	public void setTotalMark(double totalMark) {
		this.totalMark = totalMark;
	}

	public int getCreditHour() {
		return creditHour;
	}

	public void setCreditHour(int creditHour) {
		this.creditHour = creditHour;
	}
	
	public double getPoint() {
		return point;
	}

	public void setPoint(double point) {
		this.point = point;
	}
	
	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public double getGradePoint() {
		return gradePoint;
	}

	public void setGradePoint(double gradePoint) {
		this.gradePoint = gradePoint;
	}

	public void removeParent(){
		if(this.parent != null){
			FinalResult tmp = this.parent;
			this.parent = null;
			tmp.removeSubject(this);
			
		}
	}

	public SubjectResultStatus getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(SubjectResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getSubjectStatus() {
		return subjectStatus;
	}

	public void setSubjectStatus(String subjectStatus) {
		this.subjectStatus = subjectStatus;
	}

	public int getExcludeGPA() {
		return excludeGPA;
	}

	public void setExcludeGPA(int excludeGPA) {
		this.excludeGPA = excludeGPA;
	}
	
	

}
