package educate.sis.struct.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import metadata.EntityLister;

@Entity
@Table(name="enrl_subjectsection")
public class SubjectSection implements EntityLister {
	@Id
	private String id;
	private String code;
	private String name;
	private String description;
	@ManyToOne
	private LearningCentre learningCentre;
	private int sequence;
	
	public SubjectSection(){
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public LearningCentre getLearningCentre() {
		return learningCentre;
	}

	public void setLearningCentre(LearningCentre learningCentre) {
		this.learningCentre = learningCentre;
	}

	public String getValue() {
		return getDescription();
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}


	
	
}
