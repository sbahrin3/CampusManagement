package educate.parent.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.Student;
import educate.sis.general.entity.Country;
import educate.sis.general.entity.State;

@Entity
@Table(name="stu_parent")
public class Parent {
	@Id @Column(length=50)
	private String id;
	@Column(length=100)
	private String name;
	@Column(length=50)
	private String identityNo;
	@Column(length=100)
	private String address1;
	@Column(length=100)
	private String address2;
	@Column(length=50)
	private String city;
	@Column(length=50)
	private String postCode;
	@ManyToOne @JoinColumn(name="state_id")
	private State state;
	@ManyToOne @JoinColumn(name="country_id")
	private Country country;
	@ManyToOne @JoinColumn(name="nationality_id")
	private Country nationality;
	@Temporal(TemporalType.DATE)
	private Date birthDate;
	@Column(length=1)
	private String gender; //M=male, F=female
	
	@Column(length=50)
	private String email;
	@Column(length=50)
	private String phoneOffice;
	@Column(length=50)
	private String phoneHome;	
	@Column(length=50)
	private String phoneMobile;
	
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<Student> students;
	
	public Parent() {
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
	public String getIdentityNo() {
		return identityNo;
	}
	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
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
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
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
	public Country getNationality() {
		return nationality;
	}
	public void setNationality(Country nationality) {
		this.nationality = nationality;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<Student> getStudents() {
		if ( students == null ) students = new ArrayList<Student>();
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneOffice() {
		return phoneOffice;
	}

	public void setPhoneOffice(String phoneOffice) {
		this.phoneOffice = phoneOffice;
	}

	public String getPhoneHome() {
		return phoneHome;
	}

	public void setPhoneHome(String phoneHome) {
		this.phoneHome = phoneHome;
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
	}
	
	
	
}
