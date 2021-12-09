package educate.enrollment.entity;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name="quick_register_log")
public class QuickRegisterLog {
	
	@Id
	@Column(length=50)
	private String id;
	@Column(length=50)
	private String userId;
	@Temporal(TemporalType.DATE)
	private Date logDate;
	@Column(length=50)
	private String matricNo;
	@Column(length=50)
	private String studentId;
	@Column(length=50)
	private String studentName;
	@Column(length=50)
	private String studentICNo;
	@Column(length=50)
	private String studentPassportNo;
	@Temporal(TemporalType.TIME)
	private Date logTime;
	
	@Column(length=50)
	private String programCode;
	@Column(length=50)
	private String intakeCode;
	@Column(length=50)
	private String centreCode;
	private int updateRegister;
	
	public QuickRegisterLog() {
		setId(lebah.db.UniqueID.getUID());	
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getLogDate() {
		return logDate;
	}
	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	public String getMatricNo() {
		return matricNo;
	}

	public void setMatricNo(String matricNo) {
		this.matricNo = matricNo;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentICNo() {
		return studentICNo;
	}

	public void setStudentICNo(String studentICNo) {
		this.studentICNo = studentICNo;
	}

	public String getStudentPassportNo() {
		return studentPassportNo;
	}

	public void setStudentPassportNo(String studentPassportNo) {
		this.studentPassportNo = studentPassportNo;
	}

	public String getProgramCode() {
		return programCode;
	}

	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}

	public String getIntakeCode() {
		return intakeCode;
	}

	public void setIntakeCode(String intakeCode) {
		this.intakeCode = intakeCode;
	}

	public String getCentreCode() {
		return centreCode;
	}

	public void setCentreCode(String centreCode) {
		this.centreCode = centreCode;
	}

	public boolean getUpdateRegister() {
		return updateRegister == 1;
	}

	public void setUpdateRegister(boolean updateRegister) {
		this.updateRegister = updateRegister ? 1 : 0;
	}



}
