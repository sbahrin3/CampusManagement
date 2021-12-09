package educate.studentaffair.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity @Table(name="stdaf_category")

public class ClubCategory {

	@Id @Column(length=50)
	private String id;
	@Column(length=100)
	private String name;
	@Column(length=50)
	private String shortName;
	
	public ClubCategory() {
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
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	
	
	
}
