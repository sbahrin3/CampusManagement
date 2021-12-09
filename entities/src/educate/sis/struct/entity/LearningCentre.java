package educate.sis.struct.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import educate.timetabling.entity.Campus;
import metadata.EntityLister;

@Entity
@Table(name="struc_learningcentre")
public class LearningCentre implements EntityLister {
	@Id
	private String id;
	private String address;
	private String name;
	private String code;
	private String matricCode;
	
	@Column(length=50)
	private String address1;
	@Column(length=50)
	private String address2;
	@Column(length=50)
	private String address3;
	@Column(length=50)
	private String address4;
	@Column(length=50)
	private String telephone;
	@Column(length=50)
	private String fax;
	@Column(length=50)
	private String website;
	
	
	@ManyToOne
	private Institution institution;
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<Campus> campuses;
	
	private int mainCampus;
	
	
	
	public LearningCentre() {
		setId(lebah.db.UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return getCode()+" "+getName();
	}
	public void setInstitution(Institution institution) {
		this.institution = institution;
	}
	public Institution getInstitution() {
		return institution;
	}
	public String getMatricCode() {
		if ( matricCode != null && !"".equals(matricCode)) return matricCode;
		else return "";
	}
	public void setMatricCode(String matricCode) {
		this.matricCode = matricCode;
	}
	
	public void setMainCampus(boolean b) {
		mainCampus = b ? 1 : 0;
	}
	
	public boolean getMainCampus() {
		return mainCampus == 1;
	}
	public List<Campus> getCampuses() {
		if ( campuses == null ) campuses = new ArrayList<Campus>();
		return campuses;
	}
	public void setCampuses(List<Campus> campuses) {
		this.campuses = campuses;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	public String getAddress4() {
		return address4;
	}
	public void setAddress4(String address4) {
		this.address4 = address4;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}

	
}
