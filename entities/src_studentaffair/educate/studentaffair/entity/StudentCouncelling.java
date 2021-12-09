package educate.studentaffair.entity;


import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.Student;

@Entity @Table(name="stdaf_student_councelling")
public class StudentCouncelling {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="student_id")
	private Student student;
	@ManyToOne @JoinColumn(name="issue_id")
	private CouncellingIssue issue;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="studentCouncelling")
	private List<StudentCouncellingSession> sessions;
	
	@Column(length=50)
	private String referralType; //student, lecturer, staff
	private String referralTypeRemark;
	@Column(length=50)
	private String referralId;
	private String referralName;
	private String referralContactNo;
	private String referralEmail;
	private String referralReason;
	
	@Lob
	private String issueSummary;
	@Lob
	private String studentRemark;
	private int aggree;
	
	@ManyToOne @JoinColumn(name="councellor_id")
	private Councellor councellor;
	
	@Lob
	private String councellorRemark;
	
	@Temporal(TemporalType.DATE)
	private Date referredDate;
	
	public StudentCouncelling() {
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

	public CouncellingIssue getIssue() {
		return issue;
	}

	public void setIssue(CouncellingIssue issue) {
		this.issue = issue;
	}

	public String getReferralType() {
		return referralType;
	}

	public void setReferralType(String referralType) {
		this.referralType = referralType;
	}

	public String getReferralId() {
		return referralId;
	}

	public void setReferralId(String referralId) {
		this.referralId = referralId;
	}

	public String getReferralName() {
		return referralName;
	}

	public void setReferralName(String referralName) {
		this.referralName = referralName;
	}

	public String getReferralContactNo() {
		return referralContactNo;
	}

	public void setReferralContactNo(String referralContactNo) {
		this.referralContactNo = referralContactNo;
	}

	public String getReferralEmail() {
		return referralEmail;
	}

	public void setReferralEmail(String referralEmail) {
		this.referralEmail = referralEmail;
	}

	public String getReferralReason() {
		return referralReason;
	}

	public void setReferralReason(String referralReason) {
		this.referralReason = referralReason;
	}

	public String getIssueSummary() {
		return issueSummary;
	}

	public void setIssueSummary(String issueSummary) {
		this.issueSummary = issueSummary;
	}

	public String getStudentRemark() {
		return studentRemark;
	}

	public void setStudentRemark(String studentRemark) {
		this.studentRemark = studentRemark;
	}

	public boolean getAggree() {
		return aggree == 1;
	}

	public void setAggree(boolean aggree) {
		this.aggree = aggree ? 1 : 0;
	}

	public Councellor getCouncellor() {
		return councellor;
	}

	public void setCouncellor(Councellor councellor) {
		this.councellor = councellor;
	}

	public Date getReferredDate() {
		return referredDate;
	}

	public void setReferredDate(Date refferedDate) {
		this.referredDate = refferedDate;
	}

	public List<StudentCouncellingSession> getSessions() {
		return sessions;
	}

	public void setSessions(List<StudentCouncellingSession> sessions) {
		this.sessions = sessions;
	}

	public String getReferralTypeRemark() {
		return referralTypeRemark;
	}

	public void setReferralTypeRemark(String referralTypeRemark) {
		this.referralTypeRemark = referralTypeRemark;
	}

	public String getCouncellorRemark() {
		return councellorRemark;
	}

	public void setCouncellorRemark(String councellorRemark) {
		this.councellorRemark = councellorRemark;
	}


	
	

}
