package educate.sis.exam.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Subject;

@Entity
@Table(name="exam_subject_grade")
public class SubjectGrade {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="subject_id")
	private Subject subject;
	@ManyToOne @JoinColumn(name="program_id")
	private Program program;
	@ManyToOne @JoinColumn(name="grade_id")
	private MarkingGrade markingGrade;
	
	public SubjectGrade() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}

	public MarkingGrade getMarkingGrade() {
		return markingGrade;
	}

	public void setMarkingGrade(MarkingGrade markingGrade) {
		this.markingGrade = markingGrade;
	}
	
	

}
