/**
 * 
 */
package onapp.agent.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import onapp.entity.OnappCity;
import onapp.entity.OnappCountry;
import onapp.entity.OnappState;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
@Entity
@Table(name = "onapp_agent")
public class OnappAgent { 
	
	@Id @Column(length=50)
	private String id;
	private int agentType; //0=company, 1=individual
	@Column(length=100)
	private String name;
	@Column(length=50)
	private String email;
	@Column(length=50)
	private String telephoneNo;
	@Column(length=50)
	private String faxNo;
	@Column(length=50)
	private String mobileNo;
	@Column(length=150)
	private String address1;
	@Column(length=150)
	private String address2;
	@Column(length=10)
	private String postcode;
	
	@ManyToOne @JoinColumn(name="country_id")
	private OnappCountry country;
	@ManyToOne @JoinColumn(name="state_id")
	private OnappState state;
	@ManyToOne @JoinColumn(name="city_id")
	private OnappCity city;
	
	@Column(length=50)
	private String stateName;
	@Column(length=50)
	private String cityName;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="agent")
	private List<OnappContact> contacts;
	
		
	public OnappAgent() {
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

	public int getAgentType() {
		return agentType;
	}

	public void setAgentType(int agentType) {
		this.agentType = agentType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephoneNo() {
		return telephoneNo;
	}

	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
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

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public OnappCountry getCountry() {
		return country;
	}

	public void setCountry(OnappCountry country) {
		this.country = country;
	}

	public OnappState getState() {
		return state;
	}

	public void setState(OnappState state) {
		this.state = state;
	}

	public OnappCity getCity() {
		return city;
	}

	public void setCity(OnappCity city) {
		this.city = city;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public List<OnappContact> getContacts() {
		if ( contacts == null ) contacts = new ArrayList<OnappContact>();
		return contacts;
	}

	public void setContacts(List<OnappContact> contacts) {
		this.contacts = contacts;
	}
	
	

	
}
