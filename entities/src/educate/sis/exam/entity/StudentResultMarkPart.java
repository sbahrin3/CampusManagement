package educate.sis.exam.entity;
import javax.persistence.*;

@Entity
@Table(name="exam_studentresultmarkpart")
public class StudentResultMarkPart {
	
	@Id
	private String id;
	private double result;
	
	@ManyToOne
	private SubjectResult subjectResult;
	@OneToOne
	private MarkPart markPart;
	
	public StudentResultMarkPart(){
		setId(lebah.util.UIDGenerator.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public SubjectResult getSubjectResult() {
		return subjectResult;
	}

	public void setSubjectResult(SubjectResult subjectResult) {
		this.subjectResult = subjectResult;
	}

	public MarkPart getMarkPart() {
		return markPart;
	}

	public void setMarkPart(MarkPart markPart) {
		this.markPart = markPart;
	}
	
	

}
