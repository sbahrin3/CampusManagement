package educate.sis.struct.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="struc_userlearningcentre")
public class UserLearningCentre {
	
	@Id
	@Column(length=50)
	private String id;
	@Column(length=50)
	private String userId;
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<LearningCentre> learningCentres;
	
	public UserLearningCentre() {
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
	public List<LearningCentre> getLearningCentres() {
		if ( learningCentres == null ) learningCentres = new ArrayList<LearningCentre>();
		return learningCentres;
	}
	public void setLearningCentres(List<LearningCentre> learningCentres) {
		this.learningCentres = learningCentres;
	}
	
	
}
