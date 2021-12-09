package educate.studentaffair.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.Student;

@Entity @Table(name="stdaf_student_club")
public class StudentClub {
	
	@Id @Column(length=50)
	private String id;
	
	@ManyToOne @JoinColumn(name="student_id")
	private Student student;
	@ManyToOne @JoinColumn(name="club_id")
	private Club club;
	@ManyToOne @JoinColumn(name="position_id")
	private StudentClubPosition position;
	@Column(length=50) 
	private String status; //active, not-active, 
	@Temporal(TemporalType.DATE)
	private Date dateJoined;
	
	
	public StudentClub() {
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
	public Club getClub() {
		return club;
	}
	public void setClub(Club club) {
		this.club = club;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getDateJoined() {
		return dateJoined;
	}
	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}

	public StudentClubPosition getPosition() {
		return position;
	}

	public void setPosition(StudentClubPosition position) {
		this.position = position;
	}

	
	
	

}
