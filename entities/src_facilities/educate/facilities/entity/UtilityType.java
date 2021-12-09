package educate.facilities.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity @Table(name="fac_utility_type")
public class UtilityType {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=100)
	private String name;
	private int sequence;
	@Column(length=20)
	private String unitRead;
	
	public UtilityType() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getUnitRead() {
		return unitRead;
	}

	public void setUnitRead(String unitRead) {
		this.unitRead = unitRead;
	}
	
	

}
