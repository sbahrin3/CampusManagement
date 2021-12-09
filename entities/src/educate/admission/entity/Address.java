package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import educate.sis.general.entity.Country;
import educate.sis.general.entity.State;

@Embeddable
public class Address {
	
	private String address1;
	private String address2;
	private String address3;
	private String address4;
	private String city;
	@Column(length=10)
	private String postcode;
	@ManyToOne
	private State state;
	@ManyToOne
	private Country country; 
	private String statel;
	
	public Address() {
		
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public void setStatel(String statel) {
		this.statel = statel;
	}

	public String getStatel() {
		return statel;
	}	
}
