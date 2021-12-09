package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ttb_category")
public class Category {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=255)
	private String name;
	
	@Column(length=255)
	private String description;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="category")
	private List<SubCategory> subCategories;
	
	public Category() {
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

	public List<SubCategory> getSubCategories() {
		if ( subCategories == null ) subCategories = new ArrayList<SubCategory>();
		return subCategories;
	}

	public void setSubCategories(List<SubCategory> subCategories) {
		this.subCategories = subCategories;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
