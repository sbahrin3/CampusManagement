package educate.timetabling.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ttb_subcategory")
public class SubCategory {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=255)
	private String name;
	@ManyToOne @JoinColumn(name="category_id")
	private Category category;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="subCategory")
	private List<SubCategoryType> subCategoryTypes;
	
	public SubCategory() {
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
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}

	public List<SubCategoryType> getSubCategoryTypes() {
		return subCategoryTypes;
	}

	public void setSubCategoryTypes(List<SubCategoryType> subCategoryTypes) {
		this.subCategoryTypes = subCategoryTypes;
	}
	
	

}
