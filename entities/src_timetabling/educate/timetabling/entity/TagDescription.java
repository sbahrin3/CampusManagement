package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ttb_tag_description")
public class TagDescription {
	
	@Id @Column(length=50)
	private String id;
	private int tagNumber;
	@Column(length=255)
	private String description;
	
	public TagDescription() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTagNumber() {
		return tagNumber;
	}
	public void setTagNumber(int tagNumber) {
		this.tagNumber = tagNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
