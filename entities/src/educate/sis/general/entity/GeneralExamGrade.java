package educate.sis.general.entity;
import javax.persistence.*;
import metadata.EntityLister;

@Entity
@Table(name="cm_generalexamgrade")
public class GeneralExamGrade implements EntityLister {

	@Id
	private String id;
	private int ranking;
	private String name;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private ExamGradeScheme scheme;
	
	public GeneralExamGrade(){
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return getName();
	}

	public ExamGradeScheme getScheme() {
		return scheme;
	}

	public void setScheme(ExamGradeScheme scheme) {
		this.scheme = scheme;
	}
	
	
	
}
