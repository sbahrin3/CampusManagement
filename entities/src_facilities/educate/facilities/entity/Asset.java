package educate.facilities.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.sis.struct.entity.LearningCentre;

@Entity @Table(name="fac_asset")
public class Asset {
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="category_id")
	private AssetCategory category;
	@Column(length=200)
	private String description;
	@Column(length=100)
	private String tagNo;
	@Column(length=100)
	private String serialNo;
	private int purchaseYear;
	@Temporal(TemporalType.DATE)
	private Date purchaseDate;
	private double purchaseValue;
	@Column(length=100)
	private String brandName;
	@Column(length=100)
	private String modelName;
	@Column(length=100)
	private String locationDescription;
	@ManyToOne @JoinColumn(name="campus_id")
	private LearningCentre campus;
	@Column(length=20)
	private String status; //good, faulty, lost, repair, dispose
	private String photoFileName;
	
	@Column(length=50)
	private String createUserId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	@Temporal(TemporalType.TIMESTAMP)
	private String remoteAddress;
	
	@Column(length=50)
	private String modifyUserId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifyDate;
	@Temporal(TemporalType.TIMESTAMP)
	private String modifyRemoteAddress;	
	
	private int loanable;
	
	public Asset() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public AssetCategory getCategory() {
		return category;
	}
	public void setCategory(AssetCategory category) {
		this.category = category;
	}
	public String getTagNo() {
		return tagNo;
	}
	public void setTagNo(String tagNo) {
		this.tagNo = tagNo;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public int getPurchaseYear() {
		return purchaseYear;
	}
	public void setPurchaseYear(int purchaseYear) {
		this.purchaseYear = purchaseYear;
	}
	public double getPurchaseValue() {
		return purchaseValue;
	}
	public void setPurchaseValue(double purchaseValue) {
		this.purchaseValue = purchaseValue;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getLocationDescription() {
		return locationDescription;
	}
	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
	}
	public LearningCentre getCampus() {
		return campus;
	}
	public void setCampus(LearningCentre campus) {
		this.campus = campus;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhotoFileName() {
		return photoFileName;
	}

	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getModifyUserId() {
		return modifyUserId;
	}

	public void setModifyUserId(String modifyUserId) {
		this.modifyUserId = modifyUserId;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getModifyRemoteAddress() {
		return modifyRemoteAddress;
	}

	public void setModifyRemoteAddress(String modifyRemoteAddress) {
		this.modifyRemoteAddress = modifyRemoteAddress;
	}

	public boolean getLoanable() {
		return loanable == 1;
	}

	public void setLoanable(boolean loanable) {
		this.loanable = loanable ? 1 : 0;
	}
	
	

}
