package educate.studentaffair.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.Student;

@Entity @Table(name="stdaf_enquiry")
public class Enquiry {
	
	@Id @Column(length=50)
	private String id;
	@Lob
	private String enquiryText;
	@Lob
	private String actionText;	
	@Temporal(TemporalType.DATE)
	private Date datePosted;
	@Temporal(TemporalType.DATE)
	private Date dateClosed;	
	@Column(length=50)
	private String status;
	private int closed;
	@ManyToOne @JoinColumn(name="student_id")
	private Student student;
	
	public Enquiry() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEnquiryText() {
		return enquiryText;
	}
	public void setEnquiryText(String enquiryText) {
		this.enquiryText = enquiryText;
	}
	public Date getDatePosted() {
		return datePosted;
	}
	public void setDatePosted(Date datePosted) {
		this.datePosted = datePosted;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateClosed() {
		return dateClosed;
	}

	public void setDateClosed(Date dateClosed) {
		this.dateClosed = dateClosed;
	}

	public boolean getClosed() {
		return closed == 1;
	}

	public void setClosed(boolean closed) {
		this.closed = closed ? 1 : 0;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public String getActionText() {
		return actionText;
	}

	public void setActionText(String actionText) {
		this.actionText = actionText;
	}
	
	

}
