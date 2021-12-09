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

@Entity @Table(name="stdaf_student_discipline")
public class StudentDiscipline {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="student_id")
	private Student student;
	@ManyToOne @JoinColumn(name="case_id")
	private DisciplinaryCase disciplinaryCase;
	@Temporal(TemporalType.DATE)
	private Date reportedDate;
	@Temporal(TemporalType.DATE)
	private Date hearingDate;
	@Lob
	private String description;

	@Lob
	private String complainantTestimony;
	@Lob
	private String studentTestimony;
	@Lob
	private String crossExaminationResult;
	
	@ManyToOne @JoinColumn(name="complainant_id")
	private Student complainant;
	
	private String complainantName;
	
	@Column(length=50)
	private String status;
	private int caseClosed;
	private int penaltyImposed;
	
	public StudentDiscipline() {
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
	public DisciplinaryCase getDisciplinaryCase() {
		return disciplinaryCase;
	}
	public void setDisciplinaryCase(DisciplinaryCase disciplinaryCase) {
		this.disciplinaryCase = disciplinaryCase;
	}
	public Date getReportedDate() {
		return reportedDate;
	}
	public void setReportedDate(Date reportedDate) {
		this.reportedDate = reportedDate;
	}
	public Date getHearingDate() {
		return hearingDate;
	}
	public void setHearingDate(Date hearingDate) {
		this.hearingDate = hearingDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean getCaseClosed() {
		return caseClosed == 1;
	}
	public void setCaseClosed(boolean caseClosed) {
		this.caseClosed = caseClosed ? 1 : 0;
	}
	public boolean getPenaltyImposed() {
		return penaltyImposed == 1;
	}
	public void setPenaltyImposed(boolean penaltyImposed) {
		this.penaltyImposed = penaltyImposed ? 1 : 0;
	}

	public String getComplainantTestimony() {
		return complainantTestimony;
	}

	public void setComplainantTestimony(String complainantTestimony) {
		this.complainantTestimony = complainantTestimony;
	}

	public String getStudentTestimony() {
		return studentTestimony;
	}

	public void setStudentTestimony(String studentTestimony) {
		this.studentTestimony = studentTestimony;
	}

	public String getCrossExaminationResult() {
		return crossExaminationResult;
	}

	public void setCrossExaminationResult(String crossExaminationResult) {
		this.crossExaminationResult = crossExaminationResult;
	}

	public Student getComplainant() {
		return complainant;
	}

	public void setComplainant(Student complainant) {
		this.complainant = complainant;
	}

	public String getComplainantName() {
		return complainantName;
	}

	public void setComplainantName(String complainantName) {
		this.complainantName = complainantName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	
	
}
