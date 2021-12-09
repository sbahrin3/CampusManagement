package educate.sis.struct.entity;

import java.util.Date;

import javax.persistence.*;

import educate.enrollment.entity.Student;

@Entity
@Table(name="struc_accomodation_status")
public class StudentAccomodationStatus {
	
	@Id
	@Column(length=50)
	private String id;
	
	@ManyToOne
	private StudentAccomodation accomodation;
	@ManyToOne
	private Student student;
	private int expired; //equal 1 if student no longer staying 
	
	@Temporal(TemporalType.DATE)
	private Date date;
	@Temporal(TemporalType.TIME)
	private Date time;
	
	public StudentAccomodationStatus() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	public StudentAccomodation getAccomodation() {
		return accomodation;
	}
	public void setAccomodation(StudentAccomodation accomodation) {
		this.accomodation = accomodation;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	
	public void setExpired(boolean b) {
		expired = b ? 1 : 0;
	}
	
	public boolean getExpired() {
		return expired == 1;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	

}
