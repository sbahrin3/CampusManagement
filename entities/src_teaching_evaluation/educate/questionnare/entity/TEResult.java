package educate.questionnare.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.Teacher;


@Entity
@Table(name="te_result")
public class TEResult {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="questionnare_id")
	private TEQuestionnare questionnare;
	
	@Temporal(TemporalType.DATE)
	private Date startDate;
	
	@ManyToOne(fetch= FetchType.LAZY) @JoinColumn(name="subject_id")
	private Subject subject;
	@ManyToOne(fetch= FetchType.LAZY) @JoinColumn(name="teacher_id")
	private Teacher teacher;
	@ManyToOne(fetch= FetchType.LAZY) @JoinColumn(name="student_id")
	private Student student;
	
	
	@Column(length=50)
	private String questionRefNo;
	private int sequence;
	@Column(length=50)
	private String questionType;
	
	private int answerNum;
	
	@Column(length=255)
	private String answerText;
	@Lob
	private String answerLargeText;
	
	public TEResult() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TEQuestionnare getQuestionnare() {
		return questionnare;
	}
	public void setQuestionnare(TEQuestionnare questionnare) {
		this.questionnare = questionnare;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	public Teacher getTeacher() {
		return teacher;
	}
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public String getQuestionRefNo() {
		return questionRefNo;
	}
	public void setQuestionRefNo(String questionRefNo) {
		this.questionRefNo = questionRefNo;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public int getAnswerNum() {
		return answerNum;
	}
	public void setAnswerNum(int answerNum) {
		this.answerNum = answerNum;
	}
	public String getAnswerText() {
		return answerText;
	}
	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
	public String getAnswerLargeText() {
		return answerLargeText;
	}
	public void setAnswerLargeText(String answerLargeText) {
		this.answerLargeText = answerLargeText;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	

	
	
	

}
