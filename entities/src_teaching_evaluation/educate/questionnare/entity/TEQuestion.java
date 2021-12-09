package educate.questionnare.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="te_question")
public class TEQuestion {
	
	@Id @Column(length=50)
	public String id;
	@Column(length=255)
	public String questionText;
	@Lob
	public String questionText2;
	public int questionType; 	//1 - ChooseLevelType1, 2 - ChooseLevelType2
								// ChooseLevelType1: 1 - strongly disagree, 2 - disagree, 3 - nuetral, 4 - agree, 5 - strongly agree
								// ChooseLevelType2: 1 - poor, 2 - average, 3 - good, - 4 - very good, 5 - excellent
	public int sequence;
	
	@Column(length=20)
	public String type;
	@ManyToOne @JoinColumn(name="likert_id")
	public TELikert likert;
	
	@ManyToOne @JoinColumn(name="category_id")
	public TECategory category;
	
	//choices - easy to hardcode
	@Column(length=100)
	private String choice1;
	@Column(length=100)
	private String choice2;
	@Column(length=100)
	private String choice3;
	@Column(length=100)
	private String choice4;
	@Column(length=100)
	private String choice5;
	@Column(length=100)
	private String choice6;
	
	//correct answer
	private int correct1;
	private int correct2;
	private int correct3;
	private int correct4;
	private int correct5;
	private int correct6;
	
	
	
	
	public TEQuestion() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public int getQuestionType() {
		return questionType;
	}

	public void setQuestionType(int questionType) {
		this.questionType = questionType;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public TECategory getCategory() {
		return category;
	}

	public void setCategory(TECategory category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TELikert getLikert() {
		return likert;
	}

	public void setLikert(TELikert likert) {
		this.likert = likert;
	}

	public String getChoice1() {
		return choice1;
	}

	public void setChoice1(String choice1) {
		this.choice1 = choice1;
	}

	public String getChoice2() {
		return choice2;
	}

	public void setChoice2(String choice2) {
		this.choice2 = choice2;
	}

	public String getChoice3() {
		return choice3;
	}

	public void setChoice3(String choice3) {
		this.choice3 = choice3;
	}

	public String getChoice4() {
		return choice4;
	}

	public void setChoice4(String choice4) {
		this.choice4 = choice4;
	}

	public String getChoice5() {
		return choice5;
	}

	public void setChoice5(String choice5) {
		this.choice5 = choice5;
	}

	public String getChoice6() {
		return choice6;
	}

	public void setChoice6(String choice6) {
		this.choice6 = choice6;
	}

	public String getQuestionText2() {
		return questionText2;
	}

	public void setQuestionText2(String questionText2) {
		this.questionText2 = questionText2;
	}

	public boolean getCorrect1() {
		return correct1 == 1;
	}

	public void setCorrect1(boolean correct1) {
		this.correct1 = correct1 ? 1 : 0;
	}

	public boolean getCorrect2() {
		return correct2 == 1;
	}

	public void setCorrect2(boolean correct2) {
		this.correct2 = correct2 ? 1 : 0;
	}

	public boolean getCorrect3() {
		return correct3 == 1;
	}

	public void setCorrect3(boolean correct3) {
		this.correct3 = correct3 ? 1 : 0;
	}

	public boolean getCorrect4() {
		return correct4 == 1;
	}

	public void setCorrect4(boolean correct4) {
		this.correct4 = correct4 ? 1 : 0;
	}

	public boolean getCorrect5() {
		return correct5 == 1;
	}

	public void setCorrect5(boolean correct5) {
		this.correct5 = correct5 ? 1 : 0;
	}

	public boolean getCorrect6() {
		return correct6 == 1;
	}

	public void setCorrect6(boolean correct6) {
		this.correct6 = correct6 ? 1 : 0;
	}
	
	public int getCorrectSingleChoice() {
		if ( correct1 == 1 ) return 1;
		if ( correct2 == 1 ) return 2;
		if ( correct3 == 1 ) return 3;
		if ( correct4 == 1 ) return 4;
		if ( correct5 == 1 ) return 5;
		return 0;
	}

}
