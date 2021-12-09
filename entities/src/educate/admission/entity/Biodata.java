package educate.admission.entity;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.sis.general.entity.Gender;
import educate.sis.general.entity.Nationality;
import educate.sis.general.entity.Race;
import educate.sis.general.entity.Religion;

@Embeddable
public class Biodata {
	
	private String icno;
	
	@Temporal(TemporalType.DATE)
	private Date dob;
	
	@Column(length=20)
	private String passport;
	
	private String name;
	
	@Column(length=60)
	private String displayName;
	
	private String stateBorn;
	
	@ManyToOne
	private Gender gender;
	
	@ManyToOne
	private Nationality nationality;
	
	@Column(length=20)
	private String telephoneNo;
	
	@Column(length=20)
	private String mobileNo;
	
	@ManyToOne
	private Race race;
	@ManyToOne
	private Religion religion;
	
	private String email;
	@Column(length=50)
	private String emailId;
	private String emailPassword;
	
	private String email2;
	
	@Column(name="visa_number", length=30)
	private String visaNumber;
	
	@Column(name="visa_expire_date")
	@Temporal(TemporalType.DATE)
	private Date visaExpireDate;
	
	@Column(name="visa_issue_date")
	@Temporal(TemporalType.DATE)
	private Date visaIssueDate;
	
	@Column(name="passport_expire_date")
	@Temporal(TemporalType.DATE)
	private Date passportExpireDate;
	
	@Column(name="passport_issue_date")
	@Temporal(TemporalType.DATE)
	private Date passportIssueDate;
	
	public Biodata() { 
		    
	}
	
	public String getIcOrPassport() {
		return icno != null && !"".equals(icno.trim()) ? icno : passport;
	}
	
	public Date getDob() {
		return dob;
	}
	public void setDob(Object d) {
		if      ( d instanceof Date ) dob = (Date) d;
		else if ( d instanceof String) setDob((String) d);
	}
	
	public void setDob(Date date) {
		this.dob = date;
	}
	
	/*
	 * The format for date is "dd-mm-yyyy"
	 */
	
	public void setDob(String date) {
		setDob(getDate(date));
	}	
	

	
	public String getIcno() {
		return icno;
	}
	public void setIcno(String icno) {
		this.icno = icno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Nationality getNationality() {
		return nationality;
	}

	public void setNationality(Nationality nationality) {
		this.nationality = nationality;
	}

	public String getTelephoneNo() {
		return telephoneNo;
	}

	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	public String getEmail2() {
		return email2;
	}

	public String getVisaNumber() {
		if (visaNumber == null) {
			visaNumber = "";
		}
		return visaNumber;
	}

	public void setVisaNumber(String visaNumber) {
		if (visaNumber == null) {
			this.visaNumber = "";
		} else {
			this.visaNumber = visaNumber;
		}
	}

	public Date getVisaExpireDate() {
		return visaExpireDate;
	}

//	public void setVisaExpireDate(Date visaExpireDate) {
//		this.visaExpireDate = visaExpireDate;
//	}
	
	public void setVisaExpireDate(Object d) {
		if      ( d instanceof Date ) visaExpireDate = (Date) d;
		else if ( d instanceof String) setVisaExpireDate((String) d);
	}
	
	public void setVisaExpireDate(String date) {
		setVisaExpireDate(getDate(date));
	}
	
	public Date getVisaIssueDate() {
		return visaIssueDate;
	}

//	public void setVisaIssueDate(Date visaIssueDate) {
//		this.visaIssueDate = visaIssueDate;
//	}
	
	public void setVisaIssueDate(Object d) {
		if      ( d instanceof Date ) visaIssueDate = (Date) d;
		else if ( d instanceof String) setVisaIssueDate((String) d);
	}
	
	public void setVisaIssueDate(String date) {
		setVisaIssueDate(getDate(date));
	}
	
	

	public Date getPassportExpireDate() {
		return passportExpireDate;
	}

//	public void setPassportExpireDate(Date passportExpireDate) {
//		this.passportExpireDate = passportExpireDate;
//	}
	
	public void setPassportExpireDate(Object d) {
		if      ( d instanceof Date ) passportExpireDate = (Date) d;
		else if ( d instanceof String) setPassportExpireDate((String) d);
	}
	
	public void setPassportExpireDate(String date) {
		setPassportExpireDate(getDate(date));
	}

	public Date getPassportIssueDate() {
		return passportIssueDate;
	}

//	public void setPassportIssueDate(Date passportIssueDate) {
//		this.passportIssueDate = passportIssueDate;
//	}

	public void setPassportIssueDate(Object d) {
		if      ( d instanceof Date ) passportIssueDate = (Date) d;
		else if ( d instanceof String) setPassportIssueDate((String) d);
	}	

	public void setPassportIssueDate(String date) {
		setPassportIssueDate(getDate(date));
	}
	
	public Date getDate(String date) {
		if (date == null || "".equals(date)) return null;
		String separator = "-";
		int day = Integer.parseInt(date.substring(0, date.indexOf(separator)).trim());
		int month = Integer.parseInt(date.substring(date.indexOf(separator)+1, date.lastIndexOf(separator)).trim())-1;
		int year = Integer.parseInt(date.substring(date.lastIndexOf(separator)+1).trim());
		return new GregorianCalendar(year, month, day).getTime();
	}

	public Religion getReligion() {
		return religion;
	}

	public void setReligion(Religion religion) {
		this.religion = religion;
	}

	public String getStateBorn() {
		return stateBorn;
	}

	public void setStateBorn(String stateBorn) {
		this.stateBorn = stateBorn;
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public void setVisaExpireDate(Date visaExpireDate) {
		this.visaExpireDate = visaExpireDate;
	}

	public void setVisaIssueDate(Date visaIssueDate) {
		this.visaIssueDate = visaIssueDate;
	}

	public void setPassportExpireDate(Date passportExpireDate) {
		this.passportExpireDate = passportExpireDate;
	}

	public void setPassportIssueDate(Date passportIssueDate) {
		this.passportIssueDate = passportIssueDate;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	
	
}
