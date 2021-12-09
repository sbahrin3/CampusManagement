package educate.alumni.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity @Table(name="almn_member")
public class AlumniMember {
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String userId;
	@Column(length=100)
	private String fullName;
	@Column(length=50)
	private String icno;
	@Column(length=50)
	private String title;
	@Column(length=100)
	private String citizenship;
	@Column(length=100)
	private String citizenshipOther;
	private String permanentAddress;
	@Column(length=100)
	private String email;
	@Column(length=50)
	private String mobileNo;
	@Column(length=50)
	private String telephoneNo;
	@Column(length=100)
	private String diploma;
	@Column(length=100)
	private String firstDegree;
	@Column(length=100)
	private String masterDegree;
	@Column(length=100)
	private String phd;
	@Column(length=100)
	private String otherDegree;
	private int diplomaYear;
	private int firstDegreeYear;
	private int masterDegreeYear;
	private int phdYear;
	private int otherDegreeYear;
	
	private String photoFileName;
	private String avatarName;
	
	private String status; //membership status - pending, approved, rejected
	@Temporal(TemporalType.DATE)
	private Date statusDate;
	
	@Temporal(TemporalType.DATE)
	private Date applyDate;
	@Column(length=50)
	private String remoteAddress;
	
	@Column(length=50)
	private String initPassword;
	
	
	private int unemployed;
	private int entrepreneur;
	
	@Lob
    private byte[] profileImage;
	@Lob
    private byte[] avatarImage;
	
	
	
	public AlumniMember() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCitizenship() {
		return citizenship;
	}
	public void setCitizenship(String citizenship) {
		this.citizenship = citizenship;
	}
	public String getCitizenshipOther() {
		return citizenshipOther;
	}
	public void setCitizenshipOther(String citizenshipOther) {
		this.citizenshipOther = citizenshipOther;
	}
	public String getPermanentAddress() {
		return permanentAddress;
	}
	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getTelephoneNo() {
		return telephoneNo;
	}
	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}
	public String getDiploma() {
		return diploma;
	}
	public void setDiploma(String diploma) {
		this.diploma = diploma;
	}
	public String getFirstDegree() {
		return firstDegree;
	}
	public void setFirstDegree(String firstDegree) {
		this.firstDegree = firstDegree;
	}
	public String getMasterDegree() {
		return masterDegree;
	}
	public void setMasterDegree(String masterDegree) {
		this.masterDegree = masterDegree;
	}
	public String getPhd() {
		return phd;
	}
	public void setPhd(String phd) {
		this.phd = phd;
	}
	public String getOtherDegree() {
		return otherDegree;
	}
	public void setOtherDegree(String otherDegree) {
		this.otherDegree = otherDegree;
	}
	public int getDiplomaYear() {
		return diplomaYear;
	}
	public void setDiplomaYear(int diplomaYear) {
		this.diplomaYear = diplomaYear;
	}
	public int getFirstDegreeYear() {
		return firstDegreeYear;
	}
	public void setFirstDegreeYear(int firstDegreeYear) {
		this.firstDegreeYear = firstDegreeYear;
	}
	public int getMasterDegreeYear() {
		return masterDegreeYear;
	}
	public void setMasterDegreeYear(int masterDegreeYear) {
		this.masterDegreeYear = masterDegreeYear;
	}
	public int getPhdYear() {
		return phdYear;
	}
	public void setPhdYear(int phdYear) {
		this.phdYear = phdYear;
	}
	public int getOtherDegreeYear() {
		return otherDegreeYear;
	}
	public void setOtherDegreeYear(int otherDegreeYear) {
		this.otherDegreeYear = otherDegreeYear;
	}

	public String getPhotoFileName() {
		return photoFileName;
	}

	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getInitPassword() {
		return initPassword;
	}

	public void setInitPassword(String initPassword) {
		this.initPassword = initPassword;
	}

	public String getAvatarName() {
		return avatarName;
	}

	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIcno() {
		return icno;
	}

	public void setIcno(String icno) {
		this.icno = icno;
	}

	public boolean getEntrepreneur() {
		return entrepreneur == 1;
	}

	public void setEntrepreneur(boolean entrepreneur) {
		this.entrepreneur = entrepreneur ? 1 : 0;
	}

	public boolean getUnemployed() {
		return unemployed == 1;
	}

	public void setUnemployed(boolean unemployed) {
		this.unemployed = unemployed ? 1 : 0;
	}

	public byte[] getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
	}

	public byte[] getAvatarImage() {
		return avatarImage;
	}

	public void setAvatarImage(byte[] avatarImage) {
		this.avatarImage = avatarImage;
	}
	
	
	

}
