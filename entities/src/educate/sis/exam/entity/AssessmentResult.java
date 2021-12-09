package educate.sis.exam.entity;

import javax.persistence.*;

@Entity
@Table(name="exam_assessment_result")
public class AssessmentResult {
	
	@Id
	private String id;
	@ManyToOne
	private SubjectResult subjectResult;
	@OneToOne
	private CourseworkItem courseworkItem;
	private double marks;
	
	public AssessmentResult() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public CourseworkItem getCourseworkItem() {
		return courseworkItem;
	}
	public void setCourseworkItem(CourseworkItem courseworkItem) {
		this.courseworkItem = courseworkItem;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public double getMarks() {
		return marks;
	}

	public void setMarks(double marks) {
		this.marks = marks;
	}

	public SubjectResult getSubjectResult() {
		return subjectResult;
	}
	public void setSubjectResult(SubjectResult subjectResult) {
		this.subjectResult = subjectResult;
	}
	
	@Override
	public boolean equals(Object o) {
		AssessmentResult ass1 = (AssessmentResult) o;
		if ( this.getCourseworkItem() != null )
			if ( ass1.getCourseworkItem().getId().equals(this.getCourseworkItem().getId())) return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		if ( this.getCourseworkItem() != null ) return this.getCourseworkItem().getId().hashCode();
		return 0;
	}
	
	

}
