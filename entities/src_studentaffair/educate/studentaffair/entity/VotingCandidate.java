package educate.studentaffair.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.enrollment.entity.Student;

@Entity @Table(name="stdaf_voting_candidate")
public class VotingCandidate {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="student_id")
	private Student student;
	@ManyToOne @JoinColumn(name="position_id")
	private VotingPosition position;
	private int removed;
	
	public VotingCandidate() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}

	public VotingPosition getPosition() {
		return position;
	}

	public void setPosition(VotingPosition position) {
		this.position = position;
	}

	public boolean getRemoved() {
		return removed == 1;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed ? 1 : 0;
	}
	
	

}
