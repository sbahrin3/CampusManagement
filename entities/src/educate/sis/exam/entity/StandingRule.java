package educate.sis.exam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="result_standing_rule")
public class StandingRule {

	@Id @Column(length=50)
	private String id;
	int sequence;
	@Column(length=5)
	private String comparator; //2 types only, eqOrGreaterThan (egt), lessThan (lt)
	private String comparator2; //for gpa
	private double cgpaValue; //by standard this value should be 2.00
	private double gpaValue;
	@ManyToOne @JoinColumn(name="prev_standing_id")
	private Standing previousStanding;
	@ManyToOne @JoinColumn(name="standing_id")
	private Standing currentStanding;
	
	private String programLevelType;
	
	public StandingRule() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getComparator() {
		return comparator;
	}
	public void setComparator(String comparator) {
		this.comparator = comparator;
	}
	public double getCgpaValue() {
		return cgpaValue;
	}
	public void setCgpaValue(double cgpaValue) {
		this.cgpaValue = cgpaValue;
	}

	public Standing getPreviousStanding() {
		return previousStanding;
	}

	public void setPreviousStanding(Standing prevStanding) {
		this.previousStanding = prevStanding;
	}

	public Standing getCurrentStanding() {
		return currentStanding;
	}

	public void setCurrentStanding(Standing standing) {
		this.currentStanding = standing;
	}

	public String getComparator2() {
		return comparator2;
	}

	public void setComparator2(String comparator2) {
		this.comparator2 = comparator2;
	}

	public double getGpaValue() {
		return gpaValue;
	}

	public void setGpaValue(double gpaValue) {
		this.gpaValue = gpaValue;
	}

	public String getProgramLevelType() {
		return programLevelType;
	}

	public void setProgramLevelType(String programLevelType) {
		this.programLevelType = programLevelType;
	}
	
	
	
	
}
