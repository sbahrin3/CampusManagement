package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ttb_subcategory_type_dtl")
public class SubCategoryTypeDetail {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=100)
	private String name;
	@Column(length=255)
	private String description;
	
	@ManyToOne @JoinColumn(name="subcategory_id")
	private SubCategory subCategory;
	@ManyToOne @JoinColumn(name="Subcategory_type_id")
	private SubCategoryType subCategoryType;
	
	public SubCategoryTypeDetail() {
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
	public SubCategory getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}
	public SubCategoryType getSubCategoryType() {
		return subCategoryType;
	}
	public void setSubCategoryType(SubCategoryType subCategoryType) {
		this.subCategoryType = subCategoryType;
	}
	
	
		
	

}
