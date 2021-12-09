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

@Entity @Table(name="stdaf_councelling_session")
public class StudentCouncellingSession {
	
	@Id @Column(length=50)
	private String id;
	
	@ManyToOne @JoinColumn(name="councellor_id")
	private Councellor councellor;
	
	@ManyToOne @JoinColumn(name="studentCouncelling_id")
	private StudentCouncelling studentCouncelling;
	@Temporal(TemporalType.TIMESTAMP)
	private Date sessionDate;
	@Lob
	private String sessionReport;
	private String sessionSummary;
	
	private int termination;
	@Lob
	private String terminationReason;
	
	public StudentCouncellingSession() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Councellor getCouncellor() {
		return councellor;
	}
	public void setCouncellor(Councellor councellor) {
		this.councellor = councellor;
	}
	public StudentCouncelling getStudentCouncelling() {
		return studentCouncelling;
	}
	public void setStudentCouncelling(StudentCouncelling studentCouncelling) {
		this.studentCouncelling = studentCouncelling;
	}
	public Date getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}
	public String getSessionReport() {
		return sessionReport;
	}
	public void setSessionReport(String sessionReport) {
		this.sessionReport = sessionReport;
	}
	public String getSessionSummary() {
		return sessionSummary;
	}
	public void setSessionSummary(String sessionSummary) {
		this.sessionSummary = sessionSummary;
	}
	public int getTermination() {
		return termination;
	}
	public void setTermination(int termination) {
		this.termination = termination;
	}
	public String getTerminationReason() {
		return terminationReason;
	}
	public void setTerminationReason(String terminationReason) {
		this.terminationReason = terminationReason;
	}
	
	
	

}
