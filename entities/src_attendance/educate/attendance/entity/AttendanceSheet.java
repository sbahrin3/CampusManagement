package educate.attendance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.StudentStatus;

@Entity
@Table(name="att_sheet")
public class AttendanceSheet {

	@Id @Column(length=50)
	private String id;
	@Temporal(TemporalType.DATE)
	private Date date;


	@ManyToOne
	private StudentStatus student;
	private int status; //1 - present, 0 - absent
	
	public AttendanceSheet() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public StudentStatus getStudent() {
		return student;
	}
	public void setStudent(StudentStatus student) {
		this.student = student;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}


}
