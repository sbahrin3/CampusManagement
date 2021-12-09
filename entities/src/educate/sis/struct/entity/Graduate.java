package educate.sis.struct.entity;

import javax.persistence.*;

import educate.enrollment.entity.Student;

@Entity
@Table(name="enrl_graduate")
public class Graduate {

	@Id
	private String id;
	@OneToOne
	private Student student;
	@ManyToOne
	private Session session;
	private String remark;
	private int clearanceFinance;
	private int clearanceLibrary;
	private int clearanceStudentAffair;
	private int clearanceFaculty;
	private int clearance;  //all clearance, supaya senang nak buat query
	private String status = ""; // "", "APPLIED"
	private String financeRemark;
	private String libraryRemark;
	private String studentAffairRemark;
	private String facultyRemark;
	@Column(length=50)
	private String referenceNo;
	
	private int attendRehearsal;
	private int attendCeremony;
	
	public Graduate() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}

	public boolean getClearanceFinance() {
		return clearanceFinance == 1 ? true : false;
	}

	public void setClearanceFinance(boolean clearanceFinance) {
		this.clearanceFinance = clearanceFinance ? 1 : 0;
		this.clearance = (this.clearanceLibrary == 1) && (this.clearanceFinance == 1) && (this.clearanceStudentAffair == 1) && (this.clearanceFaculty == 1) ? 1 : 0;
	}

	public boolean getClearanceLibrary() {
		return clearanceLibrary == 1 ? true : false;
	}

	public void setClearanceLibrary(boolean clearanceLibrary) {
		this.clearanceLibrary = clearanceLibrary ? 1 : 0;
		this.clearance = (this.clearanceLibrary == 1) && (this.clearanceFinance == 1) && (this.clearanceStudentAffair == 1) && (this.clearanceFaculty == 1) ? 1 : 0;
	}

	public boolean getClearanceStudentAffair() {
		return clearanceStudentAffair == 1 ? true : false;
	}

	public void setClearanceStudentAffair(boolean clearanceStudentAffair) {
		this.clearanceStudentAffair = clearanceStudentAffair ? 1 : 0;
		this.clearance = (this.clearanceLibrary == 1) && (this.clearanceFinance == 1) && (this.clearanceStudentAffair == 1) && (this.clearanceFaculty == 1) ? 1 : 0;
	}
	
	public boolean getClearanceFaculty() {
		return clearanceFaculty == 1 ? true : false;
	}

	public void setClearanceFaculty(boolean clearanceStudentAffair) {
		this.clearanceFaculty = clearanceStudentAffair ? 1 : 0;
		this.clearance = (this.clearanceLibrary == 1) && (this.clearanceFinance == 1) && (this.clearanceStudentAffair == 1) && (this.clearanceFaculty == 1) ? 1 : 0;
	}	

	public boolean getClearance() {
		return clearance == 1 ? true : false;
	}

	public void setClearance(boolean clearance) {
		this.clearance = clearance ? 1 : 0;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFinanceRemark() {
		return financeRemark;
	}

	public void setFinanceRemark(String financeRemark) {
		this.financeRemark = financeRemark;
	}

	public String getLibraryRemark() {
		return libraryRemark;
	}

	public void setLibraryRemark(String libraryRemark) {
		this.libraryRemark = libraryRemark;
	}

	public String getStudentAffairRemark() {
		return studentAffairRemark;
	}

	public void setStudentAffairRemark(String studentaffairRemark) {
		this.studentAffairRemark = studentaffairRemark;
	}

	public String getFacultyRemark() {
		return facultyRemark;
	}

	public void setFacultyRemark(String facultyRemark) {
		this.facultyRemark = facultyRemark;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public boolean getAttendRehearsal() {
		return attendRehearsal == 1;
	}

	public void setAttendRehearsal(boolean attendRehearsal) {
		this.attendRehearsal = attendRehearsal ? 1 : 0;
	}

	public boolean getAttendCeremony() {
		return attendCeremony == 1;
	}

	public void setAttendCeremony(boolean attendCeremony) {
		this.attendCeremony = attendCeremony ? 1 : 0;
	}
	
	
	
}
