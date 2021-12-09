package educate.enrollment.entity;
import javax.persistence.*;
import educate.sis.struct.entity.Subject;

import java.util.*;

@Entity
@Table(name="enrl_subject_withdraw")
public class SubjectWithdraw {
	
	@Id
	private String id;
	private String semester;
	private String status;
	private String act;
	@Temporal(TemporalType.DATE)
	private Date date;
	@ManyToOne
	private Subject subject;
	@ManyToOne
	private Student student;
	
	public SubjectWithdraw(){
		setId(lebah.db.UniqueID.getUID());
	}
	
	public SubjectWithdraw(String semester, String status, String act, Date date, Subject subject, Student student){
		setSemester(semester);
		setStatus(status);
		setAct(act);
		setDate(date);
		setSubject(subject);
		setStudent(student);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

}
