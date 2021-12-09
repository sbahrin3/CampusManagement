package educate.studentaffair.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity @Table(name="stdaf_club")
public class Club {
	
	@Id @Column(length=50)
	private String id;
	
	@ManyToOne @JoinColumn(name="category_id")
	private ClubCategory category;
	
	@Column(length=100)
	private String shortName;
	@Column(length=200)
	private String name;
	@Lob
	private String description;
	
	public Club() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public ClubCategory getCategory() {
		return category;
	}

	public void setCategory(ClubCategory category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	


}
