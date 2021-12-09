package educate.questionnare.entity;

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

@Entity
@Table(name="te_user_answerlog")
public class TEUserAnswerLog {
	
	@Id @Column(length=100)
	private String id;
	
	@ManyToOne @JoinColumn(name="log_id")
	private TEUserLog userLog;
	@ManyToOne @JoinColumn(name="question_id")
	private TEPartQuestion question;	
	
	private int likertValue;
	private int singleChoice;
	private int choice1;
	private int choice2;
	private int choice3;
	private int choice4;
	private int choice5;
	
	@Column(length=255)
	private String textAnswer;
	@Lob
	private String essayAnswer;
	@Temporal(TemporalType.DATE)
	private Date dateInput;
	
	public TEUserAnswerLog() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TEUserLog getUserLog() {
		return userLog;
	}
	public void setUserLog(TEUserLog userLog) {
		this.userLog = userLog;
	}
	public int getLikertValue() {
		return likertValue;
	}
	public void setLikertValue(int likertValue) {
		this.likertValue = likertValue;
	}
	public int getSingleChoice() {
		return singleChoice;
	}
	public void setSingleChoice(int singleChoice) {
		this.singleChoice = singleChoice;
	}
	public int getChoice1() {
		return choice1;
	}
	public void setChoice1(int choice1) {
		this.choice1 = choice1;
	}
	public int getChoice2() {
		return choice2;
	}
	public void setChoice2(int choice2) {
		this.choice2 = choice2;
	}
	public int getChoice3() {
		return choice3;
	}
	public void setChoice3(int choice3) {
		this.choice3 = choice3;
	}
	public int getChoice4() {
		return choice4;
	}
	public void setChoice4(int choice4) {
		this.choice4 = choice4;
	}
	public int getChoice5() {
		return choice5;
	}
	public void setChoice5(int choice5) {
		this.choice5 = choice5;
	}
	public String getTextAnswer() {
		return textAnswer;
	}
	public void setTextAnswer(String textAnswer) {
		this.textAnswer = textAnswer;
	}
	public String getEssayAnswer() {
		return essayAnswer;
	}
	public void setEssayAnswer(String essayAnswer) {
		this.essayAnswer = essayAnswer;
	}

	public TEPartQuestion getQuestion() {
		return question;
	}

	public void setQuestion(TEPartQuestion question) {
		this.question = question;
	}

	public Date getDateInput() {
		return dateInput;
	}

	public void setDateInput(Date dateInput) {
		this.dateInput = dateInput;
	}
	
	

}
