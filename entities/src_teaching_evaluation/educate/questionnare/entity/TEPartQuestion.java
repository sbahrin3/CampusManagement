package educate.questionnare.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="te_part_question")
public class TEPartQuestion {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String refNo;
	@ManyToOne @JoinColumn(name="part_id")
	private TEPart part;
	@ManyToOne(cascade=CascadeType.PERSIST) @JoinColumn(name="question_id")
	private TEQuestion question;
	
	@OneToOne @JoinColumn(name="next1_id")
	private TEPartQuestion nextQuestion1;
	@OneToOne @JoinColumn(name="next2_id")
	private TEPartQuestion nextQuestion2;
	@OneToOne @JoinColumn(name="next3_id")
	private TEPartQuestion nextQuestion3;
	@OneToOne @JoinColumn(name="next4_id")
	private TEPartQuestion nextQuestion4;
	@OneToOne @JoinColumn(name="next5_id")
	private TEPartQuestion nextQuestion5;
	@OneToOne @JoinColumn(name="next6_id")
	private TEPartQuestion nextQuestion6;
	
	private int firstQuestion;
	private int jumpType;
	private int sequence;
	private int sequence2;
	
	private int required;
	private int minimumChars;
	
	public TEPartQuestion() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public TEPart getPart() {
		return part;
	}
	public void setPart(TEPart part) {
		this.part = part;
	}

	public TEQuestion getQuestion() {
		return question;
	}
	public void setQuestion(TEQuestion question) {
		this.question = question;
	}

	public TEPartQuestion getNextQuestion1() {
		return nextQuestion1;
	}

	public void setNextQuestion1(TEPartQuestion nextQuestion1) {
		this.nextQuestion1 = nextQuestion1;
	}

	public TEPartQuestion getNextQuestion2() {
		return nextQuestion2;
	}

	public void setNextQuestion2(TEPartQuestion nextQuestion2) {
		this.nextQuestion2 = nextQuestion2;
	}

	public TEPartQuestion getNextQuestion3() {
		return nextQuestion3;
	}

	public void setNextQuestion3(TEPartQuestion nextQuestion3) {
		this.nextQuestion3 = nextQuestion3;
	}

	public TEPartQuestion getNextQuestion4() {
		return nextQuestion4;
	}

	public void setNextQuestion4(TEPartQuestion nextQuestion4) {
		this.nextQuestion4 = nextQuestion4;
	}

	public TEPartQuestion getNextQuestion5() {
		return nextQuestion5;
	}

	public void setNextQuestion5(TEPartQuestion nextQuestion5) {
		this.nextQuestion5 = nextQuestion5;
	}

	public TEPartQuestion getNextQuestion6() {
		return nextQuestion6;
	}

	public void setNextQuestion6(TEPartQuestion nextQuestion6) {
		this.nextQuestion6 = nextQuestion6;
	}

	public int getFirstQuestion() {
		return firstQuestion;
	}

	public void setFirstQuestion(int firstQuestion) {
		this.firstQuestion = firstQuestion;
	}

	public boolean getJumpType() {
		return jumpType == 1;
	}

	public void setJumpType(boolean jumpType) {
		this.jumpType = jumpType ? 1 : 0;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getSequence2() {
		return sequence2;
	}

	public void setSequence2(int sequence2) {
		this.sequence2 = sequence2;
	}

	public boolean getRequired() {
		return required == 1;
	}

	public void setRequired(boolean required) {
		this.required = required ? 1 : 0;
	}

	public int getMinimumChars() {
		return minimumChars;
	}

	public void setMinimumChars(int minimumChars) {
		this.minimumChars = minimumChars;
	}
	
	

}
