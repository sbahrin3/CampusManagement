package educate.enrollment.entity;
import javax.persistence.*;
import educate.sis.struct.entity.Subject;
import java.util.*;

@Entity
@Table(name="enrl_subject_transfer")
public class SubjectTransfer {
	
	@Id
	private String id;
	@Temporal(TemporalType.DATE)
	private Date date;
	private String status;
	private String act;
	@ManyToOne
	private Subject subject;
	@ManyToOne
	private Student student;
	
	public SubjectTransfer() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public SubjectTransfer(Date date, String status, String act, Subject subject, Student student){
		setDate(date);
		setStatus(status);
		setAct(act);
		setSubject(subject);
		setStudent(student);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

/*	public void setDate(Object d) {
		if ( d instanceof Date ) date = (Date) d;
		else if ( d instanceof String) {
			setDate((String) d);
		}
	}
	
	public void setDate(String date) {
		if (date == null || "".equals(date)) return;
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		setDate(new GregorianCalendar(year, month, day).getTime());
	}*/
	
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
