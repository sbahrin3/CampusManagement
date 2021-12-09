package educate.questionnare.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="te_user_answer")
public class TEUserAnswer {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String userId;
	@ManyToOne @JoinColumn(name="question_id")
	private TEQuestion question;
	private int choice; //likert choice
	
	private int choice1;
	private int choice2;
	private int choice3;
	private int choice4;
	private int choice5;
	private int choice6;
	
	@Lob
	private String textAnswer;
	
	public TEUserAnswer() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public TEQuestion getQuestion() {
		return question;
	}

	public void setQuestion(TEQuestion question) {
		this.question = question;
	}

	public int getChoice() {
		return choice;
	}

	public void setChoice(int choice) {
		this.choice = choice;
	}

	public String getTextAnswer() {
		return textAnswer;
	}

	public void setTextAnswer(String textAnswer) {
		this.textAnswer = textAnswer;
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

	public int getChoice6() {
		return choice6;
	}

	public void setChoice6(int choice6) {
		this.choice6 = choice6;
	}


	
	

}
