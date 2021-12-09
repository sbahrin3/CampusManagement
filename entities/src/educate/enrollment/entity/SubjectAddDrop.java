package educate.enrollment.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectReg;

@Entity
@Table(name="enrl_subject_add_drop")
public class SubjectAddDrop {
	
	@Id
	private String id;
	private String act;
	@Column(length=15)
	private String status;
	@Temporal(TemporalType.DATE)
	private Date date;

	/**
	 * 2010-03-22. Subject entity will be used for Add request instead of SubjectReg.
	 * This is because not all subjects added are subjects listed in program structure.
	 */
	@OneToOne
	@Column(name="subject_id")
	private Subject subject;
	
	/**
	 * We deal with SubjectReg when doing Add Subject request. This will ensure
	 * that we are only adding Subjects that are in the ProgramStructure.
	 * ---
	 * as of 2010-03-22, Subject will be used instead. 
	 */
	@Deprecated
	@OneToOne(fetch= FetchType.LAZY)
	@Column(name="subject_reg_id")
	private SubjectReg subjectReg;
	
	/**
	 * We deal with StudentSubject when doing Drop Subject request. Because when we
	 * Drop a Subject we are actually deleting a StudentSubject record.
	 */
	@OneToOne(fetch= FetchType.LAZY)
	@Column(name="student_subject_id")
	private StudentSubject studentSubject;
	
	@ManyToOne(fetch= FetchType.LAZY)
	@Column(name="student_id")
	private Student student;
	
	// Shaiful Nizam, 2010-02-14
	// use session instead.
	@Deprecated
	private String semester;
	
	@ManyToOne
	@Column(name="session_id")
	private Session session;

	@Column(name="subject_name", length=100)
	private String subjectName;
	
	@Column(name="subject_code", length=10)
	private String subjectCode;
	
	@Column(name="category_name", length=20)
	private String categoryName;

	@Column(name="credit_hrs", length=2)
	private String creditHrs;
	
	@Column(name="section_name", length=20)
	private String sectionName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_of_update")
	private Date dateOfUpdate;
	
// ==================================
	
	public SubjectAddDrop(){
		setId(lebah.db.UniqueID.getUID());
	}

	@Deprecated
	public SubjectAddDrop(Date date, Subject subject, Student student, String act, String status, String semester){
		setDate(date);
		setSubject(subject);
		setStudent(student);
		setAct(act);
		setStatus(status);
		setSemester(semester);
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
	
	public String getDate(String format) {
		if (format != null || "".equals(format)) {
			java.text.DateFormat df = new java.text.SimpleDateFormat(format);
			return df.format(date);
		} else {
			return date.toString();
		}
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public String getAct() {
		return act;
	}
	
	public void setAct(String act) {
		this.act = act;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
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

	// By Shaiful Nizam, 2010-02-14
	@Deprecated
	public String getSemester() {
		return semester;
	}

	// By Shaiful Nizam, 2010-02-14
	@Deprecated
	public void setSemester(String semester) {
		this.semester = semester;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@Deprecated
	public SubjectReg getSubjectReg() {
		return subjectReg;
	}

	/**
	 * Use subjectReg for Add Subject request. This is to make sure
	 * that the subject exist in the program structure.
	 * @param subjectReg
	 */
	@Deprecated
	public void setSubjectReg(SubjectReg subjectReg) {
		this.subjectReg = subjectReg;
	}

	public StudentSubject getStudentSubject() {
		return studentSubject;
	}

	/**
	 * Use StudentSubject for Drop Subject request. Since we will be deleting the
	 * StudentSubject entity, we a reference to the StudentSubject.
	 * @param studentSubject
	 */
	public void setStudentSubject(StudentSubject studentSubject) {
		this.studentSubject = studentSubject;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		if (categoryName == null) {
			this.categoryName = "Undefined";
		} else {
			this.categoryName = categoryName;
		}
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		if (subjectName == null) {
			this.subjectName = "Undefined";
		} else {
			this.subjectName = subjectName;
		}
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		if (subjectCode == null) {
			this.subjectCode = "Undefined";
		} else {
			this.subjectCode = subjectCode;
		}
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		if (sectionName == null) {
			this.sectionName = "Undefined";
		} else {
			this.sectionName = sectionName;
		}
	}

	public String getCreditHrs() {
		return creditHrs;
	}

	public void setCreditHrs(String creditHrs) {
		if (creditHrs == null) {
			this.creditHrs = "Undefined";
		} else {
			this.creditHrs = creditHrs;
		}
	}

	public Date getDateOfUpdate() {
		return dateOfUpdate;
	}

	public void setDateOfUpdate(Date dateOfUpdate) {
		this.dateOfUpdate = dateOfUpdate;
	}
	
	public String getDateOfUpdate(String format) {
		if (dateOfUpdate != null) {
			if (format != null || "".equals(format)) {
				java.text.DateFormat df = new java.text.SimpleDateFormat(format);
				return df.format(dateOfUpdate);
			} else {
				return dateOfUpdate.toString();
			}
		} else {
			return "";
		}
	}
}
