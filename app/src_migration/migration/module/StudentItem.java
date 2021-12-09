package migration.module;

public class StudentItem {

	private String sessionCode;
	private String programCode;
	private String matricNo;
	private String name;
	private String icno;
	private String gender;
	private String raceCode;
	private String birthDate;
	private String phoneNo;
	private String address;
	private String stateCode;
	private String nationalityCode;
	
	public StudentItem(String sessionCode, String programCode, String matricNo, String name,
			String icno, String gender, String raceCode, String birthDate, String phoneNo, String address,
			String stateCode, String nationalityCode) {
		
		this.sessionCode = sessionCode;
		this.programCode = programCode;
		this.matricNo = matricNo;
		this.name = name;
		this.icno = icno;
		this.gender = gender;
		this.raceCode = raceCode;
		this.birthDate = birthDate;
		this.phoneNo = phoneNo;
		this.address = address;
		this.stateCode = stateCode;
		this.nationalityCode = nationalityCode;
		
	}
	
	public String getSessionCode() {
		return sessionCode;
	}
	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}
	public String getProgramCode() {
		return programCode;
	}
	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}
	public String getMatricNo() {
		return matricNo;
	}
	public void setMatricNo(String matricNo) {
		this.matricNo = matricNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcno() {
		return icno;
	}
	public void setIcno(String icno) {
		this.icno = icno;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getRaceCode() {
		return raceCode;
	}
	public void setRaceCode(String raceCode) {
		this.raceCode = raceCode;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getNationalityCode() {
		return nationalityCode;
	}
	public void setNationalityCode(String nationalityCode) {
		this.nationalityCode = nationalityCode;
	}
	
	
}
