package educate.questionnare.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="teaching_evaluation")
public class TeachingEvaluation {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String facultyName;
	@Column(length=50)
	private String courseName;
	@Column(length=50)
	private String codeName;
	@Column(length=50)
	private String semesterYear;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	private int q1;
	private int q2;
	private int q3;
	private int q4;
	private int q5;
	private int q6;
	private int q7;
	private int q8;
	private int q9;
	private int q10;
	private int q11;
	private int q12;
	private int q13;
	private int q14;
	private int q15;
	@Column(length=255)
	private String comment1;
	@Column(length=255)
	private String comment2;
	private int overallRating;
	private int q16;
	private int q17;
	private int q18;
	private int q19;
	private int q20;
	private int q21;
	private int q22;
	private int q23;
	private int q24;
	@Column(length=255)
	private String comment3;

	public TeachingEvaluation() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getSemesterYear() {
		return semesterYear;
	}

	public void setSemesterYear(String semesterYear) {
		this.semesterYear = semesterYear;
	}

	public int getQ1() {
		return q1;
	}

	public void setQ1(int q1) {
		this.q1 = q1;
	}

	public int getQ2() {
		return q2;
	}

	public void setQ2(int q2) {
		this.q2 = q2;
	}

	public int getQ3() {
		return q3;
	}

	public void setQ3(int q3) {
		this.q3 = q3;
	}

	public int getQ4() {
		return q4;
	}

	public void setQ4(int q4) {
		this.q4 = q4;
	}

	public int getQ5() {
		return q5;
	}

	public void setQ5(int q5) {
		this.q5 = q5;
	}

	public int getQ6() {
		return q6;
	}

	public void setQ6(int q6) {
		this.q6 = q6;
	}

	public int getQ7() {
		return q7;
	}

	public void setQ7(int q7) {
		this.q7 = q7;
	}

	public int getQ8() {
		return q8;
	}

	public void setQ8(int q8) {
		this.q8 = q8;
	}

	public int getQ9() {
		return q9;
	}

	public void setQ9(int q9) {
		this.q9 = q9;
	}

	public int getQ10() {
		return q10;
	}

	public void setQ10(int q10) {
		this.q10 = q10;
	}

	public int getQ11() {
		return q11;
	}

	public void setQ11(int q11) {
		this.q11 = q11;
	}

	public int getQ12() {
		return q12;
	}

	public void setQ12(int q12) {
		this.q12 = q12;
	}

	public int getQ13() {
		return q13;
	}

	public void setQ13(int q13) {
		this.q13 = q13;
	}

	public int getQ14() {
		return q14;
	}

	public void setQ14(int q14) {
		this.q14 = q14;
	}

	public int getQ15() {
		return q15;
	}

	public void setQ15(int q15) {
		this.q15 = q15;
	}

	public String getComment1() {
		return comment1;
	}

	public void setComment1(String comment1) {
		this.comment1 = comment1;
	}

	public String getComment2() {
		return comment2;
	}

	public void setComment2(String comment2) {
		this.comment2 = comment2;
	}

	public int getOverallRating() {
		return overallRating;
	}

	public void setOverallRating(int overallRating) {
		this.overallRating = overallRating;
	}

	public int getQ16() {
		return q16;
	}

	public void setQ16(int q16) {
		this.q16 = q16;
	}

	public int getQ17() {
		return q17;
	}

	public void setQ17(int q17) {
		this.q17 = q17;
	}

	public int getQ18() {
		return q18;
	}

	public void setQ18(int q18) {
		this.q18 = q18;
	}

	public int getQ19() {
		return q19;
	}

	public void setQ19(int q19) {
		this.q19 = q19;
	}

	public int getQ20() {
		return q20;
	}

	public void setQ20(int q20) {
		this.q20 = q20;
	}

	public int getQ21() {
		return q21;
	}

	public void setQ21(int q21) {
		this.q21 = q21;
	}

	public int getQ22() {
		return q22;
	}

	public void setQ22(int q22) {
		this.q22 = q22;
	}

	public int getQ23() {
		return q23;
	}

	public void setQ23(int q23) {
		this.q23 = q23;
	}

	public int getQ24() {
		return q24;
	}

	public void setQ24(int q24) {
		this.q24 = q24;
	}

	public String getComment3() {
		return comment3;
	}

	public void setComment3(String comment3) {
		this.comment3 = comment3;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
	
	

}
