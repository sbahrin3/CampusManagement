package educate.sis.struct.entity;

import javax.persistence.*;

@Entity
@Table(name="struc_graduation_subj")
public class GraduationSubjectRequirement {
	
	@Id
	private String id;
	@ManyToOne
	private Subject subject;
	private String mark;
	private double point;
	
	public GraduationSubjectRequirement() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public double getPoint() {
		return point;
	}

	public void setPoint(double point) {
		this.point = point;
	}
	
	
	

}
