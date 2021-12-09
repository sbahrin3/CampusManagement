package educate.lms2.entity;

import javax.persistence.*;

@Entity
@Table(name="lms2_classroom")
public class Classroom {
	
	@Id
	@Column(length=100)
	private String id;
	
	@Column(length=100)
	private String subjectId;
	@Column(length=100)
	private String subjectTitle;
	
	public Classroom() {
	}
	
	public Classroom(String subjectId, String subjectTitle, String group) {
		this.id = subjectId + group;
		this.subjectId = subjectId;
		this.subjectTitle = subjectTitle;
	}
	
	public Classroom(String subjectId, String subjectTitle) {
		this(subjectId, subjectTitle, "");
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getSubjectTitle() {
		return subjectTitle;
	}
	public void setSubjectTitle(String subjectTitle) {
		this.subjectTitle = subjectTitle;
	}
	
	
	
	

}
