package educate.enrollment.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



@Entity
@Table(name="enrl_update_log")
public class UpdatePrimaryDataLog {
	
	@Id @Column(length=50)
	private String id;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTime;
	@Column(length=100)
	private String oldName;
	@Column(length=50)
	private String oldICNo;
	@Column(length=50)
	private String oldPassportNo;
	@ManyToOne @JoinColumn(name="student_id")
	private Student student;
	@Column(length=50)
	private String userId;
	
	public UpdatePrimaryDataLog() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	public String getOldICNo() {
		return oldICNo;
	}
	public void setOldICNo(String oldICNo) {
		this.oldICNo = oldICNo;
	}
	public String getOldPassportNo() {
		return oldPassportNo;
	}
	public void setOldPassportNo(String oldPassportNo) {
		this.oldPassportNo = oldPassportNo;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	

}
