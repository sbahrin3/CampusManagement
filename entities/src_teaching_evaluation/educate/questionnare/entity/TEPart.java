package educate.questionnare.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.emory.mathcs.backport.java.util.Collections;

@Entity
@Table(name="te_part")
public class TEPart {

	@Id @Column(length=50)
	public String id;
	@Column(length=50)
	public String title;
	@Column(length=255)
	public String description;
	public int sequence;
	
	@ManyToOne @JoinColumn(name="set_id")
	private TESet set;
	
	@Lob
	private String instructionText;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="part")
	private List<TEPartQuestion> questions;
	
//    @ManyToMany(cascade=CascadeType.PERSIST)
//    @JoinTable(name = "te_part_questions",
//    joinColumns = {@JoinColumn(name = "part_id", referencedColumnName = "id")}, 
//    inverseJoinColumns = {@JoinColumn(name = "question_id", referencedColumnName = "id")})  
//	List<TEQuestion> questions;
	
	
	
	public TEPart() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public TESet getSet() {
		return set;
	}

	public void setSet(TESet set) {
		this.set = set;
	}

	public String getInstructionText() {
		return instructionText;
	}

	public void setInstructionText(String instructionText) {
		this.instructionText = instructionText;
	}

	public List<TEPartQuestion> getQuestions() {
		if ( questions == null ) questions = new ArrayList<TEPartQuestion>();
		Collections.sort(questions, new SequenceComparator());
		return questions;
	}

	public void setQuestions(List<TEPartQuestion> questions) {
		this.questions = questions;
	}  
	
	
	
	static class SequenceComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			TEPartQuestion r1 = (TEPartQuestion) o1;
			TEPartQuestion r2 = (TEPartQuestion) o2;
			return r1.getSequence() > r2.getSequence() ? 1 : -1;
		}
		
	}	

	
}
